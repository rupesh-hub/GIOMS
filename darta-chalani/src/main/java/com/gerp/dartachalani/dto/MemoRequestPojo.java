package com.gerp.dartachalani.dto;

import com.gerp.dartachalani.dto.document.SysDocumentsPojo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Size;
import java.util.List;


/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemoRequestPojo {

    private Long id;

    @Size(min =  1,  max = 200, message = "Subject must not be greater than 200")
    private String subject;
    private String content;
    private Boolean isDraft;
    private Long memoId;
    private String signature;
    private Boolean signatureIsActive;

    private Integer documentId;
    private List<MultipartFile> document;
    private List<Long> documentsToRemove;
    private List<Long> referencesToRemove;
    private List<Long> attachedMemoToRemove;
    private List<Long> attachedDispatchToRemove;
    private List<Long> attachedDartaToRemove;

    private MemoApprovalPojo approval;
        private List<Long> attachedMemoId;
    private List<Long> referenceMemoId;
    private List<Long> attachedDispatchId;
    private List<Long> chalaniReferenceId;
    private List<Long> attachedDartaId;
    private List<Long> receivedReferenceId;

    private List<SysDocumentsPojo> systemDocuments;

    private Long taskId;
    private Long projectId;

    private String hashContent;

    private Boolean isReadyToSubmit = Boolean.FALSE;
}
