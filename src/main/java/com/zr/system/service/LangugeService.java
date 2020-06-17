package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Languge;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.LanguageVo;

import javax.websocket.EncodeException;
import java.io.IOException;

/**
* @Author: 张忍
* @Date: 2020-03-29 15:21
*/
public interface LangugeService extends IService<Languge>{


        DataGridView queryAllLanguage(LanguageVo languageVo);


    DataGridView queryAllLanguageNoPage();

    Languge saveLanguage(Languge language) throws IOException, EncodeException;

    void incrementProjectNum(Integer languge);

    void decrementProjectNum(Integer languge);

    DataGridView queryProjectLanguage();
}
