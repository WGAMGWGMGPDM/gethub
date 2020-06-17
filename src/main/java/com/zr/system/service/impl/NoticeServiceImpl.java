package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.system.common.DataGridView;
import com.zr.system.vo.NoticeVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.mapper.NoticeMapper;
import com.zr.system.domain.Notice;
import com.zr.system.service.NoticeService;
/**
* @Author: 张忍
* @Date: 2020-03-09 15:25
*/
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService{
    @Autowired
    private NoticeMapper noticeMapper;
    @Override
    public DataGridView queryAllNotice(NoticeVo noticeVo) {
        IPage<Notice> page = new Page<>(noticeVo.getPage(),noticeVo.getLimit());
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(noticeVo.getTitle()),"title",noticeVo.getTitle());
        queryWrapper.like(StringUtils.isNotBlank(noticeVo.getOpername()),"opername",noticeVo.getOpername());
        queryWrapper.ge(noticeVo.getStartTime()!=null,"createtime",noticeVo.getStartTime());
        queryWrapper.le(noticeVo.getEndTime()!=null,"createtime",noticeVo.getEndTime());
        queryWrapper.orderByDesc("createtime");
        this.noticeMapper.selectPage(page,queryWrapper);

        return new DataGridView(page.getTotal(),page.getRecords());
    }
}
