package com.gerp.dartachalani.controller.memo;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.constant.PermissionConstants;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.document.DocumentMasterResponsePojo;
import com.gerp.dartachalani.dto.document.DocumentResponsePojo;
import com.gerp.dartachalani.dto.document.SysDocumentsPojo;
import com.gerp.dartachalani.dto.systemFiles.SystemFilesDto;
import com.gerp.dartachalani.model.memo.Memo;
import com.gerp.dartachalani.model.memo.MemoForward;
import com.gerp.dartachalani.service.MemoService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.generic.controllers.BaseController;
import com.gerp.shared.pojo.ApprovalPojo;
import com.gerp.shared.pojo.GlobalApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@RestController
@RequestMapping("/memo")
public class MemoController extends BaseController {

    private final MemoService memoService;
    private final CustomMessageSource customMessageSource;

    public MemoController(MemoService memoService, CustomMessageSource customMessageSource) {
        this.memoService = memoService;
        this.customMessageSource = customMessageSource;
        this.moduleName = PermissionConstants.MEMO_MODULE_NAME;
        this.permissionName = PermissionConstants.MEMOS + "_" + PermissionConstants.MEMO;
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @Operation(summary = "Save Tippani", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoRequestPojo.class))})
    @PostMapping
    public ResponseEntity<?> create(@Valid @ModelAttribute MemoRequestPojo memoRequestPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            Memo memo = memoService.saveMemo(memoRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.create", customMessageSource.get(moduleName.toLowerCase())),
                            memo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    //@PreAuthorize("hasPermission(#this.this.permissionName,'update')")
    @Operation(summary = "Update Tippani", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoRequestPojo.class))})
    @PutMapping
    public ResponseEntity<?> update(@Valid @ModelAttribute MemoRequestPojo memoRequestPojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            memoService.updateMemo(memoRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                            memoRequestPojo.getId())
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    //    @PreAuthorize("hasPermission(#this.this.permissionName,'create')")
    @PostMapping("/forward")
    public ResponseEntity<?> forwardMemo(@Valid @RequestBody MemoForwardRequestPojo memoForwardRequestPojo, BindingResult bindingResult) throws BindException {

        if (!bindingResult.hasErrors()) {
            List<MemoForward> memoForwards = memoService.forwardMemo(memoForwardRequestPojo);
            return ResponseEntity.ok(
                    successResponse(customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                            memoForwards)
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PostMapping("/content")
    @Operation(summary = "Add Content", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoContentPojo.class))})
    public ResponseEntity<?> content(@Valid @RequestBody MemoContentPojo memoComment, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = memoService.saveContent(memoComment);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.create", customMessageSource.get("received.message")),
                            id
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/content")
    @Operation(summary = "Update Content", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoContentPojo.class))})
    public ResponseEntity<?> editContent(@Valid @RequestBody MemoContentPojo memoComment, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = memoService.editContent(memoComment);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.update", customMessageSource.get("received.message")),
                            id
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @PutMapping("/content/doc")
    @Operation(summary = "Update Document", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoContentPojo.class))})
    public ResponseEntity<?> editContentDoc(@Valid @ModelAttribute MemoContentPojo memoComment, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = memoService.editContentDoc(memoComment);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.update", customMessageSource.get("received.message")),
                            id
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

//    @PostMapping("/comment")
//    public ResponseEntity<?> comment(@Valid @RequestBody MemoContentPojo memoComment, BindingResult bindingResult) throws BindException {
//        if(!bindingResult.hasErrors()) {
//            Long id = memoService.saveComment(memoComment);
//            return ResponseEntity.ok(
//                    successResponse(
//                            customMessageSource.get("crud.create", customMessageSource.get("received.message")),
//                            id
//                    )
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }
//
//    @PutMapping("/comment")
//    public ResponseEntity<?> editComment(@Valid @RequestBody MemoContentPojo memoComment, BindingResult bindingResult) throws BindException {
//        if(!bindingResult.hasErrors()) {
//            Long id = memoService.editComment(memoComment);
//            return ResponseEntity.ok(
//                    successResponse(
//                            customMessageSource.get("crud.update", customMessageSource.get("received.message")),
//                            id
//                    )
//            );
//        } else {
//            throw new BindException(bindingResult);
//        }
//    }

