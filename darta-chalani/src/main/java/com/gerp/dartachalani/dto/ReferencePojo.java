package com.gerp.dartachalani.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReferencePojo {

    private Long id;
    private String letterType;

    private List<Long> referenceMemoId;
    private List<Long> chalaniReferenceId;
    private List<Long> receivedReferenceId;

    private List<Long> attachedMemoId;
    private List<Long> attachedDispatchId;
    private List<Long> attachedDartaId;

    private List<Long> referencesToRemove;

    private Long memoToRemove;
    private Long dispatchToRemove;
    private Long dartaToRemove;
}
