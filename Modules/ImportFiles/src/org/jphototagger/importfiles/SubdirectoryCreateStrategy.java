package org.jphototagger.importfiles;

import java.io.File;

import org.jphototagger.api.collections.PositionProvider;
import org.jphototagger.api.component.DisplayNameProvider;

/**
 * @author Elmar Baumann
 */
public interface SubdirectoryCreateStrategy extends DisplayNameProvider, PositionProvider {

    String createSubdirectoryName(File file);
}
