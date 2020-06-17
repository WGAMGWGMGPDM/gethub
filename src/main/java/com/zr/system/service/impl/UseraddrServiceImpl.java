package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.domain.User;
import com.zr.system.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.domain.Useraddr;
import com.zr.system.mapper.UseraddrMapper;
import com.zr.system.service.UseraddrService;
/**
* @Author: 张忍
* @Date: 2020-05-19 19:18
*/
@Service
public class UseraddrServiceImpl extends ServiceImpl<UseraddrMapper, Useraddr> implements UseraddrService{

    @Autowired
    private UseraddrMapper useraddrMapper;

    @Override
    public DataGridView loadUserAddr() {
        QueryWrapper<Useraddr> queryWrapper = new QueryWrapper<>();
        queryWrapper.notIn("num",0);
        List<Useraddr> useraddrs = this.useraddrMapper.selectList(queryWrapper);
        List<Map<String,Object>> res = new ArrayList<>();
        for (Useraddr useraddr : useraddrs) {

            Map<String,Object> map = new HashMap<>();
            map.put("name",useraddr.getAddr());
            map.put("value",useraddr.getNum());
            res.add(map);
        }
        return new DataGridView(res);
    }

    @Override
    public void incrementAddrNum(String addr) {
        this.useraddrMapper.incrementAddrNum(addr);
    }

    @Override
    public void decrementAddrNum(String addr) {
        this.useraddrMapper.decrementAddrNum(addr);
    }
}
