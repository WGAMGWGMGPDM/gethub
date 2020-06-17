package com.zr.repo.service;

import com.zr.repo.domain.Repository;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.repo.vo.RepoVo;
import com.zr.system.common.DataGridView;
import org.apache.ibatis.annotations.Param;

import java.io.IOException;
import java.util.List;

/**
* @Author: 张忍
* @Date: 2020-03-19 23:19
*/
public interface RepositoryService extends IService<Repository>{


    DataGridView queryOwnRepo(Integer id);

    Repository saveRepo(Repository repository) throws IOException;

    Repository updateRepo(Repository repository);

    Boolean incrementProjectNum(Integer reopid);

    Boolean decrementProjectNum(Integer reopid);

    DataGridView queryAllRepo(RepoVo repoVo);
}
