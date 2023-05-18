//package com.gerp.sbm.service.imp;
//
//import com.gerp.sbm.constant.ErrorMessages;
//import com.gerp.sbm.model.assets.AgricultureDetail;
//import com.gerp.sbm.pojo.RequestPojo.AgricultureDetailRequestPojo;
//import com.gerp.sbm.repo.AgricultureDetailRepo;
//import com.gerp.sbm.service.AgricultureService;
//import org.apache.commons.beanutils.BeanUtils;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//@Transactional
//@Service
//public class AgricultureServiceImpl implements AgricultureService {
//
//   private final AgricultureDetailRepo agricultureDetailRepo;
//
//    public AgricultureServiceImpl(AgricultureDetailRepo agricultureDetailRepo) {
//        this.agricultureDetailRepo = agricultureDetailRepo;
//    }
//
//    @Override
//    public Long addAgriculture(AgricultureDetailRequestPojo pojo) {
//        AgricultureDetail agricultureDetail = new AgricultureDetail();
//        convertToAgricultureEntity(pojo, agricultureDetail);
//        agricultureDetailRepo.save(agricultureDetail);
//        return agricultureDetail.getId();
//    }
//
//    @Override
//    public Long updateAgriculture(AgricultureDetailRequestPojo agricultureDetailRequestPojo) {
//        if (agricultureDetailRequestPojo.getId() == null){
//            throw new RuntimeException(ErrorMessages.Id_IS_MISSING.getMessage());
//        }
//        Optional<AgricultureDetail> agricultureDetailOptional = agricultureDetailRepo.findById(agricultureDetailRequestPojo.getId());
//        if (!agricultureDetailOptional.isPresent()){
//            throw new RuntimeException(ErrorMessages.AGRICULTURE_DETAIL_NOT_FOUND.getMessage());
//        }
////        AgricultureDetail agricultureDetail =
//        return null;
//    }
//
//    private void convertToAgricultureEntity(AgricultureDetailRequestPojo pojo, AgricultureDetail agricultureDetail)  {
//       try {
//           BeanUtils.copyProperties(agricultureDetail, pojo);
//       }catch (Exception e){
//           throw new RuntimeException(e.getMessage());
//       }
//    }
//}
