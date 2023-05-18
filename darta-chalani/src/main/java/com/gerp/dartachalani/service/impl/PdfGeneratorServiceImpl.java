package com.gerp.dartachalani.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.Proxy.ConvertHtlToFileProxy;
import com.gerp.dartachalani.Proxy.UserMgmtServiceData;
import com.gerp.dartachalani.dto.*;
import com.gerp.dartachalani.dto.kasamu.KasamuResponsePaginationPojo;
import com.gerp.dartachalani.dto.kasamu.KasamuResponsePojo;
import com.gerp.dartachalani.mapper.DispatchLetterMapper;
import com.gerp.dartachalani.mapper.MemoMapper;
import com.gerp.dartachalani.mapper.ReceivedLetterMapper;
import com.gerp.dartachalani.model.kasamu.Kasamu;
import com.gerp.dartachalani.repo.kasamu.KasamuRepository;
import com.gerp.dartachalani.service.DispatchLetterService;
import com.gerp.dartachalani.service.PdfGeneratorService;
import com.gerp.dartachalani.service.ReceivedLetterService;
import com.gerp.dartachalani.token.TokenProcessorService;
import com.gerp.shared.exception.CustomException;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;
import com.gerp.shared.utils.NumericConverter;
import com.gerp.shared.utils.nepaliDateConverter.DateConverter;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

    private final TemplateEngine templateEngine;
    private final ReceivedLetterService receivedLetterService;
    private final UserMgmtServiceData userMgmtServiceData;
    private final TokenProcessorService tokenProcessorService;
    private final ConvertHtlToFileProxy convertHtlToFileProxy;
    private final DateConverter dateConverter;
    private final NumericConverter numericConverter;
    private final DispatchLetterService dispatchLetterService;
    private final ReceivedLetterMapper receivedLetterMapper;
    private final DispatchLetterMapper dispatchLetterMapper;
    private final MemoMapper memoMapper;
    private ModelMapper modelMapper;
    private final KasamuRepository kasamuRepository;

    public PdfGeneratorServiceImpl(TemplateEngine templateEngine, ReceivedLetterService receivedLetterService, UserMgmtServiceData userMgmtServiceData, TokenProcessorService tokenProcessorService, ConvertHtlToFileProxy convertHtlToFileProxy, DateConverter dateConverter, NumericConverter numericConverter, DispatchLetterService dispatchLetterService, ReceivedLetterMapper receivedLetterMapper, DispatchLetterMapper dispatchLetterMapper, MemoMapper memoMapper, ModelMapper modelMapper, KasamuRepository kasamuRepository) {
        this.templateEngine = templateEngine;
        this.receivedLetterService = receivedLetterService;
        this.userMgmtServiceData = userMgmtServiceData;
        this.tokenProcessorService = tokenProcessorService;
        this.convertHtlToFileProxy = convertHtlToFileProxy;
        this.dateConverter = dateConverter;
        this.numericConverter = numericConverter;
        this.dispatchLetterService = dispatchLetterService;
        this.receivedLetterMapper = receivedLetterMapper;
        this.dispatchLetterMapper = dispatchLetterMapper;
        this.memoMapper = memoMapper;
        this.modelMapper = modelMapper;
        this.kasamuRepository = kasamuRepository;
    }

    @Override
    public byte[] generatePdfForInbox(GetRowsRequest paginatedRequest, String lang) {
        paginatedRequest.setLimit(10);
        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojoPage = receivedLetterService.filterData(paginatedRequest);
        StringBuilder html;
        try {
            html = getBodyContain(lang, receivedLetterResponsePojoPage);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("EN")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
    }

    @Override
    public byte[] getDartaReportPdf(GetRowsRequest paginatedRequest, String lang) {
        Page<ReceivedLetterResponsePojo> receivedLetterResponsePojoPage = getDartaReportData(paginatedRequest);
        StringBuilder html;
        try {
            html = getBodyContain(lang, receivedLetterResponsePojoPage);
        } catch (Exception e) {
            throw new CustomException(e.getMessage());
        }

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("EN")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
    }

    @Override
    public byte[] getChalaniReportPdf(GetRowsRequest paginatedRequest, String lang) {

        // todo check
        Page<DispatchLetterResponsePojo> chalaniData = getChalaniReportData(paginatedRequest);

        StringBuilder html = getBodyForChalani(lang, chalaniData);

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("EN")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }

        //todo check
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
    }

    @Override
    public byte[] getTippaniReportPdf(GetRowsRequest paginatedRequest, String lang) {

        Page<MemoResponsePojo> page = getTippaniReportData(paginatedRequest);

        StringBuilder html = getBodyForTippani(lang, page);

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("EN")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);

    }

    @Override
    public byte[] getKasamuReportPdf(GetRowsRequest paginatedRequest, String lang) {
        KasamuResponsePaginationPojo page = getKasamuReportData(paginatedRequest);

        StringBuilder html = getBodyForKasamu(lang, page);

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("EN")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
    }

    @Override
    public Page<ReceivedLetterResponsePojo> getDartaReportData(GetRowsRequest paginationRequest) {
        long startTime = System.nanoTime();
        Page<ReceivedLetterResponsePojo> page = new Page<>(paginationRequest.getPage(), paginationRequest.getLimit());

        page = receivedLetterMapper.getDartaReportData(page, tokenProcessorService.getOfficeCode(), paginationRequest.getSearchField());

       page.getRecords().parallelStream().forEach(data -> {
            data.setCreatedDateBs(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            data.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
            if (data.getDispatchDateEn() != null)
                data.setDispatchDateBs(dateConverter.convertAdToBs(data.getDispatchDateEn().toString()));
            if (data.getDispatchDateNp() != null)
                data.setDispatchDateNp(dateConverter.convertBSToDevnagari(data.getDispatchDateNp()));

            OfficeMinimalPojo officePojo = userMgmtServiceData.getOfficeDetailMinimal(data.getSenderOfficeCode());
            data.setSenderOfficeName(officePojo != null ? officePojo.getNameEn() : null);
            data.setSenderOfficeNameNp(officePojo != null ? officePojo.getNameNp() : null);
            data.setOfficeDetails(userMgmtServiceData.getOfficeDetail(data.getSenderOfficeCode()));

            if (data.getLetterType() != null && data.getLetterType().equals("Receive")) {
                ReceivedLetterDetailResponsePojo details = receivedLetterMapper.getDetails(data.getId());
                data.setDetails(details);
            }
        });
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000;
        return page;
    }

    @Override
    public Page<DispatchLetterResponsePojo> getChalaniReportData(GetRowsRequest paginationRequest) {
        Page<DispatchLetterResponsePojo> page = new Page<>(paginationRequest.getPage(), paginationRequest.getLimit());

        page = dispatchLetterMapper.getChalaniReportData(page, tokenProcessorService.getOfficeCode(), paginationRequest.getSearchField());

        page.getRecords().forEach(data -> {

            if (data.getDispatchDateNp() != null)
                data.setDispatchDateNp(dateConverter.convertBSToDevnagari(data.getDispatchDateNp()));
            if (data.getDispatchDateEn() != null)
                data.setDispatchDateBs(dateConverter.convertAdToBs(data.getDispatchDateEn().toString()));

            if (data.getSenderOfficeCode() != null && !data.getSenderOfficeCode().equals("")) {
                OfficePojo officePojo = userMgmtServiceData.getOfficeDetail(data.getSenderOfficeCode());
                data.setSenderOfficeDetail(officePojo);
            }

            if (data.getSenderPisCode() != null) {
                EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(data.getSenderPisCode());

                data.setSenderDetail(employeePojo);
            }
        });

        return page;
    }

    @Override
    public Page<MemoResponsePojo> getTippaniReportData(GetRowsRequest paginationRequest) {

        Page<MemoResponsePojo> page = new Page<>(paginationRequest.getPage(), paginationRequest.getLimit());

        page = memoMapper.getTippaniReportData(page, tokenProcessorService.getOfficeCode(), paginationRequest.getSearchField());

        page.getRecords().forEach(data -> {
            if (data.getCreatedDate() != null) {
                data.setCreatedDateBs(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
                data.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(data.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
            }
            if (data.getOfficeCode() != null) {
                OfficePojo officePojo = userMgmtServiceData.getOfficeDetail(data.getOfficeCode());
                if (officePojo != null) {
                    OfficePojo officePojo1 = new OfficePojo();
                    officePojo1.setNameNp(officePojo.getNameNp());
                    officePojo1.setNameEn(officePojo.getNameEn());

                    data.setCreatorOfficeDetails(officePojo1);
                }
            }
            if (data.getPisCode() != null) {
                EmployeePojo employeePojo = userMgmtServiceData.getEmployeeDetail(data.getPisCode());
                data.setCreator(employeePojo);
            }
        });
        return page;
    }

    @Override
    public KasamuResponsePaginationPojo getKasamuReportData(GetRowsRequest paginationRequest) {
        String officeCode = tokenProcessorService.getOfficeCode();
        KasamuResponsePaginationPojo pagination = new KasamuResponsePaginationPojo();
        Pageable pageable = PageRequest.of(paginationRequest.getPage(), paginationRequest.getLimit());
        String activeFiscalYearCode = userMgmtServiceData.findActiveFiscalYear().getCode();
        List<Kasamu> list = kasamuRepository.getByOffice(officeCode, activeFiscalYearCode, pageable);
        Integer total = kasamuRepository.countByOffice(officeCode, activeFiscalYearCode);
        int page = paginationRequest.getPage();
        int limit = paginationRequest.getLimit();
        pagination.setCurrent(page);
        pagination.setSize(limit);
        pagination.setPages((int) Math.ceil(((double) total / limit)));
        pagination.setTotal(total);
        pagination.setRecords(list.stream().map(x -> modelMapper.map(x, KasamuResponsePojo.class)).collect(Collectors.toList()));
        pagination.getRecords().stream().forEach(x -> {
            EmployeePojo creator = userMgmtServiceData.getEmployeeDetail(x.getPisCode());
            EmployeePojo employee = userMgmtServiceData.getEmployeeDetail(x.getEmployeePisCode());
            x.setCreator(creator);
            x.setEmployee(employee);
            x.setCreatedDateBs(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString()));
            x.setCreatedDateNp(dateConverter.convertBSToDevnagari(dateConverter.convertAdToBs(x.getCreatedDate().toLocalDateTime().toLocalDate().toString())));
        });

        return pagination;
    }

    @Override
    public Map<String, Object> getDartaReportSearchRecommendation() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<DartaSearchRecommendationDto> dartaSearchRecommendationData = receivedLetterMapper.getDartaReportSearchRecommendation(officeCode);
        Map<String, Object> response = new HashMap<>();
        // extracting unique data for every column returned from the above query
        List<Map<String, Object>> uniqueManualSenderName = dartaSearchRecommendationData.stream()
                .filter(obj -> obj.getManualSenderName() != null && !obj.getManualSenderName().isEmpty() && !obj.getManualSenderName().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> {
                            Map<String, Object> tempMap = new HashMap<>();
                            tempMap.put("name", obj.getManualSenderName());
                            return tempMap;
                        }
                )
                .distinct()
                .collect(Collectors.toList());

        List<Map<String, Object>> uniqueSenderOfficeCode = dartaSearchRecommendationData.stream()
                .filter(obj -> obj.getSenderOfficeCode() != null && !obj.getSenderOfficeCode().isEmpty() && !obj.getSenderOfficeCode().equals("na"))
                .collect(Collectors.toList())
                .stream()
                .map(obj -> {
                    Map<String, Object> tempMap = new HashMap<>();
                    tempMap.put("senderOfficeCode", obj.getSenderOfficeCode());
                    return tempMap;
                })
                .distinct()
                .collect(Collectors.toList());

        // building response body
        List<Map<String, Object>> manualSenderDetails = uniqueManualSenderName.stream().map(obj -> {
            Map<String, Object> tempSenderDetail = new HashMap<>();
            tempSenderDetail.put("name", obj.get("name"));
            return tempSenderDetail;
        })
                .collect(Collectors.toList());

        List<Map<String, Object>> senderDetails = uniqueSenderOfficeCode.stream().map(obj -> {

            Map<String, Object> tempOfficeDetail = new HashMap<>();
            OfficeMinimalPojo senderOfficeCode = userMgmtServiceData.getOfficeDetailMinimal((String) obj.get("senderOfficeCode"));
            if (senderOfficeCode == null) {
                return null;
            }
            tempOfficeDetail.put("officeNameEn", senderOfficeCode.getNameEn());
            tempOfficeDetail.put("officeNameNp", senderOfficeCode.getNameNp());
            tempOfficeDetail.put("officeCode", senderOfficeCode.getCode());
            return tempOfficeDetail;
        })
                .collect(Collectors.toList());

        response.put("manualSenderDetails", manualSenderDetails);
        response.put("senderDetails", senderDetails);
        return response;
    }

    @Override
    public Map<String, Object> getChalaniReportSearchRecommendation() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<SearchRecommendationDto> chalaniSearchRecommendation = dispatchLetterMapper.getChalaniReportSearchRecommendation(officeCode);
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> senderDetails = chalaniSearchRecommendation.stream().map(obj -> {

            Map<String, Object> tempOfficeDetail = new HashMap<>();
            OfficeMinimalPojo senderOfficeCode = userMgmtServiceData.getOfficeDetailMinimal(obj.getSenderOfficeCode());
            if (senderOfficeCode == null) {
                return null;
            }
            tempOfficeDetail.put("officeNameEn", senderOfficeCode.getNameEn());
            tempOfficeDetail.put("officeNameNp", senderOfficeCode.getNameNp());
            tempOfficeDetail.put("officeCode", senderOfficeCode.getCode());
            return tempOfficeDetail;
        })
                .collect(Collectors.toList());

        response.put("senderDetails", senderDetails);

        return response;
    }

    @Override
    public Map<String, Object> getTippaniReportSearchRecommendation() {
        String officeCode = tokenProcessorService.getOfficeCode();
        List<SearchRecommendationDto> tippaniReportSearchRecommendation = memoMapper.getTippaniReportSearchRecommendation(officeCode);
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> senderDetails = tippaniReportSearchRecommendation.stream().map(obj -> {

            Map<String, Object> tempOfficeDetail = new HashMap<>();
            OfficeMinimalPojo senderOfficeCode = userMgmtServiceData.getOfficeDetailMinimal(obj.getSenderOfficeCode());
            if (senderOfficeCode == null) {
                return null;
            }
            tempOfficeDetail.put("officeNameEn", senderOfficeCode.getNameEn());
            tempOfficeDetail.put("officeNameNp", senderOfficeCode.getNameNp());
            tempOfficeDetail.put("officeCode", senderOfficeCode.getCode());
            return tempOfficeDetail;
        })
                .collect(Collectors.toList());

        response.put("senderDetails", senderDetails);
        return response;
    }

    @Override
    public byte[] generatePdfFoChalani(GetRowsRequest paginatedRequest, String lang) {
        paginatedRequest.setOfficeCode(tokenProcessorService.getOfficeCode());
        paginatedRequest.setLimit(1000);
        Page<DispatchLetterResponsePojo> chalaniData = dispatchLetterService.filterData(paginatedRequest);

        StringBuilder html = getBodyForChalani(lang, chalaniData);

        Context context = new Context();
        StringBuilder heading = new StringBuilder();
        if (lang.equalsIgnoreCase("EN")) {
            heading.append("NEPAL GOVERNMENT <br>");
        } else {
            heading.append("नेपाल सरकार <br>");
        }
        List<String> topOfficeDetails = topOfficeDetails(tokenProcessorService.getOfficeCode(), new ArrayList<>(), lang, 1);
        for (int i = topOfficeDetails.size() - 1; i >= 0; i--) {
            if (i == topOfficeDetails.size() - 2) {
                heading.append("<span style=\"font-size:20px\">").append(topOfficeDetails.get(i)).append("</span>");
            } else {
                heading.append(topOfficeDetails.get(i));
            }
            heading.append("<br>");
        }
//        heading.append(topOfficeDetails.get(topOfficeDetails.size()-1));
        context.setVariable("header", heading.toString());
        context.setVariable("body", html);
        String process = templateEngine.process("header.html", context);
        FileConverterPojo fileConverterPojo = new FileConverterPojo(process);
        return convertHtlToFileProxy.getFileConverter(fileConverterPojo);
    }

    private StringBuilder getBodyForChalani(String lang, Page<DispatchLetterResponsePojo> chalaniData) {
        StringBuilder html = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        html.append("<h4 style = \"text-align: center;\">चिठ्ठी पुर्जी चलानी किताब</h4>");
        if (!lang.equalsIgnoreCase("EN")) {
            html.append("<thead><tr>" +
                    "<th>क्र.सं.</th>" +
//                    "<th>दर्ता नम्बर </th>" +
//                    "<th>दर्ता मिति (वि.सं.)</th>" +
                    "<th>चलानी नम्बर</th>" +
                    "<th>पत्र संख्या</th>" +
                    "<th>चलानी मिति (वि.सं.)</th>" +

//                    "<th>गोपनियता</th>" +
//                    "<th>प्राथमिकता</th>" +
                    "<th>पत्रको चलान हुने कार्यालयको नाम</th>" +
                    "<th>पत्र बनाउने ब्यक्तिको नाम</th>" +
                    "<th>विषय</th>" +
                    "<th>स्थिती</th>" +
                    "</tr><thead><tbody style=\"font-size: 12px;\">");
            chalaniData.getRecords().forEach(obj -> {
                html.append("<tr> <td>").append(numericConverter.convertEnglishNumbersToNepaliNumbers(count.get() + "")).append("</td>")
                        .append("<td>").append(obj.getDispatchNo() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getDispatchNo())).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getReferenceCode() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getReferenceCode()).substring(1)).append("</td>")
                        .append("<td>").append(obj.getDispatchDateNp()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        //.append("<td>").append(setReceiverName(obj)).append("</td>")
                        .append("<td>").append(obj.getSenderOfficeDetail() != null ? obj.getSenderOfficeDetail().getNameNp() : "-").append("</td>")
                        .append("<td>").append(obj.getSenderDetail() != null ? obj.getSenderDetail().getNameNp() : "-").append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>")
                        .append("<td>").append(obj.getDlStatus() != null ? obj.getDlStatus().getValueNepali() : "-").append("</td>");
                count.getAndIncrement();
            });
        } else {
            html.append("<tr>" +
                    "<th>S.N</th>" +
//                    "<th>Darta No.</th>" +
//                    "<th>Darta Date (B.S.)</th>" +
                    "<th>Dispatch No.</th>" +
                    "<th>Reference No.</th>" +
                    "<th>Dispatch Date (B.S.)</th>" +
//                    "<th>Letter Privacy</th>" +
//                    "<th>Letter Priority</th>" +
                    "<th>Sender Office</th>" +
                    "<th>Creator Name</th>" +
                    "<th>Subject</th>" +
                    "<th>Status</th></tr>");

            chalaniData.getRecords().forEach(obj -> {
                html.append("<tr> <td>").append(count.get()).append("</td>")
                        .append("<td>").append(obj.getDispatchNo() == null ? "-" : obj.getDispatchNo()).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getReferenceCode() == null ? "-" : obj.getReferenceCode()).append("</td>")
                        .append("<td>").append(obj.getDispatchDateBs()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        //.append("<td>").append(obj.getCreator().getNameEn()).append("</td>")
                        .append("<td>").append(obj.getSenderOfficeDetail() != null ? obj.getSenderOfficeDetail().getNameEn() : "-").append("</td>")
                        .append("<td>").append(obj.getSenderDetail() != null ? obj.getSenderDetail().getNameEn() : "-").append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>")
                        .append("<td>").append(obj.getDlStatus() != null ? obj.getDlStatus().getValueEnglish() : "-").append("</td>");
                count.getAndIncrement();
            });
        }
        html.append("</tbody>");
        return html;
    }

    private StringBuilder getBodyForTippani(String lang, Page<MemoResponsePojo> chalaniData) {
        StringBuilder html = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        html.append("<h4 style = \"text-align: center;\">चिठ्ठी पुर्जी टिप्पणी किताब</h4>");
        if (!lang.equalsIgnoreCase("EN")) {
            html.append("<thead><tr>" +
                    "<th>क्र.सं.</th>" +
                    "<th>टिप्पणी नं </th>" +
//                   "<th>टिप्पणी मिति (वि.सं.)</th>" +
//                    "<th>चलानी नम्बर</th>" +
                    "<th>पत्र संख्या</th>" +
                    "<th>टिप्पणी मिति (वि.सं.) </th>" +

//                    "<th>गोपनियता</th>" +
//                    "<th>प्राथमिकता</th>" +
                    "<th>टिप्पणी पठाउने कार्यालयको नाम</th>" +
                    "<th>टिप्पणी बनाउने ब्यक्तिको नाम</th>" +
                    "<th>विषय</th>" +
                    "<th>स्थिती</th></tr><thead><tbody style=\"font-size: 12px;\">");
            chalaniData.getRecords().forEach(obj -> {
                html.append("<tr> <td>").append(numericConverter.convertEnglishNumbersToNepaliNumbers(count.get() + "")).append("</td>")
                        .append("<td>").append(obj.getTippaniNo() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(String.valueOf(obj.getTippaniNo()))).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getReferenceNo() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getReferenceNo()).substring(1)).append("</td>")
                        .append("<td>").append(obj.getCreatedDateNp()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        //.append("<td>").append(setReceiverName(obj)).append("</td>")
                        .append("<td>").append(obj.getCreatorOfficeDetails() != null ? obj.getCreatorOfficeDetails().getNameNp() : "-").append("</td>")
                        .append("<td>").append(obj.getCreator() != null ? obj.getCreator().getNameNp() : "-").append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>")
                        .append("<td>").append(obj.getStatus() != null ? obj.getStatus().getValueNepali() : "-").append("</td>");
                count.getAndIncrement();
            });
        } else {
            html.append("<tr>" +
                    "<th>S.N</th>" +
                    "<th>Memo No.</th>" +
//                    "<th>Darta Date (B.S.)</th>" +
//                    "<th>Dispatch No.</th>" +
                    "<th>Reference No.</th>" +
                    "<th>Memo Date (B.S.)</th>" +
//                    "<th>Letter Privacy</th>" +
//                    "<th>Letter Priority</th>" +
                    "<th>Sender Office</th>" +
                    "<th>Creator Name</th>" +
                    "<th>Subject</th>" +
                    "<th>Status</th></tr>");

            chalaniData.getRecords().forEach(obj -> {
                html.append("<tr> <td>").append(count.get()).append("</td>")
                        .append("<td>").append(obj.getTippaniNo() == null ? "-" : obj.getTippaniNo()).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getReferenceNo() == null ? "-" : obj.getReferenceNo()).append("</td>")
                        .append("<td>").append(obj.getCreatedDateBs()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        //.append("<td>").append(obj.getCreator().getNameEn()).append("</td>")
                        .append("<td>").append(obj.getCreatorOfficeDetails() != null ? obj.getCreatorOfficeDetails().getNameEn() : "-").append("</td>")
                        .append("<td>").append(obj.getCreator() != null ? obj.getCreator().getNameEn() : "-").append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>")
                        .append("<td>").append(obj.getStatus() != null ? obj.getStatus().getValueEnglish() : "-").append("</td>");
                count.getAndIncrement();
            });
        }
        html.append("</tbody>");
        return html;
    }

    private StringBuilder getBodyForKasamu(String lang, KasamuResponsePaginationPojo kasamuData) {
        StringBuilder html = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        html.append("<h4 style = \"text-align: center;\">का. स. मु. रिपोर्ट</h4>");
        if (!lang.equalsIgnoreCase("EN")) {
            html.append("<thead><tr>" +
                    "<th>क्र.सं.</th>" +
                    "<th>दर्ता नम्बर</th>" +
                    "<th>दर्ता मिति (वि.सं.)</th>" +
                    "<th>क.सं.नं</th>" +
//                    "<th>गोपनियता</th>" +
//                    "<th>प्राथमिकता</th>" +
                    "<th>पेश गर्ने कर्मचारी</th>" +
                    "<th>दर्ता गर्ने कर्मचारी</th>" +
                    "<th>विषय</th>" +
                   // "<th>स्थिती</th>" +
                    "</tr><thead><tbody style=\"font-size: 12px;\">");
            kasamuData.getRecords().forEach(obj -> {
                html.append("<tr> <td>").append(numericConverter.convertEnglishNumbersToNepaliNumbers(count.get() + "")).append("</td>")
                        .append("<td>").append(obj.getRegistrationNo() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(String.valueOf(obj.getRegistrationNo()))).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getCreatedDateBs() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getCreatedDateBs()).substring(1)).append("</td>")
                        .append("<td>").append(obj.getEmployeePisCode()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        //.append("<td>").append(setReceiverName(obj)).append("</td>")
                        .append("<td>").append(obj.getEmployee() != null ? obj.getEmployee().getNameNp() : "-").append("</td>")
                        .append("<td>").append(obj.getCreator() != null ? obj.getCreator().getNameNp() : "-").append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>");
                       // .append("<td>").append(obj.getStatus() != null ? obj.getStatus().getValueNepali() : "-").append("</td>");
                count.getAndIncrement();
            });
        } else {
            html.append("<tr>" +
                    "<th>S.N</th>" +
                    "<th>Darta No.</th>" +
                    "<th>Darta Date (B.S.)</th>" +
                    "<th>Employee Piscode</th>" +
//                    "<th>Letter Privacy</th>" +
//                    "<th>Letter Priority</th>" +
                    "<th>Ka Sa Mu Person</th>" +
                    "<th>Ka Sa Mu Creator</th>" +
                    "<th>Subject</th>" +
                    //"<th>Status</th>" +
                    "</tr>");

            kasamuData.getRecords().forEach(obj -> {
                html.append("<tr> <td>").append(count.get()).append("</td>")
                        .append("<td>").append(obj.getRegistrationNo() == null ? "-" : obj.getRegistrationNo()).append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getCreatedDate() == null ? "-" : obj.getCreatedDate()).append("</td>")
                        .append("<td>").append(obj.getEmployeePisCode()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        //.append("<td>").append(obj.getCreator().getNameEn()).append("</td>")
                        .append("<td>").append(obj.getEmployee() != null ? obj.getEmployee().getNameEn() : "-").append("</td>")
                        .append("<td>").append(obj.getCreator() != null ? obj.getCreator().getNameEn() : "-").append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>");
                        //.append("<td>").append(obj.getStatus() != null ? obj.getStatus().getValueEnglish() : "-").append("</td>");
                count.getAndIncrement();
            });
        }
        html.append("</tbody>");
        return html;
    }

    private String setReceiverName(DispatchLetterResponsePojo obj) {
        StringBuilder value = new StringBuilder();
        if (obj.getDispatchLetterExternal() != null)
            obj.getDispatchLetterExternal().forEach(obj1 -> value.append(obj1.getReceiverName()).append(", "));
        if (obj.getDispatchLetterInternal() != null) {
            obj.getDispatchLetterInternal().forEach(obj1 -> value.append(obj1.getEmployeeNameNp()).append(", "));
        }
        if (value.length() == 0) {
            value.append("-");
        } else {
            value.replace(value.length() - 2, value.length() - 1, " ");
        }
        return value.toString();
    }

    private StringBuilder getBodyContain(String lang, Page<ReceivedLetterResponsePojo> receivedLetterResponsePojoPage) {
        StringBuilder html = new StringBuilder();
        AtomicInteger count = new AtomicInteger(1);
        html.append("<h4 style = \"text-align: center;\">चिठ्ठी पुर्जी दर्ता किताब</h4>");
        if (lang.equalsIgnoreCase("EN")) {
            html.append("<tr>" +
                    "<th>S.N</th>" +
                    "<th>Darta No.</th>" +
                    "<th>Darta Date (B.S.)</th>" +
//                    "<th>Dispatch No.</th>" +
                    "<th>Reference No.</th>" +
                    "<th>Dispatch Date (B.S.)</th>" +
//                    "<th>Letter Privacy</th>" +
//                    "<th>Letter Priority</th>" +
                    "<th>Sender Individual/Office</th>" +
                    "<th>Subject</th>" +
                    "<th>Status</th></tr>");
            receivedLetterResponsePojoPage.getRecords().forEach(obj -> {
                String senderName = "";
                if (obj.getEntryType() != null && obj.getEntryType()) {
                    senderName = obj.getDetails().getManualSenderName();
                } else {
                    senderName = obj.getSenderOfficeName();
                }
                html.append("<tr> <td>").append(count.get()).append("</td>")
                        .append("<td>").append(obj.getRegistrationNo() != null ? obj.getRegistrationNo() : "-").append("</td>")
                        .append("<td>").append(obj.getCreatedDateBs() != null ? obj.getDispatchDateBs() : "-").append("</td>")
//                        .append("<td>").append(obj.getDispatchNo()).append("</td>")
                        .append("<td>").append(obj.getReferenceNo() != null ? obj.getReferenceNo() : "-").append("</td>")
                        .append("<td>").append(obj.getDispatchDateBs() != null ? obj.getDispatchDateBs() : "-").append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueEnglish()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueEnglish()).append("</td>")
                        .append("<td>").append(senderName).append("</td>")
                        .append("<td>").append(obj.getSubject()).append("</td>")
                        .append("<td>").append(obj.getStatus() != null ? obj.getStatus().getValueEnglish() : "-").append("</td>");
                count.getAndIncrement();
            });
        } else {
           try {
               html.append("<thead><tr>" +
                       "<th>क्र.सं.</th>" +
                       "<th>दर्ता नम्बर </th>" +
                       "<th>दर्ता मिति (वि.सं.)</th>" +
                       "<th>चलानी नम्बर</th>" +
                       "<th>पत्र संख्या</th>" +
                       "<th>पत्रको मिति (वि.सं.)</th>" +
//                    "<th>गोपनियता</th>" +
//                    "<th>प्राथमिकता</th>" +
                       "<th>पठाउने व्यक्ति/कार्यालय</th>" +
                       "<th>विषय</th>" +
                       "<th>स्थिती</th>" +
                       "</tr></thead><tbody style=\"font-size: 12px;\">");
               receivedLetterResponsePojoPage.getRecords().forEach(obj -> {
                   String senderName = "";
                   if (obj.getEntryType() != null && obj.getEntryType()) {
                       senderName = obj.getDetails() == null ? "N/A" :obj.getDetails().getManualSenderName();
                   } else {
                       senderName = obj.getSenderOfficeNameNp();
                   }
                   html.append("<tr> <td>").append(numericConverter.convertEnglishNumbersToNepaliNumbers(count.get() + "")).append("</td>")
                           .append("<td>").append(obj.getRegistrationNo()).append("</td>")
                           .append("<td>").append(obj.getCreatedDateNp()).append("</td>")
                           .append("<td>").append(obj.getDispatchNo() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getDispatchNo())).append("</td>")
                           .append("<td>").append(obj.getReferenceNo() == null ? "-" : numericConverter.convertEnglishNumbersToNepaliNumbers(obj.getReferenceNo())).append("</td>")
                           .append("<td>").append(obj.getDispatchDateNp()).append("</td>")
//                        .append("<td>").append(obj.getLetterPrivacy().getEnum().getValueNepali()).append("</td>")
//                        .append("<td>").append(obj.getLetterPriority().getEnum().getValueNepali()).append("</td>")
                           .append("<td>").append(senderName).append("</td>")
                           .append("<td>").append(obj.getSubject()).append("</td>")
                           .append("<td>").append(obj.getStatus().getValueNepali()).append("</td>");
                   count.getAndIncrement();
               });
               html.append("</tbody>");
           } catch (Exception exception) {
               exception.printStackTrace();
           }
        }

        html.append("</tr>");
        return html;
    }

    private List<String> topOfficeDetails(String officeCOde, List<String> value, String type, int count) {
        OfficePojo officeDetail = userMgmtServiceData.getOfficeDetail(officeCOde);
        if (count == 1) {
            value.add(type.equalsIgnoreCase("EN") ? officeDetail.getAddressEn() : officeDetail.getAddressNp());
        }
        value.add(type.equalsIgnoreCase("EN") ? officeDetail.getNameEn() : officeDetail.getNameNp());

        if (officeDetail.getParentCode() == null || officeDetail.getParentCode().equalsIgnoreCase("8886")) {
            return value;
        }
        return topOfficeDetails(officeDetail.getParentCode(), value, type, ++count);
    }
}
