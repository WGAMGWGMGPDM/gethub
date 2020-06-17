package com.zr.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.blog.domain.Tag;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 15:56
 */
public interface TagMapper extends BaseMapper<Tag> {
    void insertTagArticle(@Param("aid") Integer aid, @Param("tid") Integer tid);

    void insertTag(@Param("id") Integer id,@Param("name") String name);

    List<Integer> queryTidByAid(Integer aid);

    List<Integer> queryAidByTid(Integer tid);

    void deleteArticleTagByAid(@Param("aid") Serializable aid);
    void deleteArticleTagByTid(@Param("tid") Serializable tid);

    void deleteUserTagHotByiTd(@Param("tid") Serializable id);
    void deleteUserTagHotByUid(@Param("uid") Serializable id);

    void decreaseArticleNumByid(@Param("tid") Integer tid);
}