package net.dg.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.dg.constraint.FieldMatch;
import org.hibernate.validator.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match.")
@FieldMatch(first = "email", second = "confirmEmail", message = "The email fields must match.")
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "firstname")
    private String firstName;

    @Column(name = "lastname")
    private String lastName;

    private String email;

    @Transient
    private String confirmEmail;

    private String password;

    @Transient
    private String confirmPassword;

    private boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private Collection<Role> roles;

}
