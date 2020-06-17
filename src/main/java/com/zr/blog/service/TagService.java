package com.zr.blog.service;

import com.zr.blog.domain.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.blog.vo.TagVo;
import com.zr.system.common.DataGridView;

import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 15:46
 */
public interface TagService extends IService<Tag> {
    void saveTagArticle(Integer id, Integer id1);

    Tag saveTag(Tag tag1);

    Tag updateTag(Tag tag);

    List<Tag> getTagByAid(Integer id);

    List<Integer> queryAidsByTid(Integer tid);

    void removeArticleTagByAid(Integer id);

    DataGridView queryAllTag(TagVo tagVo);

    DataGridView loadAllTagNoPage();

    void decreaseArticleNumByAid(Integer id);
}

