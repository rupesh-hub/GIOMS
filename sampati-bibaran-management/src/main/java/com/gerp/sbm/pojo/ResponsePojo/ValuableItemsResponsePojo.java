package com.gerp.sbm.pojo.ResponsePojo;

import com.gerp.sbm.pojo.RequestPojo.ValuableItemsRequestPojo;
import lombok.Data;

@Data
public class ValuableItemsResponsePojo {

    private Long id;

    private ValuableItemsRequestPojo valuableItemsRequestPojo;
}
