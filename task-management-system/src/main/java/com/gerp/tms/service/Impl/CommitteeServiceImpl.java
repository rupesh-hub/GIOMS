package com.gerp.tms.service.Impl;

import com.gerp.tms.constant.ErrorMessages;
import com.gerp.tms.mapper.CommitteeMapper;
import com.gerp.tms.pojo.request.CommitteeRequestPojo;
import com.gerp.tms.pojo.response.CommitteeResponsePojo;
import com.gerp.tms.converter.CommitteeConverter;
import com.gerp.tms.model.committee.Committee;
import com.gerp.tms.pojo.response.CommitteeWiseProjectResponsePojo;
import com.gerp.tms.repo.CommitteeRepository;
import com.gerp.tms.service.CommitteeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CommitteeServiceImpl implements CommitteeService {

    private final CommitteeRepository committeeRepository;
    private final CommitteeConverter committeeConverter;
    private final CommitteeMapper committeeMapper;

    public CommitteeServiceImpl(CommitteeRepository committeeRepository, CommitteeConverter committeeConverter, CommitteeMapper committeeMapper) {
        this.committeeRepository = committeeRepository;
        this.committeeConverter = committeeConverter;
        this.committeeMapper = committeeMapper;
    }

    @Override
    public Integer createCommittee(CommitteeRequestPojo committeeRequestPojo) {
      Committee committee =  committeeConverter.toEntity(committeeRequestPojo);
      committeeRepository.save(committee);
      return committee.getId();
    }

    @Override
    public Integer updateCommittee(CommitteeRequestPojo committeeRequestPojo) {
       if (committeeRequestPojo.getId() == null){
           throw new RuntimeException(ErrorMessages.Id_IS_MISSING.getMessage());
       }
       Committee committee = getCommittee(committeeRequestPojo.getId());
       committee.setCommitteeNameNp(committeeRequestPojo.getCommitteeNameNp());
       committee.setCommitteeName(committeeRequestPojo.getCommitteeName());

       committeeRepository.save(committee);
        return committee.getId();
    }

    @Override
    public CommitteeResponsePojo getCommitteeDetails(Integer id) {
      return  committeeConverter.toResponse(getCommittee(id));
    }

    @Override
    public List<CommitteeResponsePojo> getCommittees() {
        return committeeConverter.toResponses(committeeRepository.findAll());
    }

    @Override
    public void deleteCommittee(Integer id) {
        committeeRepository.deleteById(id);
    }

    @Override
    public CommitteeWiseProjectResponsePojo getCommitteeWiseProject(int committeeId) {
      return committeeMapper.getCommitteeWiseProject(committeeId);
    }

    private Committee getCommittee(Integer id) {
        Committee committee = committeeRepository.getOne(id);
        if (committee == null ){
            throw new RuntimeException(ErrorMessages.COMMITTEE_NOT_FOUND.getMessage());
        }
        return committee;
    }


}
