package com.gerp.dartachalani.repo;

import com.gerp.dartachalani.model.dispatch.DispatchedLetterReceiver;
import com.gerp.shared.generic.api.GenericSoftDeleteRepository;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

public interface DispatchReceiverRepo extends GenericSoftDeleteRepository<DispatchedLetterReceiver, Long> {
}
