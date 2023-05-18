package com.gerp.tms.service.Impl;

import com.gerp.tms.mapper.ProjectMapper;
import com.gerp.tms.pojo.response.OfficeWiseProjectResponsePojo;
import com.gerp.tms.service.OfficeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OfficeServiceImpl implements OfficeService {

    private final ProjectMapper projectMapper;

    public OfficeServiceImpl(ProjectMapper projectMapper) {
        this.projectMapper = projectMapper;
    }

    @Override
    public List<OfficeWiseProjectResponsePojo> getOfficeWiseProject( String officeId) {
       List<OfficeWiseProjectResponsePojo> officeWiseProjectResponsePojos =  projectMapper.getOfficeWiseProject(officeId);
        officeWiseProjectResponsePojos.forEach(owp->{
            owp.setProjectCount(owp.getCompletedProject().size() + owp.getIncompleteProject().size());
        });
        return officeWiseProjectResponsePojos;
    }
}
