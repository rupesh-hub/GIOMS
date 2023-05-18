package com.gerp.attendance.config.openapi3;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "GIOMS Attendance API",
                version = "v2",
                description = "This app provides REST APIs for GIOMS Attendance ",
                contact = @Contact(
                        name = "Infodevelopers Pvt. Ltd.",
                        email = "info@infodev.com.np",
                        url = "https://infodev.com.np/"
                )
        ),
        servers = {
                @Server(
                        url = "/attendance",
                        description = "DEV Server"
                ),
                @Server(
                        url = "/attendance",
                        description = "PROD Server"
                )
        }
)
@SecurityScheme(
        name = "oauth2",
        type = SecuritySchemeType.OPENIDCONNECT,
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "bearer",
        openIdConnectUrl = "http://103.69.124.84:9091/auth"
        ,
        flows = @OAuthFlows(
//				implicit = @OAuthFlow(
//						authorizationUrl = "http://localhost:9000/auth",
//						scopes = {
//								@OAuthScope(name = "read", description = "read access"),
//								@OAuthScope(name = "write", description = "Write access")
//						}
//				),
                password = @OAuthFlow(
                        tokenUrl = "http:103.69.124.84:9091/auth/realms/gerp-services/protocol/openid-connect/token"
                )
        )
)
@SecurityScheme(
		name = "oauth2",
		type = SecuritySchemeType.APIKEY,
		in = SecuritySchemeIn.QUERY,
		description = "zzz",
//		bearerFormat = "jwt",
		paramName = "zz"
)
public class OpenApiConfig {

}
