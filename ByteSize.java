/*
 * Copyright © 2024 Cask Data, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.cdap.wrangler.utils;

public class ByteSize {

  private static final long KILO = 1024;
  private static final long MEGA = KILO * 1024;
  private static final long GIGA = MEGA * 1024;
  private static final long TERA = GIGA * 1024;

  public static String of(long bytes) {
    if (bytes < KILO) {
      return bytes + " B";
    } else if (bytes < MEGA) {
      return String.format("%.2f KB", bytes / (double) KILO);
    } else if (bytes < GIGA) {
      return String.format("%.2f MB", bytes / (double) MEGA);
    } else if (bytes < TERA) {
      return String.format("%.2f GB", bytes / (double) GIGA);
    } else {
      return String.format("%.2f TB", bytes / (double) TERA);
    }
  }
}

