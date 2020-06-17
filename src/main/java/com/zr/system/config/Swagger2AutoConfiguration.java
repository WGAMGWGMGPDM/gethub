package com.zr.system.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @Author: 张忍
 * @Date: 2020-04-16 18:19
 */
@Configuration
@EnableSwagger2 // 启用swagger2
public class Swagger2AutoConfiguration {
    /**
     * 在IOC容器里面创建可以对象可以扫描Controller里面的是否有Swagger相关的注解 如果，swagger会生成相关的文档
     *
     * @return
     */
    @Bean
    public Docket swaggerSpringMvcPlugin() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().description("这是项目托管系统的后台的API工具")
                .contact(new Contact("张忍", "http://gethub.ren", "1072492799@qq.com"))
                .version("1.0")
                .license("GETHUB")
                .build();
    }
}

