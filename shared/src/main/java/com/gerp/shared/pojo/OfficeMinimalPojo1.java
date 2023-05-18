package com.gerp.shared.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class OfficeMinimalPojo1 {

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

    public OfficeMinimalPojo1(String code, String name, String nameN) {
        this.code = code;
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
        if (!(o instanceof OfficeMinimalPojo1))
            return false;
        OfficeMinimalPojo1 object = (OfficeMinimalPojo1) o;
        return new EqualsBuilder()
                .append(code, object.code)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
