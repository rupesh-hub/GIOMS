package com.gerp.usermgmt.services.MasterDashboard.impl;

import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import com.gerp.usermgmt.Proxy.AttendanceServiceData;
import com.gerp.usermgmt.Proxy.DartaChalaniServiceData;
import com.gerp.usermgmt.mapper.organization.OfficeMapper;
import com.gerp.usermgmt.pojo.MasterDashboardPojo.*;
import com.gerp.usermgmt.pojo.MasterDataPojo;
import com.gerp.usermgmt.repo.office.OfficeRepo;
import com.gerp.usermgmt.services.MasterDashboard.MasterDashboardService;
import com.gerp.usermgmt.services.organization.office.OfficeService;
import com.gerp.usermgmt.token.TokenProcessorService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@Transactional
public class MasterDashboardServiceImpl implements MasterDashboardService {
    private final OfficeRepo officeCategory;
    private final OfficeService officeService;
    private final OfficeMapper officeMapper;
    private final AttendanceServiceData attendanceServiceData;
    private final DartaChalaniServiceData dartaChalaniServiceData;
    private final DateConverter dateConverter;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    public MasterDashboardServiceImpl(AttendanceServiceData attendanceServiceData,
                                      OfficeRepo officeCategory,
                                      OfficeMapper officeMapper,
                                      DartaChalaniServiceData dartaChalaniServiceData,
                                      OfficeService officeService,
                                      DateConverter dateConverter) {
        this.attendanceServiceData = attendanceServiceData;
        this.officeService = officeService;
        this.dateConverter = dateConverter;
        this.officeMapper = officeMapper;
        this.dartaChalaniServiceData = dartaChalaniServiceData;
        this.officeCategory = officeCategory;
    }
//    @Override
//    public MasterDashboardResponsePojo getMasterDashboardData(MasterDashboardPojo masterDashboardPojo) {
//        LocalDate fromDate=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(masterDashboardPojo.getTopTenOffice().getFromDateNp()));
//        LocalDate toDate=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(masterDashboardPojo.getTopTenOffice().getToDateNp()));
//
//        LocalDate fromDateOfficeWise=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(masterDashboardPojo.getOfficeWiseDetail().getDateDetail().getFromDateNp()));
//        LocalDate toDateOfficeWise=dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(masterDashboardPojo.getOfficeWiseDetail().getDateDetail().getToDateNp()));
//        MasterDashboardResponsePojo masterDashboardResponsePojo=new MasterDashboardResponsePojo();
//        List<MasterDetailPojo> masterDetailPojoList=new ArrayList<>();
//        if(masterDashboardPojo.getOfficeWiseDetail().getOfficeCode()==null){
//          officeMapper.getAllParentOffice().stream().forEach(o->{
//              List<String> listOfOffice=new ArrayList<>();
//              listOfOffice.addAll(officeMapper.getAllChildOfficeCode(o.getCode()));
//              MasterDetailPojo masterDetailPojo=attendanceServiceData.getOfficeKaajLeave(fromDate,toDate,listOfOffice);
//              MasterDetailPojo masterDetailPojoDarta=dartaChalaniServiceData.getOfficeDartaCount(fromDate,toDate,listOfOffice);
//
//              masterDetailPojo.setOfficeNameEn(officeCategory.findById(o.getCode()).get().getNameEn());
//              masterDetailPojo.setOfficeNameNp(officeCategory.findById(o.getCode()).get().getNameNp());
//              masterDetailPojo.setKaajCount(masterDetailPojo.getKaajCount());
//              masterDetailPojo.setLeaveCount(masterDetailPojo.getLeaveCount());
//              masterDetailPojo.setTotalDarta(masterDetailPojoDarta.getTotalDarta());
//              masterDetailPojo.setManualCount(masterDetailPojoDarta.getManualCount());
//              masterDetailPojo.setTippaniCount(masterDetailPojoDarta.getTippaniCount());
//              masterDetailPojo.setChalaniCount(masterDetailPojoDarta.getChalaniCount());
//              masterDetailPojo.setAutoCount(masterDetailPojoDarta.getAutoCount());
//
//              masterDetailPojoList.add(masterDetailPojo);
//            });
//            masterDashboardResponsePojo.setOfficeDetails(masterDetailPojoList);
//        }else {
//            officeMapper.getChildOffices(masterDashboardPojo.getOfficeWiseDetail().getOfficeCode()).stream().forEach(o -> {
//                List<String> listOfOffice = new ArrayList<>();
//                listOfOffice.addAll(officeMapper.getAllChildOfficeCode(o.getCode()));
//                MasterDetailPojo masterDetailPojo = attendanceServiceData.getOfficeKaajLeave(fromDate, toDate, listOfOffice);
//                MasterDetailPojo masterDetailPojoDarta=dartaChalaniServiceData.getOfficeDartaCount(fromDate,toDate,listOfOffice);
//
//                masterDetailPojo.setOfficeNameEn(officeCategory.findById(o.getCode()).get().getNameEn());
//                masterDetailPojo.setOfficeNameNp(officeCategory.findById(o.getCode()).get().getNameNp());
//                masterDetailPojo.setKaajCount(masterDetailPojo.getKaajCount());
//                masterDetailPojo.setLeaveCount(masterDetailPojo.getLeaveCount());
//                masterDetailPojo.setTotalDarta(masterDetailPojoDarta.getTotalDarta());
//                masterDetailPojo.setManualCount(masterDetailPojoDarta.getManualCount());
//                masterDetailPojo.setTippaniCount(masterDetailPojoDarta.getTippaniCount());
//                masterDetailPojo.setChalaniCount(masterDetailPojoDarta.getChalaniCount());
//                masterDetailPojo.setAutoCount(masterDetailPojoDarta.getAutoCount());
//                masterDetailPojoList.add(masterDetailPojo);
//            });
//            masterDashboardResponsePojo.setOfficeDetails(masterDetailPojoList);
//
//        }
//
//        TopTenOfficeDetailPojo topTenOfficeDetailPojo=new TopTenOfficeDetailPojo();
//        //kaaj
//        topTenOfficeDetailPojo.setKaajList(attendanceServiceData.filterWithKaajLeave(fromDate,toDate).getKaajList()
//                .stream().map(y->{
//                   return   this.getDartaCount(fromDate,toDate,y.getOfficeCode(),y);
//        }).collect(Collectors.toList()));
//        topTenOfficeDetailPojo.setLeaveList(attendanceServiceData.filterWithKaajLeave(fromDate,toDate).getLeaveList()
//                .stream().map(y->{
//                     return this.getDartaCount(fromDate,toDate,y.getOfficeCode(),y);
//        }).collect(Collectors.toList()));
//        //darta
//        topTenOfficeDetailPojo.setDartaList(dartaChalaniServiceData.getMasterDasboardDarta(fromDate,toDate).getDartaList()
//        .stream().map(y->{
//            return this.getKaajLeaveCountForDarta(fromDate,toDate,Arrays.asList(y.getOfficeCode()), y);
//                }).collect(Collectors.toList()));
//        topTenOfficeDetailPojo.setChalaniList(dartaChalaniServiceData.getMasterDasboardDarta(fromDate,toDate).getChalaniList()
//        .stream().map(y->{
//            return this.getKaajLeaveCount(fromDate,toDate,Arrays.asList(y.getOfficeCode()),y);
//                }).collect(Collectors.toList()));
//        //tippani
//        topTenOfficeDetailPojo.setTippaniList(dartaChalaniServiceData.getMasterDasboardDarta(fromDate,toDate).getTippaniList()
//        .stream().map(y->{
//            return this.getKaajLeaveCount(fromDate,toDate,Arrays.asList(y.getOfficeCode()),y);
//                }).collect(Collectors.toList()));
//        masterDashboardResponsePojo.setTopTenOfficeDetailList(topTenOfficeDetailPojo);
//
//        return masterDashboardResponsePojo;
//    }

