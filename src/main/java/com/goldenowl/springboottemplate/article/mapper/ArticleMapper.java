package com.goldenowl.springboottemplate.article.mapper;

import com.goldenowl.springboottemplate.article.dto.ArticleDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleDetailDTO;
import com.goldenowl.springboottemplate.article.dto.ArticleSaveDTO;
import com.goldenowl.springboottemplate.article.entity.ArticleEntity;
import com.goldenowl.springboottemplate.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {UserMapper.class})
public interface ArticleMapper {

    ArticleDetailDTO mapToArticleDetailDTO(ArticleEntity articleEntity);

    ArticleDTO mapToDto(ArticleEntity entity);

    ArticleEntity mapToEntity(ArticleSaveDTO dto);

    void mapToEntity(ArticleSaveDTO dto, @MappingTarget ArticleEntity articleEntity);
}
