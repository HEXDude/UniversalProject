package com.hexdude.myaccount.infrastructure.configuration;

import com.hexdude.myaccount.infrastructure.property.OpenApiProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.CorsEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementPortType;
import org.springframework.boot.actuate.endpoint.ExposableEndpoint;
import org.springframework.boot.actuate.endpoint.web.*;
import org.springframework.boot.actuate.endpoint.web.annotation.ControllerEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.annotation.ServletEndpointsSupplier;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author HEXDude
 * @date 2023/1/12
 * @brief OpenAPI配置文件
 */
@Configuration
@ConditionalOnProperty(value = "openapi.enable")
@EnableOpenApi
public class OpenApiConfiguration {

    /**
     * 在运行配置时注入OpenAPI配置文件
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenApiProperties openApiProperties() {
        return new OpenApiProperties();
    }

    /**
     * 生成在线文档中首页的相关信息
     */
    private ApiInfo apiInfo(OpenApiProperties openApiProperties) {
        return new ApiInfo(openApiProperties.getTitle(),
                openApiProperties().getDescription(),
                "工程版本：" + openApiProperties().getProjectVersion() + "SpringBoot版本：" + openApiProperties.getSpringBootVersion(),
                openApiProperties.getTerms(),
                new Contact(openApiProperties.getContactName(), openApiProperties.getContactUrl(), openApiProperties.getContactEmailAddress()),
                openApiProperties.getLicense(),
                openApiProperties.getLicenseUrl(),
                Collections.emptyList());
    }

    /**
     * 创建OpenApi在线文档
     */
    @Bean
    public Docket api(OpenApiProperties openApiProperties) {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo(openApiProperties))
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }

    /* 解决OpenApi与SpringBoot2.6+不兼容的问题 */
    @Bean
    public WebMvcEndpointHandlerMapping webEndpointServletHandlerMapping(WebEndpointsSupplier webEndpointsSupplier, ServletEndpointsSupplier servletEndpointsSupplier, ControllerEndpointsSupplier controllerEndpointsSupplier, EndpointMediaTypes endpointMediaTypes, CorsEndpointProperties corsProperties, WebEndpointProperties webEndpointProperties, Environment environment) {
        List<ExposableEndpoint<?>> allEndpoints = new ArrayList<>();
        Collection<ExposableWebEndpoint> webEndpoints = webEndpointsSupplier.getEndpoints();
        allEndpoints.addAll(webEndpoints);
        allEndpoints.addAll(servletEndpointsSupplier.getEndpoints());
        allEndpoints.addAll(controllerEndpointsSupplier.getEndpoints());
        String basePath = webEndpointProperties.getBasePath();
        EndpointMapping endpointMapping = new EndpointMapping(basePath);
        boolean shouldRegisterLinksMapping = this.shouldRegisterLinksMapping(webEndpointProperties, environment, basePath);
        return new WebMvcEndpointHandlerMapping(endpointMapping, webEndpoints, endpointMediaTypes, corsProperties.toCorsConfiguration(), new EndpointLinksResolver(allEndpoints, basePath), shouldRegisterLinksMapping, null);
    }

    private boolean shouldRegisterLinksMapping(WebEndpointProperties webEndpointProperties, Environment environment, String basePath) {
        return webEndpointProperties.getDiscovery().isEnabled() && (StringUtils.hasText(basePath) || ManagementPortType.get(environment).equals(ManagementPortType.DIFFERENT));
    }
}
