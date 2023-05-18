package com.gerp.dartachalani.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gerp.dartachalani.dto.DispatchLetterResponsePojo;
import com.gerp.dartachalani.dto.MemoResponsePojo;
import com.gerp.dartachalani.dto.ReceivedLetterResponsePojo;
import com.gerp.dartachalani.dto.kasamu.KasamuResponsePaginationPojo;
import com.gerp.shared.generic.api.pagination.request.GetRowsRequest;

import java.util.Map;

public interface PdfGeneratorService {

    byte[] generatePdfForInbox(GetRowsRequest paginatedRequest, String lang);

    byte[] getDartaReportPdf(GetRowsRequest paginatedRequest, String lang);

    byte[] getChalaniReportPdf(GetRowsRequest paginatedRequest, String lang);

    byte[] getTippaniReportPdf(GetRowsRequest paginatedRequest, String lang);

    byte[] getKasamuReportPdf(GetRowsRequest paginatedRequest, String lang);

    Page<ReceivedLetterResponsePojo> getDartaReportData(GetRowsRequest paginationRequest);

    Page<DispatchLetterResponsePojo> getChalaniReportData(GetRowsRequest paginationRequest);

    Page<MemoResponsePojo> getTippaniReportData(GetRowsRequest paginationRequest);

    KasamuResponsePaginationPojo getKasamuReportData(GetRowsRequest paginationRequest);

    Map<String, Object> getDartaReportSearchRecommendation();

    Map<String, Object> getChalaniReportSearchRecommendation();

    Map<String, Object> getTippaniReportSearchRecommendation();


    byte[] generatePdfFoChalani(GetRowsRequest paginatedRequest, String lang);
}
