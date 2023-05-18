package com.gerp.usermgmt.services.transfer;


import com.gerp.usermgmt.model.transfer.EmployeeRequestCheckList;
import com.gerp.usermgmt.repo.transfer.EmployeeRequestCheckListRepo;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartUpSeeder implements ApplicationListener<ContextRefreshedEvent> {



    private final EmployeeRequestCheckListRepo employeeRequestCheckListRepo;

    public StartUpSeeder(EmployeeRequestCheckListRepo employeeRequestCheckListRepo) {
        this.employeeRequestCheckListRepo = employeeRequestCheckListRepo;
    }


    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        addCheckList();
    }

    private void addCheckList() {
       if (employeeRequestCheckListRepo.findAll().size() ==0){
           addEmployeeChecklist("निजामती सेवा ऐन, नियमावली एवं सरुवा नीति बमोजिम\n" +
                   "भौगोलिक क्षेत्रमा/कार्यालयमा काम गर्नु पर्ने अवधि");
           addEmployeeChecklist(" पहिले सो कार्यालयमा कार्य गरिसकेको");
           addEmployeeChecklist("घर पायक");
           addEmployeeChecklist("पति पत्नी दुवै सरकारी सेवामा");
           addEmployeeChecklist("कर्मचारीको उमेर");
           addEmployeeChecklist("फाजिलमा रहे नरहेको");
           addEmployeeChecklist("मन्त्रालय/विभागको सहमतिपत्र");
           addEmployeeChecklist("अनिवार्य अवकाश हुने अवधि");
           addEmployeeChecklist("हाल कार्यरत निकायमा रुजु हाजिर दिन");
           addEmployeeChecklist("सरुवा हुन चाहेको निकायमा तत्काल दरवन्दी रिक्तको अवस्था");
           addEmployeeChecklist("मिति २०६४।४।१ पश्चात्राजपत्र अनङ्कित पदमा नियुक्ति हुनेको हकमा सम्बन्धित अञ्चलमा");
           addEmployeeChecklist("अध्ययन वा तालीम काज/अन्तर्राष्ट्रिय संस्थामा काम गरेको विकास समिति तथा आयोजना काजमा");
       }
    }

    private void addEmployeeChecklist(String name) {
        EmployeeRequestCheckList employeeRequestCheckList = new EmployeeRequestCheckList();
        employeeRequestCheckList.setName(name);
        employeeRequestCheckListRepo.save(employeeRequestCheckList);
    }


}
