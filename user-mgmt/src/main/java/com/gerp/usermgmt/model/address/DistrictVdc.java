package com.gerp.usermgmt.model.address;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.generic.api.ActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Table(name = "district_vdc")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DistrictVdc extends ActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @SequenceGenerator(name = "designation_detail_seq_gen", sequenceName = "designation_detail_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "district_code", foreignKey = @ForeignKey(name = "fk_district_vdc"))
    private District district;

    @ManyToOne
    @JoinColumn(name = "municipality_vdc_code", foreignKey = @ForeignKey(name = "fk_district_vdc"))
    private MunicipalityVdc municipalityVdc;


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
