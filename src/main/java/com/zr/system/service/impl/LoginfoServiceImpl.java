package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.system.common.DataGridView;
import com.zr.system.vo.LoginfoVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.domain.Loginfo;
import com.zr.system.mapper.LoginfoMapper;
import com.zr.system.service.LoginfoService;
@Service
public class LoginfoServiceImpl extends ServiceImpl<LoginfoMapper, Loginfo> implements LoginfoService{

    @Autowired
    private LoginfoMapper loginfoMapper;

    @Override
    public DataGridView queryAllLoginfo(LoginfoVo loginfoVo) {
        IPage<Loginfo> page = new Page<>(loginfoVo.getPage(), loginfoVo.getLimit());
        QueryWrapper<Loginfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(loginfoVo.getLoginname()),"loginname",loginfoVo.getLoginname());
        queryWrapper.like(StringUtils.isNotBlank(loginfoVo.getLoginip()),"loginip",loginfoVo.getLoginip());
        queryWrapper.ge(loginfoVo.getStartTime()!=null,"logintime",loginfoVo.getStartTime());
        queryWrapper.le(loginfoVo.getEndTime()!=null,"logintime",loginfoVo.getEndTime());
        queryWrapper.orderByDesc("logintime");

        this.loginfoMapper.selectPage(page,queryWrapper);
        DataGridView dataGridView = new DataGridView(page.getTotal(), page.getRecords());
        System.out.println(dataGridView.getData());



        return new DataGridView(page.getTotal(),page.getRecords());
    }
}
