package com.gerp.attendance.model.kaaj;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gerp.shared.enums.DurationType;
import com.gerp.shared.generic.api.AuditActiveAbstract;
import com.gerp.shared.jackson.StringToTimeStampDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Builder
@Table(name = "kaaj_request_on_behalf")
public class KaajRequestOnBehalf extends AuditActiveAbstract {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "kaaj_request_on_behalf_seq_gen")
    @SequenceGenerator(name = "kaaj_request_on_behalf_seq_gen", sequenceName = "seq_kaaj_request_on_behalf", initialValue = 1, allocationSize = 1)
    private Integer id;

    private String pisCode;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "from_date_en")
    private LocalDate fromDateEn;

    @JsonDeserialize(using = StringToTimeStampDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "to_date_en")
    private LocalDate toDateEn;

    private String fromDateNp;

    private String toDateNp;

    @Column(name = "group_order")
    private Integer groupOrder;

    @ManyToOne
    @JoinColumn(name = "kaaj_request_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "FK_kaaj_request_on_behalf_KaajRequest"))
    private KaajRequest kaajRequest;

    @Enumerated(EnumType.STRING)
    private DurationType durationType;

}
