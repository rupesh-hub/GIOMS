package com.gerp.shared.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RoleGroupEnum {
            REQUESTTRANSFER,
            MAINTRANSFER,

            OFFICE_HEAD,

            REVIEWER,
            DASHBOARD_DAILY_LOG_VIEW,
            DEFAULT,
            SUPER_ADMIN,
            GENERAL_USER,
            OFFICE_ADMINISTRATOR,
            CONTRACTEMPLOYEE,
            DCU,
            DARTACREATOR,
            TIPPANIAPPROVER,
            DARTACHALANIUSER,
            DARTACHALANIREPORTVIEWER,
            KARAREMP,
            KARAAR,
            TRANSFERADMIN,
            MEETINGMANAGEMENT,
            SYSTEM_SCREEN_SETUP,
            KARMACHARIPRASASHAN,
            DIRECTORDRI,
            MINISTER,
            APPROVER,
            CHALANIAPPROVE,
            KARAREMPLOYEE,
            ATTENDANCEREVIEWER,
            DARTAVIEWER,
            APPROVETRANSFER,
            MANUALKAAJ,
            ORGANIZATIONADMIN,
            MASTERDASHBOARD,
            SECTIONOFFICER,
            TRANSFERTEST,
            TRANSFERHEAD,
            TRUST,
            CHALANIAPPROVER,
            KOHINOORTEST,
            OFFICEHEAD,
            GENERALUSER
}
