import React, { useEffect, useState } from "react";
import AddButton from "../../components/AddButton";
import ScheduleDashCard from "../../components/ScheduleDashCard";
import { Calendar, Clock, Users } from "lucide-react";
import { useNavigate } from "react-router";
import { APP_ROUTE, buildRoute } from "../../config/route.config";
import { useGetDepartmentSchedulesQuery } from "../../helpers/redux/slices/extendedApis/scheduleApi";
import { Dropdown } from "primereact/dropdown";
import { SelectButton } from "primereact/selectbutton";
import { useGetAllDepartmentsQuery } from "../../helpers/redux/slices/extendedApis/departmentsApi";
import Spinner from "../../components/Spinner";
import { Card } from "primereact/card";
import { Tag } from "primereact/tag";
import { formatDate, formatTime } from "../../helpers/utils";

const Schedules = () => {
    const navigate = useNavigate()

    const [data, setData] = useState({
        departmentId: null,
        status: "active"
    })
    const { data: departments, isLoading: isLoadingDepartments } = useGetAllDepartmentsQuery({})
    const { data: schedules, isLoading: isLoadingSchedules } = useGetDepartmentSchedulesQuery({
        departmentId: data.departmentId,
        status: data.status
    }, {
        refetchOnMountOrArgChange: true,
        skip: !data.departmentId
    })

    useEffect(() => {
        if (!data?.departmentId && departments && departments?.data?.length > 0) {
            setData({
                departmentId: departments?.data[0]?.id,
            })
        }
    }, [departments, departments?.data, departments?.data?.length])

    const handleViewSchedule = (id) => {

        navigate(`/${buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.SCHEDULES, APP_ROUTE.GENERATE_SCHEDULE, "assign", id)}`)
    }

    if (isLoadingDepartments || isLoadingSchedules) {
        return <Spinner />
    }

    return (
        <div className="m-8 flex flex-col gap-4">
            <section className="flex flex-row items-center justify-between">
                <div className="flex flex-col items-start justify-start left-0 w-full">
                    <h1 className="font-bold text-3xl">Schedules</h1>
                    <p className="text-sm text-gray-500 pt-2">
                        Generate and manage shift schedules
                    </p>
                </div>
                <div>
                    <AddButton onClick={() => navigate(`/${buildRoute(APP_ROUTE.DASHBOARD, APP_ROUTE.SCHEDULES, APP_ROUTE.GENERATE_SCHEDULE)}`)} buttonName={"Generate Schedule"} />
                </div>
            </section>
            <section className="grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
                <ScheduleDashCard value={5} title={"Active Schedules"} icon={<Calendar />} styles={"bg-green-200 rounded-lg p-3 transition-colors text-green-600"} />
                <ScheduleDashCard value={5} title={"Inactive Schedules"} icon={<Clock />} styles={"bg-yellow-200 rounded-lg p-3 transition-colors text-amber-700"} />
                <ScheduleDashCard value={5} title={"Pending Schedules"} icon={<Users />} styles={"bg-blue-200 rounded-lg p-3 transition-colors text-blue-500"} />
            </section>
            <section className="pt-2">
                <h2 className="text-left">View schedules</h2>
                <div className="flex gap-2 py-2">
                    <Dropdown
                        value={data?.departmentId}
                        onChange={(e) => setData({ ...data, departmentId: e.value })}
                        options={departments?.data || []}
                        optionLabel="name"
                        optionValue="id"
                        placeholder="Select department"
                    />
                    <SelectButton
                        value={data?.status}
                        onChange={(e) => setData({ ...data, status: e.value })}
                        options={[{ "label": "Active", "value": "active" }, { "label": "Inactive", "value": "inactive" }]}
                    />
                </div>

            </section>
            <section className="grid md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-2 gap-4">
                {
                    schedules?.data?.map((schedule) => (
                        <Card key={schedule?.scheduleId} className="hover:shadow-lg transition-shadow">
                            <div>
                                <div className="flex items-center justify-between">
                                    <div>
                                        <p className="text-gray-600 mt-1">{schedule?.departmentName}</p>
                                    </div>
                                    <Tag severity={
                                        schedule?.is_active ? "success" : "danger"
                                    } value={schedule?.is_active ? "Active" : "Inactive"} />
                                </div>
                            </div>
                            <div className="p-3">
                                <div className="grid grid-cols-1 gap-6">
                                    <div>
                                        <div className="space-y-2 text-sm">
                                            <div className="flex items-center">
                                                <Calendar className="w-4 h-4 mr-2 text-gray-400" />
                                                <span className="text-gray-600">
                                                    {formatDate(schedule?.startTime)} - {formatDate(schedule?.endTime)}
                                                </span>
                                            </div>
                                            <div className="flex items-center">
                                                <Users className="w-4 h-4 mr-2 text-gray-400" />
                                                <span className="text-gray-600">
                                                    {schedule?.summary?.employee_count_in_schedule} assignments
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    {
                                        schedule?.summary?.shifts?.length > 0 &&
                                        <div>
                                            <h4 className="text-left font-medium text-gray-900 mb-3">Assignments</h4>
                                            <div className="space-y-2">
                                                {schedule?.summary?.shifts?.map((shift, index) => (
                                                    <div key={index} className="p-3 bg-gray-50 rounded-lg">
                                                        <div className="flex justify-between items-start">
                                                            <div>
                                                                <p className="text-left font-medium text-sm">{shift?.shift_details?.name}</p>
                                                                <p className="text-left text-xs text-gray-500">{formatTime(shift?.shift_details?.startTime) + " - " + formatTime(shift?.shift_details?.endTime)}</p>
                                                            </div>
                                                            <div className="text-right">
                                                                <p className="text-xs text-gray-500 border border-gray-200 p-2 rounded-sm bg-white">
                                                                    {shift?.employee_count_in_shift} assigned out of {shift?.shift_details?.maxPeople} max
                                                                </p>
                                                            </div>
                                                        </div>
                                                    </div>
                                                ))}
                                            </div>
                                        </div>
                                    }
                                </div>
                                <div className="mt-4 pt-4 border-t border-gray-100">
                                    <div className="flex space-x-2 gap-2">
                                        <button className="bg-blue-50 text-blue-600 border border-gray-200 px-3 py-1.5 rounded-sm cursor-pointer hover:bg-blue-600 hover:text-white" onClick={() => handleViewSchedule(schedule?.scheduleId)}>View Details</button>
                                        <button className="bg-white text-gray-700 border border-gray-200 px-3 py-1.5 rounded-sm cursor-pointer hover:bg-blue-600 hover:text-white" onClick={() => handleViewSchedule(schedule?.scheduleId)}>Edit</button>
                                        {schedule?.is_draft && (
                                            <button className="bg-green-600 text-white border border-gray-200 px-3 py-1.5 rounded-sm cursor-pointer hover:bg-green-600 hover:text-white">Activate</button>
                                        )}
                                    </div>
                                </div>
                            </div>
                        </Card>
                    ))
                }
            </section>
        </div>
    );
};

export default Schedules;