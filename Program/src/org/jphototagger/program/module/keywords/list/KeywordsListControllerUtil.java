package org.jphototagger.program.module.keywords.list;

import java.util.List;

/**
 * @author Elmar Baumann
 */
final class KeywordsListControllerUtil {

    public static String keywordPathString(List<String> keywords) {
        if (keywords == null) {
            throw new NullPointerException("keywords == null");
        }

        StringBuilder sb = new StringBuilder();
        int index = 0;

        for (String keyword : keywords) {
            sb.append(index == 0 ? "" : " / ");
            sb.append(keyword);
            index++;
        }

        return sb.toString();
    }

    private KeywordsListControllerUtil() {
    }
}
