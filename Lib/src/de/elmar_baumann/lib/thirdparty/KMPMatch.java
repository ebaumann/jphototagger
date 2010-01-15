package de.elmar_baumann.lib.thirdparty;

// Code: http://www.fmi.uni-sofia.bg/fmi/logic/vboutchkova/sources/KMPMatch_java.html

/**
 * The Knuth-Morris-Pratt Algorithm for Pattern Matching modified for comparing
 * byte arrays.
 *
 * @version 2010-01-15
 */
public final class KMPMatch {

  private final byte[] bytes;
  private final byte[] pattern;
  private int[]        failure;
  private int          matchPoint;

  public KMPMatch(byte[] bytes, byte[] pattern) {
    this.bytes   = bytes;
    this.pattern = pattern;
    failure      = new int[pattern.length];
    computeFailure();
  }

  public int getMatchPoint() {
    return matchPoint;
  }


  public boolean match() {
    // Tries to find an occurence of the pattern in the string

    int j = 0;
    if (bytes.length == 0) return false;

    for (int i = 0; i < bytes.length; i++) {
      while (j > 0 && pattern[j] != bytes[i]) {
        j = failure[j - 1];
      }
      if (pattern[j] == bytes[i]) { j++; }
      if (j == pattern.length) {
        matchPoint = i - pattern.length + 1;
        return true;
      }
    }
    return false;
  }

  public boolean match1() {

    int i = 0;
    int j = 0;
    if (bytes.length == 0) return false;

    while (i + pattern.length - j <= bytes.length) {
      if (j >= pattern.length) {
        matchPoint = i - pattern.length;
        return true;
      }
      if (bytes[i] == pattern[j]) {
        i++;
        j++;
      } else {
        if (j > 0) { j = failure[j - 1]; }
        else { i++; }
      }
    }
    return false;
  }

  /**
   * Computes the failure function using a boot-strapping process,
   * where the pattern is matched against itself.
   */
  private void computeFailure() {

    int j = 0;
    for (int i = 1; i < pattern.length; i++) {
      while (j > 0 && pattern[j] != pattern[i]) { j = failure[j - 1]; }
      if (pattern[j] == pattern[i]) { j++; }
      failure[i] = j;
    }
  }
 }
