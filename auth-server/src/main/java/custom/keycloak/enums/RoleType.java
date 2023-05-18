package custom.keycloak.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum RoleType {
    SYSTEM,
    ADMIN_CREATE,
    OFFICE_CREATE;
}
