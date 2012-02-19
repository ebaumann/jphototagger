package org.jphototagger.api.file;

import java.io.File;

import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.api.component.DisplayNameProvider;

/**
 * @author Elmar Baumann
 */
public interface SubdirectoryCreateStrategy extends DisplayNameProvider, PositionProvider {

    String suggestSubdirectoryName(File file);
}
