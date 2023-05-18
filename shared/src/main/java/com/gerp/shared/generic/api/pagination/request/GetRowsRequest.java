package com.gerp.shared.generic.api.pagination.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.gerp.shared.enums.OrderBy;
import com.gerp.shared.generic.api.pagination.filter.CustomFilter;
import com.gerp.shared.generic.api.pagination.ordertype.OrderType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyList;

@AllArgsConstructor
@Data
public class GetRowsRequest implements Serializable {

    private Set<String> sectionCodes;

    private Map<String, Object> searchField;

    private int page, limit;
    private Long fiscalYear;
    private Long role;
    private String year;
    private Boolean isApprover = false;
    private Boolean selfCreatedUser;
    private Boolean forReport = false;
    private Boolean forReportDetail = false;


    private Boolean userStatus;
    private String month;

    private Boolean forLeave = false;
    private Boolean forDaily = false;
    private Boolean forKaaj = false;
    private Boolean forHoliday = false;

    private String pisCode;
    private Long sectionId;
    private Integer officeTypeId;
    private String designationCode;
    private String districtCode;
    private String officeName;
    private String sectionPisCode;
    private Boolean giomsActive;
    private String officeCode;
    private String approverPisCode;
    private String report;
    private String nameEn;
    private String nameNp;
    private String name;
    private String serviceStatusCode;
    private String transferStatus;
    private String targetOfficeCode;
    private String requestedOfficeCode;
    /* expiring date
    * */
    private Integer days;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    // Darta Chalani : Type of letter
    private String letterType;
    private Long id;

    private String attendanceStatus;

    //not used
    // row group columns
    private List<ColumnVO> rowGroupCols;

    // value columns
    private List<ColumnVO> valueCols;

    // pivot columns
    private List<ColumnVO> pivotCols;

    // true if pivot mode is one, otherwise false
    private boolean pivotMode;

    // what groups the user is viewing
    private List<String> groupKeys;
    //not used

    // if filtering, what the filter model is
//    private Map<String, ColumnFilter> filterModel;
    private List<CustomFilter> filterModel;

    // if sorting, what the sort model is
    private List<SortModel> sortModel;

    //for darta chalani pagination api sorting
    private int orderBy;
    private OrderType orderType;

    private Boolean manualLeave = Boolean.FALSE;

    public GetRowsRequest() {
        this.rowGroupCols = emptyList();
        this.valueCols = emptyList();
        this.pivotCols = emptyList();
        this.groupKeys = emptyList();
        this.filterModel = emptyList();
        this.sortModel = emptyList();
        this.page = 1;
        this.limit = 10;
    }

    public int getPage() {
        return page;
    }
}