    MasterDetailPojo getDartaCount(LocalDate fromDate, LocalDate toDate, String officeCode, MasterDetailPojo masterPojo) {
         MasterDetailPojo masterDetailPojo=dartaChalaniServiceData.getOfficeDartaCount(fromDate,toDate,Arrays.asList(officeCode));
        masterPojo.setOfficeNameEn(officeCategory.findById(officeCode).get().getNameEn());
        masterPojo.setOfficeNameNp(officeCategory.findById(officeCode).get().getNameNp());
        masterPojo.setAutoCount(masterDetailPojo.getAutoCount());
        masterPojo.setManualCount(masterDetailPojo.getManualCount());
        masterPojo.setTotalDarta(masterDetailPojo.getTotalDarta());
        masterPojo.setChalaniCount(masterDetailPojo.getChalaniCount());
        masterPojo.setTippaniCount(masterDetailPojo.getTippaniCount());
        return masterPojo;
    }

    MasterDetailPojo getKaajLeaveCount(LocalDate fromDate, LocalDate toDate, List<String> officeCode, MasterDetailPojo masterPojo) {
//        MasterDetailPojo masterDetailPojo=attendanceServiceData.getOfficeKaajLeave(fromDate,toDate,officeCode);
        masterPojo.setOfficeNameEn(officeCategory.findById(officeCode.get(0)).get().getNameEn());
        masterPojo.setOfficeNameNp(officeCategory.findById(officeCode.get(0)).get().getNameNp());
//        masterPojo.setKaajCount(masterDetailPojo.getKaajCount());
//        masterPojo.setLeaveCount(masterDetailPojo.getLeaveCount());
        return masterPojo;
    }

