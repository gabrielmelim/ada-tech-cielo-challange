package com.cielo.precadastroclientes.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@OpenAPIDefinition(
        info = @Info(
                title = "Desafio_2 - Fila de atendimento",
                version = "1.0",
                license = @License(
                        name = "CieloAPI"
                )
        ),
        servers = @Server(
                url = "http://localhost:8080"
        )
)
public class SwaggerConfig extends WebMvcConfigurationSupport {

}
