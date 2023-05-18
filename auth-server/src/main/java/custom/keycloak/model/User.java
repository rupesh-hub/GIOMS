package custom.keycloak.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import custom.keycloak.constant.StringConstants;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(name = "UNIQUE_user_user", columnNames = {"user_name"}),
        @UniqueConstraint(name = "UNIQUE_user_pisempcode", columnNames = {"pis_employee_code"})
})

@NamedQueries({
        @NamedQuery(name="getUserByUsername", query="select u from User u where u.username = :username and u.isActive = true "),
        @NamedQuery(name="getUserCount", query="select count(u) from User u"),
        @NamedQuery(name="getAllUsers", query="select u from User u"),
        @NamedQuery(name="searchForUser", query="select u from User u where " +
                "( lower(u.username) like :search ) order by u.username"),
})

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends AuditActiveAbstract {

    @Id
    @SequenceGenerator(name = "users_seq", sequenceName = "users_seq", allocationSize = 1)
    @GeneratedValue(generator = "users_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @NotNull
    @Column(name = "user_name")
//    @Size(min = StringConstants.DEFAULT_MIN_SIZE_USER, max = StringConstants.DEFAULT_MAX_SIZE_20)
    private String username;

    @NotNull
    @NotBlank
    @JsonBackReference(value = "passBackRef")
    @Size(min = StringConstants.DEFAULT_MIN_SIZE)
    private String password;

    @Column(name = "is_password_changed")
    private Boolean isPasswordChanged = false;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "last_login_date")
    private String lastLoginDate;

    @Builder.Default
    @Column(name = "account_non_expired")
    private boolean accountNonExpired = true;
    @Builder.Default
    @Column(name = "credentials_non_expired")
    private boolean credentialsNonExpired = true;
    @Builder.Default
    @Column(name = "account_non_locked")
    private boolean accountNonLocked = true;

    /**
     * That roles that user belongs to
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            foreignKey = @ForeignKey(name = "FK_users_roles_user_id"),
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseForeignKey = @ForeignKey(name = "FK_users_roles_role_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(name = "UNIQUE_user_role", columnNames = {"user_id", "role_id"})
    )
    @JsonIgnore
    private Collection<RoleGroup> roles;

    @Column(name = "pis_employee_code")
    private String pisEmployeeCode;
    @Column(name = "office_code")
    private String officeCode;

    @Transient
    private String email;

    /**
     * Default Constructor
     *
     * @param user User
     */
    public User(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.setActive(user.isActive());
        this.accountNonExpired = user.isAccountNonExpired();
        this.credentialsNonExpired = user.isCredentialsNonExpired();
        this.accountNonLocked = user.isAccountNonLocked();
        this.roles = user.getRoles();
    }

    public User(Long id) {
        this.id = id;
    }

}
