package com.goldenowl.springboottemplate.article.repository;

import com.goldenowl.springboottemplate.article.entity.ArticleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<ArticleEntity, String> {

}
