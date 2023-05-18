package com.gerp.sbm.repo;

import com.gerp.sbm.model.sampati.SampatiMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface SampatiMasterRepo extends JpaRepository<SampatiMaster,Long> {
    @Query(nativeQuery = true,value = "select fa.piscode as pis,fa.content as fa_content ,bd.content as bd_content,ad.content as ad_content,\n" +
            "\t   l.content as loan_content,od.content as od_content,ss.content as share_content ,vi.content as vi_content,\n" +
            "\t   v.content as v_content\n" +
            "from fixed_asset fa \n" +
            "\tleft join  bank_details bd on(bd.piscode=fa.piscode) \n" +
            "\tleft join agriculture_detail ad on(ad.piscode=fa.piscode) \n" +
            "\tleft join loan l on(l.piscode=fa.piscode)  \n" +
            "\tleft join other_detail od on(od.piscode=fa.piscode) \n" +
            "\tleft join share ss on(ss.piscode=fa.piscode)\n" +
            "\tleft join valuable_items vi on(vi.piscode=fa.piscode) \n" +
            "\tleft join vehicle v on (v.piscode=fa.piscode) \n" +
            "\twhere fa.piscode =?1")
    List<Map<String, Object>> findOtherProperties(String piscode);

    @Query(nativeQuery = true,value = "select * from sampati_master where emp_isp_code=?1")
    Optional<SampatiMaster> findDetails(String piscode);

    @Query(nativeQuery = true,value = "select * from sampati_master where emp_isp_code=?1 and fiscal_year_code=?2")
    Optional<SampatiMaster> findSampatiBibaran(String piscode, String fiscal_year_code);


    @Query(nativeQuery = true,value = "select distinct emp_pis_code from sampati_master where fiscal_year_eng=?1 and fiscal_year_nep=?2")
    List<String> findAlreadyExistUser(String fiscal_year_eng,String fiscal_year_nep);

}
