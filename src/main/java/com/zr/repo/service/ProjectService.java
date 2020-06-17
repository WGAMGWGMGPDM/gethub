package com.zr.repo.service;

import com.zr.repo.domain.Project;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.repo.vo.ProjectVo;
import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
* @Author: 张忍
* @Date: 2020-03-20 14:23
*/
public interface ProjectService extends IService<Project>{


    DataGridView loadProjectByRepoId(ProjectVo projectVo);

    Project updateProject(Project project);

    Project saveProject(Project project) throws IOException;

    DataGridView queryProjectInfo(ProjectVo projectVo);

    DataGridView queryFileInfo(Integer fileid);

    DataGridView queryProjectInfoFolder(ProjectVo projectVo);

    ResultObj downloadProject(Integer id, HttpServletResponse response) throws IOException;

    DataGridView loadProjectBySearchkey(ProjectVo projectVo);

    DataGridView loadMyProject(ProjectVo projectVo);

    void archivedProject(ProjectVo projectVo);

    DataGridView loadArchiveProjectByPid(ProjectVo projectVo);

    void removeArchiveProject(Integer id);

    DataGridView queryProjectSubmit(Integer id, String year);

    DataGridView queryProjectMaster(Integer id);

    List<Project> queryProjectByUid(Integer id);

    DataGridView loadAllProject(ProjectVo projectVo);

    void starProject(Integer uid, Integer id);

    void unStarProject(Integer uid, Integer id);

    ResultObj queryStar(Integer uid, Integer id);

    DataGridView loadStarProject(ProjectVo projectVo);
}
