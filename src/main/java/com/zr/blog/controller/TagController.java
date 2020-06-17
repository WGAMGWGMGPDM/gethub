package com.zr.blog.controller;

import com.zr.blog.service.TagService;
import com.zr.blog.vo.TagVo;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Languge;
import com.zr.system.service.LangugeService;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/tag")
public class TagController {
    @Autowired
    private TagService tagService;

    /**
     * 加载所有标签
     * @param tagVo
     * @return
     */
    @RequestMapping("loadAllTag")
    public Object loadAllTag(TagVo tagVo){
        return this.tagService.queryAllTag(tagVo);
    }

    /**
     * 加载所有标签不分页
     * @return
     */
    @RequestMapping("loadAllTagNoPage")
    public Object loadAllTagNoPage(){
        return this.tagService.loadAllTagNoPage();
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteTag")
    @RequiresPermissions("tag:delete")
    public ResultObj deleteTag(Integer id){
        try {
            this.tagService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
}
