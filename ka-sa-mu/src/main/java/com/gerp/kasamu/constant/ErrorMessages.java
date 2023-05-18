package com.gerp.kasamu.constant;

public enum ErrorMessages {
    REMARKS_IS_MISSING("Remarks is missing"),
    TOTAL_TASK_ESTIMATION_IS_LESS("The total target is less than 100"),
    ID_IS_MISSING("Id is missing"),
    KASAMU_DETAIL_NOT_FOUND("kasamu detail not found"),
    EMPSHRENI_IS_MISSING("Employee shreni is missing"),
    SUPERVISOR_PIS_CODE_MISSING("Supervisor details is missing"),
    OFFICE_CODE_IS_MISSING("Office details is missing"),
    ACHIEVEDMENT_RESULT_MISSING("Achievement result is missing"),
    KASAMU_MASTER_NO_FOUND("Kasamu request not found"),
    REVIEW_ALREADY_GIVEN_SUPERVISOR("Review already given by supervisor"),
    REVIEW_ALREADY_GIVEN_PURNARAWAL("Review already given by evaluator"),
    Evaluator_PIS_CODE_MISSING("Evaluator code is missing"),
    REVIEW_ALREADY_GIVEN_COMMITTEE("Review already given by committee"),
    COMMITTEE_INDICATOR_NOT_FOUND("Committee indicator not found"),
    TOPIC_NOT_FOUND("Topic not found");

    private String message;
    ErrorMessages(String message){
        this.message = message;
    }

    public String getMessage(){
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
