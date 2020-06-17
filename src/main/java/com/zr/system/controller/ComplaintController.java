package com.zr.system.controller;

import com.zr.blog.domain.Article;
import com.zr.blog.service.ArticleService;
import com.zr.repo.domain.Project;
import com.zr.repo.service.ProjectService;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Complaint;
import com.zr.system.domain.User;
import com.zr.system.service.ComplaintService;
import com.zr.system.service.UserService;
import com.zr.system.vo.ComplaintVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/complaint")
public class ComplaintController {
    @Autowired
    private ComplaintService complaintService;
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ArticleService articleService;

    /**
     * 加载所有投诉
     * @param complaintVo
     * @return
     */
    @RequestMapping("loadAllComplaint")
    public Object loadAllComplaint(ComplaintVo complaintVo){
        return this.complaintService.queryAllComplaint(complaintVo);
    }

    /**
     * 添加投诉
     * @param complaint
     * @return
     */
    @RequestMapping("addComplaint")
    @RequiresPermissions("complaint:add")
    public ResultObj addComplaint(Complaint complaint){
        try {
            User user = this.userService.getById(complaint.getUid());
            complaint.setUname(user.getName());
            if (complaint.getType().equals(Constant.COMPLAINT_TYPE_PROJECT)){
                Project project = this.projectService.getById(complaint.getComplaintid());
                complaint.setComplaintname(project.getName());

            }else if(complaint.getType().equals(Constant.COMPLAINT_TYPE_ARTICLE)){
                Article article = this.articleService.getById(complaint.getComplaintid());
                complaint.setComplaintname(article.getTitle());
            }
            complaint.setComplainttime(new Date());
            this.complaintService.saveComplaint(complaint);
            return ResultObj.SUBMIT_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.SUBMIT_ERROR;
        }
    }

    /**
     * 结单投诉
     * @param complaint
     * @return
     */
    @RequestMapping("solveComplaint")
    @RequiresPermissions("complaint:solve")
    public ResultObj updateComplaint(Complaint complaint){
        try {
            complaint.setSolvedtime(new Date());
            this.complaintService.updateById(complaint);
            return ResultObj.SOLVE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.SOLVE_ERROR;
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteComplaint")
    @RequiresPermissions("complaint:delete")
    public ResultObj deleteComplaint(Integer id){
        try {
            this.complaintService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 批量删除
     */
    @RequestMapping("/batchDeleteComplaint")
    @RequiresPermissions("complaint:batchDelete")
    public ResultObj batchDeleteComplaint(Integer[] ids){
        try {
            if (null!=ids&&ids.length>0){
                List<Integer> idList = new ArrayList<>();
                for (Integer id : ids) {
                    idList.add(id);
                }
                this.complaintService.removeByIds(idList);
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
