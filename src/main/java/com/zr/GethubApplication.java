package com.zr;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@MapperScan(basePackages = {"com.zr.system.mapper","com.zr.repo.mapper","com.zr.blog.mapper","com.zr.system.common.file"})
@EnableCaching
public class GethubApplication {

    public static void main(String[] args) {
        SpringApplication.run(GethubApplication.class, args);
    }

}
