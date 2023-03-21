package com.tarzan.recommend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

/**
 * @author tarzab
 */
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean
    public Docket moduleDocket() { return docket("网站接口", "com.tarzan.recommend.controller"); }


    private Docket docket(String groupName, String basePackages) {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(groupName)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackages))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("泰山文档处理 接口文档系统")
                .description("泰山文档处理 api接口文档系统")
                .license("Powered By Tarzan Liu")
                .licenseUrl("http://127.0.0.1")
                .termsOfServiceUrl("http://127.0.0.1")
                .contact(new Contact("tarzan Liu", "http://127.0.0.1", "1334512682@qq.com"))
                .version("V1.0.0")
                .build();
    }



}

