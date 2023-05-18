package com.gerp.tms.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gerp.shared.generic.api.TimeStampAbstract;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditAbstractTms extends TimeStampAbstract {

        @CreatedBy
        @Column(name = "created_by", updatable = false)
        private String createdBy;

        @LastModifiedBy
        @Column(name = "last_modified_by")
        private String lastModifiedBy;

        @Transient
        private String label;

        @Column(name = "is_active")
        @JsonProperty("isActive")
        private Boolean isActive = true;


    public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getLastModifiedBy() {
            return lastModifiedBy;
        }

        public void setLastModifiedBy(String lastModifiedBy) {
            this.lastModifiedBy = lastModifiedBy;
        }

        public String getLabel() {
            return label;
        }

    @JsonIgnore
    public Boolean isActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Boolean getActive() {
        return isActive;
    }
}
