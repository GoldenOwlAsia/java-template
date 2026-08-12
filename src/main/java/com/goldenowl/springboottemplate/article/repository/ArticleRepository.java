package com.goldenowl.springboottemplate.article.repository;

import com.goldenowl.springboottemplate.article.entity.ArticleEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, String> {

  @EntityGraph(attributePaths = {"author"})
  Page<ArticleEntity> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"author", "author.roles", "author.roles.permissions"})
  Optional<ArticleEntity> findDetailedById(String id);
}
