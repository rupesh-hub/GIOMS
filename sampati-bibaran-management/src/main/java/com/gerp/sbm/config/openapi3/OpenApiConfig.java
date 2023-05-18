package com.gerp.sbm.config.openapi3;

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
                title = "GIOMS Sampati Bibaran API",
                version = "v2",
                description = "This app provides REST APIs for GIOMS Sampati Bibaran",
                contact = @Contact(
                        name = "Infodevelopers Pvt. Ltd.",
                        email = "info@infodev.com.np",
                        url = "https://infodev.com.np/"
                )
        ),
        servers = {
                @Server(
                        url = "/sbm",
                        description = "DEV Server"
                ),
                @Server(
                        url = "/sbm",
                        description = "PROD Server"
                )
        }
)
@SecurityScheme(
        name = "oauth2",
        type = SecuritySchemeType.OAUTH2,
        in = SecuritySchemeIn.HEADER,
        bearerFormat = "bearer",
        flows = @OAuthFlows(
//				implicit = @OAuthFlow(
//						authorizationUrl = "http://localhost:9000/auth",
//						scopes = {
//								@OAuthScope(name = "read", description = "read access"),
//								@OAuthScope(name = "write", description = "Write access")
//						}
//				),
                password = @OAuthFlow(
                        tokenUrl = "http://localhost:9000/oauth/token"
                ),
                clientCredentials = @OAuthFlow(
                        tokenUrl = "http://localhost:9000/oauth/token"
                )
        )
)
//@SecurityScheme(
//		name = "oauth2",
//		type = SecuritySchemeType.APIKEY,
//		in = SecuritySchemeIn.QUERY,
//		description = "zzz",
////		bearerFormat = "jwt",
//		paramName = "zz"
//)
public class OpenApiConfig {

}
