package com.goldenowl.springboottemplate.article.service.impl;

import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.app.utils.AuthenticationUtils;
import com.goldenowl.springboottemplate.article.dto.ArticleDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleDetailDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleSaveDTO;
import com.goldenowl.springboottemplate.article.entity.ArticleEntity;
import com.goldenowl.springboottemplate.article.mapper.ArticleMapper;
import com.goldenowl.springboottemplate.article.repository.ArticleRepository;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArticleServiceImplTest {

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private UserService userService;

    @Mock
    private ArticleMapper articleMapper;

    @InjectMocks
    private ArticleServiceImpl articleService;

    private static final String ARTICLE_ID = "article-id-123";
    private static final String USERNAME = "testuser";

    // Mock data sample
    private ArticleSaveDTO articleSaveDTO;
    private ArticleEntity articleEntity;
    private UserEntity userEntity;
    private ArticleDTO articleDTO;
    private ArticleDetailDTO articleDetailDTO;

    @BeforeEach
    void setUp() {
        articleSaveDTO = new ArticleSaveDTO();
        articleSaveDTO.setTitle("Test Article");

        userEntity = new UserEntity();
        userEntity.setUsername(USERNAME);

        articleEntity = new ArticleEntity();
        articleEntity.setId(ARTICLE_ID);
        articleEntity.setAuthor(userEntity);

        articleDTO = new ArticleDTO();
        articleDTO.setTitle("Test Article");

        articleDetailDTO = new ArticleDetailDTO();
        articleDetailDTO.setTitle("Test Article Detail");
    }

    @Test
    void createArticle_shouldSaveAndReturnId() {
        // Mock AuthenticationUtils.getCurrentUsername()
        try (MockedStatic<AuthenticationUtils> utilities = Mockito.mockStatic(AuthenticationUtils.class)) {
            utilities.when(AuthenticationUtils::getCurrentUsername).thenReturn(USERNAME);

            when(userService.getUserByUsername(USERNAME)).thenReturn(userEntity);
            when(articleMapper.mapToEntity(articleSaveDTO)).thenReturn(articleEntity);
            when(articleRepository.save(articleEntity)).thenReturn(articleEntity);

            String id = articleService.createArticle(articleSaveDTO);

            assertEquals(ARTICLE_ID, id);
            verify(userService).getUserByUsername(USERNAME);
            verify(articleRepository).save(articleEntity);
            verify(articleMapper).mapToEntity(articleSaveDTO);
            assertEquals(userEntity, articleEntity.getAuthor());
        }
    }

    @Test
    void updateArticle_shouldUpdateArticle() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));

        doAnswer(invocation -> {
            ArticleSaveDTO dto = invocation.getArgument(0);
            ArticleEntity entity = invocation.getArgument(1);
            entity.setTitle(dto.getTitle());
            return null;
        }).when(articleMapper).mapToEntity(articleSaveDTO, articleEntity);

        articleService.updateArticle(ARTICLE_ID, articleSaveDTO);

        verify(articleRepository).findById(ARTICLE_ID);
        verify(articleMapper).mapToEntity(articleSaveDTO, articleEntity);
        verify(articleRepository).save(articleEntity);
    }

    @Test
    void updateArticle_shouldThrowWhenNotFound() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleService.updateArticle(ARTICLE_ID, articleSaveDTO);
        });
    }

    @Test
    void deleteArticle_shouldDeleteArticle() {
        when(articleRepository.existsById(ARTICLE_ID)).thenReturn(true);

        articleService.deleteArticle(ARTICLE_ID);

        verify(articleRepository).existsById(ARTICLE_ID);
        verify(articleRepository).deleteById(ARTICLE_ID);
    }

    @Test
    void deleteArticle_shouldThrowWhenNotFound() {
        when(articleRepository.existsById(ARTICLE_ID)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> {
            articleService.deleteArticle(ARTICLE_ID);
        });
    }

    @Test
    void getArticles_shouldReturnPageOfArticles() {
        Pageable pageable = PageRequest.of(0, 10);
        List<ArticleEntity> articles = List.of(articleEntity);
        Page<ArticleEntity> articlePage = new PageImpl<>(articles, pageable, 1);

        when(articleRepository.findAll(pageable)).thenReturn(articlePage);
        when(articleMapper.mapToDto(articleEntity)).thenReturn(articleDTO);

        Page<ArticleDTO> result = articleService.getArticles(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(articleDTO.getTitle(), result.getContent().getFirst().getTitle());

        verify(articleRepository).findAll(pageable);
        verify(articleMapper).mapToDto(articleEntity);
    }

    @Test
    void getArticleDetailById_shouldReturnArticleDetail() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.of(articleEntity));
        when(articleMapper.mapToArticleDetailDTO(articleEntity)).thenReturn(articleDetailDTO);

        ArticleDetailDTO result = articleService.getArticleDetailById(ARTICLE_ID);

        assertEquals(articleDetailDTO.getTitle(), result.getTitle());
        verify(articleRepository).findById(ARTICLE_ID);
        verify(articleMapper).mapToArticleDetailDTO(articleEntity);
    }

    @Test
    void getArticleDetailById_shouldThrowWhenNotFound() {
        when(articleRepository.findById(ARTICLE_ID)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            articleService.getArticleDetailById(ARTICLE_ID);
        });
    }
}
