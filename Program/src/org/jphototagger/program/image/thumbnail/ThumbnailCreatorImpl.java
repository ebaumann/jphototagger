package org.jphototagger.program.image.thumbnail;

import java.awt.Image;
import java.io.File;
import java.util.Collection;
import org.jphototagger.api.image.ThumbnailCreator;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 *
 * @author Elmar Baumann
 */
@ServiceProvider(service = ThumbnailCreator.class)
public final class ThumbnailCreatorImpl implements ThumbnailCreator {

    @Override
    public Image createThumbnail(File file) {
        Image thumbnail = ThumbnailUtil.getThumbnail(file);

        if (thumbnail != null) {
            return thumbnail;
        }

        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        for (ThumbnailCreator tnCreator : tnCreators) {
            if (!(tnCreator instanceof ThumbnailCreatorImpl)) {
                thumbnail = tnCreator.createThumbnail(file);

                if (thumbnail != null) {
                    return thumbnail;
                }
            }
        }

        return null;
    }

    @Override
    public Image createFromEmbeddedThumbnail(File file) {
        Image thumbnail = ThumbnailUtil.getEmbeddedThumbnail(file);

        if (thumbnail != null) {
            return thumbnail;
        }

        Collection<? extends ThumbnailCreator> tnCreators = Lookup.getDefault().lookupAll(ThumbnailCreator.class);

        for (ThumbnailCreator tnCreator : tnCreators) {
            if (!(tnCreator instanceof ThumbnailCreatorImpl)) {
                thumbnail = tnCreator.createFromEmbeddedThumbnail(file);

                if (thumbnail != null) {
                    return thumbnail;
                }
            }
        }

        return null;
    }
}
