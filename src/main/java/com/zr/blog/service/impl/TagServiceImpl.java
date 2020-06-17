package com.zr.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.blog.vo.TagVo;
import com.zr.system.common.DataGridView;
import com.zr.system.domain.Languge;
import com.zr.system.utils.AppUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.blog.domain.Tag;
import com.zr.blog.mapper.TagMapper;
import com.zr.blog.service.TagService;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 15:46
 */
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    private TagMapper tagMapper;
    /**
     * 保存标签文章关系
     * @param aid
     * @param tid
     */
    @Override
    public void saveTagArticle(Integer aid, Integer tid) {
        this.tagMapper.insertTagArticle(aid,tid);
    }

    /**
     * 保存标签
     * @param tag
     */
    @CachePut(cacheNames = "com.zr.blog.service.impl.TagServiceImpl",key = "#result.id")
    @Override
    public Tag saveTag(Tag tag) {
        //查询标签
        QueryWrapper<Tag> queryWrapper = new QueryWrapper();
        queryWrapper.eq("name",tag.getName());
        Tag one = this.tagMapper.selectOne(queryWrapper);
        if (null!=one){
            //增加文章数
            one.setArticlenum(one.getArticlenum()+1);
            this.updateById(one);
        }else {
            this.tagMapper.insertTag(tag.getId(),tag.getName());
            //查询标签
            QueryWrapper<Tag> queryWrapper1 = new QueryWrapper();
            queryWrapper1.eq("name",tag.getName());
            one = this.tagMapper.selectOne(queryWrapper1);
        }
        return one;
    }

    /**
     * 更新标签
     * @param tag
     * @return
     */
    @CachePut(cacheNames = "com.zr.blog.service.impl.TagServiceImpl",key = "#result.id")
    @Override
    public Tag updateTag(Tag tag) {
        this.tagMapper.updateById(tag);
        return tag;
    }

    @Cacheable(cacheNames = "com.zr.blog.service.impl.TagServiceImpl",key = "#id")
    @Override
    public Tag getById(Serializable id) {
        return super.getById(id);
    }

    /**
     * 删除标签
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = "com.zr.blog.service.impl.TagServiceImpl",key = "#id")
    @Override
    public boolean removeById(Serializable id) {
        //删除用户标签热度关系
        this.tagMapper.deleteUserTagHotByiTd(id);
        //删除文章标签关系
        this.tagMapper.deleteArticleTagByTid(id);
        return super.removeById(id);
    }

    /**
     * 根据文章id加载标签
     * @param aid
     * @return
     */
    @Override
    public List<Tag> getTagByAid(Integer aid) {
        List<Integer> tid = this.tagMapper.queryTidByAid(aid);
        if (tid.size()>0&&null!=tid){
            List<Tag> tags = this.tagMapper.selectBatchIds(tid);
            return tags;
        }
        return null;
    }

    /**
     * 根据标签id查询文章id
     * @param tid
     * @return
     */
    @Override
    public List<Integer> queryAidsByTid(Integer tid) {
        List<Integer> aid = this.tagMapper.queryAidByTid(tid);
        return aid;
    }

    /**
     * 根据文章id删除文章标签关系表
     * @param aid
     */
    @Override
    public void removeArticleTagByAid(Integer aid) {
        this.tagMapper.deleteArticleTagByAid(aid);
    }

    /**
     * 查询所有标签
     * @param tagVo
     * @return
     */
    @Override
    public DataGridView queryAllTag(TagVo tagVo) {
        IPage<Tag> page = new Page<>(tagVo.getPage(),tagVo.getLimit());
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(tagVo.getName()),"name",tagVo.getName());
        this.tagMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 查询所有标签不分页
     * @return
     */
    @Override
    public DataGridView loadAllTagNoPage() {
        IPage<Tag> page = new Page<>(1,30);
        QueryWrapper<Tag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("articlenum");
        this.tagMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getRecords());
    }

    /**
     * 根据文章id减少标签文章数
     * @param aid
     */
    @Override
    public void decreaseArticleNumByAid(Integer aid) {
        List<Integer> tids = this.tagMapper.queryTidByAid(aid);
        TagService tagService = AppUtil.getContext().getBean(TagService.class);
        for (Integer tid : tids) {
            Tag tag = tagService.getById(tid);
            tag.setArticlenum(tag.getArticlenum()-1);
            if(tag.getArticlenum()==0){
                tagService.removeById(tag.getId());
            }else {
                tagService.updateTag(tag);
            }
        }

    }
}

