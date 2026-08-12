package com.goldenowl.springboottemplate.app.utils;

import java.time.LocalDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

  public static LocalDateTime getExpiredTime(int seconds) {
    return LocalDateTime.now().plusSeconds(seconds);
  }
}
