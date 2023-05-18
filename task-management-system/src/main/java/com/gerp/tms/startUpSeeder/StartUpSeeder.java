package com.gerp.tms.startUpSeeder;

import com.gerp.tms.constant.Status;
import com.gerp.tms.model.report.Months;
import com.gerp.tms.model.task.TaskProgressStatus;
import com.gerp.tms.repo.MonthRepo;
import com.gerp.tms.repo.TaskProgressStatusRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StartUpSeeder implements ApplicationListener<ContextRefreshedEvent> {

    private final TaskProgressStatusRepository taskProgressStatusRepository;
    private final MonthRepo monthRepo;

    public StartUpSeeder(TaskProgressStatusRepository taskProgressStatusRepository, MonthRepo monthRepo) {
        this.taskProgressStatusRepository = taskProgressStatusRepository;
        this.monthRepo = monthRepo;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

        taskProgressStatusSeeder();
        seedMonths();
    }

    private void seedMonths() {
        if (monthRepo.findAll().size() == 0){
            List<Months> months = new ArrayList<>();
            Months months1 = new Months("BAISAKH".toUpperCase(),"बैशाख");
            Months months2 = new Months("Jestha".toUpperCase(),"जेठ");
            Months months3 = new Months("Asar".toUpperCase(),"असार");
            Months months4 = new Months("Shrawan".toUpperCase(),"श्रावण");
            Months months5 = new Months("Bhadau".toUpperCase(),"भदौ");
            Months months6 = new Months("Aswin".toUpperCase(),"आश्विन");
            Months months7 = new Months("Kartik".toUpperCase(),"कार्तिक");
            Months months8 = new Months("Mansir".toUpperCase(),"मंसिर");
            Months months9 = new Months("Poush".toUpperCase(),"पुष");
            Months months10 = new Months("Magh".toUpperCase(),"माघ");
            Months months11= new Months("Falgun".toUpperCase(),"फाल्गुन");
            Months months12= new Months("Chaitra".toUpperCase(),"चैत्र");

            months.add(months1);
            months.add(months2);
            months.add(months3);
            months.add(months4);
            months.add(months5);
            months.add(months6);
            months.add(months7);
            months.add(months8);
            months.add(months9);
            months.add(months10);
            months.add(months11);
            months.add(months12);

            monthRepo.saveAll(months);
        }
    }

    private void taskProgressStatusSeeder() {
        if (taskProgressStatusRepository.findAll().size() == 0){
            List<TaskProgressStatus> taskProgressStatusList = new ArrayList<>();

            TaskProgressStatus taskProgressStatus = new TaskProgressStatus();
            taskProgressStatus.setStatusName(Status.TODO.getValueEnglish());
            taskProgressStatus.setStatusNameNp(Status.TODO.getValueNepali());
            taskProgressStatus.setDeleteAble(false);
            taskProgressStatusList.add(taskProgressStatus);

            taskProgressStatus = new TaskProgressStatus();
            taskProgressStatus.setStatusName(Status.STARTED.getValueEnglish());
            taskProgressStatus.setStatusNameNp(Status.STARTED.getValueNepali());
            taskProgressStatus.setDeleteAble(false);
            taskProgressStatusList.add(taskProgressStatus);

            taskProgressStatus = new TaskProgressStatus();
            taskProgressStatus.setStatusName(Status.COMPLETED.getValueEnglish());
            taskProgressStatus.setStatusNameNp(Status.COMPLETED.getValueNepali());
            taskProgressStatus.setDeleteAble(false);
            taskProgressStatusList.add(taskProgressStatus);

            taskProgressStatusRepository.saveAll(taskProgressStatusList);
        }
    }
}
