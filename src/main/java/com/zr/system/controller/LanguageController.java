package com.zr.system.controller;

import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.ResultObj;

import com.zr.system.domain.Languge;
import com.zr.system.service.LangugeService;
import com.zr.system.utils.AppUtil;
import com.zr.system.vo.LanguageVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/language")
public class LanguageController {
    @Autowired
    @Lazy
    private LangugeService languageService;

    /**
     * 加载所有语言
     * @param languageVo
     * @return
     */
    @RequestMapping("loadAllLanguage")
    public Object loadAllLanguage(LanguageVo languageVo){
        return this.languageService.queryAllLanguage(languageVo);
    }

    /**
     * 添加语言
     * @param language
     * @return
     */
    @RequestMapping("addLanguage")
    @RequiresPermissions("language:add")
    public ResultObj addLanguage(Languge language){
        try {
            language.setNum(Constant.DEFAULT_PROJECT_NUM);
            this.languageService.saveLanguage(language);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }
    /**
     * 删除
     */
    @RequestMapping("/deleteLanguage")
    @RequiresPermissions("language:delete")
    public ResultObj deleteLanguage(Integer id){
        try {
            this.languageService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
}
