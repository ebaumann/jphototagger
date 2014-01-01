package org.jphototagger.lib.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jphototagger.api.collections.PositionProvider;

/**
 * @author Elmar Baumann
 */
public final class LayerUtil {

    public static void logWarningIfNotUniquePositions(Collection<? extends PositionProvider> positionProviders) {
        if (positionProviders == null) {
            throw new NullPointerException("positionProviders == null");
        }
        Logger logger = Logger.getLogger(LayerUtil.class.getName());
        Map<Integer, List<PositionProvider>> providersOfPosition = new HashMap<>();
        for (PositionProvider positionProvider : positionProviders) {
            int positionOfProvider = positionProvider.getPosition();
            List<PositionProvider> pop = providersOfPosition.get(positionOfProvider);
            if (pop == null) {
                pop = new ArrayList<>();
                providersOfPosition.put(positionOfProvider, pop);
            }
            pop.add(positionProvider);
            if (pop.size() > 1) {
                logger.log(Level.WARNING, "More than 1 Position Providers claims Position {0}: {1}", new Object[]{positionOfProvider, pop});
            }
        }
    }

    private LayerUtil() {
    }
}
