package com.gerp.attendance.util;

import com.gerp.attendance.config.DelegationGenerator;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GeneratorType;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class DelegationAbstract extends AuditActiveAbstract {
//    @GeneratorType(
//            type = DelegationGenerator.class,
//            when = GenerationTime.ALWAYS
//    )
    @Column(name = "delegated_id")
    private Integer delegatedId;
}
