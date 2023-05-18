package com.gerp.dartachalani.config.openapi3;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info = @Info(
                title = "GIOMS Darta Chalani API",
                version = "v2",
                description = "This app provides REST APIs for GIOMS Darta Chalani ",
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
