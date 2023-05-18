package com.gerp.usermgmt.controller.usermgmt;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.generic.GenericCrudController;
import com.gerp.usermgmt.constant.PermissionConstants;
import com.gerp.usermgmt.mapper.usermgmt.ScreenGroupMapper;
import com.gerp.usermgmt.model.ScreenGroup;
import com.gerp.usermgmt.pojo.CustomPage;
import com.gerp.usermgmt.services.usermgmt.ScreenGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/screen-groups")
public class ScreenGroupController extends GenericCrudController<ScreenGroup, Long> {
    private final ScreenGroupService screenGroupService;

    @Autowired private ScreenGroupMapper screenGroupMapper;

    public ScreenGroupController(ScreenGroupService screenGroupService) {
        this.screenGroupService = screenGroupService;
        this.moduleName = PermissionConstants.SCREEN_GROUP_SETUP_MODULE_NAME;
        this.permissionName = PermissionConstants.SCREEN_SETUP + "_" + PermissionConstants.SCREEN_GROUP_SETUP;
    }

    @Override
    @PostMapping(value = "paginated")
    public ResponseEntity<?> getPaginated(@RequestBody GetRowsRequest paginatedRequest) {
        CustomPage<ScreenGroup> page = new CustomPage(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = screenGroupMapper.selectPageMap(
                page,
                paginatedRequest.getSearchField()
        );
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @PostMapping(value = "paginated2")
    public ResponseEntity<?> getPaginated2(@RequestBody GetRowsRequest paginatedRequest) {
        Page<ScreenGroup> page = new Page(paginatedRequest.getPage(), paginatedRequest.getLimit());
        page = screenGroupMapper.selectPageMap(
                page,
                paginatedRequest.getSearchField()
        );
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }
}
