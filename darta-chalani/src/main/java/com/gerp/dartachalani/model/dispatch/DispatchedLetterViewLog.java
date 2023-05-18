package com.gerp.dartachalani.model.dispatch;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "dispatched_letter_view_log", indexes =@Index(columnList = "encrypted_key"))
public class DispatchedLetterViewLog extends AuditAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "dispatched_letter_view_log_seq_gen")
    @SequenceGenerator(name = "dispatched_letter_view_log_seq_gen", sequenceName = "seq_dispatched_letter_view_log", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "encrypted_key")
    private String encryptedKey;

    @Column(name = "otp", length = 6)
    private String otp;

    @Column(name = "otp_expiry_time")
    private Timestamp otpExpiryTime;

    @Column(name = "latitude", length = 20)
    private String latitude;

    @Column(name = "longitude", length = 20)
    private String longitude;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "is_verified")
    private Boolean isVerified;

    @Column(name = "is_seen")
    private Boolean isSeen= false;

    @Column(name = "attempt_count")
    private Integer attemptCount;



    @Override
    public Long getId() {
        return id;
    }
}
