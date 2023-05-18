package com.gerp.usermgmt.mapper.usermgmt;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.usermgmt.model.ScreenGroup;
import com.gerp.usermgmt.pojo.CustomPage;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

@Mapper
public interface ScreenGroupMapper extends BaseMapper<ScreenGroup> {

    CustomPage<ScreenGroup> selectPageMap(CustomPage<ScreenGroup> page, @Param("map") Map<String, Object> map);

    Page<ScreenGroup> selectPageMap(Page<ScreenGroup> page, @Param("map") Map<String, Object> map);



}
