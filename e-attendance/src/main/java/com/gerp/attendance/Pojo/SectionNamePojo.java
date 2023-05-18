package com.gerp.attendance.Pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.gerp.shared.pojo.IdNamePojo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang.builder.EqualsBuilder;

import java.util.Objects;

@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SectionNamePojo {
    private Long id;
    private String nameEn;
    private String nameNp;
    private String code;

    public SectionNamePojo(Long id, String nameEn, String nameNp) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameNp = nameNp;
    }

    public SectionNamePojo(Long id, String nameEn, String nameNp,String code) {
        this.id = id;
        this.nameEn = nameEn;
        this.nameNp = nameNp;
        this.code = code;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof IdNamePojo))
            return false;
        SectionNamePojo object = (SectionNamePojo) o;
        return new EqualsBuilder()
                .append(id, object.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
