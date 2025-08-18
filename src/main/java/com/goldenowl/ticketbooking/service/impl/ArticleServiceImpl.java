package com.goldenowl.ticketbooking.service.impl;

import com.goldenowl.ticketbooking.configuration.CustomCacheConfig;
import com.goldenowl.ticketbooking.exception.ResourceNotFoundException;
import com.goldenowl.ticketbooking.ultis.AuthenticationUtils;
import com.goldenowl.ticketbooking.dto.request.ArticleDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleDetailDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleSaveDTO;
import com.goldenowl.ticketbooking.entity.ArticleEntity;
import com.goldenowl.ticketbooking.mapper.ArticleMapper;
import com.goldenowl.ticketbooking.repository.ArticleRepository;
import com.goldenowl.ticketbooking.service.ArticleService;
import com.goldenowl.ticketbooking.entity.UserEntity;
import com.goldenowl.ticketbooking.service.UserService;
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
