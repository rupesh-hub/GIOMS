package custom.keycloak.auth.mfa.smsAuthenticator.gateway;

public interface SmsService {

	void send(String phoneNumber, String message);

}
