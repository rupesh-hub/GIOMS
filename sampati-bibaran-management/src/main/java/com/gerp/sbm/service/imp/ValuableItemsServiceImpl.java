package com.gerp.sbm.service.imp;

import com.gerp.sbm.Helper.HelperUtil;
import com.gerp.sbm.constant.ErrorMessages;
import com.gerp.sbm.mapper.ValuableItemsMapper;
import com.gerp.sbm.model.assets.ValuableItems;
import com.gerp.sbm.pojo.RequestPojo.CashAndGoldRequestPojo;
import com.gerp.sbm.pojo.ResponsePojo.CashAndGoldResponsePojo;
import com.gerp.sbm.repo.ValuableItemsRepository;
import com.gerp.sbm.service.ValuableItemsService;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ValuableItemsServiceImpl implements ValuableItemsService {

    private final ValuableItemsRepository valuableItemsRepository;
    private final ValuableItemsMapper valuableItemsMapper;

    public ValuableItemsServiceImpl(ValuableItemsRepository valuableItemsRepository, ValuableItemsMapper valuableItemsMapper) {
        this.valuableItemsRepository = valuableItemsRepository;
        this.valuableItemsMapper = valuableItemsMapper;
    }

    @Override
    public Long addValuableItems(CashAndGoldRequestPojo cashAndGoldRequestPojo) {
        ValuableItems valuableItems = new ValuableItems();
        convertToEntityValuableItems(cashAndGoldRequestPojo, valuableItems);
        valuableItemsRepository.save(valuableItems);
        return valuableItems.getId();
    }

    @Override
    public Long updateValuableItems(CashAndGoldRequestPojo cashAndGoldRequestPojo) {
        if (cashAndGoldRequestPojo.getId() == null){
            throw new RuntimeException(ErrorMessages.Id_IS_MISSING.getMessage());
        }
        ValuableItems valuableItems = getValuableItems(cashAndGoldRequestPojo);
        if (!valuableItems.getCreatedBy().toString().equalsIgnoreCase(HelperUtil.getLoginEmployeeCode())){
            throw new RuntimeException(ErrorMessages.NOT_PERMIT_TO_UPDATE.getMessage());
        }
        convertToEntityValuableItems(cashAndGoldRequestPojo,valuableItems);
        valuableItems.setId(cashAndGoldRequestPojo.getId());
        valuableItemsRepository.save(valuableItems);
        return valuableItems.getId();
    }

    private ValuableItems getValuableItems(CashAndGoldRequestPojo cashAndGoldRequestPojo) {
        Optional<ValuableItems> valuableItemsOptional = valuableItemsRepository.findById(cashAndGoldRequestPojo.getId());
        if (!valuableItemsOptional.isPresent()){
            throw new RuntimeException(ErrorMessages.VALUABLE_ITEMS_NOT_FOUND.getMessage());
        }
        return valuableItemsOptional.orElse(new ValuableItems());
    }

    @Override
    public List<CashAndGoldResponsePojo> getValuableItems(Long id, String sortOrder) {
        List<CashAndGoldResponsePojo> cashAndGoldResponsePojos = valuableItemsMapper.getValuableItems(id,sortOrder);
       if ( cashAndGoldResponsePojos.get(0) != null &&  !cashAndGoldResponsePojos.get(0).getCreatedBy().equals(HelperUtil.getLoginEmployeeCode())){
           throw new RuntimeException(ErrorMessages.NOT_PERMIT_TO_GET.getMessage());
       }
       return cashAndGoldResponsePojos;
    }

    @Override
    public Long deleteValuableItemsById(Long id) {
         valuableItemsRepository.deleteById(id);
         return id;
    }

    private void convertToEntityValuableItems(CashAndGoldRequestPojo cashAndGoldRequestPojo, ValuableItems valuableItems) {
        try {
            BeanUtils.copyProperties(valuableItems, cashAndGoldRequestPojo);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
