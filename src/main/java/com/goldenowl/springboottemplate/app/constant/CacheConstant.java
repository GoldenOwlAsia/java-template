package com.goldenowl.springboottemplate.app.constant;

import java.time.Duration;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CacheConstant {

  public static final String ARTICLE_DETAIL = "articleDetail";

  public static final Duration ARTICLE_DETAIL_TTL = Duration.ofMinutes(5);

  public static final Duration DEFAULT_TTL = Duration.ofMinutes(10);
}
