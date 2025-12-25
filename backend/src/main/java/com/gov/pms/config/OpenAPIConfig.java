package com.gov.pms.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfig {
    
    @Bean
    public OpenAPI pmsOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8080");
        localServer.setDescription("Local Development Server");
        
        Contact contact = new Contact();
        contact.setName("PMS Development Team");
        contact.setEmail("support@pms.gov.vn");
        
        License license = new License()
                .name("Internal Use Only")
                .url("https://pms.gov.vn/license");
        
        Info info = new Info()
                .title("Planning Management System (PMS) API")
                .version("2.0.0 - Phase 2")
                .contact(contact)
                .description("API documentation for PMS - Performance Management System. " +
                        "This system manages Plans, Objectives (with hierarchical structure), and Key Results.")
                .termsOfService("https://pms.gov.vn/terms")
                .license(license);
        
        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer));
    }
}
