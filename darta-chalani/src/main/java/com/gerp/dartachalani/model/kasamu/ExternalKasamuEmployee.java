package com.gerp.dartachalani.model.kasamu;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.gerp.shared.generic.api.AuditAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ExternalKasamuEmployee extends AuditAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "external_kasamu_employee_seq_gen")
    @SequenceGenerator(name = "external_kasamu_employee_seq_gen", sequenceName = "seq_external_kasamu_employee", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "pis_code")
    private String pisCode;

    @Column(name = "name")
    private String name;

    //pad
    @Column(name = "designation_code")
    private String designationCode;

    //shreni
    @Column(name = "position_code")
    private String positionCode;

    @Column(name = "service_code")
    private String serviceCode;

    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "sub_group_code")
    private String subGroupCode;

    @Column(name = "current_office_name")
    private String currentOfficeName;

    @OneToOne
    @JoinColumn(name = "kasamu_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_kasamu__external_kasamu_employee"))
    @JsonBackReference
    private Kasamu kasamu;
}
