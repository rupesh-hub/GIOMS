//package com.gerp.attendance.generator;
//
//import com.gerp.attendance.model.approval.DecisionApproval;
//import org.hibernate.Session;
//import org.hibernate.tuple.ValueGenerator;
//
//public class RecordIdGenerator implements ValueGenerator<Long> {
//
//    @Override
//    public Long generateValue(Session session, Object owner) {
//        DecisionApproval decisionApproval = (DecisionApproval) owner;
//        Long recordId = Long.valueOf(0);
//        switch (decisionApproval.getCode()){
//            case LR:
//                recordId = decisionApproval.getLeaveRequestDetail().getRecordId();
//                break;
//            case KR:
//                break;
//            default:
//                break;
//        }
//        return recordId;
//    }
//}
