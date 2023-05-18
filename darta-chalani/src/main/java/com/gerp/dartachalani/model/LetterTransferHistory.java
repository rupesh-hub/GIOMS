package com.gerp.dartachalani.model;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "letter_transfer_history")
public class LetterTransferHistory extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "letter_transfer_history_seq_gen")
    @SequenceGenerator(name = "letter_transfer_history_seq_gen", sequenceName = "seq_letter_transfer_history", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "history_data", columnDefinition = "text")
    private String historyData;

}
