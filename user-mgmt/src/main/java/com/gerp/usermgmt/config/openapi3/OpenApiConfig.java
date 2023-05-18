package com.gerp.usermgmt.config.openapi3;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "GIOMS Organization Profiling and User Management API",
                version = "v2",
                description = "This app provides REST APIs for GIOMS Organization Profiling and User Management ",
                contact = @Contact(
                        name = "Infodevelopers Pvt. Ltd.",
                        email = "info@infodev.com.np",
                        url = "https://infodev.com.np/"
                )
        ),
        servers = {
                @Server(
                        url = "<dev url>",
                        description = "DEV Server"
                ),
                @Server(
                        url = "<prod url>",
                        description = "PROD Server"
                )
        }
)
public class OpenApiConfig {

}
