package com.gerp.dartachalani.model;

import com.gerp.dartachalani.dto.enums.DcTablesEnum;
import com.gerp.dartachalani.model.dispatch.DispatchLetter;
import com.gerp.dartachalani.model.receive.ReceivedLetterForward;
import com.gerp.dartachalani.utils.DelegationAbstract;
import com.gerp.shared.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "delegation_table_mapper")
public class DelegationTableMapper extends DelegationAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "delegation_table_mapper_seq_gen")
    @SequenceGenerator(name = "delegation_table_mapper_seq_gen", sequenceName = "seq_delegation_table_mapper", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "table_name")
    @Enumerated(EnumType.STRING)
    private DcTablesEnum tableName;

    @Column(name = "status_from")
    @Enumerated(EnumType.STRING)
    private Status statusFrom;

    @Column(name = "status_to")
    @Enumerated(EnumType.STRING)
    private Status statusTo;

    @ManyToOne
    @JoinColumn(name = "dispatch_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_dispatch_letter__delegation_table_mapper"))
    private DispatchLetter dispatchLetter;

    @ManyToOne
    @JoinColumn(name = "received_letter_forward_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_received_letter_forward__delegation_table_mapper"))
    private ReceivedLetterForward receivedLetterForward;

}
