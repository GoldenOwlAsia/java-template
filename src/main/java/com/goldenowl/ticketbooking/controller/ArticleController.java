package com.goldenowl.ticketbooking.controller;

import com.goldenowl.ticketbooking.dto.request.ArticleDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleDetailDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleSaveDTO;
import com.goldenowl.ticketbooking.service.ArticleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/articles")
class ArticleController {

    private final ArticleService articleService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ARTICLE_CREATE')")
    ResponseEntity<Void> createArticle(@Valid @RequestBody ArticleSaveDTO articleSaveDTO) {
        String savedId = articleService.createArticle(articleSaveDTO);
        return ResponseEntity.created(URI.create("/api/v1/articles/" + savedId)).build();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ARTICLE_UPDATE')")
    void updateArticle(@PathVariable String id, @Valid @RequestBody ArticleSaveDTO articleSaveDTO) {
        articleService.updateArticle(id, articleSaveDTO);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ARTICLE_DELETE')")
    void deleteArticle(@PathVariable String id) {
        articleService.deleteArticle(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ARTICLE_READ')")
    ArticleDetailDTO getArticleDetailById(@PathVariable String id) {
        return articleService.getArticleDetailById(id);
    }

    @GetMapping("/paging")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAuthority('ARTICLE_READ')")
    Page<ArticleDTO> getArticles(
            @PageableDefault(page = 0, size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return articleService.getArticles(pageable);
    }
}
