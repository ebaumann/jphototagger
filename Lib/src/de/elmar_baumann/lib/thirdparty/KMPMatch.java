package de.elmar_baumann.lib.thirdparty;

//Code: http://stackoverflow.com/questions/1507780/searching-for-a-sequence-of-bytes-in-a-binary-file-with-java
//"Master"-Source?: http://www.fmi.uni-sofia.bg/fmi/logic/vboutchkova/sources/KMPMatch_java.html

/**
 * The Knuth-Morris-Pratt Algorithm for Pattern Matching modified for comparing
 * byte arrays.
 *
 * @version 2010-01-15
 */
public final class KMPMatch {

    /**
     * Finds the first occurrence of the pattern in the text.
     * @param data    data
     * @param pattern pattern
     * @return first occurrence of the pattern in the text
     */
    public static int indexOf(byte[] data, byte[] pattern) {
        int[] failure = computeFailure(pattern);
        int   j       = 0;

        if (data.length == 0) {
            return -1;
        }

        for (int i = 0; i < data.length; i++) {
            while ((j > 0) && (pattern[j] != data[i])) {
                j = failure[j - 1];
            }

            if (pattern[j] == data[i]) {
                j++;
            }

            if (j == pattern.length) {
                return i - pattern.length + 1;
            }
        }

        return -1;
    }

    /**
     * Computes the failure function using a boot-strapping process,
     * where the pattern is matched against itself.
     */
    private static int[] computeFailure(byte[] pattern) {
        int[] failure = new int[pattern.length];
        int   j       = 0;

        for (int i = 1; i < pattern.length; i++) {
            while ((j > 0) && (pattern[j] != pattern[i])) {
                j = failure[j - 1];
            }

            if (pattern[j] == pattern[i]) {
                j++;
            }

            failure[i] = j;
        }

        return failure;
    }

    private KMPMatch() {}
}
