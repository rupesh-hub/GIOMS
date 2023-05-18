package com.gerp.dartachalani.service.impl;

import com.gerp.dartachalani.Proxy.DocumentServiceData;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.nepalDate.Days;
import com.gerp.dartachalani.mapper.DashboardMapper;
import com.gerp.dartachalani.service.DashboardService;
import com.gerp.dartachalani.service.DispatchLetterService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.pojo.DateListPojo;
import com.gerp.shared.pojo.FiscalYearPojo;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;


@Service
public class DashboardServiceImpl implements DashboardService {
    private final DashboardMapper dashboardMapper;
    private final TokenProcessorService tokenProcessorService;
    private final UserMgmtServiceData userMgmtServiceData;
    private final DateConverter dateConverter;
    private final DocumentServiceData documentDataService;
    private final DispatchLetterServiceImpl dispatchLetterServiceImpl;


    public DashboardServiceImpl(DashboardMapper dashboardMapper, TokenProcessorService tokenProcessorService, UserMgmtServiceData userMgmtServiceData, DateConverter dateConverter, DocumentServiceData documentDataService, DispatchLetterServiceImpl dispatchLetterServiceImpl) {
        this.dashboardMapper = dashboardMapper;
        this.tokenProcessorService = tokenProcessorService;
        this.userMgmtServiceData = userMgmtServiceData;
        this.dateConverter = dateConverter;
        this.documentDataService = documentDataService;
        this.dispatchLetterServiceImpl = dispatchLetterServiceImpl;
    }

    private String getConvertAdToBs() {
        return dateConverter.convertAdToBs(LocalDate.now().toString()).substring(0, 4);
    }

    @Override
    public DashboardCountPojo getDashboardCount(String sectionId, String employeeCode, String officeCode, LocalDate startDate, LocalDate endDate, String fiscalYear, String toOfficeCode) {
        DashboardCountPojo dashboardCountPojo;
        boolean equals = true;
        FiscalYearPojo fiscalYearPojo = fiscalYear != null ? userMgmtServiceData.getFiscalDetailByFiscalYearCode(fiscalYear) : getFiscalYearPojo();
        LocalDateTime searchStartDate = startDate == null ? fiscalYearPojo.getStartDate().atTime(00, 00, 00) : startDate.atTime(00, 00, 00);
        LocalDateTime searchEndDate = endDate == null ? fiscalYearPojo.getEndDate().atTime(23, 59, 59) : endDate.atTime(23, 59, 59);
        EmployeePojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetail(tokenProcessorService.getPisCode());

        String employeeSectionId = employeeMinimalPojo != null ? employeeMinimalPojo.getSectionId() != null ? employeeMinimalPojo.getSectionId() : null : null;

        String officeCodeBack = null;
        Boolean isOfficeHead = false;
        String section = null;
        Boolean isSuperAdmin = false;

        if (tokenProcessorService.isSuperAdmin() || tokenProcessorService.isOrganizationAdmin()) {
            officeCodeBack = officeCode != null && !officeCode.equals("") ? officeCode : null;
            toOfficeCode = toOfficeCode != null && !toOfficeCode.equals("") ? toOfficeCode : null;
            isSuperAdmin = true;
        }

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            officeCodeBack = officeCode != null && !officeCode.equals("") ? officeCode : employeeMinimalPojo != null ? employeeMinimalPojo.getOffice() != null ? employeeMinimalPojo.getOffice().getCode() : null : null;
            isOfficeHead = true;
            section = sectionId != null ? !sectionId.equals("") ? sectionId : null : null;
        } else {
            section = sectionId != null ? !sectionId.equals("") ? sectionId : employeeSectionId : employeeSectionId;
        }

        //for work on transferred employee letter
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();