//    @PreAuthorize("hasPermission(#this.this.permissionName,'update')")
//    @PutMapping("/forward/status")
//    public ResponseEntity<?> updateStatus(@RequestBody StatusPojo statusPojo) {
//        memoService.updateStatus(statusPojo);
//        return ResponseEntity.ok(
//                successResponse(
//                        customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
//                        statusPojo.getId()
//                )
//        );
//    }

    @PutMapping("/approval")
    @Operation(summary = "Approval Flow", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApprovalPojo.class))})
    public ResponseEntity<?> updateApproval(@RequestBody ApprovalPojo approvalPojo) {
        memoService.updateApproval(approvalPojo);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
                        approvalPojo.getId()
                )
        );
    }

    @PutMapping("/suggestion")
    @Operation(summary = "Raaye Flow", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApprovalPojo.class))})
    public ResponseEntity<?> updateSuggestion(@RequestBody ApprovalPojo approvalPojo) {
        memoService.updateSuggestion(approvalPojo);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.status", customMessageSource.get(moduleName.toLowerCase())),
                        approvalPojo.getId()
                )
        );
    }

    @GetMapping("/suggestion")
    public ResponseEntity<?> getSuggestion() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getSuggestions();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/suggestion/forward")
    public ResponseEntity<?> getSuggestionForward() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getSuggestionsForward();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @PutMapping("/external/approval")
    @Operation(summary = "Send back from raaye", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ApprovalPojo.class))})
    public ResponseEntity<?> externalApproval(@RequestBody ApprovalPojo approvalPojo) {
        memoService.externalApproval(approvalPojo);
        return ResponseEntity.ok(
                successResponse(
                        customMessageSource.get("crud.forward", customMessageSource.get(moduleName.toLowerCase())),
                        approvalPojo.getId()
                )
        );
    }

    @GetMapping("/drafts")
    public ResponseEntity<?> getDrafts() {
        ArrayList<MemoResponsePojo> memoResponsePojoList = memoService.getDrafts();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/saved")
    public ResponseEntity<?> getSaved() {
        ArrayList<MemoResponsePojo> memoResponsePojoList = memoService.getSaved();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAllMemo() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getAllMemo();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/get-all/approval")
    public ResponseEntity<?> getAllMemoForApproval() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getAllMemoForApproval();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/get-all/approval/forwarded")
    public ResponseEntity<?> getAllMemoForApprovalForwarded() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getAllMemoForApprovalForwarded();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/get-all/forwarded")
    public ResponseEntity<?> getAllMemoForwarded() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getAllMemoForwarded();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/get-all/in-progress")
    public ResponseEntity<?> getAllMemoInProgress() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getAllMemoInProgress();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/get-all/finalized")
    public ResponseEntity<?> getAllMemoFinalized() {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getAllMemoFinalized();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Tippani by id", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<?> getMemoById(@PathVariable("id") Long id) {
        MemoResponsePojo memoResponse = memoService.getMemoById(id);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponse)
        );
    }

    /**
     * this api is for getting memo by id for reference memo
     * this api not return reference memos
     */
    @GetMapping("/for-reference")
    @Operation(summary = "Get Tippani by id for reference", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a memo by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<?> getMemoByIdForReference(@RequestParam("memoId") Long memoId,
                                                     @RequestParam(value = "referencedMemoId") Long referencedMemoId) {
        MemoReferenceResponsePojo memoResponse = memoService.getMemoByIdForReference(memoId, referencedMemoId, 1);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponse)
        );
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<?> getMemoByParentId(@PathVariable("parentId") Long parentId) {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getMemoByParentId(parentId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/receiver/{receiverPisCode}")
    public ResponseEntity<?> getMemoByReceiverPisCode(@PathVariable("receiverPisCode") String receiverPisCode) {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getMemoByReceiverPisCode(receiverPisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/receiver/in-progress/{receiverPisCode}")
    public ResponseEntity<?> getMemoReceiverInProgress(@PathVariable("receiverPisCode") String receiverPisCode) {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getMemoReceiverInProgress(receiverPisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    @GetMapping("/receiver/finalized/{receiverPisCode}")
    public ResponseEntity<?> getMemoReceiverFinalized(@PathVariable("receiverPisCode") String receiverPisCode) {
        List<MemoResponsePojo> memoResponsePojoList = memoService.getMemoReceiverFinalized(receiverPisCode);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponsePojoList)
        );
    }

    /**
     * url = "eg. memo/115"
     * this api is used to delete the letter
     * only drafted letters are allowed to delete
     * only the letter creator is allowed to delete letter
     **/
//    @PreAuthorize("hasPermission(#this.this.permissionName,'delete')")
    @DeleteMapping("/{memoId}")
    @Operation(summary = "Delete draft Tippani", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Soft delete memo", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoContentPojo.class))})
    public ResponseEntity<?> deleteMemo(@PathVariable("memoId") Long memoId) {
        memoService.deleteMemo(memoId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        memoId)
        );
    }

    /**
     * url = "eg. memo/archive/115"
     * this api is used to archive the letter
     * only reverted letter are allowed to archive
     * only the letter creator is allowed to archive letter
     **/
    @DeleteMapping("/archive/{memoId}")
    @Operation(summary = "Archive reverted Tippani", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Archive memo", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoContentPojo.class))})
    public ResponseEntity<?> archiveMemo(@PathVariable("memoId") Long memoId) {
        memoService.archiveMemo(memoId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.archive", customMessageSource.get(moduleName.toLowerCase())),
                        memoId)
        );
    }

    /**
     * url = "eg. memo/115"
     * this api is used to restore the letter
     * only the letter creator is allowed to restore letter
     **/
    @PutMapping("/{memoId}")
    @Operation(summary = "restore archived Tippani", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoContentPojo.class))})
    public ResponseEntity<?> restoreMemo(@PathVariable("memoId") Long memoId) {
        memoService.restoreMemo(memoId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.restore", customMessageSource.get(moduleName.toLowerCase())),
                        memoId)
        );
    }

    @DeleteMapping("/content/{contentId}")
    public ResponseEntity<?> deleteContent(@PathVariable("contentId") Long contentId) {
        memoService.deleteContent(contentId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.delete", customMessageSource.get(moduleName.toLowerCase())),
                        contentId)
        );
    }

    @PostMapping(value = "/paginated")
    @Operation(summary = "Get tippani list according to payload", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<?> getPaginatedData(@RequestBody GetRowsRequest paginatedRequest) {
        Page<MemoResponsePojo> page = memoService.filterData(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @PostMapping(value = "/archive-list")
    @Operation(summary = "Get archive tippani list according to payload", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<?> getArvhiveTippani(@RequestBody GetRowsRequest paginatedRequest) {
        Page<MemoResponsePojo> page = memoService.getArchiveTippani(paginatedRequest);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        page)
        );
    }

    @GetMapping("/system/doc")
    @Operation(summary = "Get all documents already in the system", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a list", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = SysDocumentsPojo.class))})
    public ResponseEntity<?> getSystemDocuments() {
        List<SysDocumentsPojo> documentList = memoService.getSystemDocuments();
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get("system.file")),
                        documentList)
        );
    }

    @PostMapping("/reference")
    @Operation(summary = "Save References", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ReferencePojo.class))})
    public ResponseEntity<?> reference(@Valid @RequestBody ReferencePojo referencePojo, BindingResult bindingResult) throws BindException {
        if (!bindingResult.hasErrors()) {
            Long id = memoService.saveReference(referencePojo);
            return ResponseEntity.ok(
                    successResponse(
                            customMessageSource.get("crud.create", customMessageSource.get("reference.letter")),
                            id
                    )
            );
        } else {
            throw new BindException(bindingResult);
        }
    }

    @GetMapping("/verification-link/{id}")
    public ResponseEntity<?> getVerificationLink(@PathVariable Long id) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                memoService.getVerificationLink(id)));
    }

    @PutMapping(value = "/imp")
    @Operation(summary = "Set important flag for tippani user", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Returns a id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<GlobalApiResponse> setImportantFlag(@RequestParam Long id, @RequestParam boolean value) {
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.update", customMessageSource.get(moduleName.toLowerCase())),
                        memoService.setImportantFlag(id, value))
        );
    }

    /**
     * Get search recommendation for tippani view(frontend)
     *
     * @return
     */
    @GetMapping("/tippani-search-recommendation")
    public ResponseEntity<GlobalApiResponse> getTippaniSearchRecommendation() {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), memoService.getTippaniSearchRecommendation()));
    }

    /**
     * this api gives the list of referenced tippani list
     * ex: if tippani A -> B -> C then with the id of tippani C this api give list of tippani B,A.
     **/
    @GetMapping("/reference-list/{memoId}")
    public ResponseEntity<GlobalApiResponse> getListOfReferencedMemos(@PathVariable("memoId") Long memoId) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), memoService.getListOfReferencedMemos(memoId)));
    }

    /**
     * this api gives the list of activity logs
     * if referenceMemoId is not send then list return by memoId
     * if referenceMemoId is send then list return by referencememoId
     **/
    @GetMapping("/activity-logs")
    public ResponseEntity<GlobalApiResponse> getActivityLogs(@RequestParam("memoId") Long memoId,
                                                             @RequestParam(value = "referenceMemoId", required = false) Long referenceMemoId,
                                                             @RequestParam(value = "fiscalYearCode", required = false) String fiscalYearCode) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), memoService.getActivityLog(memoId, referenceMemoId, fiscalYearCode)));
    }

    /**
     * this api gives the list of memo id and subject that are referenced
     **/
    @GetMapping("/subject-search-list")
    public ResponseEntity<GlobalApiResponse> getMemoSubjectSearchList(@RequestParam("memoId") Long memoId) {
        return ResponseEntity.ok(successResponse(customMessageSource.get("success.retrieve"), memoService.getMemoSubjectSearchList(memoId)));
    }

    /**
     * this api is for getting memo templates by id for reference memo
     * this api not return all reference memos templates
     */
    @GetMapping("/for-reference-templates")
    @Operation(summary = "Get Tippani templates by id for reference", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a memo templates by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoResponsePojo.class))})
    public ResponseEntity<?> getTemplateListOfLinkedTippani(@RequestParam("memoId") Long memoId) {
        List<MemoReferenceResponsePojo> memoResponse = memoService.getTemplatesOfAllLinkedReferenceMemos(memoId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponse)
        );
    }

    /**
     * this api is for getting memo documents by id for reference memo
     * this api not return all reference memos documents
     * this api filters by memoReferenceId
     */
    @GetMapping("/reference-list-documents")
    @Operation(summary = "Get Tippani documents by id for reference", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a memo documents by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoReferenceDoc.class))})
    public ResponseEntity<?> getMemoReferenceDocuments(@RequestParam("memoId") Long memoId,
                                                       @RequestParam(value = "referenceMemoId", required = false) Long referenceMemoId) {
        List<MemoReferenceDoc> memoResponse = memoService.getDocumentsOfAllLinkedReferenceMemos(memoId, referenceMemoId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponse)
        );
    }

    /**
     * this api is for getting memo attached files by id for reference memo
     * this api not return all reference attached system documents ie. attached memo list
     * this api filters by memoReferenceId
     */
    @GetMapping("/attached-system-doc")
    @Operation(summary = "Get Tippani attached system documents by id for reference", tags = {"Memo"}, security = {@SecurityRequirement(name = "bearer-key")})
    @ApiResponse(responseCode = "200", description = "Return a memo documents by id", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = MemoReferenceDoc.class))})
    public ResponseEntity<?> getMemoSystemDocuments(@RequestParam("memoId") Long memoId,
                                                       @RequestParam(value = "referenceMemoId", required = false) Long referenceMemoId) {
        List<SystemFilesDto> memoResponse = memoService.getSystemDocumentsOfAllLinkedReferenceMemos(memoId, referenceMemoId);
        return ResponseEntity.ok(
                successResponse(customMessageSource.get("crud.get", customMessageSource.get(moduleName.toLowerCase())),
                        memoResponse)
        );
    }
}
