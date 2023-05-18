package com.gerp.usermgmt.converter.organiztion.office;

import com.gerp.usermgmt.model.office.Office;
import com.gerp.usermgmt.model.office.OfficeSetup;
import com.gerp.usermgmt.pojo.organization.office.OfficeSetupPojo;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OfficeSetupConverter {

    private ModelMapper modelMapper;

    public OfficeSetupConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public OfficeSetupPojo toPojo(OfficeSetup officeSetup){
        OfficeSetupPojo officeSetupPojo = new OfficeSetupPojo();
        modelMapper.map(officeSetup, officeSetupPojo);
        officeSetupPojo.setOfficeCode(officeSetup.getOffice() !=null? officeSetupPojo.getOfficeCode() : null);

        return officeSetupPojo;
    }

    public OfficeSetup toModel(OfficeSetupPojo officeSetupPojo){
        OfficeSetup officeSetup = new OfficeSetup();
        modelMapper.map(officeSetupPojo, officeSetup);
        Office office = new Office();
        office.setCode(officeSetupPojo.getOfficeCode());
        officeSetup.setOffice(office);
        officeSetup.setStepStatus(officeSetupPojo.getOfficeSetupStatus());

        return officeSetup;
    }
}
