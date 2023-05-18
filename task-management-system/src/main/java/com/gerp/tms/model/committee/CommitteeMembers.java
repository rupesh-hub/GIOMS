package com.gerp.tms.model.committee;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Data
@DynamicUpdate
@Table(name = "committee_members")
public class CommitteeMembers {
    public CommitteeMembers(String memberId) {
        this.memberId = memberId;
    }

    @Id
    @GeneratedValue(generator = "committee_members_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "committee_members_seq_gen", sequenceName = "seq_committee_members", initialValue = 1, allocationSize = 1)
    private Integer id;


    @Column(name = "member_id")
    private String memberId;

}
