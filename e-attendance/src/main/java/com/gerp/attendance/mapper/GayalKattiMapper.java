package com.gerp.attendance.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.attendance.Pojo.GayalKattiResponsePojo;
import com.gerp.attendance.Pojo.document.DocumentMasterPojo;
import com.gerp.attendance.Pojo.document.DocumentPojo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author Rohit Sapkota
 * @version 1.0.0
 * @since 1.0.0
 */

@Mapper
public interface GayalKattiMapper {

    List<GayalKattiResponsePojo> getAllActiveGayalKatti();

//    List<GayalKattiResponsePojo> getGayalKattiByApprover(@Param("approverCode")String approverCode);

    GayalKattiResponsePojo getGayalKattiById(@Param("id") Long id);

    List<GayalKattiResponsePojo> getByPisCode(@Param("pisCode") String pisCode);

    List<GayalKattiResponsePojo> getByOfficeCode(@Param("officeCode") String officeCode);

    Page<GayalKattiResponsePojo> filterData(Page<GayalKattiResponsePojo> page,
                                            @Param("pisCode") String pisCode,
                                            @Param("searchField") Map<String, Object> searchField);

    List<DocumentMasterPojo> getDocuments(Long id);
}
