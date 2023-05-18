package custom.keycloak.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import custom.keycloak.enums.RoleType;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "role_group", uniqueConstraints = @UniqueConstraint(
        columnNames = "role_group_key", name = "UNIQUE_name_rolegroupname"
))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleGroup extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "role_group_seq", sequenceName = "role_group_seq", allocationSize = 1)
    @GeneratedValue(generator = "role_group_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "role_group_name")
    private String name;

    @Column(name = "role_group_key", nullable = false, length = 50)
    private String key;

    private String description;

    @JsonIgnore
    @Column(name = "role_type")
    private RoleType roleType = RoleType.OFFICE_CREATE;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "role_group_id", foreignKey = @ForeignKey(name = "FK_role"))
    @JsonIgnore
    Collection<RoleGroupScreenModulePrivilege> roleGroupScreenModulePrivilegeList;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<User> users;

    @Column(name = "office_code")
    private String officeCode;

    public RoleGroup(String name, String description, RoleType roleType) {
        this.name = name;
        this.description = description;
        this.roleType = roleType;
    }

    public RoleGroup(Long id) {
        this.id = id;
    }
}
