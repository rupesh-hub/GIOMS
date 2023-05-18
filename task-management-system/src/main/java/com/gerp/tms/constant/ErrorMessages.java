package com.gerp.tms.constant;

public enum ErrorMessages {

    Id_IS_MISSING("Id is missing"),
    PHASE_NOT_FOUND("Phase not found"),
    COMMITTEE_NOT_FOUND("Committee not found"),
    TASK_PROGRESS_STATUS_NOT_FOUND("Task progress status not found"),
    PROJECT_NOT_FOUND("Project not found"),
    TASK_PROGRESS_ALREADY_EXIST("Task status already exist in the system"),
    TASK_NOT_FOUND("Task not found"),
    PROJECT_CANNOT_BE_COMPLETED("Tasks of this project hasn't completed yet."),
    PHASE_CANNOT_BE_COMPLETED("Tasks of this  phase hasn't completed yet."),
    PROJECT_IS_NOT_APPROVED("Project hasn't been approved yet"),
    PROJECT_PHASE_NOT_FOUND("Project doesn't have the entered phase."),
    RATING_NOT_MATCH("Rating cannot be more than five"),
    TASK_ALREADY_HAS_RATING("Rating already given for this task"),
    TASK_RATING_NOT_FOUND("Rating for this task not found"),
    TASK_NOT_COMPLETED("Task not completed"),
    PROJECT_IS_NOT_ALLOWED_TO_UPDATE("Project cannot be update because already approved."),
    TASK_NOT_ALLOWED_TO_UPDATE("Task is not allowed to update because already approved"),
    PHASE_ALREADY_ADDED("Phase already added to the project"),
    PROJECT_ALREADY_BOOKED_MARKED("Project already booked marked"),
    NOT_PERMIT_TO_UPDATE("You are not permitted to update"),
    NOT_PERMIT_TO_DELETE("You are not permitted to delete");

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
