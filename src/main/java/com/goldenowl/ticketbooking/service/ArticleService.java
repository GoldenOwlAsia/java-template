package com.goldenowl.ticketbooking.service;

import com.goldenowl.ticketbooking.dto.request.ArticleDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleDetailDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleSaveDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArticleService {
    String createArticle(ArticleSaveDTO articleSaveDTO);
    void updateArticle(String id, ArticleSaveDTO articleSaveDTO);
    void deleteArticle(String id);
    Page<ArticleDTO> getArticles(Pageable pageable);
    ArticleDetailDTO getArticleDetailById(String id);
}
