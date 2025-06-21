package com.servicecops.project.services.base;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.services.ShiftManagementService;
import com.servicecops.project.services.auth.AuthService;
import com.servicecops.project.services.institutions.InstitutionService;
import com.servicecops.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebActionsService {

    private final AuthService authService;
    private final InstitutionService institutionService;
    private final ShiftManagementService shiftManagementService;
    public OperationReturnObject processAction(String service, String action, JSONObject payload) {
        return switch (service) {
            case "Auth" -> authService.process(action, payload);
            case "Institution" -> institutionService.process(action, payload);
            case "Shift" -> shiftManagementService.process(action, payload);

            default -> {
                OperationReturnObject res = new OperationReturnObject();
                res.setReturnCodeAndReturnMessage(404, "UNKNOWN SERVICE");
                yield res;
            }
        };
    }
}