    MasterDetailPojo getKaajLeaveCountForDarta(LocalDate fromDate, LocalDate toDate, List<String> officeCode, MasterDetailPojo masterPojo) {
        MasterDetailPojo masterDetailPojo = attendanceServiceData.getOfficeKaajLeave(fromDate, toDate, officeCode);
        masterPojo.setOfficeNameEn(officeCategory.findById(officeCode.get(0)).get().getNameEn());
        masterPojo.setOfficeNameNp(officeCategory.findById(officeCode.get(0)).get().getNameNp());
        masterPojo.setKaajCount(masterDetailPojo.getKaajCount());
        masterPojo.setLeaveCount(masterDetailPojo.getLeaveCount());
        return masterPojo;
    }

    @Override
    public MasterDashboardResponsePojo getMasterDashboardData(OfficeWiseDetailPojo officeWiseDetailPojo) {

        Long orgTypeId = null;
        if (!tokenProcessorService.isAdmin()) {
            orgTypeId = tokenProcessorService.getOrganisationTypeId();
        }

        LocalDate fromDateOfficeWise = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(officeWiseDetailPojo.getDateDetail().getFromDateNp()));
        LocalDate toDateOfficeWise = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(officeWiseDetailPojo.getDateDetail().getToDateNp()));
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();
        List<MasterDetailPojo> masterDetailPojoList = new ArrayList<>();
        if (officeWiseDetailPojo.getOfficeCode() == null) {
            officeMapper.getAllParentOffice(orgTypeId).stream().forEach(o -> {
                List<String> listOfOffice = new ArrayList<>();
                listOfOffice.addAll(officeMapper.getAllChildOfficeCode(o.getCode()));
                MasterDetailPojo masterDetailPojos = this.masterDashboard(fromDateOfficeWise, toDateOfficeWise, listOfOffice, o.getCode());

                masterDetailPojoList.add(masterDetailPojos);
            });
            masterDashboardResponsePojo.setOfficeDetails(masterDetailPojoList);
        } else {
            officeMapper.getChildOffices(officeWiseDetailPojo.getOfficeCode()).stream().forEach(o -> {
                List<String> listOfOffice = new ArrayList<>();
                listOfOffice.addAll(officeMapper.getAllChildOfficeCode(o.getCode()));
                MasterDetailPojo masterDetailPojos = this.masterDashboard(fromDateOfficeWise, toDateOfficeWise, listOfOffice, o.getCode());

                masterDetailPojoList.add(masterDetailPojos);
//                MasterDetailPojo masterDetailPojo = attendanceServiceData.getOfficeKaajLeave(fromDateOfficeWise, toDateOfficeWise, listOfOffice);
//                MasterDetailPojo masterDetailPojoDarta=dartaChalaniServiceData.getOfficeDartaCount(fromDateOfficeWise,toDateOfficeWise,listOfOffice);
//                masterDetailPojo.setOfficeCode(o.getCode());
//                masterDetailPojo.setOfficeNameEn(officeCategory.findById(o.getCode()).get().getNameEn());
//                masterDetailPojo.setOfficeNameNp(officeCategory.findById(o.getCode()).get().getNameNp());
//                masterDetailPojo.setKaajCount(masterDetailPojo.getKaajCount());
//                masterDetailPojo.setLeaveCount(masterDetailPojo.getLeaveCount());
//                masterDetailPojo.setTotalDarta(masterDetailPojoDarta.getTotalDarta());
//                masterDetailPojo.setManualCount(masterDetailPojoDarta.getManualCount());
//                masterDetailPojo.setTippaniCount(masterDetailPojoDarta.getTippaniCount());
//                masterDetailPojo.setChalaniCount(masterDetailPojoDarta.getChalaniCount());
//                masterDetailPojo.setAutoCount(masterDetailPojoDarta.getAutoCount());
//                masterDetailPojoList.add(masterDetailPojo);
            });
            masterDashboardResponsePojo.setOfficeDetails(masterDetailPojoList);

        }
        return masterDashboardResponsePojo;
    }

    @Override
    public MasterDashboardResponsePojo getTopOfficeDetail(FilterDataWisePojo filterDataWisePojo) {
        LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(filterDataWisePojo.getFromDateNp()));
        LocalDate toDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(filterDataWisePojo.getToDateNp()));
        List<String> lowerofficeList = officeMapper.getLowerOffice(tokenProcessorService.getOfficeCode());
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();

        TopTenOfficeDetailPojo topTenOfficeDetailPojo = new TopTenOfficeDetailPojo();

        Integer orderBy = filterDataWisePojo.getOrderBy() != null ? filterDataWisePojo.getOrderBy() : 1;
        String orderType = filterDataWisePojo.getOrderType() != null ? filterDataWisePojo.getOrderType().intValue() == 0 ? "ASC" : "DESC" : "DESC";


