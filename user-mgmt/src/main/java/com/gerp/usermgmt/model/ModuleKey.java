package com.gerp.usermgmt.model;

import com.gerp.shared.generic.api.AuditAbstract;
import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "module_key", uniqueConstraints = @UniqueConstraint(
        columnNames = {"module_key"}, name = "unique_name_module"
))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModuleKey extends AuditAbstract {

    @Id
    @SequenceGenerator(name = "module_seq", sequenceName = "module_seq", allocationSize = 1)
    @GeneratedValue(generator = "module_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "module_name")
    private String name;

    @Column(name = "module_key")
    private String key;


    public void setKey(String key) {
        this.key = key.trim().toUpperCase().replaceAll("\\s+", "");
    }

}
