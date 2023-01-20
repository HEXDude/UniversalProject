package com.hexdude.universalproject.infrastructure.configuration;

import com.hexdude.universalproject.infrastructure.property.OpenApiProperties;
import io.swagger.annotations.Api;
import org.springframework.boot.actuate.endpoint.web.servlet.WebMvcEndpointHandlerMapping;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

/**
 * @author HEXDude
 * @date 2023/1/12
 * @brief OpenAPI配置文件
 */
@Configuration
@ConditionalOnBean(value = WebMvcEndpointHandlerMapping.class)
@ConditionalOnProperty(value = "openapi.enable")
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
                "工程版本：" + openApiProperties().getProjectVersion() + "，SpringBoot版本：" + openApiProperties.getSpringBootVersion(),
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
    public Docket api(OpenApiProperties openApiProperties, Environment environment) {
        System.out.println("Docket初始了");
        // 配置启用OpenApi的环境
        Profiles acceptedProfiles = Profiles.of("dev");
        boolean enableOpenApi = environment.acceptsProfiles(acceptedProfiles);

        return new Docket(DocumentationType.OAS_30)
                .enable(enableOpenApi)
                // 文档信息
                .apiInfo(apiInfo(openApiProperties))
                .select()
                // 扫描的位置
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
                // 指定的节点位置
                .paths(PathSelectors.any())
                .build();
    }
}
