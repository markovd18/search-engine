package cz.zcu.kiv.nlp.ir;

import org.apache.commons.lang3.StringUtils;

public class ValidationUtils {

  public static <T> void checkNotNull(final T obj, final String objectName) throws IllegalArgumentException {
    if (obj == null) {
      final String name = objectName != null ? objectName : "Parameter";
      throw new IllegalArgumentException(name + " may not be null");
    }
  }

  public static void checkNotBlank(final String str, final String objectName) throws IllegalArgumentException {
    if (StringUtils.isBlank(str)) {
      final String name = objectName != null ? objectName : "Parameter";
      throw new IllegalArgumentException(name + " may not be blank");
    }
  }
}
