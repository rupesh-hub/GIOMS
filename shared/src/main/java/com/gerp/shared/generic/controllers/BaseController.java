package com.gerp.shared.generic.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.enums.ResponseStatus;
import com.gerp.shared.pojo.GlobalApiResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * Base Controller
 */
public class BaseController {


    /**
     * ObjectMapper instance
     */
    public ObjectMapper objectMapper = new ObjectMapper();

//    /**
//     * Global API Response Instance
//     */
//    @Autowired
//    protected GlobalApiResponse globalApiResponse;

    /**
     * API Success ResponseStatus
     */
    protected final ResponseStatus API_SUCCESS_STATUS = ResponseStatus.SUCCESS;

    /**
     * API Error ResponseStatus
     */
    protected final ResponseStatus API_ERROR_STATUS = ResponseStatus.FAIL;

    /**
     * Message Source Instance
     */
    @Autowired
    protected CustomMessageSource customMessageSource;

    /**
     * Module Name
     */
    protected String moduleName;
    protected String moduleName2;
    protected String moduleName3;
    protected String screenName;
    protected String permissionName;
    protected String permissionName2;
    protected String permissionApproval;
    protected String permissionReport;
    protected String permissionReport2;

    /**
     * This module is used to fetch the available permissions of current user in particular module
     */
    protected String module;
    protected String module2;
    protected Map<String, String> permissions;

    /**
     * Function that sends successful API Response
     *
     * @param message
     * @param data
     * @return
     */
    protected GlobalApiResponse successResponse(String message, Object data) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatus(API_SUCCESS_STATUS);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(data);
        return globalApiResponse;
    }

    /**
     * Function that sends error API Response
     *
     * @param message
     * @param errors
     * @return
     */
    protected GlobalApiResponse errorResponse(String message, Object errors) {
        GlobalApiResponse globalApiResponse = new GlobalApiResponse();
        globalApiResponse.setStatus(API_ERROR_STATUS);
        globalApiResponse.setMessage(message);
        globalApiResponse.setData(errors);
        return globalApiResponse;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public String getPermissionName2() {
        return permissionName2;
    }

    public String getPermissionApproval() {
        return permissionApproval;
    }

    public String getPermissionReport() {
        return permissionReport;
    }

    public String getPermissionReport2() {
        return permissionReport2;
    }
}
