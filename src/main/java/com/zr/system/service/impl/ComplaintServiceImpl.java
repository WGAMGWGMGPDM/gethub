package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.blog.service.ArticleService;
import com.zr.repo.service.ProjectService;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.domain.Notice;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import com.zr.system.vo.ComplaintVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.domain.Complaint;
import com.zr.system.mapper.ComplaintMapper;
import com.zr.system.service.ComplaintService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: 张忍
 * @Date: 2020-04-20 23:09
 */
@Service
public class ComplaintServiceImpl extends ServiceImpl<ComplaintMapper, Complaint> implements ComplaintService {
    @Autowired
    private ComplaintMapper complaintMapper;
    @Autowired
    private UserService userService;


    /**
     * 添加投诉
     *
     * @param complaint
     * @return
     */
    @Override
    public Complaint saveComplaint(Complaint complaint) {
        this.complaintMapper.insert(complaint);
        return complaint;
    }

    /**
     * 查询所有投诉
     *
     * @param complaintVo
     * @return
     */
    @Override
    public DataGridView queryAllComplaint(ComplaintVo complaintVo) {
        IPage<Complaint> page = new Page<>(complaintVo.getPage(), complaintVo.getLimit());
        QueryWrapper<Complaint> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(complaintVo.getUsername()), "uname", complaintVo.getUsername());
        queryWrapper.like(StringUtils.isNotBlank(complaintVo.getComplaintname()), "complaintname", complaintVo.getComplaintname());
        queryWrapper.eq(null!=complaintVo.getType(),"type",complaintVo.getType());
        if (null!=complaintVo.getSolved()){
            if (complaintVo.getSolved().equals(Constant.COMPLAINT_SOLVE_TRUE)){
                queryWrapper.isNotNull("solvedtime");
            }else if(complaintVo.getSolved().equals(Constant.COMPLAINT_SOLVE_FALSE)){
                queryWrapper.isNull("solvedtime");
            }
        }
        queryWrapper.ge(complaintVo.getStartTime() != null, "complainttime", complaintVo.getStartTime());
        queryWrapper.le(complaintVo.getEndTime() != null, "complainttime", complaintVo.getEndTime());
        queryWrapper.orderByDesc("complainttime");
        this.complaintMapper.selectPage(page, queryWrapper);
        List<Complaint> records = page.getRecords();
        for (Complaint record : records) {
            if (null!=record.getSolvedid()){
                User byId = this.userService.getById(record.getSolvedid());
                record.setSolvedname(byId.getName());
            }
        }

        return new DataGridView(page.getTotal(), page.getRecords());
    }
}

