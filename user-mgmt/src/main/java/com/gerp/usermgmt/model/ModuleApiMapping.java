package com.gerp.usermgmt.model;

import com.gerp.shared.enums.ApiMethod;
import com.gerp.shared.generic.api.TimeStampAbstract;
import lombok.*;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "module_api_mapping",uniqueConstraints = @UniqueConstraint(columnNames = {"module_id", "privilege_id", "api", "method"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleApiMapping extends TimeStampAbstract {
    @Id
    @SequenceGenerator(name = "module_api_mapping_seq", sequenceName = "module_api_mapping_seq", allocationSize = 1)
    @GeneratedValue(generator = "module_api_mapping_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "module_id", columnDefinition = "int references module(id)", nullable = false)
    private Long moduleId;

    @Column(name = "privilege_id", columnDefinition = "int references privilege(id)", nullable = false)
    private Long privilegeId;

    @Column(name = "api", nullable = false, columnDefinition = "varchar(250)")
    private String api;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", nullable = false,columnDefinition = "varchar(256)")
    private ApiMethod method;

    @Column(name = "is_active")
    private Boolean isActive = Boolean.TRUE;

    @Transient
    private String securityKey;
}
