package com.gerp.usermgmt.config.generator;

import com.gerp.shared.configuration.ApplicationContextHolder;
import com.gerp.shared.exception.ServiceValidationException;
import com.gerp.usermgmt.model.employee.Employee;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.beans.beancontext.BeanContext;
import java.io.Serializable;
import java.util.Properties;

@Slf4j
public class EmployeePisCodeGenerator extends SequenceStyleGenerator {
    public static final String VALUE_PREFIX_PARAMETER = "valuePrefix";
    public static final String VALUE_PREFIX_DEFAULT = "";
    private String valuePrefix;

    public static final String NUMBER_FORMAT_PARAMETER = "numberFormat";
    public static final String NUMBER_FORMAT_DEFAULT = "%d";
    private String numberFormat;


    @Override
    public Serializable generate(SharedSessionContractImplementor session,
                                 Object object) throws HibernateException {
        Employee e = (Employee) object;
        PrefixGenerationUtils prefixGenerationUtils = ApplicationContextHolder.getBean(PrefixGenerationUtils.class);
        log.info("prefixGenerationUtils: "+prefixGenerationUtils);
        log.info("employee organization type: " + e.getOrganisationTypeId());
        log.info("employee office code : " + e.getOffice().getCode());

        String prefixOffice = prefixGenerationUtils.getOfficePrefix(e.getOrganisationTypeId(), session, e.getOffice().getCode());
        String prefix = "";
        Employee employee = (Employee) object;
        if (employee.getPisCode() == null) {
            if (employee.getEmployeeServiceStatus() == null) {
                throw new RuntimeException("employee service status cannot be null");
            } else {
                switch (employee.getEmployeeServiceStatus().getCode()) {
                    case EmployeeServiceStatusConstant.PERMANENT_EMPLOYEE_SERVICE_CODE:
                        break;
                    case EmployeeServiceStatusConstant.CONTRACT_EMPLOYEE_SERVICE_CODE:
                        prefix = "KR_";
                        break;
                    case EmployeeServiceStatusConstant.TEMPORARY_EMPLOYEE_SERVICE_CODE:
                        prefix = "TP_";
                        break;
                    case EmployeeServiceStatusConstant.UNASSIGNED_EMPLOYEE_SERVICE_CODE:
                        prefix = "UN_";
                        break;
                    default:
                        throw new ServiceValidationException("Please Select Valid Employee Service");
                }

            }
            return prefixOffice + "_" + prefix + String.format(numberFormat, super.generate(session, object));
        } else {
            return employee.getPisCode();
        }
    }

    @Override
    public void configure(Type type, Properties params,
                          ServiceRegistry serviceRegistry) throws MappingException {
        super.configure(LongType.INSTANCE, params, serviceRegistry);
        valuePrefix = ConfigurationHelper.getString(VALUE_PREFIX_PARAMETER,
                params, VALUE_PREFIX_DEFAULT);
        numberFormat = ConfigurationHelper.getString(NUMBER_FORMAT_PARAMETER,
                params, NUMBER_FORMAT_DEFAULT);
    }

    // k_0001 , k_0002
}
