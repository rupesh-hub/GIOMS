package com.gerp.usermgmt.model.office;

import com.gerp.shared.generic.api.AuditAbstract;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.utils.StringConstants;
import com.gerp.usermgmt.enums.OrganisationCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "organisation_type")
@Builder
public class OrganisationType extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organisation_type_seq_gen")
    @SequenceGenerator(name = "organisation_type_seq_gen", sequenceName = "organisation_type_seq_gen", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "name_en", length = StringConstants.DEFAULT_MAX_SIZE_50)
    private String nameEn;

    @Column(name = "name_np", length = StringConstants.DEFAULT_MAX_SIZE_200)
    private String nameNp;

    @Column(name = "organisation_category")
    @Enumerated(EnumType.STRING)
    private OrganisationCategory organisationCategory;

    @Column(name = "prefix", length = StringConstants.DEFAULT_MAX_SIZE_10)
    private String prefix;

    public OrganisationType(Long id) {
        this.id = id;
    }
}
