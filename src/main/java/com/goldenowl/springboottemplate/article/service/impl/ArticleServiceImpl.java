package com.goldenowl.springboottemplate.article.service.impl;

import com.goldenowl.springboottemplate.app.config.CustomCacheConfig;
import com.goldenowl.springboottemplate.app.exception.ResourceNotFoundException;
import com.goldenowl.springboottemplate.app.utils.AuthenticationUtils;
import com.goldenowl.springboottemplate.article.dto.ArticleDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleDetailDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleSaveDTO;
import com.goldenowl.springboottemplate.article.entity.ArticleEntity;
import com.goldenowl.springboottemplate.article.mapper.ArticleMapper;
import com.goldenowl.springboottemplate.article.repository.ArticleRepository;
import com.goldenowl.springboottemplate.article.service.ArticleService;
import com.goldenowl.springboottemplate.user.entity.UserEntity;
import com.goldenowl.springboottemplate.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final UserService userService;
    private final ArticleMapper articleMapper;

    @Override
    public String createArticle(ArticleSaveDTO articleSaveDTO) {
        log.info("Creating article with title: {}", articleSaveDTO.getTitle());
        // Get author
        UserEntity author = userService.getUserByUsername(AuthenticationUtils.getCurrentUsername());

        // Set article data
        ArticleEntity articleEntity = articleMapper.mapToEntity(articleSaveDTO);
        articleEntity.setAuthor(author);

        var savedEntity = articleRepository.save(articleEntity);
        log.info("Article created successfully with ID: {}", savedEntity.getId());

        return savedEntity.getId();
    }

    @Override
    @CacheEvict(value = CustomCacheConfig.CACHE_ARTICLE_DETAIL, key = "#id")
    public void updateArticle(String id, ArticleSaveDTO articleSaveDTO) {
        ArticleEntity articleEntity = getArticleEntity(id);
        articleMapper.mapToEntity(articleSaveDTO, articleEntity);
        articleRepository.save(articleEntity);
    }

    @Override
    @CacheEvict(value = CustomCacheConfig.CACHE_ARTICLE_DETAIL, key = "#id")
    public void deleteArticle(String id) {
        log.info("Deleting article with ID: {}", id);
        if (!articleRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        articleRepository.deleteById(id);
        log.info("Article deleted successfully: {}", id);
    }

    @Override
    public Page<ArticleDTO> getArticles(Pageable pageable) {
        log.debug("Fetching articles with pageable: {}", pageable);
        Page<ArticleEntity> articleEntityPage = articleRepository.findAll(pageable);
        return articleEntityPage.map(articleMapper::mapToDto);
    }

    @Override
    @Cacheable(value = CustomCacheConfig.CACHE_ARTICLE_DETAIL, key = "#id")
    public ArticleDetailDTO getArticleDetailById(String id) {
        log.info("Fetching article detail for ID: {}", id);
        var article = getArticleEntity(id);
        return articleMapper.mapToArticleDetailDTO(article);
    }

    private ArticleEntity getArticleEntity(String id) {
        return articleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User", "id", id)
        );
    }
}
