package custom.keycloak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "role_group_screen_module_privilege")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleGroupScreenModulePrivilege implements BaseEntity {

    @Id
    @SequenceGenerator(name = "role_group_screen_module_privilege_seq", sequenceName = "role_group_screen_module_privilege_seq", allocationSize = 1)
    @GeneratedValue(generator = "role_group_screen_module_privilege_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_group_id")
    private RoleGroup roleGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", foreignKey = @ForeignKey(name = "FK_RGSMP_INDIVIDUALSCREEN"))
    private IndividualScreen individualScreen;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id", foreignKey = @ForeignKey(name = "FK_RGSMP_MODULE"))
    private Module module;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "privilege_id", foreignKey = @ForeignKey(name = "FK_RGSMP_PRIVILEGE"))
    private Privilege privilege;


}