//        MasterDataPojo masterDataPojo = new MasterDataPojo();
//        masterDataPojo.setMasterDashboardPojoList(attendanceServiceData.filterWithKaajLeave(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo()).getKaajList().getMasterDashboardPojoList()
//                .stream().map(y -> {
//                    return this.getDartaCount(fromDate, toDate, y.getOfficeCode(), y);
//                }).collect(Collectors.toList()));
//
//
//
//        //kaaj
//
//        TopTenOfficeDetailPojo leaveKaajData = attendanceServiceData.filterWithKaajLeave(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo());
//
//        masterDataPojo.setTotalData(leaveKaajData.getKaajList().getTotalData());
//        masterDataPojo.setTotalPages(leaveKaajData.getKaajList().getTotalPages());
//        masterDataPojo.setCurrentPage(leaveKaajData.getKaajList().getCurrentPage());
//        topTenOfficeDetailPojo.setKaajList(masterDataPojo);
//
//        MasterDataPojo masterDataPojoLeave = new MasterDataPojo();
//        masterDataPojoLeave.setMasterDashboardPojoList(attendanceServiceData.filterWithKaajLeave(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo()).getLeaveList().getMasterDashboardPojoList()
//                .stream().map(y -> {
//                    return this.getDartaCount(fromDate, toDate, y.getOfficeCode(), y);
//                }).collect(Collectors.toList()));
//        topTenOfficeDetailPojo.setLeaveList(masterDataPojoLeave);
//
//        masterDataPojoLeave.setTotalData(leaveKaajData.getLeaveList().getTotalData());
//        masterDataPojoLeave.setTotalPages(leaveKaajData.getLeaveList().getTotalPages());
//        masterDataPojoLeave.setCurrentPage(leaveKaajData.getLeaveList().getCurrentPage());

        MasterDataPojo masterDataPojoDarta = new MasterDataPojo();
        TopTenOfficeDetailPojo dartaList;
        if(orderBy.intValue() == 6 || orderBy.intValue() ==7){
            dartaList = attendanceServiceData.filterWithKaajLeave(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo(), orderBy, orderType);
            masterDataPojoDarta.setMasterDashboardPojoList(dartaList.getKaajList().getMasterDashboardPojoList()
                .stream().map(y -> {
                    return this.getDartaCount(fromDate, toDate, y.getOfficeCode(), y);
                }).collect(Collectors.toList()));

            masterDataPojoDarta.setTotalData(dartaList.getKaajList().getTotalData());
            masterDataPojoDarta.setTotalPages(dartaList.getKaajList().getTotalPages());
            masterDataPojoDarta.setCurrentPage(dartaList.getKaajList().getCurrentPage());
        }
        else{
             dartaList = dartaChalaniServiceData.getMasterDasboardDarta(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo(), this.convertListToString(lowerofficeList), orderBy, orderType);
            masterDataPojoDarta.setMasterDashboardPojoList(dartaList.getDartaList().getMasterDashboardPojoList()
                    .stream().map(y -> {
                        return this.getKaajLeaveCountForDarta(fromDate, toDate, Arrays.asList(y.getOfficeCode()), y);
                    }).collect(Collectors.toList()));


            masterDataPojoDarta.setTotalData(dartaList.getDartaList().getTotalData());
            masterDataPojoDarta.setTotalPages(dartaList.getDartaList().getTotalPages());
            masterDataPojoDarta.setCurrentPage(dartaList.getDartaList().getCurrentPage());
        }

        topTenOfficeDetailPojo.setDartaList(masterDataPojoDarta);


        //-------------------------------------------------- for master dashboard total

        String totalList = convertListToString(lowerofficeList);
        MasterDashboardTotalPojo totalDarta = dartaChalaniServiceData.getMasterDasboardDartaTotal(fromDate, toDate, totalList);

        MasterDashboardTotalPojo totalLeave = attendanceServiceData.getMasterDashboardTotal(fromDate, toDate, totalList);

        MasterDashboardTotalPojo totalPojo = new MasterDashboardTotalPojo();
        totalPojo.setAutoDarta(totalDarta.getAutoDarta());
        totalPojo.setManualDarta(totalDarta.getManualDarta());
        totalPojo.setTotalDarta(totalDarta.getTotalDarta());
        totalPojo.setChalani(totalDarta.getChalani());
        totalPojo.setTippani(totalDarta.getTippani());
        totalPojo.setLeave(totalLeave.getLeave());
        totalPojo.setKaaj(totalLeave.getKaaj());

        masterDataPojoDarta.setTotalCount(totalPojo);

        //---------------------------------------------------------------------------------
