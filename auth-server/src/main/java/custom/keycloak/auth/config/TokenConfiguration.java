package custom.keycloak.auth.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.keycloak.common.util.Time;

import java.util.Optional;

public class TokenConfiguration {
    @JsonProperty("tokenLifespanInSeconds")
    private Integer tokenLifespanInSeconds;

    public Integer getTokenLifespanInSeconds() {
        return this.tokenLifespanInSeconds;
    }

    public void setTokenLifespanInSeconds(Integer tokenLifespanInSeconds) {
        this.tokenLifespanInSeconds = tokenLifespanInSeconds;
    }

    public int computeTokenExpiration(int maxExpiration, boolean longLivedTokenAllowed) {
        return ((Integer)Optional.<Integer>ofNullable(this.tokenLifespanInSeconds)
                .map(lifespan -> Integer.valueOf(Time.currentTime() + lifespan.intValue()))
                .map(requestedExpiration -> Integer.valueOf(longLivedTokenAllowed ? requestedExpiration.intValue() : Math.min(maxExpiration, requestedExpiration.intValue())))
                .orElse(Integer.valueOf(maxExpiration))).intValue();
    }
}
