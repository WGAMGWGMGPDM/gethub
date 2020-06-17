package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.repo.domain.Project;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.common.WebSocketServer;
import com.zr.system.domain.Notice;
import com.zr.system.vo.LanguageVo;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import javax.websocket.EncodeException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.mapper.LangugeMapper;
import com.zr.system.domain.Languge;
import com.zr.system.service.LangugeService;
/**
* @Author: 张忍
* @Date: 2020-03-29 15:21
*/
@Service
public class LangugeServiceImpl extends ServiceImpl<LangugeMapper, Languge> implements LangugeService{
    @Autowired
    private LangugeMapper langugeMapper;
    @Override
    public DataGridView queryAllLanguage(LanguageVo languageVo) {
        IPage<Languge> page = new Page<>(languageVo.getPage(),languageVo.getLimit());
        QueryWrapper<Languge> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(languageVo.getName()),"name",languageVo.getName());
        this.langugeMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @Override
    public DataGridView queryAllLanguageNoPage() {
        List<Languge> languges = this.langugeMapper.selectList(new QueryWrapper<>());
        return new DataGridView(Long.valueOf(languges.size()),languges);
    }

    @CachePut(cacheNames = "com.zr.system.service.impl.LangugeServiceImpl",key = "#result.id")
    @Override
    public Languge saveLanguage(Languge language) throws IOException{
        this.langugeMapper.insert(language);
        int count = this.count();
        WebSocketServer.sendInfo("{'languageCount':"+count+"}");
        return language;
    }

    /**
     * 自增语言项目数
     * @param languge 语言ID
     */
    @Override
    public void incrementProjectNum(Integer languge) {
        this.langugeMapper.incrementProjectNum(languge);
    }

    /**
     * 自减语言项目数
     * @param languge 语言ID
     */
    @Override
    public void decrementProjectNum(Integer languge) {
        this.langugeMapper.decrementProjectNum(languge);
    }

    /**
     * 加载首页语言项目信息
     * @return
     */
    @Override
    public DataGridView queryProjectLanguage() {
        List<Languge> languges = this.langugeMapper.selectList(new QueryWrapper<>());

        Map<String,Object> res = new HashMap<>();//返回结果集
        List<Map<String,Object>> dataRes = new ArrayList<>();//数据结果集
        List<String> itemList = new ArrayList<>();//标签结果集
        Map<String,Object> selectMap = new HashMap<>();//是否选中结果集

        for (int i = 0; i < languges.size(); i++) {
            Map<String,Object> tempmap = new HashMap<>();
            tempmap.put("name", languges.get(i).getName());
            tempmap.put("value",languges.get(i).getNum());
            dataRes.add(tempmap);
            itemList.add(languges.get(i).getName());
            if (i<6){
                selectMap.put(languges.get(i).getName(),true);
            }else {
                selectMap.put(languges.get(i).getName(),false);
            }
        }


        res.put("legendData",itemList);
        res.put("seriesData",dataRes);
        res.put("selected",selectMap);
        return new DataGridView(res);
    }

    @CacheEvict(cacheNames = "com.zr.system.service.impl.LangugeServiceImpl",key = "#id")
    @SneakyThrows
    @Override
    public boolean removeById(Serializable id) {
        boolean flag = super.removeById(id);
        if (flag){
            int count = this.count();
            WebSocketServer.sendInfo("{'languageCount':"+count+"}");
        }
        return flag;
    }

    @Cacheable(cacheNames = "com.zr.system.service.impl.LangugeServiceImpl",key = "#id")
    @Override
    public Languge getById(Serializable id) {
        return super.getById(id);
    }
}
