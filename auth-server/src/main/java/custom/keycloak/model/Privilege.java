package custom.keycloak.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity

@Table(name = "privilege", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"privilege_key"}, name = "unique_privilege_privilegename"),
})
@Builder
public class Privilege extends TimeStampAbstract {

    @Id
    @SequenceGenerator(name = "privilege_seq", sequenceName = "privilege_seq", allocationSize = 1)
    @GeneratedValue(generator = "privilege_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "privilege_name")
    private String name;

    @NotNull
    @Column(name = "privilege_key")
    private String key;

    public void setName(String name) {
        this.name = name.trim().toUpperCase().replaceAll("\\s+", "");
    }

    public Privilege(Long id) {
        this.id = id;
    }
}
