package com.gerp.usermgmt.util;

import com.gerp.shared.configuration.CustomMessageSource;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.usermgmt.converter.organiztion.employee.EmployeeConverter;
import com.gerp.usermgmt.model.employee.Employee;
import com.gerp.usermgmt.pojo.organization.employee.EmployeeExcelPojo;
import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ExcelUploadUtils {

    private final EmployeeConverter employeeConverter;

    @Autowired
    private final CustomMessageSource customMessageSource;
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public ExcelUploadUtils(EmployeeConverter employeeConverter, CustomMessageSource customMessageSource) {
        this.employeeConverter = employeeConverter;
        this.customMessageSource = customMessageSource;
    }

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }
    private Optional<String> getExtensionByStringHandling(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    public List<Employee> excelToEmployeeEntity(MultipartFile file) throws IOException {
        List<EmployeeExcelPojo> employeeList = null;
        List<Employee> employeeListData = null;
        String fileName = getExtensionByStringHandling(file.getOriginalFilename()).orElseThrow(() -> new ServiceValidationException(customMessageSource.get("excel.invalid.format")));
           if (fileName.equalsIgnoreCase("xlsx") || fileName.equalsIgnoreCase("xls")) {
               PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings()
                       .addListDelimiter(";")
                       .build();
               try {
                   employeeList = Poiji.fromExcel(file.getInputStream(),
                           PoijiExcelType.XLSX, EmployeeExcelPojo.class,
                           options);
               } catch (Exception ex) {
                   ex.printStackTrace();
                   throw new ServiceValidationException(customMessageSource.get("excel.invalid.format"));
               }
           }


        if(employeeList != null && !employeeList.isEmpty()) {
            employeeListData = employeeList.stream().map(employeeConverter::toEntity).collect(Collectors.toList());
           }
           return employeeListData;


       }

//    public Employee econvertEmployee(EmployeeExcelPojo employeeExcelPojo) {
//        Employee employee = new Employee();
//        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.map(employeeExcelPojo , employee);
//        return employee;
//    }
}