//        MasterDataPojo masterDataPojoChalani = new MasterDataPojo();
//        masterDataPojoChalani.setMasterDashboardPojoList(dartaChalaniServiceData.getMasterDasboardDarta(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo(), this.convertListToString(lowerofficeList)).getChalaniList().getMasterDashboardPojoList()
//                .stream().map(y -> {
//                    return this.getKaajLeaveCount(fromDate, toDate, Arrays.asList(y.getOfficeCode()), y);
//                }).collect(Collectors.toList()));
//        topTenOfficeDetailPojo.setChalaniList(masterDataPojoDarta);
//
//        masterDataPojoChalani.setTotalData(dartaList.getChalaniList().getTotalData());
//        masterDataPojoChalani.setTotalPages(dartaList.getChalaniList().getTotalPages());
//        masterDataPojoChalani.setCurrentPage(dartaList.getChalaniList().getCurrentPage());
//
//        MasterDataPojo masterDataPojoTippani = new MasterDataPojo();
//        masterDataPojoTippani.setMasterDashboardPojoList(dartaChalaniServiceData.getMasterDasboardDarta(fromDate, toDate, filterDataWisePojo.getLimit(), filterDataWisePojo.getPageNo(), this.convertListToString(lowerofficeList)).getTippaniList().getMasterDashboardPojoList()
//                .stream().map(y -> {
//                    return this.getKaajLeaveCount(fromDate, toDate, Arrays.asList(y.getOfficeCode()), y);
//                }).collect(Collectors.toList()));
//        masterDataPojoTippani.setTotalPages(dartaList.getTippaniList().getTotalPages());
//        masterDataPojoTippani.setTotalData(dartaList.getTippaniList().getTotalData());
//        masterDataPojoTippani.setCurrentPage(dartaList.getTippaniList().getCurrentPage());
//        topTenOfficeDetailPojo.setTippaniList(masterDataPojoTippani);

        masterDashboardResponsePojo.setTopTenOfficeDetailList(topTenOfficeDetailPojo);

        return masterDashboardResponsePojo;
    }

    @Override
    public InputStreamSource getTopOfficeDetailExcel(FilterDataWisePojo filterDataWisePojo, String lan) {
        LocalDate fromDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(filterDataWisePojo.getFromDateNp()));
        LocalDate toDate = dateConverter.convertToLocalDateViaInstant(dateConverter.convertBsToAd(filterDataWisePojo.getToDateNp()));

        String fromDateNp = filterDataWisePojo.getFromDateNp();
        String toDateNp = filterDataWisePojo.getToDateNp();

        List<String> lowerofficeList = officeMapper.getLowerOffice(tokenProcessorService.getOfficeCode());
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();

        TopTenOfficeDetailPojo topTenOfficeDetailPojo = new TopTenOfficeDetailPojo();

