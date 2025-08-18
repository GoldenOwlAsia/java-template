package com.goldenowl.ticketbooking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticleSaveDTO {
    @NotBlank(message = "Title can not be null or empty")
    private String title;

    @NotBlank(message = "Content can not be null or empty")
    private String content;
}
