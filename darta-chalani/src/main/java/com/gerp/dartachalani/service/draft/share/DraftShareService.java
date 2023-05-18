package com.gerp.dartachalani.service.draft.share;

import com.gerp.dartachalani.dto.DraftShareDto;
import com.gerp.dartachalani.dto.DraftShareLogPojo;
import com.gerp.dartachalani.dto.DraftSharePojo;

import java.util.List;

public interface DraftShareService {

    boolean share(DraftShareDto draftShareDto);

    boolean update(DraftShareDto draftShareDto);

    List<DraftSharePojo> getDraftShareList(Long dispatchId, Long memoId);

    List<DraftSharePojo> getDraftShareLog(Long dispatchId, Long memoId);

    boolean removeEmployee(String pisCode, String sectionCode, Long dispatchId, Long memoId);
}
