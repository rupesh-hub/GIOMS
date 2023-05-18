package com.gerp.dartachalani.model;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.common.aliasing.qual.Unique;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "footer_data")
public class FooterData extends AuditActiveAbstract {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "footer_data_seq_gen")
    @SequenceGenerator(name = "footer_data_seq_gen", sequenceName = "seq_footer_data", initialValue = 1, allocationSize = 1)
    private Long id;

    @Column(name = "office_code")
    private String officeCode;

    @Column(name = "footer")
    @Type(type = "org.hibernate.type.TextType")
    private String footer;

}
