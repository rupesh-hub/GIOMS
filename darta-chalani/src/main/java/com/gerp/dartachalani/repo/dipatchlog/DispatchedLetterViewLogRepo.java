package com.gerp.dartachalani.repo.dipatchlog;

import com.gerp.dartachalani.model.dispatch.DispatchedLetterViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface DispatchedLetterViewLogRepo extends JpaRepository<DispatchedLetterViewLog, Long> {

    DispatchedLetterViewLog findByEncryptedKeyAndOtp(String encrpytedKey, String otp);

    @Modifying
    @Query("update DispatchedLetterViewLog dlvl set dlvl.otpExpiryTime = current_timestamp where dlvl.mobile = :mobileNumber and dlvl.encryptedKey = :key and dlvl.otpExpiryTime > current_timestamp ")
    void deactivateAllOtp(@Param("mobileNumber") String mobile, @Param("key") String key);

}

