package com.gerp.usermgmt.model.transfer;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Getter
@Setter
@Entity
@DynamicUpdate
@NoArgsConstructor
@Table(name = "trasnfer_to_be_viewed")
public class TransferToBeViewed extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trasnfer_to_be_viewed_seq_gen")
    @SequenceGenerator(name = "trasnfer_to_be_viewed", sequenceName = "trasnfer_to_be_viewed_config", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code", columnDefinition = "VARCHAR(10)")
    private String officeCode;
    @Column(name = "transfer_history_id")
    private Long transferHistoryId;

    @Column(columnDefinition = "VARCHAR(10)")
    private String type;

    public TransferToBeViewed(String officeCode, Long transferHistoryId,String type) {
        this.officeCode = officeCode;
        this.transferHistoryId = transferHistoryId;
        this.type = type;
    }
}
