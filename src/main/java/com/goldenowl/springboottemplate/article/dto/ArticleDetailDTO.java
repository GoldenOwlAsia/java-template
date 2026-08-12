package com.goldenowl.springboottemplate.article.dto;

import com.goldenowl.springboottemplate.user.dto.UserProfileDTO;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleDetailDTO {

  private String id;

  private String title;

  private String content;

  private LocalDateTime createdAt;

  private UserProfileDTO author;
}
