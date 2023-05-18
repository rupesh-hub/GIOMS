package com.gerp.usermgmt.model.office;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


// remove this later
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "office_type")
@Builder
@DynamicUpdate
public class OfficeType  extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "office_type_seq_gen")
    @SequenceGenerator(name = "office_type_seq_gen", sequenceName = "office_type_seq_gen", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(name = "name_np", nullable = false)
    @NotNull
    @NotBlank
    private String nameNp;

    @NotNull
    @NotBlank
    @Column(name = "name_en", nullable = false)
    private String nameEn;

    public OfficeType(Integer id) {
        this.id = id;
    }
}
