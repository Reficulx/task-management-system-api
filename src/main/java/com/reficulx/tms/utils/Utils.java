package com.reficulx.tms.utils;

import java.util.Objects;

public class Utils {


  /**
   * evaluate whether the string is non-null and non-empty
   *
   * @return
   */
  public static boolean isValidString(String s) {
    return !Objects.isNull(s) && (s.trim().length() > 0);
  }
}
