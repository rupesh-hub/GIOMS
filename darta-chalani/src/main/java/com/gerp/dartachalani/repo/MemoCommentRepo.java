package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.memo.MemoComment;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

public interface MemoCommentRepo extends GenericSoftDeleteRepository<MemoComment, Long> {
}
