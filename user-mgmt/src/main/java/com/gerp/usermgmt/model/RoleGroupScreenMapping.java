package com.gerp.usermgmt.model;

import com.gerp.shared.generic.api.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@Table(name = "role_group_screen_mapping", uniqueConstraints = @UniqueConstraint(
        name = "unique_rgis_individualscreen", columnNames = {"role_group_id", "individual_screen_id"}
))
@NoArgsConstructor
@AllArgsConstructor
public class RoleGroupScreenMapping implements BaseEntity {
    @Id
    @SequenceGenerator(name = "role_group_screen_mapping_seq", sequenceName = "role_group_screen_mapping_seq", allocationSize = 1)
    @GeneratedValue(generator = "role_group_screen_mapping_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_group_id")
    private RoleGroup roleGroup;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "individual_screen_id")
    private IndividualScreen individualScreen;

//    private Boolean status;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "screen_group_id")
//    private ScreenGroup screenGroup;
}
