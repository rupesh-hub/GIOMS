package com.gerp.kasamu.service.impl;

import com.gerp.kasamu.Proxy.EmployeeDetailsProxy;
import com.gerp.kasamu.model.kasamu.KasamuRequestForView;
import com.gerp.kasamu.pojo.KasamuRequestReviewListPojo;
import com.gerp.kasamu.pojo.request.RequestForViewPojo;
import com.gerp.kasamu.repo.RequestForViewRepo;
import com.gerp.kasamu.service.KasamuRequestForViewService;
import com.gerp.kasamu.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.utils.CrudMessages;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class KasamuRequestForViewServiceImpl implements KasamuRequestForViewService {

    private final RequestForViewRepo requestForViewRepo;
    private final TokenProcessorService tokenProcessorService;
    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final CustomMessageSource customMessageSource;

    public KasamuRequestForViewServiceImpl(RequestForViewRepo requestForViewRepo, TokenProcessorService tokenProcessorService, EmployeeDetailsProxy employeeDetailsProxy, CustomMessageSource customMessageSource) {
        this.requestForViewRepo = requestForViewRepo;
        this.tokenProcessorService = tokenProcessorService;
        this.employeeDetailsProxy = employeeDetailsProxy;
        this.customMessageSource = customMessageSource;
    }

    @Override
    public Long requestForView(RequestForViewPojo kasamuMasterRequestPojo) {
        KasamuRequestForView kasamuRequestForView = new KasamuRequestForView();
        kasamuRequestForView.setRequestToPisCode(kasamuMasterRequestPojo.getPisCode());
        kasamuRequestForView.setReason(kasamuMasterRequestPojo.getReason());
        requestForViewRepo.save(kasamuRequestForView);
        return kasamuRequestForView.getId();
    }

    @Override
    public List<KasamuRequestReviewListPojo> getRequestedList() {
        List<KasamuRequestForView> kasamuRequestForViews = requestForViewRepo.findBExpiredOrNull(false);
        return kasamuRequestForViews.parallelStream().map(obj -> {
            KasamuRequestReviewListPojo kasamuRequestForViewListPojo = new KasamuRequestReviewListPojo();
            kasamuRequestForViewListPojo.setRequestedBy(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getCreatedBy().toString()));
            kasamuRequestForViewListPojo.setReason(obj.getReason());
            kasamuRequestForViewListPojo.setRequestedToSee(employeeDetailsProxy.getEmployeeDetailMinimal(obj.getRequestToPisCode()));
            kasamuRequestForViewListPojo.setApproved(obj.getApproved());
            return kasamuRequestForViewListPojo;
        }).collect(Collectors.toList());
    }

    @Override
    public Long decisionGivenBy(Long id, Boolean decision) {
        Optional<KasamuRequestForView> kasamuRequestForView = requestForViewRepo.findById(id);
        if (!kasamuRequestForView.isPresent()){
           throw  new RuntimeException(customMessageSource.get(CrudMessages.notExist,"Review request"));
        }
        KasamuRequestForView entity = kasamuRequestForView.orElse(new KasamuRequestForView());
        entity.setApproved(decision);
        entity.setApprovedByPisCode(tokenProcessorService.getPisCode());
        if (decision){
            entity.setExpired(false);
        }
        return requestForViewRepo.save(entity).getId();
    }
}
