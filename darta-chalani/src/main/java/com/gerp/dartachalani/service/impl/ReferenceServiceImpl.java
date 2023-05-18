package com.gerp.dartachalani.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.mapper.DispatchLetterMapper;
import com.gerp.dartachalani.mapper.MemoMapper;
import com.gerp.dartachalani.mapper.ReceivedLetterMapper;
import com.gerp.dartachalani.service.ReferenceService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.pojo.employee.EmployeeMinimalPojo;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
@Transactional
public class ReferenceServiceImpl implements ReferenceService {

    private static final String TIPPANI = "tippani";
    private static final String CHALANI = "chalani";
    private static final String DARTA = "darta";
    private MemoMapper memoMapper;
    private DispatchLetterMapper dispatchLetterMapper;
    private ReceivedLetterMapper receivedLetterMapper;
    @Autowired
    private DateConverter dateConverter;

    @Autowired
    private TokenProcessorService tokenProcessorService;

    @Autowired
    private UserMgmtServiceData userMgmtProxy;

    public ReferenceServiceImpl(MemoMapper memoMapper,
                                DispatchLetterMapper dispatchLetterMapper,
                                ReceivedLetterMapper receivedLetterMapper) {
        this.memoMapper = memoMapper;
        this.dispatchLetterMapper = dispatchLetterMapper;
        this.receivedLetterMapper = receivedLetterMapper;
    }

    @Override
    public Page<DartaChalaniGenericPojo> getReferences(GetRowsRequest paginatedRequest) {
        Page<DartaChalaniGenericPojo> page = new Page<>(paginatedRequest.getPage(), paginatedRequest.getLimit());

        switch (paginatedRequest.getLetterType()) {
            case DARTA:

                if (paginatedRequest.getSearchField().containsKey("fromDateNp") && paginatedRequest.getSearchField().containsKey("toDateNp")) {
                    String fromDateNp = paginatedRequest.getSearchField().get("fromDateNp").toString();
                    String toDateNp = paginatedRequest.getSearchField().get("toDateNp").toString();

                    paginatedRequest.getSearchField().putIfAbsent("fromDate", getAdFormattedDate(fromDateNp));
                    paginatedRequest.getSearchField().putIfAbsent("toDate", getAdFormattedDate(toDateNp));
                }

                if (paginatedRequest.getSearchField().containsKey("fromDartaDateNp") && paginatedRequest.getSearchField().containsKey("toDartaDateNp")) {
                    String fromDartaDateNp = paginatedRequest.getSearchField().get("fromDartaDateNp").toString();
                    String toDartaDateNp = paginatedRequest.getSearchField().get("toDartaDateNp").toString();

                    paginatedRequest.getSearchField().putIfAbsent("fromDartaDate", getAdFormattedDate(fromDartaDateNp));
                    paginatedRequest.getSearchField().putIfAbsent("toDartaDate", getAdFormattedDate(toDartaDateNp));
                }

                page = receivedLetterMapper.getDataForReference(
                        page,
                        tokenProcessorService.getPisCode(),
                        tokenProcessorService.getOfficeCode(),
                        paginatedRequest.getSearchField()
                );
                page.getRecords().forEach(x -> x.setLetterType(DARTA));
                break;
            case CHALANI:
                page = dispatchLetterMapper.getDataForReference(
                        page,
                        tokenProcessorService.getPisCode(),
                        tokenProcessorService.getOfficeCode(),
                        paginatedRequest.getSearchField()
                );
                page.getRecords().forEach(x -> x.setLetterType(CHALANI));
                break;
            case TIPPANI:
                page = memoMapper.getDataForReference(
                        page,
                        tokenProcessorService.getPisCode(),
                        tokenProcessorService.getOfficeCode(),
                        paginatedRequest.getSearchField()
                );
                page.getRecords().forEach(x -> x.setLetterType(TIPPANI));
                break;
            default:
                break;
        }

        if (page.getRecords() != null && !page.getRecords().isEmpty()) {
            for (DartaChalaniGenericPojo data : page.getRecords()) {
                data.setCreatedDateNp(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            }
        }

        return page;
    }

    @Override
    public List<EmployeeMinimalPojo> getInvolvedUsers(UserDetailsPojo request) {
        List<String> pisCodes = new ArrayList<>();
        List<EmployeeMinimalPojo> employeeMinimalPojos = new ArrayList<>();

        switch (request.getLetterType()) {
            case DARTA:
                ReceivedLetterResponsePojo data = receivedLetterMapper.getReceivedLetter(request.getId());
                pisCodes.add(data.getPisCode());
                pisCodes.addAll(receivedLetterMapper.getInvolvedUsers(request.getId()));
                employeeMinimalPojos.addAll(getEmployeeMinimals(pisCodes));
                break;
            case CHALANI:
                DispatchLetterDTO chalani = dispatchLetterMapper.getDispatchLetterDetailById(request.getId());
                pisCodes.add(chalani.getSenderPisCode());
                pisCodes.addAll(dispatchLetterMapper.getInvolvedUsers(request.getId()));
                employeeMinimalPojos.addAll(getEmployeeMinimals(pisCodes));
                break;
            case TIPPANI:
                MemoResponsePojo tippani = memoMapper.getMemoById(request.getId());
                pisCodes.add(tippani.getPisCode());
                pisCodes.addAll(memoMapper.getInvolvedUsers(request.getId()));
                employeeMinimalPojos.addAll(getEmployeeMinimals(pisCodes));
                break;
            default:
                break;
        }

        return employeeMinimalPojos;
    }

    private List<EmployeeMinimalPojo> getEmployeeMinimals(List<String> pisCodes) {
        List<EmployeeMinimalPojo> employeeMinimalPojos = new ArrayList<>();

        if (pisCodes != null && !pisCodes.isEmpty()) {
            for (String pisCode : pisCodes) {
                if (pisCode != null)
                    employeeMinimalPojos.add(userMgmtProxy.getEmployeeDetailMinimal(pisCode));
            }
        }
        return employeeMinimalPojos;
    }

    private String getAdFormattedDate(String dateStr) {
        Date date = dateConverter.convertBsToAd(dateStr);
        Calendar calendarFrom = Calendar.getInstance();
        calendarFrom.setTime(date);
        return calendarFrom.get(Calendar.YEAR) + "-" + (calendarFrom.get(Calendar.MONTH) + 1) + "-" + calendarFrom.get(Calendar.DAY_OF_MONTH);
    }
}
