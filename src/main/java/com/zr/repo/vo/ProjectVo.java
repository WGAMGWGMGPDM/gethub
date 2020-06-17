package com.zr.repo.vo;

import com.zr.system.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:49
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class ProjectVo extends BaseVo{
    private Integer repoId;
    private Integer id;
    private String searchkey;
    private String name;
    private String remark;
    private Integer language;
    private String version;
    //项目所属人id
    private Integer user;
    //项目所属人姓名
    private String masterName;
    private Integer isShow;
}
