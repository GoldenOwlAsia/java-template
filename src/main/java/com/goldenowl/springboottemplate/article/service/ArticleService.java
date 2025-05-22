package com.goldenowl.springboottemplate.article.service;

import com.goldenowl.springboottemplate.article.dto.ArticleDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleDetailDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleSaveDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    String createArticle(ArticleSaveDTO articleSaveDTO);
    void updateArticle(String id, ArticleSaveDTO articleSaveDTO);
    void deleteArticle(String id);
    Page<ArticleDTO> getArticles(Pageable pageable);
    ArticleDetailDTO getArticleDetailById(String id);
}
