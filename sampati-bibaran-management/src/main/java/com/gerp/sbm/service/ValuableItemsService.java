package com.gerp.sbm.service;

import com.gerp.sbm.pojo.RequestPojo.CashAndGoldRequestPojo;
import com.gerp.sbm.pojo.ResponsePojo.CashAndGoldResponsePojo;

import java.util.List;

public interface ValuableItemsService {
    Long addValuableItems(CashAndGoldRequestPojo cashAndGoldRequestPojo);

    Long updateValuableItems(CashAndGoldRequestPojo cashAndGoldRequestPojo);

    List<CashAndGoldResponsePojo> getValuableItems(Long id, String sortOrder);

    Long deleteValuableItemsById(Long id);
}
