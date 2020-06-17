package com.zr.blog.service;

import com.zr.blog.domain.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.blog.vo.ArticleVo;
import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
* @Author: 张忍
* @Date: 2020-03-30 11:46
*/
public interface ArticleService extends IService<Article>{
        Article saveArticle(Article article) throws IOException;

        Article updateArticle(Article article);

        Article tempArticle(Article article);

        Article loadTempArticle(Integer uid);

        DataGridView loadMyArticle(ArticleVo articleVo);

        void removeArticleById(Integer id) throws IOException;

        DataGridView loadAllArticleAvailable(ArticleVo articleVo);

        DataGridView queryArticleSubmit(Integer id, String year);

    DataGridView queryRecommendArticle(Integer id);

        void saveUserTag(Integer id, Integer id1);

        DataGridView loadHotArticle(ArticleVo articleVo);

    DataGridView loadAllArticleIgnoreAvailable(ArticleVo articleVo);

    void starArticle(Integer uid, Integer id);

    void unStarArticle(Integer uid, Integer id);

    ResultObj queryStar(Integer uid, Integer id);

    DataGridView loadStarArticle(ArticleVo articleVo);

    Integer queryStarnumById(Integer aid);
}
