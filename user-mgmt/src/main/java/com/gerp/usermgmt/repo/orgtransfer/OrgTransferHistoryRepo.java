package com.gerp.usermgmt.repo.orgtransfer;

import com.gerp.shared.generic.api.GenericRepository;
import com.gerp.usermgmt.model.orgtransfer.OrgTransferHistory;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrgTransferHistoryRepo extends GenericRepository<OrgTransferHistory,Long> {

    Optional<OrgTransferHistory> findOrgTransferHistoryByOrgTransferRequestId(@Param("orgTransferRequestId") Long orgTransferRequestId);

    @org.springframework.transaction.annotation.Transactional
    @Query(value = "select e.pis_code ,\n" +
            "        case\n" +
            "        when e.middle_name_np IS NOT NULL \n" +
            "           then concat(e.first_name_en, ' ', e.middle_name_en, ' ', e.last_name_en)\n" +
            "           else concat(e.first_name_en, ' ', e.last_name_en) \n" +
            "        end as name_en,\n" +
            "        case \n" +
            "       when e.middle_name_np IS NOT NULL \n" +
            "           then concat(e.first_name_np, ' ', e.middle_name_np, ' ', e.last_name_np)\n" +
            "           else concat(e.first_name_np, ' ', e.last_name_np) \n" +
            "        end as name_np,oth.from_office_code ,\n" +
            "        oth.target_office_code as target_office_code,oth.office_joining_date_en as source_office_join_date,\n" +
            "        TO_CHAR(oth.expected_join_date_en, 'YYYY-MM-DD') as ex_end_joining_date_en,\n" +
            "        oth.expected_join_date_np as end_joing_date_np,\n" +
            "        (select o.name_en as org_name_en from office o where o.code=oth.from_office_code) as source_office_en,\n" +
            "        (select o.name_np as org_name_en from office o where o.code=oth.from_office_code) as source_office_np,\n" +
            "        (select os.name_en as org_name_np from office os where os.code=oth.target_office_code) as target_office_en,\n" +
            "        (select os.name_np as org_name_np from office os where os.code=oth.target_office_code) as target_office_np,\n" +
            "        fd.name_en as fd_name_en,fd.name_np as fd_name_np,oth.transfer_type \n" +
            "        from employee e         \n" +
            "        right join org_transfer_history oth on e.pis_code =oth .pis_code \n" +
            "        right  join functional_designation fd on fd.code =e.designation_code  \n" +
            "        where oth .pis_code =?1 order by oth.expected_join_date_en ",nativeQuery = true)
    List<Map<String,Object>> getTransferHistory(String pisCode);

    @Query(value = " select e.pis_code ,\n" +
            "        case\n" +
            "        when e.middle_name_np IS NOT NULL \n" +
            "           then concat(e.first_name_en, ' ', e.middle_name_en, ' ', e.last_name_en)\n" +
            "           else concat(e.first_name_en, ' ', e.last_name_en) \n" +
            "        end as name_en,\n" +
            "        case \n" +
            "       when e.middle_name_np IS NOT NULL \n" +
            "           then concat(e.first_name_np, ' ', e.middle_name_np, ' ', e.last_name_np)\n" +
            "           else concat(e.first_name_np, ' ', e.last_name_np) \n" +
            "        end as name_np,oth.from_office_code ,\n" +
            "       oth.target_office_code as target_office_code,oth.office_joining_date_en as source_office_join_date,\n"+
            "        TO_CHAR(oth.expected_join_date_en, 'YYYY-MM-DD') as ex_end_joining_date_en,\n" +
            "        oth.expected_join_date_np as end_joing_date_np,\n" +
            "        (select o.name_en as org_name_en  from office o where o.code=oth.from_office_code) as source_office_en,\n" +
            "        (select os.name_np as org_name_np  from office os where os.code=oth.from_office_code) as source_office_np,\n" +
            "        (select o.name_en as org_name_en  from office o where o.code=oth.target_office_code) as target_office_en,\n" +
            "        (select os.name_np as org_name_np  from office os where os.code=oth.target_office_code) as target_office_np,\n" +
            "        fd.name_en as fd_name_en,fd.name_np as fd_name_np,oth.transfer_type  \n" +
            "        from employee e         \n" +
            "        right join org_transfer_history oth on e.pis_code =oth .pis_code \n" +
            "        right  join functional_designation fd on fd.code =e.designation_code  \n" +
            "        where oth.pis_code =?1\n" +
            "        and expected_join_date_en <=(select max(expected_join_date_en)  from" +
            "        org_transfer_history oth2 where pis_code=?2 and from_office_code=?3) order by\n" +
            "        expected_join_date_en ",nativeQuery = true)
    List<Map<String,Object>> transferPreAndAfter(String pisCode,String pPisCode,String oOfficeCode);
}
