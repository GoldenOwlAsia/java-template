package com.goldenowl.ticketbooking.mapper;

import com.goldenowl.ticketbooking.dto.request.ArticleDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleDetailDTO;
import com.goldenowl.ticketbooking.dto.request.ArticleSaveDTO;
import com.goldenowl.ticketbooking.entity.ArticleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {UserMapper.class})
public interface ArticleMapper {

    ArticleDetailDTO mapToArticleDetailDTO(ArticleEntity articleEntity);

    ArticleDTO mapToDto(ArticleEntity entity);

    ArticleEntity mapToEntity(ArticleSaveDTO dto);

    void mapToEntity(ArticleSaveDTO dto, @MappingTarget ArticleEntity articleEntity);
}
