package custom.keycloak.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "module", uniqueConstraints = @UniqueConstraint(
        columnNames = {"module_key","screen_id"}, name = "unique_name_module"
))
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Module extends AuditAbstract {

    @Id
    @SequenceGenerator(name = "module_seq", sequenceName = "module_seq", allocationSize = 1)
    @GeneratedValue(generator = "module_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "module_name")
    private String name;

    @Column(name = "module_key")
    private String key;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "screen_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private IndividualScreen individualScreen;


    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "module_privilege_table")
    private List<Privilege> privilegeList;

    public void setName(String name) {
        this.name = name.trim().toUpperCase().replaceAll("\\s+", "");
    }

    public Module(Long id) {
        this.id = id;
    }
}
