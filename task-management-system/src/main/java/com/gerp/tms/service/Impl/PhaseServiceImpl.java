package com.gerp.tms.service.Impl;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.CrudMessages;
import com.gerp.tms.constant.ErrorMessages;
import com.gerp.tms.converter.PhaseConverter;
import com.gerp.tms.mapper.ProjectPhaseMapper;
import com.gerp.tms.model.phase.Phase;
import com.gerp.tms.model.phase.PhaseMember;
import com.gerp.tms.model.project.ProjectPhase;
import com.gerp.tms.pojo.PhaseMemberPojo;
import com.gerp.tms.pojo.request.PhaseRequestPojo;
import com.gerp.tms.pojo.response.MemberDetailsResponsePojo;
import com.gerp.tms.pojo.response.PhaseMemberResponsePojo;
import com.gerp.tms.pojo.response.PhaseResponsePojo;
import com.gerp.tms.proxy.EmployeeDetailsProxy;
import com.gerp.tms.repo.PhaseMemberRepository;
import com.gerp.tms.repo.PhaseRepository;
import com.gerp.tms.service.PhaseService;
import com.gerp.tms.token.TokenProcessorService;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class PhaseServiceImpl implements PhaseService {
    private final PhaseRepository phaseRepository;
    private final PhaseConverter phaseConverter;
    private final ProjectPhaseMapper projectPhaseMapper;
    private final PhaseMemberRepository phaseMemberRepository;
    private final EmployeeDetailsProxy employeeDetailsProxy;
    private final CustomMessageSource customMessageSource;
    private final TokenProcessorService tokenProcessorService;


    public PhaseServiceImpl(PhaseRepository phaseRepository, PhaseConverter phaseConverter, ProjectPhaseMapper projectPhaseMapper, PhaseMemberRepository phaseMemberRepository, EmployeeDetailsProxy employeeDetailsProxy, CustomMessageSource customMessageSource, TokenProcessorService tokenProcessorService) {
        this.phaseRepository = phaseRepository;
        this.phaseConverter = phaseConverter;
        this.projectPhaseMapper = projectPhaseMapper;
        this.phaseMemberRepository = phaseMemberRepository;
        this.employeeDetailsProxy = employeeDetailsProxy;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
    }

    @Override
    public Long addPhase(PhaseRequestPojo phaseRequest) {
        if (phaseRepository.existsByPhaseName(phaseRequest.getPhaseName())){
            throw new RuntimeException(customMessageSource.get(CrudMessages.alreadyExist,"Phase"));
        }
        Phase phase = phaseConverter.toEntity(phaseRequest);
         phaseRepository.save(phase);
        return phase.getId();
    }

    @Override
    public Long updatePhase(PhaseRequestPojo phaseRequest) {
        if (phaseRequest.getId() == null ){
            throw new RuntimeException(ErrorMessages.Id_IS_MISSING.getMessage());
        }
        Phase phase = phaseConverter.toEntity(phaseRequest);
        phase.setId(phaseRequest.getId());
        phaseRepository.save(phase);
        return phase.getId();
    }

    @Override
    public void deletePhase(Long id) {
        Phase phase = getPhase(id);
        List<ProjectPhase> projectPhases =  projectPhaseMapper.checkPhaseExistInProject(id);
        if (projectPhases.size() > 0){
            throw new RuntimeException(customMessageSource.get("phaseNotDelete"));
        }
        phase.setActive(!phase.isActive());
        phaseRepository.save(phase);
    }

    private Phase getPhase(Long id) {
        Optional<Phase> phaseOptional = phaseRepository.findById(id);
        if (!phaseOptional.isPresent()) {
            throw new RuntimeException(ErrorMessages.PHASE_NOT_FOUND.getMessage());
        }
        return phaseOptional.orElse(new Phase());
    }

    @Override
    public PhaseResponsePojo getPhaseDetails(Long id){
        return phaseConverter.toResponse(getPhase(id));
    }

    @Override
    public List<PhaseResponsePojo> getPhases() {
        return phaseConverter.toResponses(phaseRepository.findAll());
    }


    @Override
    public void addMembersToProjectPhase(PhaseMemberPojo phaseMemberPojo) {
       ProjectPhase projectPhase =  projectPhaseMapper.getProjectIdAndPhaseId( phaseMemberPojo.getProjectId(), phaseMemberPojo.getPhaseId());
       if (projectPhase == null){
           throw new RuntimeException(ErrorMessages.PROJECT_PHASE_NOT_FOUND.getMessage());
       }

       if (projectPhase.getPhaseMemberList() != null){
           projectPhase.getPhaseMemberList().forEach(member->{
               List<String> membersPojoList = phaseMemberPojo.getMembers().parallelStream().filter(input -> input.equalsIgnoreCase(member.getMemberId())).collect(Collectors.toList());
               if (membersPojoList.size() == 0){
                   phaseMemberRepository.deleteById(member.getId());
               }else{
                   phaseMemberPojo.getMembers().remove(membersPojoList.get(0));
               }
           }); }

      if (phaseMemberPojo.getMembers() != null && phaseMemberPojo.getMembers().size() >0){
          List<PhaseMember> phaseMemberList  = new ArrayList<>();
          phaseMemberPojo.getMembers().forEach(id->{
              PhaseMember phaseMember =new PhaseMember(id,projectPhase);
              phaseMemberList.add(phaseMember);
          });

          phaseMemberRepository.saveAll(phaseMemberList);
      }
    }

    @Override
    public List<PhaseMemberResponsePojo> getPhaseProjectMember(Integer projectId, Integer phaseId) {
        List<PhaseMemberResponsePojo> memberDetailsResponsePojoList = projectPhaseMapper.getProjectPhaseMembers(projectId,phaseId);
        memberDetailsResponsePojoList =  memberDetailsResponsePojoList.stream().peek(phase->{
           List<MemberDetailsResponsePojo> detailsResponsePojos = phase.getMemberDetailsResponsePojos().stream().map(member -> getEmployeeName(member.getMemberId())).collect(Collectors.toList());
           phase.setMemberDetailsResponsePojos(detailsResponsePojos);
        }).collect(Collectors.toList());
        return memberDetailsResponsePojoList;
    }

    private MemberDetailsResponsePojo getEmployeeName(String id){
        EmployeeMinimalPojo employeeMinimalPojo = employeeDetailsProxy.getEmployeeDetailMinimal((id));
        MemberDetailsResponsePojo memberDetailsResponsePojo = new MemberDetailsResponsePojo();
        if (employeeMinimalPojo != null){
            memberDetailsResponsePojo.setMemberId(id);
            memberDetailsResponsePojo.setMemberNameEn(employeeMinimalPojo.getEmployeeNameEn());
            memberDetailsResponsePojo.setMemberNameNp(employeeMinimalPojo.getEmployeeNameNp());
            return memberDetailsResponsePojo;
        }
        memberDetailsResponsePojo.setMemberId(id);
        return memberDetailsResponsePojo;
    }
    @Override
    public void removePhaseProjectMember(Integer projectId, Integer phaseId, Integer memberId) {
        long id = projectPhaseMapper.getProjectPhaseMemberId(projectId,phaseId,memberId);
        phaseMemberRepository.deleteById(id);
    }
}
