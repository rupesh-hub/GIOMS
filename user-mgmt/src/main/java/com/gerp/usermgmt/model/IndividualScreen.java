package com.gerp.usermgmt.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gerp.shared.generic.api.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "individual_screen", uniqueConstraints =
@UniqueConstraint(
        columnNames = {"screen_key","screen_group_id"}, name = "unique_name_individualscreen"
))
public class IndividualScreen implements BaseEntity {

    @Id
    @SequenceGenerator(name = "individual_screen_seq", sequenceName = "individual_screen_seq", allocationSize = 1)
    @GeneratedValue(generator = "individual_screen_seq", strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "screen_name")
    private String name;

    @Column(name = "screen_key")
    private String key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_group_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ScreenGroup screenGroup;

    public void setKey(String key) {
        this.key = key.trim().toUpperCase().replaceAll("\\s+", "");
    }
}
