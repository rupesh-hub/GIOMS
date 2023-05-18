package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.dto.template.StandardTemplatePojo;
import com.gerp.dartachalani.mapper.DelegationMapper;
import com.gerp.dartachalani.mapper.MemoSuggestionMapper;
import com.gerp.dartachalani.model.StandardTemplate;
import com.gerp.dartachalani.repo.StandardTemplateRepo;
import com.gerp.dartachalani.service.StandardTemplateService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.utils.CrudMessages;
import lombok.SneakyThrows;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class StandardTemplateServiceImpl implements StandardTemplateService {

    private final StandardTemplateRepo standardRepo;
    private final CustomMessageSource customMessageSource;
    private final TokenProcessorService tokenProcessorService;
    private final DelegationMapper memoSuggestionMapper;
    private static final String OFFICE_HEAD = "OFFICE_HEAD";
    private static final String SUPER_ADMIN = "SUPER_ADMIN";

    public StandardTemplateServiceImpl(StandardTemplateRepo standardRepo, CustomMessageSource customMessageSource, TokenProcessorService tokenProcessorService, DelegationMapper memoSuggestionMapper) {
        this.standardRepo = standardRepo;
        this.customMessageSource = customMessageSource;
        this.tokenProcessorService = tokenProcessorService;
        this.memoSuggestionMapper = memoSuggestionMapper;
    }

    @Override
    @SneakyThrows
    public int addStandardTemplate(StandardTemplatePojo standardTemplatePojo) {
        StandardTemplate standardTemplate = new StandardTemplate();
        BeanUtils.copyProperties(standardTemplate,standardTemplatePojo);
        String roles = tokenProcessorService.getRoles();
        if (roles.contains(OFFICE_HEAD)){
            standardTemplate.setRole(OFFICE_HEAD);
            standardTemplate.setOfficeCode(tokenProcessorService.getOfficeCode());
        }else if (roles.contains(SUPER_ADMIN)){
            standardTemplate.setRole(SUPER_ADMIN);
        }
        return standardRepo.save(standardTemplate).getId();
    }

    @Override
    public List<StandardTemplatePojo> getStandardTemplate() {
//        List<StandardTemplate> all = standardRepo.findAll();
        if (tokenProcessorService.getRoles().contains(OFFICE_HEAD)){
            return memoSuggestionMapper.getStandardTemplate(SUPER_ADMIN,null,tokenProcessorService.getUserId());
        }else if (tokenProcessorService.getRoles().contains(SUPER_ADMIN)){
         return  memoSuggestionMapper.getStandardTemplate(null,null,tokenProcessorService.getUserId());
        }
      return  memoSuggestionMapper.getStandardTemplate(SUPER_ADMIN,tokenProcessorService.getOfficeCode(),tokenProcessorService.getUserId());
//        return all.parallelStream().map(obj->new StandardTemplatePojo(obj.getId(), obj.getTemplateNameNp(), obj.getTemplateNameEn(),obj.getTemplate(),obj.getCreatedDate().toLocalDateTime().toLocalDate(),obj.getCreatedBy().toString())).collect(Collectors.toList());
    }

    @Override
    public StandardTemplatePojo getStandardTemplateById(int id) {
        StandardTemplate standardTemplate = getStandardTemplate(id);
        return new StandardTemplatePojo(standardTemplate.getId(),standardTemplate.getTemplateNameNp(),standardTemplate.getTemplateNameEn(),standardTemplate.getTemplate(),standardTemplate.getCreatedDate().toLocalDateTime().toLocalDate(),standardTemplate.getCreatedBy().toString());
    }

    @Override
    public int updateStandardTemplate(StandardTemplatePojo standardTemplatePojo) {
        if (standardTemplatePojo.getId() == 0){
            throw new CustomException(customMessageSource.get(CrudMessages.notExist,"Id"));
        }
        if (standardTemplatePojo.getCreatedBy() == null){
            throw new CustomException(customMessageSource.get(CrudMessages.notExist,"CreatedBY"));
        }
        if (!standardTemplatePojo.getCreatedBy().equalsIgnoreCase(tokenProcessorService.getUserId().toString())){
            throw new CustomException(customMessageSource.get(CrudMessages.notPermit));
        }
        StandardTemplate standardTemplate = getStandardTemplate(standardTemplatePojo.getId());
        standardTemplate.setTemplate(standardTemplatePojo.getTemplate());
        standardTemplate.setTemplateNameEn(standardTemplatePojo.getTemplateNameEn());
        standardTemplate.setTemplateNameNp(standardTemplate.getTemplateNameNp());
        standardRepo.save(standardTemplate);
        return standardTemplatePojo.getId();

    }

    @Override
    public int deleteStandardTemplateById(int id) {
        standardRepo.deleteById(id);
        return id;
    }

    private StandardTemplate getStandardTemplate(int  id) {
        Optional<StandardTemplate> standardRepoById = standardRepo.findById(id);
        if(!standardRepoById.isPresent()){
          throw new CustomException(customMessageSource.get(CrudMessages.notExist,"Template"));
        }
        return standardRepoById.orElse(new StandardTemplate());
    }


}
