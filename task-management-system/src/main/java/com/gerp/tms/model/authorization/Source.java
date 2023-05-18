package com.gerp.tms.model.authorization;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.tms.model.AuditAbstractTms;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.List;

@Entity
@DynamicUpdate
@Getter
@Setter
public class Source extends AuditAbstractTms {

    @Id
    @GeneratedValue(generator = "source_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "source_seq_gen", sequenceName = "seq_source", initialValue = 1, allocationSize = 1)
    private Integer id;

    @Column(columnDefinition = "VARCHAR(20)")
    private String sourceUcd;

    @Column(columnDefinition = "VARCHAR(20)")
    private String sourceNameE;

    @Column(columnDefinition = "VARCHAR(20)")
    private String sourceNameN;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = SourceType.class,fetch = FetchType.LAZY)
    @JoinColumn(name = "source_id",referencedColumnName = "id",foreignKey = @ForeignKey(name = "fk_source_source_type"))
    private List<SourceType> sourceTypeList;
}
