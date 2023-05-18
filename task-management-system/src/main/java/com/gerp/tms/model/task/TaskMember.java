package com.gerp.tms.model.task;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.BaseEntity;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Data
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "task_member")
public class TaskMember extends AuditAbstractTms {
    @Id
    @GeneratedValue(generator = "task_member_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "task_member_seq_gen", sequenceName = "seq_task_member", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "member_id")
    private String memberId;

    @Column(name = "is_member")
    private Boolean isMember;

}
