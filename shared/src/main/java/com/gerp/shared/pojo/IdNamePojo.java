package com.gerp.shared.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.context.i18n.LocaleContextHolder;

import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class IdNamePojo {

    private Long id;
    private String code;
    private String name;
    private String nameN;
    private String mobileNumber;
    private String officeAddress;
    private String officeAddressEn;
    private String designationEn;
    private String designationNp;
    private String sectionNameEn;
    private String sectionNameNp;
    private String remarks;
    private String officeDistrictCode;
    private String salutation;
    private Boolean isActive;
    private String forwardedDate;

    private String designationType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate actionDate;

    public IdNamePojo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public IdNamePojo(String code, String name, String nameN,LocalDate actionDate) {
        this.code = code;
        this.name = name;
        this.nameN = nameN;
        this.actionDate = actionDate;
    }

    public IdNamePojo(String code, String name, String nameN) {
        this.code = code;
        this.name = name;
        this.nameN = nameN;
    }

    public IdNamePojo(Long id, String name, String nameN, String label) {
        this.id = id;
        this.name = name;
        this.nameN = nameN;
        this.label = label;
    }

    public IdNamePojo(Long id, String name, String nameN) {
        this.id = id;
        this.name = name;
        this.nameN = nameN;
    }

    public IdNamePojo(Integer id, String name, String nameN) {
        this.id = Long.valueOf(id);
        this.name = name;
        this.nameN = nameN;
    }

    private String label;

    public String getLabel() {
        return LocaleContextHolder.getLocale().getDisplayLanguage().equalsIgnoreCase("np") ? nameN : name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof IdNamePojo))
            return false;
        IdNamePojo object = (IdNamePojo) o;
        return new EqualsBuilder()
                .append(id, object.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
