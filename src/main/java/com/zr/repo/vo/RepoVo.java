package com.zr.repo.vo;

import com.zr.system.vo.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:49
 */
@Data
@EqualsAndHashCode(callSuper =false)
public class RepoVo extends BaseVo{
    private String repoName;
    private String masterName;
}
