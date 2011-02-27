/*
 * Created on 15-May-2008
 * Copyright (C) 2008 by Andrea Vacondio.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.guiclient.business.thumbnails;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import org.pdfsam.guiclient.business.thumbnails.creators.ThumbnailsCreator;
import org.pdfsam.guiclient.dto.StringItem;

/**
 * Registy of the installed ThumbnailCreators
 * 
 * @author Andrea Vacondio
 * 
 */
public class ThumbnailCreatorsRegisty {

    private static ServiceLoader<ThumbnailsCreator> CREATORS = ServiceLoader.load(ThumbnailsCreator.class);

    /**
     * @param identifier
     * @return The requested {@link ThumbnailsCreator} or the first available of the requested one is not available. Returns null if no {@link ThumbnailsCreator} is available.
     */
    public static ThumbnailsCreator getCreator(String identifier) {
        ThumbnailsCreator retVal = null;
        if (CREATORS != null && identifier != null && identifier.length() > 0) {
            for (ThumbnailsCreator creator : CREATORS) {
                if (identifier.equals(creator.getCreatorIdentifier())) {
                    retVal = creator;
                    break;
                }
            }
        }
        // set the default one
        if (retVal == null) {
            for (ThumbnailsCreator creator : CREATORS) {
                retVal = creator;
                break;
            }
        }
        return retVal;
    }

    /**
     * @return a list of the available CREATORS
     */
    public static List<StringItem> getInstalledCreators() {
        List<StringItem> retVal = new ArrayList<StringItem>();
        for (ThumbnailsCreator creator : CREATORS) {
            retVal.add(new StringItem(creator.getCreatorIdentifier(), creator.getCreatorName()));
        }
        return retVal;
    }

    /**
     * Creates the services from the given class loader. Useful when new jars/classes are loaded at runtime
     */
    public static void reload(ClassLoader cl) {
        CREATORS = ServiceLoader.load(ThumbnailsCreator.class, cl);
    }
}
