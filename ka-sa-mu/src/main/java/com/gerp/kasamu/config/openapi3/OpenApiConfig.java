package com.gerp.kasamu.config.openapi3;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;

import java.util.Collections;


@OpenAPIDefinition(
        info = @Info(
                title = "KASAMU API",
                version = "v2",
                description = "This app provides REST APIs for oag-audit entities",
                contact = @Contact(
                        name = "Infodevelopers Pvt. Ltd.",
                        email = "info@infodev.com.np",
                        url = "https://infodev.com.np/"
                )
        ),
        servers = {
                @Server(
                        url = "http://localhost:8081",
                        description = "Local Server"
                ),
                @Server(
                        url = "<prod url>",
                        description = "PROD Server"
                )
        }
)
@SecurityScheme(
        name = "Token",
        type = SecuritySchemeType.HTTP,
        in = SecuritySchemeIn.HEADER,
        scheme = "bearer",
        bearerFormat = "JWT"
//        flows = @OAuthFlows(

////				implicit = @OAuthFlow(
////						authorizationUrl = "http://localhost:9000/auth",
////						scopes = {
////								@OAuthScope(name = "read", description = "read access"),
////								@OAuthScope(name = "write", description = "Write access")
////						}
////				),
//                password = @OAuthFlow(
//                        tokenUrl = "http://localhost:9000/oauth/token"
//                ),
//                clientCredentials = @OAuthFlow(
//                        tokenUrl = "http://localhost:9000/oauth/token"
//                )
//        )
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

