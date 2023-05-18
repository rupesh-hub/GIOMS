package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.receive.ManualReceivedLetterDetail;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

public interface ManuallyReceivedLetterRepo extends GenericSoftDeleteRepository<ManualReceivedLetterDetail, Long> {
}