//        MasterDataPojo masterDataPojoDarta = new MasterDataPojo();
//
//        masterDataPojoDarta.setMasterDashboardPojoList(dartaChalaniServiceData.getMasterDasboardDartaExcel(fromDate, toDate, this.convertListToString(lowerofficeList)).getDartaList().getMasterDashboardPojoList()
//                .stream().map(y -> {
//                    return this.getKaajLeaveCountForDarta(fromDate, toDate, Arrays.asList(y.getOfficeCode()), y);
//                }).collect(Collectors.toList()));
//        topTenOfficeDetailPojo.setDartaList(masterDataPojoDarta);

        Integer orderBy = filterDataWisePojo.getOrderBy() != null ? filterDataWisePojo.getOrderBy() : 1;
        String orderType = filterDataWisePojo.getOrderType() != null ? filterDataWisePojo.getOrderType().intValue() == 0 ? "ASC" : "DESC" : "DESC";


        MasterDataPojo masterDataPojoDarta = new MasterDataPojo();
        TopTenOfficeDetailPojo dartaList;
        if(orderBy.intValue() == 6 || orderBy.intValue() ==7){
            dartaList = attendanceServiceData.filterWithKaajLeaveExcel(fromDate, toDate, orderBy, orderType);
            masterDataPojoDarta.setMasterDashboardPojoList(dartaList.getKaajList().getMasterDashboardPojoList()
                    .stream().map(y -> {
                        return this.getDartaCount(fromDate, toDate, y.getOfficeCode(), y);
                    }).collect(Collectors.toList()));

            masterDataPojoDarta.setTotalData(dartaList.getKaajList().getTotalData());
            masterDataPojoDarta.setTotalPages(dartaList.getKaajList().getTotalPages());
            masterDataPojoDarta.setCurrentPage(dartaList.getKaajList().getCurrentPage());
        }
        else{
            dartaList = dartaChalaniServiceData.getMasterDasboardDartaExcel(fromDate, toDate, this.convertListToString(lowerofficeList), orderBy, orderType);
            masterDataPojoDarta.setMasterDashboardPojoList(dartaList.getDartaList().getMasterDashboardPojoList()
                    .stream().map(y -> {
                        return this.getKaajLeaveCountForDarta(fromDate, toDate, Arrays.asList(y.getOfficeCode()), y);
                    }).collect(Collectors.toList()));


            masterDataPojoDarta.setTotalData(dartaList.getDartaList().getTotalData());
            masterDataPojoDarta.setTotalPages(dartaList.getDartaList().getTotalPages());
            masterDataPojoDarta.setCurrentPage(dartaList.getDartaList().getCurrentPage());
        }

        topTenOfficeDetailPojo.setDartaList(masterDataPojoDarta);


        //-------------------------------------------------------------------- for master dashboard total

        String totalList = convertListToString(lowerofficeList);
        MasterDashboardTotalPojo totalDarta = dartaChalaniServiceData.getMasterDasboardDartaTotal(fromDate, toDate, totalList);

        MasterDashboardTotalPojo totalLeave = attendanceServiceData.getMasterDashboardTotal(fromDate, toDate, totalList);

        MasterDashboardTotalPojo totalPojo = new MasterDashboardTotalPojo();
        totalPojo.setAutoDarta(totalDarta.getAutoDarta());
        totalPojo.setManualDarta(totalDarta.getManualDarta());
        totalPojo.setTotalDarta(totalDarta.getTotalDarta());
        totalPojo.setChalani(totalDarta.getChalani());
        totalPojo.setTippani(totalDarta.getTippani());
        totalPojo.setLeave(totalLeave.getLeave());
        totalPojo.setKaaj(totalLeave.getKaaj());

        masterDataPojoDarta.setTotalCount(totalPojo);

        //----------------------------------------------------------------------------------------
        masterDashboardResponsePojo.setTopTenOfficeDetailList(topTenOfficeDetailPojo);

        return writeExcel(masterDataPojoDarta, fromDate.toString(), fromDateNp, toDate.toString(), toDateNp, lan);
    }

