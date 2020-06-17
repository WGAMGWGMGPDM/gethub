package com.zr.repo.service;

import com.zr.repo.domain.File;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.repo.vo.FileVo;
import com.zr.system.common.ResultObj;

/**
* @Author: 张忍
* @Date: 2020-03-22 16:28
*/
public interface FileService extends IService<File>{

    File saveFile(File file1);

    void editFile(FileVo fileVo);

    Boolean uploadFoler(File file);

    void addFile(File file);
}
