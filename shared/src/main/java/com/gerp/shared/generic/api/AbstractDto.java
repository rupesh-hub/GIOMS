package com.gerp.shared.generic.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@Data
@MappedSuperclass
@NoArgsConstructor
public abstract class AbstractDto implements Serializable {

    private Long id;

    private String createdDate;

    private String modifiedDate;

    private Long createdBy;

    private Long modifiedBy;

    private Boolean isActive;

    private String label;

    public AbstractDto(Long id) {
        this.id = id;
    }
}
