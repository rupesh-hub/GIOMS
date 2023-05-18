package custom.keycloak.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Table(name = "screen_group", uniqueConstraints = @UniqueConstraint(
        columnNames = "screen_group_key", name = "UNIQUE_name_screengroup"
))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScreenGroup extends AuditAbstract {

    @Id
    @SequenceGenerator(name = "screen_group_seq", sequenceName = "screen_group_seq", allocationSize = 1)
    @GeneratedValue(generator = "screen_group_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "screen_group_name")
    private String name;

    @Column(name = "screen_group_key")
    private String key;

    public void setName(String name) {
        this.name = name.trim().toUpperCase().replaceAll("\\s+", "");
    }
}