        listPisCodes.add(tokenPisCode);
        if (section != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, section) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, section));
        }

        dashboardCountPojo = dashboardMapper.getDashboardDartaCount(section, tokenProcessorService.getPisCode(), officeCodeBack, equals, searchStartDate, searchEndDate, isOfficeHead, isSuperAdmin, toOfficeCode, listPisCodes);

        Integer dashboardCountPojo1 = dashboardMapper.getDashBoardTipadiCount(section, tokenProcessorService.getPisCode(), officeCodeBack, equals, searchStartDate, searchEndDate, isOfficeHead, isSuperAdmin, toOfficeCode, listPisCodes);
        Integer dashboardCountPojo2 = dashboardMapper.getDashBoardChalaniCount(section, tokenProcessorService.getPisCode(), officeCodeBack, equals, searchStartDate, searchEndDate, isOfficeHead, isSuperAdmin, toOfficeCode, listPisCodes);
        dashboardCountPojo = setValues(dashboardCountPojo, dashboardCountPojo1, dashboardCountPojo2);
        dashboardCountPojo.setDarta(isOfficeHead || isSuperAdmin ? dashboardMapper.getDartaStatusWiseDetailHead(section, tokenProcessorService.getPisCode(), officeCodeBack, searchStartDate, searchEndDate,
                isOfficeHead, equals, isSuperAdmin, toOfficeCode)
                :
                dashboardMapper.getDartaStatusWiseDetail(section, tokenProcessorService.getPisCode(), officeCodeBack, searchStartDate, searchEndDate, isOfficeHead, equals, listPisCodes
                ));
        dashboardCountPojo.setChalani(dashboardMapper.getChalaniStatusWiseDetail(section, tokenProcessorService.getPisCode(), officeCodeBack, searchStartDate, searchEndDate,
                isOfficeHead, equals, isSuperAdmin, toOfficeCode, listPisCodes));
        dashboardCountPojo.setTippadi(getTippadiStatusWiseDetail(section, tokenPisCode, equals, searchStartDate, searchEndDate, officeCodeBack, isOfficeHead, isSuperAdmin, toOfficeCode, listPisCodes));
        return dashboardCountPojo;
    }

    private DashboardCountPojo getTippadiStatusWiseDetail(String sectionId, String employeePisCode, boolean equals, LocalDateTime searchStartDate, LocalDateTime searchEndDate,
                                                          String officeCode, boolean isOfficeHead, boolean isSuperAdmin, String toOfficeCode,
                                                          Set<String> listPisCodes) {
        DashboardCountPojo tippadiStatusWiseDetail = dashboardMapper.getTippadiStatusWiseDetail(sectionId, employeePisCode, officeCode,
                searchStartDate, searchEndDate, isOfficeHead, equals, isSuperAdmin, toOfficeCode, listPisCodes);
        if (tippadiStatusWiseDetail == null) return null;
        tippadiStatusWiseDetail.setTotalTippadi(tippadiStatusWiseDetail.getApprovedTippadi() + tippadiStatusWiseDetail.getPending());
        return tippadiStatusWiseDetail;
    }

    private FiscalYearPojo getFiscalYearPojo() {
        return userMgmtServiceData.findFiscalYeaDetails();
    }

    private FiscalYearPojo getFiscalYearPojoByFiscalYearCode(String fiscalYearCode) {
        return userMgmtServiceData.getFiscalDetailByFiscalYearCode(fiscalYearCode);
    }

    private DashboardCountPojo setValues(DashboardCountPojo dashboardCountPojo, Integer dashboardCountPojo1, Integer dashboardCountPojo2) {
        if (dashboardCountPojo == null) {
            dashboardCountPojo = new DashboardCountPojo();
        }

        dashboardCountPojo.setApprovedChalani(dashboardCountPojo2);
        dashboardCountPojo.setApprovedTippadi(dashboardCountPojo1);
        return dashboardCountPojo;
    }

    @Override
    public DashboardCountPojo circleGraphDashboard(String office, String period, boolean type, String sectionId, String toOfficeCode) {
        switch (period) {
            case "WEEKLY":
                return getBarChartCountWeekly(period, type, sectionId, office, toOfficeCode);
            case "MONTHLY":
                return getBarChartCountMonth(period, type, sectionId, office, toOfficeCode);
            case "YEARLY":
                return getBarChartCountYearly(period, type, sectionId, office, toOfficeCode);
            default:
                throw new CustomException("Please enter valid time period");
        }
    }

    @Override
    public DashboardCountPojo sidebarCount(String sectionId, String fiscalYearCode) {
        DashboardCountPojo dashboardBarChartPojo = new DashboardCountPojo();

        fiscalYearCode = fiscalYearCode != null ? fiscalYearCode :
                userMgmtServiceData.findActiveFiscalYear() != null ? userMgmtServiceData.findActiveFiscalYear().getCode() : "";

        //if fiscalyear is not sent from frontend then take current fiscalyear detail
        //FiscalYearPojo fiscalYearPojo = fiscalYearCode != null ? getFiscalYearPojoByFiscalYearCode(fiscalYearCode) : getFiscalYearPojo();
        String officeCode = tokenProcessorService.getOfficeCode();

        //make full time (i commented this bcz the count of letters must be filter by fiscal year not by created date bcz user can
        // go previous fiscal year and create darta of previous fiscal year)
//        LocalDateTime startDate = fiscalYearPojo.getStartDate().atTime(00, 00, 00);
//        LocalDateTime endDate = fiscalYearPojo.getEndDate().atTime(23, 59, 59);

        //for work on transferred employee chalani
        Set<String> listPisCodes = new HashSet<>();
        String tokenPisCode = tokenProcessorService.getPisCode();
        EmployeePojo tokenUser = userMgmtServiceData.getEmployeeDetail(tokenPisCode);

        listPisCodes.add(tokenPisCode);
        if (tokenUser.getSectionId() != null) {
            if (dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()) != null)
                listPisCodes.addAll(dispatchLetterServiceImpl.getPreviousPisCode(tokenPisCode, tokenUser.getSectionId()));
        }

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            dashboardBarChartPojo.setDarta(dashboardMapper.sidebarCountDartaForOfficeHead(officeCode, tokenPisCode, fiscalYearCode));
        } else {
            dashboardBarChartPojo.setDarta(dashboardMapper.sidebarCountDarta(listPisCodes, sectionId, officeCode, fiscalYearCode));
        }
        dashboardBarChartPojo.setTippadi(dashboardMapper.sidebarCountTippadi(listPisCodes, sectionId, officeCode, fiscalYearCode));
        dashboardBarChartPojo.setChalani(dashboardMapper.sidebarCountChalani(listPisCodes, sectionId, officeCode, fiscalYearCode));
        return dashboardBarChartPojo;
    }

    @Override
    public MasterDashboardResponsePojo getTotalDartaCount(String fromDate, String toDate, Integer limit, Integer pageNo, String officeList, Integer by, String type) {
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();
        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoDarta = new MasterDashboardPaginatedPojo();
//        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoTippani = new MasterDashboardPaginatedPojo();
//        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoChalani = new MasterDashboardPaginatedPojo();

        Integer listSize = Math.toIntExact(officeList.chars().filter(c -> c == ',').count() + 1);

        System.out.println("office size: " + listSize);

        Integer offset = 0;
        if (limit.intValue() == 0) {
            limit = 10;
        }

//        masterDashboardPaginatedPojoDarta.setTotalData(Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("DR")).count())));
//
//        masterDashboardPaginatedPojoDarta.setTotalPages(Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("DR")).count())) % limit == 0 ?
//                Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                        .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("DR")).count())) / limit
//                : (Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("DR")).count())) / limit) + 1);
//
        masterDashboardPaginatedPojoDarta.setTotalData(listSize);

        masterDashboardPaginatedPojoDarta.setTotalPages(listSize % limit == 0 ?
                listSize / limit
                : (listSize / limit) + 1);

        masterDashboardPaginatedPojoDarta.setCurrentPage(limit.intValue() == 0 ? 0 : pageNo);

        switch (by) {
            case 1:
                masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList, type));
                break;
            case 2:
                masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalAutoDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList, type));
                break;
            case 3:
                masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalManualDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList, type));
                break;
            case 4:
                masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalChalani(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList, type));
                break;
            case 5:
                masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalTippani(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList, type));
                break;
            default:
                masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList, type));
        }


        //masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(dashboardMapper.getTotalDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList).stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("DR"))
