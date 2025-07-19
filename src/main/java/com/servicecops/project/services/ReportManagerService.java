package com.servicecops.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.jmsoft.Moonlight.helpers.EntityCommand;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.models.jpahelpers.enums.AppDomains;
import com.servicecops.project.models.jpahelpers.sortingAndFiltering.NativeQueryHelperService;
import com.servicecops.project.services.base.BaseWebActionsService;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class ReportManagerService extends BaseWebActionsService {

    private final NativeQueryHelperService nativeQueryHelperService;

    public ReportManagerService(NativeQueryHelperService nativeQueryHelperService) {
        super();
        this.nativeQueryHelperService = nativeQueryHelperService;
    }

    private OperationReturnObject getDashboardData(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();

        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }

        Integer institutionId = search.getInteger("institution_id");
        if (!getUserDomain().equals(AppDomains.BACK_OFFICE)) {
            institutionId = authenticatedUser.getInstitutionId();

            if (institutionId == null) {
                throw new IllegalStateException("Could not resolve user's institution");
            }
        }

        String sql = """
                with departments as (select d.*
                                     from departments d
                                     where d.institution_id = :institutionId),
                     active_employees as (select e.*
                                          from employee e
                                          where (e.archived is null or e.archived = false)
                                            and e.department in (select id from departments)),
                     archived_employees as (select e.*
                                            from employee e
                                            where e.archived = true
                                              and e.department in (select id from departments)),
                     swap_requests as (select ssr.*
                                       from shift_swap_request ssr
                                       where ssr.from_employee in (select id from active_employees)
                                          or ssr.to_employee in (select id from active_employees) and ssr.status = 'PENDING'),
                     shifts as (select s.*
                                from shift s
                                where s.department_id in (select id from departments)),
                     schedules_this_month as (select sch.*
                                              from schedule sch
                                              where sch.department_id in (select id from departments)
                                                and sch.start_date >= date_trunc('month', current_date)),
                     schedules_overrall as (select sch.*
                                            from schedule sch
                                            where sch.department_id in (select id from departments))
                select (select coalesce(count(*), 0) from departments)          as departments,
                       (select coalesce(count(*), 0) from active_employees)     as employees,
                       (select coalesce(count(*), 0) from swap_requests)        as swap_requests,
                       (select coalesce(count(*), 0) from archived_employees)   as archived_employees,
                       (select coalesce(count(*), 0) from schedules_this_month) as schedules_this_month,
                       (select coalesce(count(*), 0) from schedules_overrall)   as schedules_overall,
                       (select coalesce(count(*), 0) from shifts)               as shifts
                """;

        String adminSql = """
                with departments as (select d.*
                                     from departments d),
                     active_employees as (select e.*
                                          from employee e
                                          where (e.archived is null or e.archived = false)
                                            and e.department in (select id from departments)),
                     archived_employees as (select e.*
                                            from employee e
                                            where e.archived = true
                                              and e.department in (select id from departments)),
                     swap_requests as (select ssr.*
                                       from shift_swap_request ssr
                                       where ssr.from_employee in (select id from active_employees)
                                          or ssr.to_employee in (select id from active_employees) and ssr.status = 'PENDING'),
                     shifts as (select s.*
                                from shift s
                                where s.department_id in (select id from departments)),
                     schedules_this_month as (select sch.*
                                              from schedule sch
                                              where sch.department_id in (select id from departments)
                                                and sch.start_date >= date_trunc('month', current_date)),
                     schedules_overrall as (select sch.*
                                            from schedule sch
                                            where sch.department_id in (select id from departments))
                select (select coalesce(count(*), 0) from departments)          as departments,
                       (select coalesce(count(*), 0) from active_employees)     as employees,
                       (select coalesce(count(*), 0) from swap_requests)        as swap_requests,
                       (select coalesce(count(*), 0) from archived_employees)   as archived_employees,
                       (select coalesce(count(*), 0) from schedules_this_month) as schedules_this_month,
                       (select coalesce(count(*), 0) from schedules_overrall)   as schedules_overall,
                       (select coalesce(count(*), 0) from shifts)               as shifts
                """;

        EntityCommand command = new EntityCommand();
        command.setHql(sql);

        if (getUserDomain().equals(AppDomains.BACK_OFFICE) && institutionId == null) {
            command.setHql(adminSql);
        }
        command.setParameterValue("institutionId", institutionId);
        command.setAliasToResultsMapper(AliasToEntityMapResultTransformer.INSTANCE);

        List<HashMap<String, Object>> res = nativeQueryHelperService.customFiler(command);

        OperationReturnObject returnObject = new OperationReturnObject();
        if (res.isEmpty()) {
            returnObject.setCodeAndMessageAndReturnObject(404, "No data found", null);
            return returnObject;
        }

        returnObject.setCodeAndMessageAndReturnObject(200, "Success", res.get(0));
        return returnObject;
    }

    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
        return switch (action) {
            case "getDashboardData" -> getDashboardData(request);
            default -> {
                throw new IllegalStateException("Unexpected value: " + action);
            }
        };
    }
}
