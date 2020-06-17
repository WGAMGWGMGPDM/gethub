package com.zr.system.controller;

import com.zr.system.common.ActiveUser;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Notice;
import com.zr.system.service.NoticeService;
import com.zr.system.vo.NoticeVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;

    /**
     * 加载所有公告
     * @param noticeVo
     * @return
     */
    @RequestMapping("loadAllNotice")
    public Object loadAllNotice(NoticeVo noticeVo){
        return this.noticeService.queryAllNotice(noticeVo);
    }

    /**
     * 添加公告
     * @param notice
     * @return
     */
    @RequestMapping("addNotice")
    @RequiresPermissions("notice:add")
    public ResultObj addNotice(Notice notice){
        try {
            Subject subject = SecurityUtils.getSubject();
            ActiveUser activeUser = (ActiveUser) subject.getPrincipal();
            notice.setOpername(activeUser.getUser().getName());
            notice.setCreatetime(new Date());
            this.noticeService.save(notice);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改公告
     * @param notice
     * @return
     */
    @RequestMapping("updateNotice")
    @RequiresPermissions("notice:update")
    public ResultObj updateNotice(Notice notice){
        try {
            this.noticeService.updateById(notice);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteNotice")
    @RequiresPermissions("notice:delete")
    public ResultObj deleteNotice(Integer id){
        try {
            this.noticeService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 批量删除
     */
    @RequestMapping("/batchDeleteNotice")
    @RequiresPermissions("notice:batchDelete")
    public ResultObj batchDeleteNotice(Integer[] ids){
        try {
            if (null!=ids&&ids.length>0){
                List<Integer> idList = new ArrayList<>();
                for (Integer id : ids) {
                    idList.add(id);
                }
                this.noticeService.removeByIds(idList);
                return ResultObj.DELETE_SUCCESS;
            }else {
                return new ResultObj(-1,"传入id为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }


}
