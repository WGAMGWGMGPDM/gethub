package com.zr.system.common;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-18 23:17
 */
@ConfigurationProperties(prefix = "temp")
@Data
public class TempProperties {
    private String path;
}
