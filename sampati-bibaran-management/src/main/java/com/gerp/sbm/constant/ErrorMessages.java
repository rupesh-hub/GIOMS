package com.gerp.sbm.constant;

public enum ErrorMessages {

    Id_IS_MISSING("Id is missing"),
    NOT_PERMIT_TO_UPDATE("You are not permitted to update"),
    NOT_PERMIT_TO_DELETE("You are not permitted to delete"),
    VALUABLE_ITEMS_NOT_FOUND("Valuable items details not found"), NOT_PERMIT_TO_GET("You are not permitted to view details"),
    AGRICULTURE_DETAIL_NOT_FOUND("Agro Details not found");

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
