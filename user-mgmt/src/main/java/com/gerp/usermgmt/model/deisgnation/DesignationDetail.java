package com.gerp.usermgmt.model.deisgnation;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.ActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import com.gerp.usermgmt.model.employee.Position;
import com.gerp.usermgmt.model.employee.Service;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Table(name = "designation_detail")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DesignationDetail extends ActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "designation_detail_seq_gen")
    @SequenceGenerator(name = "designation_detail_seq_gen", sequenceName = "designation_detail_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "designation_code", foreignKey = @ForeignKey(name = "fk_designation_detail_functional_designation"))
    private FunctionalDesignation designation;

    @ManyToOne
    @JoinColumn(name = "position_code", foreignKey = @ForeignKey(name = "fk_designation_detail_position"))
    private Position position;

    @ManyToOne()
    @JoinColumn(name = "service_code", foreignKey = @ForeignKey(name = "fk_designation_detail_service"))
    private Service service;

    @Column(name = "approved_date_np", columnDefinition = "VARCHAR(20)")
    private String approvedDateNp;

    private Boolean approved;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "approved_date_En")
    private LocalDate approvedDateEN;


    @Column(name = "approved_by")
    private String approvedBy;
}
