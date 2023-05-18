package com.gerp.attendance.Pojo.attendance;

public enum RealTimeAttStatus {
    processError,
    databaseError,
    comError,
    unHandeledError,
    Ok,
    invalidToken,
    invalidDate,
    pisCodeDoesntExistForOffice;
}
