package com.gerp.usermgmt.controller.auth;


import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.json.ApiDetail;
import com.gerp.usermgmt.mapper.ModuleApiMappingMapper;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/auth-detail")
public class AuthDetailController {



    private ModuleApiMappingMapper moduleApiMappingMapper;

    @Autowired
    private CustomMessageSource customMessageSource;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @GetMapping(value = "get-api-by-screen")
    public List<ApiDetail> getAPIAccessByScreen(@RequestParam String screenKey){
        if(!tokenProcessorService.isAdmin()){
            throw new AccessDeniedException(customMessageSource.get("user.unauthorized.access"));
        }
        return moduleApiMappingMapper.getAPIDetailsByScreen(screenKey);
    }

    @GetMapping(value = "get-api-access-by-role")
    public List<ApiDetail> getApiAccessByRole(@RequestParam String roleKey){
        if(!tokenProcessorService.isAdmin()){
            throw new AccessDeniedException(customMessageSource.get("user.unauthorized.access"));
        }
        return moduleApiMappingMapper.getApiDetailsByRoleKey(roleKey);
    }

    @GetMapping(value = "get-api-access-by-user")
    public List<ApiDetail> getApiAccessByUser(@RequestParam Long userId){
        if(!tokenProcessorService.isAdmin()){
            throw new AccessDeniedException(customMessageSource.get("user.unauthorized.access"));
        }
        return moduleApiMappingMapper.getApiAcessByUser(userId);
    }
}
