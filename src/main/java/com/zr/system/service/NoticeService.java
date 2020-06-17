package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Notice;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.NoticeVo;

/**
* @Author: 张忍
* @Date: 2020-03-09 15:25
*/
public interface NoticeService extends IService<Notice>{


        DataGridView queryAllNotice(NoticeVo noticeVo);
    }
