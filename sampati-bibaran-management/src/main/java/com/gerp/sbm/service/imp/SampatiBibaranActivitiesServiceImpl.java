package com.gerp.sbm.service.imp;

import com.gerp.sbm.model.sampati.SampatiMaster;
import com.gerp.sbm.model.sampati.SampatiViewRequest;
import com.gerp.sbm.repo.SampatiMasterRepo;
import com.gerp.sbm.repo.SampatiViewRequestRepo;
import com.gerp.sbm.service.SampatiBibaranActivitiesService;
import com.gerp.sbm.token.TokenProcessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;
import java.util.List;

@Service
public class SampatiBibaranActivitiesServiceImpl implements SampatiBibaranActivitiesService {

    @Autowired
    private SampatiMasterRepo sampatiMasterRepo;

    @Autowired
    private SampatiViewRequestRepo sampatiViewRequestRepo;

    @Autowired
    private TokenProcessorService tokenProcessorService;


    @Override
    public Boolean addrequest(String piscode) {

        sampatiMasterRepo.findDetails(piscode).orElseThrow(()->new ResponseStatusException(HttpStatus.NO_CONTENT,"no sampati information found for this user"));
        SampatiViewRequest request=new SampatiViewRequest();
        request.setRequest_to_piscode(piscode);
        request.setRequester_piscode(tokenProcessorService.getPisCode());
        request.setRequest_date(new Date());
        request.setRequest_time(new Date());
        request.setApproved(false);
        request.setExpired(false);
        sampatiViewRequestRepo.save(request);
        return true;
    }

    @Override
    public List<SampatiViewRequest> getReviewRequest() {
        return sampatiViewRequestRepo.findRequest(tokenProcessorService.getPisCode());
    }

    @Override
    public Boolean approvedRequest(String requester_piscode) {
        SampatiViewRequest request=sampatiViewRequestRepo.findMyReviewRequest(tokenProcessorService.getPisCode(),requester_piscode);
        request.setApproved_date(new Date());
        request.setApproved_time(new Date());
        request.setApproved(true);
        request.setApproved_by_piscode(tokenProcessorService.getPisCode());
        sampatiViewRequestRepo.save(request);
        return true;
    }

    @Override
    public SampatiMaster getSampatiBibaran(String piscode,String fiscal_year_code) {

        sampatiViewRequestRepo.findStatus(piscode).orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN,"not approved yet."));

        SampatiMaster master=sampatiMasterRepo.
                findSampatiBibaran(piscode,fiscal_year_code).orElseThrow(()->new ResponseStatusException(HttpStatus.NO_CONTENT));

        return null;
    }
}
