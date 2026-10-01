package com.goldenowl.springboottemplate.article.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDTO {

  private String id;

  private String title;

  private LocalDateTime createdAt;

  private String username;
}
