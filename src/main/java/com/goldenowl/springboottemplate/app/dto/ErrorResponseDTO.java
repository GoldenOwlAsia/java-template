package com.goldenowl.springboottemplate.app.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponseDTO {

  private int status;

  private String message;

  private String path;

  private Map<String, String> errors;
}
