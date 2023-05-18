package com.gerp.sbm.model.sampati;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@Entity
public class SampatiViewRequest {

    @Id
    @GeneratedValue(generator = "sampati_master_seq_gen", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "sampati_master_seq_gen", sequenceName = "seq_sampati_master", initialValue = 1, allocationSize = 1)
    private Long id;
    @NotNull
    private  String requester_piscode;

    @NotNull
    private String request_to_piscode;

    private Boolean approved;

    @NotNull
    @Temporal(TemporalType.DATE)
    private Date request_date;

    @NotNull
    @Temporal(TemporalType.TIME)
    private Date request_time;

    @Temporal(TemporalType.DATE)
    private Date approved_date;

    @Temporal(TemporalType.TIME)
    private Date approved_time;

    private String approved_by_piscode;

    @NotNull
    private Boolean expired;

}
