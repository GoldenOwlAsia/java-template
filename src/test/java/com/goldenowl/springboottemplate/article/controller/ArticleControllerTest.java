package com.goldenowl.springboottemplate.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goldenowl.springboottemplate.app.config.SecurityConfigTest;
import com.goldenowl.springboottemplate.article.dto.ArticleDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleDetailDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleSaveDTO;
import com.goldenowl.springboottemplate.article.service.ArticleService;
import com.goldenowl.springboottemplate.auth.service.TokenBlacklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ArticleController.class)
@Import(SecurityConfigTest.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService articleService;

    @MockBean
    private TokenBlacklistService tokenBlacklistService;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String BASE_URL = "/api/v1/articles";

    private ArticleSaveDTO articleSaveDTO;
    private ArticleDetailDTO articleDetailDTO;
    private ArticleDTO articleDTO;
    private Page<ArticleDTO> articleDTOPage;

    private static final String ARTICLE_ID = "article-id-123";

    @BeforeEach
    void setup() {
        articleSaveDTO = new ArticleSaveDTO();
        articleSaveDTO.setTitle("Test Article");
        articleSaveDTO.setContent("Valid article content");

        articleDetailDTO = new ArticleDetailDTO();
        articleDetailDTO.setTitle("Test Article Detail");

        articleDTO = new ArticleDTO();
        articleDTO.setTitle("Test Article");

        articleDTOPage = new PageImpl<>(List.of(articleDTO));
    }

    @Test
    @WithMockUser(authorities = {"ARTICLE_CREATE"})
    void createArticle_shouldReturnCreatedStatusAndLocationHeader() throws Exception {
        when(articleService.createArticle(any(ArticleSaveDTO.class))).thenReturn(ARTICLE_ID);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleSaveDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", BASE_URL + "/" + ARTICLE_ID));

        verify(articleService).createArticle(any(ArticleSaveDTO.class));
    }

    @Test
    @WithMockUser(authorities = {"ARTICLE_UPDATE"})
    void updateArticle_shouldReturnOk() throws Exception {
        doNothing().when(articleService).updateArticle(eq(ARTICLE_ID), any(ArticleSaveDTO.class));

        mockMvc.perform(put(BASE_URL + "/" + ARTICLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(articleSaveDTO)))
                .andExpect(status().isOk());

        verify(articleService).updateArticle(eq(ARTICLE_ID), any(ArticleSaveDTO.class));
    }

    @Test
    @WithMockUser(authorities = {"ARTICLE_UPDATE"})
    void updateArticle_withInvalidDTO_shouldReturnBadRequest() throws Exception {
        ArticleSaveDTO invalidDTO = new ArticleSaveDTO();

        mockMvc.perform(put(BASE_URL + "/" + ARTICLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(authorities = {"ARTICLE_DELETE"})
    void deleteArticle_shouldReturnOk() throws Exception {
        doNothing().when(articleService).deleteArticle(ARTICLE_ID);

        mockMvc.perform(delete(BASE_URL + "/" + ARTICLE_ID))
                .andExpect(status().isOk());

        verify(articleService).deleteArticle(ARTICLE_ID);
    }

    @Test
    @WithMockUser(authorities = {"ARTICLE_READ"})
    void getArticleDetailById_shouldReturnArticleDetail() throws Exception {
        when(articleService.getArticleDetailById(ARTICLE_ID)).thenReturn(articleDetailDTO);

        mockMvc.perform(get(BASE_URL + "/" + ARTICLE_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(articleDetailDTO.getTitle()));

        verify(articleService).getArticleDetailById(ARTICLE_ID);
    }

    @Test
    @WithMockUser(authorities = {"ARTICLE_READ"})
    void getArticles_shouldReturnPagedArticles() throws Exception {
        when(articleService.getArticles(any(Pageable.class))).thenReturn(articleDTOPage);

        mockMvc.perform(get(BASE_URL + "/paging")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value(articleDTO.getTitle()));

        verify(articleService).getArticles(any(Pageable.class));
    }
}
