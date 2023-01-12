package com.hexdude.myaccount.infrastructure.property;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author HEXDude
 * @date 2023/1/12
 * @brief OpenAPI配置文件
 */
@Component
@ConfigurationProperties(value = "openapi")
@Data
public class OpenApiProperties {

    /**
     * 是否开启OpenAPI
     */
    private String enable;

    /**
     * 文档标题<br>
     * 默认取spring.application.name
     */
    @Value("${spring.application.name}")
    private String title;

    /**
     * 服务条款
     */
    private String terms;

    /**
     * 描述
     */
    private String description;

    /**
     * 联系人-名称
     */
    private String contactName;

    /**
     * 联系人-联系URL
     */
    private String contactUrl;

    /**
     * 联系人-联系邮件地址
     */
    private String contactEmailAddress;

    /**
     * 版本-工程版本<br>
     * 默认取Maven根项目版本
     */
    private String projectVersion;

    /**
     * 版本-SpringBoot版本<br>
     * 默认取SpringBoot版本
     */
    private String springBootVersion;

    private String license;

    private String licenseUrl;
}
