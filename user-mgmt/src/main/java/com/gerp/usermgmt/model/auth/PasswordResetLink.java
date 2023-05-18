package com.gerp.usermgmt.model.auth;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.usermgmt.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
@Table(name = "passowrd_reset_link")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordResetLink extends AuditAbstract {
    @Id
    @SequenceGenerator(name = "password_reset_link_seq", sequenceName = "password_reset_link_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "password_reset_link_seq")
    private Long id;

    private String linkToken;

    private Date expiryDate;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "token_id")
    private PasswordResetToken tokenId;

    private Date calculateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}