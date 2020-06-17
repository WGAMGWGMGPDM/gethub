package com.zr.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.blog.domain.Article;
import com.zr.blog.domain.Tag;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
* @Author: 张忍
* @Date: 2020-03-30 11:46
*/
public interface ArticleMapper extends BaseMapper<Article> {

    Integer selectHotByUidAndTid(@Param("uid") Integer uid, @Param("tid") Integer id);

    void incrementHotByUidAndTid(@Param("uid") Integer uid, @Param("tid") Integer id);

    void insertHotByUidAndTid(@Param("uid")Integer uid, @Param("tid") Integer id, @Param("hot") Integer defaultArticlehot);

    List<Tag> selectHotByUid(@Param("uid") Integer uid);

    void insertStar(@Param("uid") Integer uid, @Param("aid") Integer aid);

    void deleteStar(@Param("uid") Integer uid, @Param("aid") Integer aid);

    Integer queryStar(@Param("uid") Integer uid, @Param("aid") Integer aid);

    List<Integer> queryAidsByUid(@Param("uid") Integer uid);

    Integer queryStarNumByAid(@Param("aid") Serializable aid);

    void deleteStarByAid(@Param("aid") Integer id);
}