//                .sorted(Comparator.comparing(MasterDataPojo::getTotalDarta).reversed()).collect(Collectors.toList()));

//-- comment for new query implementation for sorting column-wise
//        masterDashboardPaginatedPojoChalani.setTotalData(Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("CH")).count())));
//
//        masterDashboardPaginatedPojoChalani.setTotalPages(Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("CH")).count())) % limit == 0 ?
//                Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                        .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("CH")).count())) / limit
//                : (Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("CH")).count())) / limit) + 1);
//
//        masterDashboardPaginatedPojoChalani.setCurrentPage(limit.intValue() == 0 ? 0 : pageNo);
//        masterDashboardPaginatedPojoChalani.setMasterDashboardPojoList(dashboardMapper.getTotalDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList).stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("CH"))
//                .sorted(Comparator.comparing(MasterDataPojo::getTotalDarta).reversed()).collect(Collectors.toList()));
//
//
//        masterDashboardPaginatedPojoTippani.setTotalData(Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("TI")).count())));
//
//        masterDashboardPaginatedPojoTippani.setTotalPages(Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("TI")).count())) % limit == 0 ?
//                Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                        .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("TI")).count())) / limit
//                : (Integer.parseInt(String.valueOf(dashboardMapper.getDartaMasterCount(fromDate, toDate, officeList)
//                .stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("TI")).count())) / limit) + 1);
//
//        masterDashboardPaginatedPojoTippani.setCurrentPage(limit.intValue() == 0 ? 0 : pageNo);
//        masterDashboardPaginatedPojoTippani.setMasterDashboardPojoList(dashboardMapper.getTotalDarta(fromDate, toDate, limit, pageNo == 0 ? offset : (limit * pageNo), officeList).stream().filter(x -> x.getOrderStatus().equalsIgnoreCase("TI"))
//                .sorted(Comparator.comparing(MasterDataPojo::getTotalDarta).reversed()).collect(Collectors.toList()));
//
//        masterDashboardResponsePojo.setTippaniList(masterDashboardPaginatedPojoTippani);
//        masterDashboardResponsePojo.setChalaniList(masterDashboardPaginatedPojoChalani);

        masterDashboardResponsePojo.setDartaList(masterDashboardPaginatedPojoDarta);
        return masterDashboardResponsePojo;

    }

    @Override
    public MasterDashboardResponsePojo getTotalDartaCountExcel(String fromDate, String toDate, String officeList, Integer by, String type) {
        MasterDashboardResponsePojo masterDashboardResponsePojo = new MasterDashboardResponsePojo();
        MasterDashboardPaginatedPojo masterDashboardPaginatedPojoDarta = new MasterDashboardPaginatedPojo();

        List<MasterDataPojo> list;

        switch (by) {
            case 1:
                list = dashboardMapper.getTotalDartaExcel(fromDate, toDate, officeList, type);
                break;
            case 2:
                list = dashboardMapper.getTotalAutoDartaExcel(fromDate, toDate, officeList, type);
                break;
            case 3:
                list = dashboardMapper.getTotalManualDartaExcel(fromDate, toDate, officeList, type);
                break;
            case 4:
                list = dashboardMapper.getTotalChalaniExcel(fromDate, toDate, officeList, type);
                break;
            case 5:
                list = dashboardMapper.getTotalTippaniExcel(fromDate, toDate, officeList, type);
                break;
            default:
                list = dashboardMapper.getTotalDartaExcel(fromDate, toDate, officeList, type);
                break;
        }

        masterDashboardPaginatedPojoDarta.setMasterDashboardPojoList(list);

        masterDashboardResponsePojo.setDartaList(masterDashboardPaginatedPojoDarta);
        return masterDashboardResponsePojo;
    }

    @Override
    public MasterDataPojo getDartaCountByOfficeCode(String fromDate, String toDate, List<String> officeCode) {
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
        return dashboardMapper.getDartaCountByOfficeCode(fromDate, toDate, officeString.get());

    }

    @Override
    public MasterDashboardTotalPojo getMasterDashboardTotal(String fromDate, String toDate, String officeList) {

        System.out.println("o list: " + officeList);

        return dashboardMapper.getMasterDashboardTotal(fromDate, toDate, officeList);
    }


    private DashboardCountPojo getBarChartCountYearly(String period, boolean type, String sectionId, String office, String toOfficeCode) {
        DashboardCountPojo dashboardCountPojo = new DashboardCountPojo();
        List<DateListPojo> dateListPojo = userMgmtServiceData.dateRangeListOfYear(Integer.parseInt(getConvertAdToBs()));

        boolean isOfficeHead = false;
        boolean isSuperAdmin = false;
        String officeCode = null;
        if (tokenProcessorService.isSuperAdmin() || tokenProcessorService.isOrganizationAdmin()) {
            isSuperAdmin = true;
            officeCode = office != null && !office.equals("") ? office : null;
            toOfficeCode = toOfficeCode != null && !toOfficeCode.equals("") ? toOfficeCode : null;
        }
        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(tokenProcessorService.getPisCode());
            officeCode = office != null ? office : employeeMinimalPojo != null ? employeeMinimalPojo.getOfficeCode() : null;
            isOfficeHead = true;
        }

        dashboardCountPojo.setDartaCounts(dashboardMapper.getYearBarChartDarta(dateListPojo, type, tokenProcessorService.getPisCode(), sectionId, isOfficeHead, officeCode, isSuperAdmin, toOfficeCode));
        dashboardCountPojo.setChalaniCounts(dashboardMapper.getYearBarChartChalani(dateListPojo, type, tokenProcessorService.getPisCode(), sectionId, isOfficeHead, officeCode, isSuperAdmin, toOfficeCode));
        dashboardCountPojo.setTippaniCounts(dashboardMapper.getYearBarChartTippani(dateListPojo, type, tokenProcessorService.getPisCode(), sectionId, isOfficeHead, officeCode, isSuperAdmin, toOfficeCode));
        return dashboardCountPojo;
    }


    private DashboardCountPojo getBarChartCountMonth(String period, boolean type, String sectionId, String office, String toOffice) {
        DateListPojo dateListPojo = userMgmtServiceData.dateRangeList(Integer.parseInt(getConvertAdToBs()), true);
        DashboardCountPojo dashboardCountPojo = new DashboardCountPojo();
        Map<LocalDate, LocalDate> dates = new HashMap<>();
        String toBs = dateConverter.convertAdToBs(LocalDate.now().toString());

        List<List<Days>> nepaliCalender = documentDataService.getNepaliCalender(Integer.parseInt(toBs.substring(toBs.indexOf('-') + 1, toBs.lastIndexOf('-'))), toBs.substring(0, 4));

        nepaliCalender.forEach(obj -> {
            for (Days ob : obj) {
                if (ob.getNameAd() != null) {
                    dates.put(LocalDate.parse(ob.getNameAd()), LocalDate.parse(obj.get(obj.size() - 1).getNameAd()));
                    break;
                }
            }
        });

        boolean isOfficeHead = false;
        boolean isSuperAdmin = false;
        String officeCode = null;
        if (tokenProcessorService.isSuperAdmin() || tokenProcessorService.isOrganizationAdmin()) {
            isSuperAdmin = true;
            officeCode = office != null && !office.equals("") ? office : null;
            toOffice = toOffice != null && !toOffice.equals("") ? toOffice : null;
        }

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(tokenProcessorService.getPisCode());
            officeCode = employeeMinimalPojo != null ? employeeMinimalPojo.getOfficeCode() : null;
            isOfficeHead = true;
        }

        dashboardCountPojo.setDartaCounts(convertToMonth(dashboardMapper.getBarChartDarta(dateListPojo.getMinDate(), dateListPojo.getMaxDate(), period, tokenProcessorService.getPisCode(), type, sectionId, dates, isOfficeHead, officeCode, isSuperAdmin, toOffice)));
        dashboardCountPojo.setChalaniCounts(convertToMonth(dashboardMapper.getBarChartChalani(dateListPojo.getMinDate(), dateListPojo.getMaxDate(), period, tokenProcessorService.getPisCode(), type, sectionId, dates, isOfficeHead, officeCode, isSuperAdmin, toOffice)));
        dashboardCountPojo.setTippaniCounts(convertToMonth(dashboardMapper.getBarChartTippani(dateListPojo.getMinDate(), dateListPojo.getMaxDate(), period, tokenProcessorService.getPisCode(), type, sectionId, dates, isOfficeHead, officeCode, isSuperAdmin, toOffice)));
        return dashboardCountPojo;
    }

    private List<DashboardBarChartPojo> convertToMonth(List<DashboardBarChartPojo> barChartDarta) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        barChartDarta.forEach(obj -> {
            obj.setName(atomicInteger.toString());
            obj.setCurrentDate(null);
            atomicInteger.getAndIncrement();
        });
        return barChartDarta;
    }


    private DashboardCountPojo getBarChartCountWeekly(String period, boolean type, String sectionId, String office, String toOfficeCode) {
        DashboardCountPojo dashboardCountPojo = new DashboardCountPojo();
        final ZonedDateTime input = ZonedDateTime.now();
        final ZonedDateTime startOfLastWeek = input.minusWeeks(1).with(DayOfWeek.SUNDAY);
        final ZonedDateTime endOfLastWeek = startOfLastWeek.plusDays(6);

        boolean isOfficeHead = false;
        boolean isSuperAdmin = false;
        String officeCode = null;

        if (tokenProcessorService.isSuperAdmin() || tokenProcessorService.isOrganizationAdmin()) {
            isSuperAdmin = true;
            officeCode = office != null && !office.equals("") ? office : null;
            toOfficeCode = toOfficeCode != null && !toOfficeCode.equals("") ? toOfficeCode : null;
        }

        if (tokenProcessorService.getRoles().contains("OFFICE_HEAD")) {
            EmployeeMinimalPojo employeeMinimalPojo = userMgmtServiceData.getEmployeeDetailMinimal(tokenProcessorService.getPisCode());
            officeCode = employeeMinimalPojo != null ? employeeMinimalPojo.getOfficeCode() : null;
            isOfficeHead = true;
        }

        List<DashboardBarChartPojo> barChartDarta = dashboardMapper.getBarChartDarta(startOfLastWeek.toLocalDate(), endOfLastWeek.toLocalDate(), period, tokenProcessorService.getPisCode(), type, sectionId, null, isOfficeHead, officeCode, isSuperAdmin, toOfficeCode);
        findWeekOfTheDay(barChartDarta);
        dashboardCountPojo.setDartaCounts(barChartDarta);

        List<DashboardBarChartPojo> barChartChalani = dashboardMapper.getBarChartChalani(startOfLastWeek.toLocalDate(), endOfLastWeek.toLocalDate(), period, tokenProcessorService.getPisCode(), type, sectionId, null, isOfficeHead, officeCode, isSuperAdmin, toOfficeCode);
        findWeekOfTheDay(barChartChalani);
        dashboardCountPojo.setChalaniCounts(barChartChalani);

        List<DashboardBarChartPojo> barChartTippani = dashboardMapper.getBarChartTippani(startOfLastWeek.toLocalDate(), endOfLastWeek.toLocalDate(), period, tokenProcessorService.getPisCode(), type, sectionId, null, isOfficeHead, officeCode, isSuperAdmin, toOfficeCode);
        findWeekOfTheDay(barChartTippani);
        dashboardCountPojo.setTippaniCounts(barChartTippani);
        return dashboardCountPojo;
    }

    private void findWeekOfTheDay(List<DashboardBarChartPojo> barChartChalani) {
        barChartChalani.parallelStream().forEach(key -> {
            key.setName(key.getCurrentDate().getDayOfWeek().getValue() + "");
            key.setCurrentDate(null);
        });
    }


}
