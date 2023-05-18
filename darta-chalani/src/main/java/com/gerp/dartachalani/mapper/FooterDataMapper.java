package com.gerp.dartachalani.mapper;

import com.gerp.dartachalani.dto.FooterDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

@Mapper
@Component
public interface FooterDataMapper {

    @Select("select id, footer, office_code as officeCode from footer_data where office_code = #{officeCode} and is_active = true")
    FooterDataDto getFooterByOfficeCode(@Param("officeCode") String officeCode);

}
