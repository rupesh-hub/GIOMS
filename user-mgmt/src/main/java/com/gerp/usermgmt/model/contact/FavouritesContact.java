package com.gerp.usermgmt.model.contact;

import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.usermgmt.model.User;
import com.gerp.usermgmt.model.employee.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "favourites_contact")
public class FavouritesContact extends AuditActiveAbstract{

    @Id
    @SequenceGenerator(name = "favourites_contact_seq", sequenceName = "favourites_contact_seq", allocationSize = 1)
    @GeneratedValue(generator = "favourites_contact_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Employee employee;
}
