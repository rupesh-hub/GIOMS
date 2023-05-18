package com.gerp.attendance.service.impl;

import com.gerp.attendance.mapper.ValidationMapper;
import com.gerp.attendance.service.ValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * @author Sanjeena Basukala
 * @version 1.0.0
 * @since 1.0.0
 */
@Service
@Transactional
public class ValidationServiceImpl implements ValidationService {

    @Autowired
    private ValidationMapper validationMapper;

    @Override
    public void validateRequest(LocalDate fromDate, LocalDate toDate, String pisCode, String requestFor, String officeCode,Long leaveKaajId,String year, boolean appliedForOthers) {

        switch (requestFor) {

            case "specificHoliday":
                String result = validationMapper.validateSpecificHoliday(fromDate, toDate, pisCode, officeCode, appliedForOthers);
                if(result.equalsIgnoreCase("success")){
                    break;
                }else if(result.equalsIgnoreCase("leave")){
                    throw new RuntimeException("You apply for leave on same day");
                }else if(result.equalsIgnoreCase("kaaj")){
                    throw new RuntimeException("You apply for kaaj on same day");
                }
                break;
            case "Leave":
                result = validationMapper.validateLeave(fromDate, toDate, pisCode, officeCode,leaveKaajId,year, appliedForOthers);
                if (result.equalsIgnoreCase("success")) {
                    break;
                } else if (result.equalsIgnoreCase("gayal")) {
                    throw new RuntimeException("You also apply for gayal on same day");
                } else if (result.equalsIgnoreCase("kaaj")) {
                    throw new RuntimeException("You also apply for Kaaj on same day");

                } else if (result.equalsIgnoreCase("leave")) {
                    throw new RuntimeException("You also apply for leave on same day");

                }
                break;
            case "Kaaj":
                result = validationMapper.validateKaaj(fromDate, toDate, pisCode, officeCode,leaveKaajId,year);
                if (result.equalsIgnoreCase("success")) {
                    break;
                } else if (result.equalsIgnoreCase("gayal")) {
                    throw new RuntimeException("You also apply for gayal on same day");
                } else if (result.equalsIgnoreCase("kaaj")) {
                    throw new RuntimeException("You also apply for Kaaj on same day");
                } else if (result.equalsIgnoreCase("leave")) {
                    throw new RuntimeException("You also apply for leave on same day");

                }
                break;


            default:
                break;

        }

    }
}
