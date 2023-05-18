package com.gerp.usermgmt.model;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "user_role_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserRoleLog extends AuditAbstract {

    @Id
    @SequenceGenerator(name = "user_role_log_sequence", sequenceName = "user_role_log_sequence", allocationSize = 1)
    @GeneratedValue(generator = "user_role_log_sequence",strategy = GenerationType.SEQUENCE)
    private Long id;

    @Lob
    private String roleLogData;

    @Column(name = "role_changed_user_id")
    @NotNull
    private Long roleChangedUserId;



}
