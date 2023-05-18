package com.gerp.usermgmt.util;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.usermgmt.config.generator.DelegationGenerator;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.Column;

@Getter
@Setter
public abstract class DelegationAbstract extends AuditActiveAbstract {
    @GeneratorType(
            type = DelegationGenerator.class,
            when = GenerationTime.INSERT
    )
    @Column(name = "delegated_id")
    private Integer delegatedId;
}
