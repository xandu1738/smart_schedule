package com.servicecops.project.services.base;

import com.alibaba.fastjson2.JSONObject;
import com.servicecops.project.utils.OperationReturnObject;
import com.servicecops.project.utils.exceptions.AuthorizationRequiredException;

public interface BaseWebActionsImpl {
    public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException;
    public OperationReturnObject process(String action, JSONObject request) throws AuthorizationRequiredException;
}
