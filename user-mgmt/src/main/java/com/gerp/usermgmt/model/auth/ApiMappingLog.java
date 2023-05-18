package com.gerp.usermgmt.model.auth;

import com.gerp.shared.generic.api.ActiveAbstract;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Data
@Entity
@Table(name = "api_mapping_log")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiMappingLog extends ActiveAbstract {

    @Id
    @SequenceGenerator(name = "api_mapping_log_seq", sequenceName = "api_mapping_log_seq", allocationSize = 1)
    @GeneratedValue(generator = "api_mapping_log_seq", strategy = GenerationType.SEQUENCE)
    private Long id;


    @Type(type = "text")
    private String data;
}
