package com.RideShare.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
        info =@Info(
                contact = @Contact(
                        name = "Wassem Tenbakji" ,
                        email = "wasee.tenbakji@gmail.com",
                        url = "https://www.linkedin.com/in/wassem-tenbakji-a078a5206"
                ),
                description = "open api documentation for Ride Share System",
                title = " Ride Share",
                version = "1.0",
                license = @License(
                        name = "This Doc made for training purpose and can't be used for buisness or jobs," +
                                "If You need any help contact me with previous email",
                        url = "https://some-url.com"
                ),
                termsOfService = "Term of my Service"
        ),
        servers =
                {
                        @Server(
                                description = "Local ENV",
                                url = "http://localhost:3010"
                        ),
                        @Server(
                                description = "Prod ENV",
                                url = "http://199.192.19.220:3012"
                        )
                },
        security = @SecurityRequirement(name = "BearerAuth")
)
@SecurityScheme(
        name = "BearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
}
