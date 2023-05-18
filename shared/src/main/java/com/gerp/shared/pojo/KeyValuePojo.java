package com.gerp.shared.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class KeyValuePojo {
    private String key;
    private String valueNepali;
    private String valueNepaliShort;
    private String valueEnglish;
    private String name;
    private String desc;
    private String label;
    private Integer value;

    public KeyValuePojo(String key, String valueNepali, String valueEnglish) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
    }

    public KeyValuePojo(String key, String valueNepali, String valueEnglish,Integer value) {
        this.key = key;
        this.valueNepali = valueNepali;
        this.valueEnglish = valueEnglish;
        this.value=value;
    }

    public String getLabel() {
//        System.out.println(LocaleContextHolder.getLocale().getDisplayLanguage());
        return LocaleContextHolder.getLocale().getDisplayLanguage().equalsIgnoreCase("np") ? valueNepali : valueEnglish;
    }
}