//    @Override
//    public byte[] generateReport(FilterDataWisePojo filterDataWisePojo, Integer reportType) {
//        filterDataWisePojo.setLimit(1000);
//        MasterDashboardResponsePojo masterDashboardResponsePojo=new MasterDashboardResponsePojo();
//        StringBuilder html;
//        StringBuilder lateRemarks;
//        try {
//             masterDashboardResponsePojo= this.getTopOfficeDetail(filterDataWisePojo);
//        } catch (Exception e) {
//            throw new CustomException(e.getMessage());
//        }
//
//        Context context = new Context();
//        StringBuilder heading = new StringBuilder();
//        if (reportType.toString().equalsIgnoreCase("0")) {
//            heading.append("NEPAL GOVERNMENT <br>");
//        } else {
//            heading.append("नेपाल सरकार <br>");
//        }
//        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
//        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
//            if (i == topOfficeDetails.size() - 2) {
//                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
//            } else {
//                heading.append(topOfficeDetails.get(i));
//            }
//            heading.append("<br>");
//
//        }
//        heading.append("<br>");
//        if (reportType.toString().equalsIgnoreCase("0")) {
//            if (paginatedRequest.getSearchField().get("userType") != null) {
//                String userType = userMgmtMapper.getUserType(1, paginatedRequest.getSearchField());
//                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(1)).append("Monthly Detail Report of").append(StringUtils.capitalize(userType)).append("Employee").append("</span>");
//            } else {
//
//                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(1)).append("Monthly Detail Report of All Employee").append("</span>");
//
//            }
//        } else {
//            if (paginatedRequest.getSearchField().get("userType") != null) {
//                String userType = userMgmtMapper.getUserType(0, paginatedRequest.getSearchField());
//                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(0)).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(")").append(StringUtils.capitalize(userType)).append("को मासिक उपस्थिति जानकारी").append("</span>");
//            } else {
//
//                heading.append("<span style=\"font-size:14px\">").append(userMgmtMapper.getActiveFiscalYear(0)).append("(").append(dateConverter.getMonth(paginatedRequest.getMonth())).append(") को मासिक उपस्थिति जानकारी").append("</span>");
//
//            }
//
//        }
////        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
//        context.setVariable("header", heading.toString());
//        context.setVariable("body", html);
//        StringBuilder remarks=new StringBuilder();;
//        remarks.append("<span style=\"font-size:20px\">")
//                .append("विवरण:")
//                .append("</span>");
//
//
//        if(lateRemarks!=null){
//            context.setVariable("remarksTitle", remarks);
//        }
//        context.setVariable("remarks", lateRemarks);
//        String process = templateEngine.process("header.html", context);
//        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
//        return messagingServiceData.getFileConverter(fileConverterPojo);
//
//    }

    public MasterDetailPojo masterDashboard(LocalDate fromDateOfficeWise, LocalDate toDateOfficeWise, List<String> listOfOffice, String parentCode) {
        MasterDetailPojo masterDetailPojo = attendanceServiceData.getOfficeKaajLeave(fromDateOfficeWise, toDateOfficeWise, listOfOffice);
        MasterDetailPojo masterDetailPojoDarta = dartaChalaniServiceData.getOfficeDartaCount(fromDateOfficeWise, toDateOfficeWise, listOfOffice);
        masterDetailPojo.setOfficeCode(parentCode);
        masterDetailPojo.setOfficeNameEn(officeCategory.findById(parentCode).get().getNameEn());
        masterDetailPojo.setOfficeNameNp(officeCategory.findById(parentCode).get().getNameNp());
        masterDetailPojo.setKaajCount(masterDetailPojo.getKaajCount());
        masterDetailPojo.setLeaveCount(masterDetailPojo.getLeaveCount());
        masterDetailPojo.setTotalDarta(masterDetailPojoDarta.getTotalDarta());
        masterDetailPojo.setManualCount(masterDetailPojoDarta.getManualCount());
        masterDetailPojo.setTippaniCount(masterDetailPojoDarta.getTippaniCount());
        masterDetailPojo.setChalaniCount(masterDetailPojoDarta.getChalaniCount());
        masterDetailPojo.setAutoCount(masterDetailPojoDarta.getAutoCount());

        return masterDetailPojo;
    }

    public String convertListToString(List<String> officeCode) {
        AtomicReference<String> officeString = new AtomicReference<>("");
        officeString.set(officeString.get().concat("{"));
        AtomicInteger count = new AtomicInteger(1);
        officeCode.stream().forEach(x -> {
            officeString.set(officeString.get().concat(x));
            if (count.get() < officeCode.size()) {
                officeString.set(officeString.get().concat(","));

            }
            count.getAndIncrement();
        });
        officeString.set(officeString.get().concat("}"));
        return officeString.get();
    }

    private InputStreamSource writeExcel(MasterDataPojo masterDataPojo, String from, String fromNp, String to, String toNp, String lan) {
        Workbook workbook = new XSSFWorkbook();

        Sheet sheet = workbook.createSheet("Persons");
        sheet.setColumnWidth(0, 2000);
        sheet.setColumnWidth(1, 18000);
        sheet.setColumnWidth(2, 3000);
        sheet.setColumnWidth(3, 3000);
        sheet.setColumnWidth(4, 4000);
        sheet.setColumnWidth(5, 3000);
        sheet.setColumnWidth(6, 3000);
        sheet.setColumnWidth(7, 3000);
        sheet.setColumnWidth(8, 3000);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 2, 8));

        //merge cell for total
        sheet.addMergedRegion(new CellRangeAddress(masterDataPojo.getMasterDashboardPojoList().size() + 3,
                masterDataPojo.getMasterDashboardPojoList().size() + 3, 0, 1));

        Row date = sheet.createRow(0);
        Cell dateCell = date.createCell(0);
        dateCell.setCellValue(lan.equals("EN") ? "From: " + from : "बाट: " + toNepali(fromNp));

        dateCell = date.createCell(2);
        dateCell.setCellValue(lan.equals("EN") ? "To: " + to : "सम्म: " + toNepali(toNp));

        Row header = sheet.createRow(1);

        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setFontName("Arial");
        font.setFontHeightInPoints((short) 10);
        font.setBold(true);
        font.setColor(IndexedColors.BLACK1.getIndex());
        headerStyle.setFont(font);

        Cell headerCell = header.createCell(0);
        headerCell.setCellValue(lan.equals("EN") ? "SN" : "क्र.सं.");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(1);
        headerCell.setCellValue(lan.equals("EN") ? "Office Name" : "कार्यालय नाम");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(2);
        headerCell.setCellValue(lan.equals("EN") ? "Darta" : "दर्ता");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(3);
        headerCell.setCellValue(lan.equals("EN") ? "Auto Darta" : "प्रणाली दर्ता");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(4);
        headerCell.setCellValue(lan.equals("EN") ? "Manual Darta" : "म्यानुअल दर्ता");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(5);
        headerCell.setCellValue(lan.equals("EN") ? "Chalani" : "चलानी");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(6);
        headerCell.setCellValue(lan.equals("EN") ? "Tippani" : "टिप्पणी");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(7);
        headerCell.setCellValue(lan.equals("EN") ? "Leave" : "विदा");
        headerCell.setCellStyle(headerStyle);

        headerCell = header.createCell(8);
        headerCell.setCellValue(lan.equals("EN") ? "Kaaj" : "काज");
        headerCell.setCellStyle(headerStyle);


        XSSFFont fontRow = ((XSSFWorkbook) workbook).createFont();
        fontRow.setFontName("Sans-serif");
        fontRow.setFontHeightInPoints((short) 9);

        CellStyle style = workbook.createCellStyle();
        style.setWrapText(true);
        style.setFont(fontRow);

        int count = 1;
        for (MasterDetailPojo m : masterDataPojo.getMasterDashboardPojoList()) {
            Row row = sheet.createRow(count + 1);
            Cell cell = row.createCell(0);
            cell.setCellValue(toNepali(count));
            cell.setCellStyle(style);

            cell = row.createCell(1);
            cell.setCellValue(lan.equals("EN") ? m.getOfficeNameEn() : m.getOfficeNameNp());
            cell.setCellStyle(style);

            cell = row.createCell(2);
            cell.setCellValue(lan.equals("EN") ? m.getTotalDarta() + "" : toNepali(m.getTotalDarta()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            cell = row.createCell(3);
            cell.setCellValue(lan.equals("EN") ? m.getAutoCount() + "" : toNepali(m.getAutoCount()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            cell = row.createCell(4);
            cell.setCellValue(lan.equals("EN") ? m.getManualCount() + "" : toNepali(m.getManualCount()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            cell = row.createCell(5);
            cell.setCellValue(lan.equals("EN") ? m.getChalaniCount() + "" : toNepali(m.getChalaniCount()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            cell = row.createCell(6);
            cell.setCellValue(lan.equals("EN") ? m.getTippaniCount() + "" : toNepali(m.getTippaniCount()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            cell = row.createCell(7);
            cell.setCellValue(lan.equals("EN") ? m.getLeaveCount() + "" : toNepali(m.getLeaveCount()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            cell = row.createCell(8);
            cell.setCellValue(lan.equals("EN") ? m.getKaajCount() + "" : toNepali(m.getKaajCount()));
            cell.setCellStyle(style);

            CellUtil.setAlignment(cell, HorizontalAlignment.LEFT);

            count++;
        }

        // total tart from here
        Row total = sheet.createRow(masterDataPojo.getMasterDashboardPojoList().size() + 3);
        Cell cellTotal = total.createCell(0);
        cellTotal.setCellValue(lan.equals("EN") ? "Total" : "जम्मा");
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.CENTER);

        cellTotal = total.createCell(2);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getTotalDarta() + "" : toNepali(masterDataPojo.getTotalCount().getTotalDarta()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

        cellTotal = total.createCell(3);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getAutoDarta() + "" : toNepali(masterDataPojo.getTotalCount().getAutoDarta()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

        cellTotal = total.createCell(4);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getManualDarta() + "" : toNepali(masterDataPojo.getTotalCount().getManualDarta()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

        cellTotal = total.createCell(5);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getChalani() + "" : toNepali(masterDataPojo.getTotalCount().getChalani()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

        cellTotal = total.createCell(6);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getTippani() + "" : toNepali(masterDataPojo.getTotalCount().getTippani()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

        cellTotal = total.createCell(7);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getLeave() + "" : toNepali(masterDataPojo.getTotalCount().getLeave()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

        cellTotal = total.createCell(8);
        cellTotal.setCellValue(lan.equals("EN") ? masterDataPojo.getTotalCount().getKaaj() + "" : toNepali(masterDataPojo.getTotalCount().getKaaj()));
        cellTotal.setCellStyle(style);

        CellUtil.setAlignment(cellTotal, HorizontalAlignment.LEFT);

//        File currDir = new File(".");
//        String path = currDir.getAbsolutePath();
//        String fileLocation = path.substring(0, path.length() - 1) + "temp.xlsx";

//        FileOutputStream outputStream = null;
//        try {
//            outputStream = new FileOutputStream(fileLocation);
//            workbook.write(outputStream);
//            workbook.close();
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);

            return new ByteArrayResource(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException
                    ("Error while generating master dashboard excel.");
        }

    }

    private String toNepali(Long num) {
        String s = dateConverter.convertBSToDevnagari(String.valueOf(num != null ? num : 0));
        return s;
    }

    private String toNepali(String num) {
        String s = dateConverter.convertBSToDevnagari(num != null ? num : "");
        return s;
    }

    private String toNepali(int num) {
        String s = dateConverter.convertBSToDevnagari(String.valueOf(num));
        return s;
    }
}
