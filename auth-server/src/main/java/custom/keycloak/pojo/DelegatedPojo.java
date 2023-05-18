package custom.keycloak.pojo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DelegatedPojo {
    private int id;
    private LocalDateTime effectiveDate;
    private LocalDateTime expireDate;
}
