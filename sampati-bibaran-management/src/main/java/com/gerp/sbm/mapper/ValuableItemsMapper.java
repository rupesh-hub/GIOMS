package com.gerp.sbm.mapper;

import com.gerp.sbm.pojo.ResponsePojo.CashAndGoldResponsePojo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Mapper
public interface ValuableItemsMapper {
    List<CashAndGoldResponsePojo> getValuableItems(Long id, String sortOrder);
}
