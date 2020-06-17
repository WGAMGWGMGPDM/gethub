package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Complaint;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.ComplaintVo;

/**
 * @Author: 张忍
 * @Date: 2020-04-20 23:09
 */
public interface ComplaintService extends IService<Complaint> {


    Complaint saveComplaint(Complaint complaint);

    DataGridView queryAllComplaint(ComplaintVo complaintVo);
}

