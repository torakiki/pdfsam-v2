/*
 * Created on 10-Jan-2010
 * Copyright (C) 2010 by Andrea Vacondio.
 *
 * This library is provided under dual licenses.
 * You may choose the terms of the Lesser General Public License version 2.1 or the General Public License version 2
 * License at your discretion.
 * 
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation;
 * version 2.1 of the License.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * 
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; 
 * either version 2 of the License.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 
 *  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.pdfsam.console.business.pdf.bookmarks;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.lowagie.text.pdf.SimpleBookmark;

/**
 * Helper class used to process bookmarks
 * 
 * @author Andrea Vacondio
 */
public class BookmarksProcessor {

    private List bookmarks;
    private int numberOfPages;

    /**
     * @param bookmarks
     * @param numberOfPages
     */
    public BookmarksProcessor(List bookmarks, int numberOfPages) {
        super();
        if (bookmarks != null) {
            this.bookmarks = Collections.unmodifiableList(bookmarks);
        }
        this.numberOfPages = numberOfPages;
    }

    /**
     * Process the bookmarks returning a view of the whole list that contains only pages comprehended among the two limits (included) with page number shifted if necessary.
     * 
     * @param startPage
     *            start page number
     * @param endPage
     *            end page number
     * @param pageOffset
     *            if not 0 pages are shifted of the given amount
     * @return
     */
    public List processBookmarks(int startPage, int endPage, int pageOffset) {
        List books = null;
        if (bookmarks != null) {
            books = getCopyBookmarks(bookmarks);
            if (endPage < numberOfPages) {
                SimpleBookmark.eliminatePages(books, new int[] { endPage + 1, numberOfPages });
            }
            if (startPage > 1) {
                SimpleBookmark.eliminatePages(books, new int[] { 1, startPage - 1 });
                SimpleBookmark.shiftPageNumbers(books, -(startPage - 1), null);
            }
            if (pageOffset != 0) {
                SimpleBookmark.shiftPageNumbers(books, pageOffset, null);
            }
        }
        return books;
    }

    /**
     * Process the bookmarks returning a view of the whole list that contains only pages comprehended among the two limits (included) with page number shifted if necessary.
     * 
     * @param startPage
     *            start page number
     * @param endPage
     *            end page number
     * @return
     * @see BookmarksProcessor#processBookmarks(int, int, int)
     */
    public List processBookmarks(int startPage, int endPage) {
        return processBookmarks(startPage, endPage, 0);
    }

    private List getCopyBookmarks(List inputBook) {
        List retVal = new ArrayList();
        for (Iterator it = inputBook.listIterator(); it.hasNext();) {
            HashMap map = (HashMap) it.next();
            retVal.add(getCopyMap(map));
        }
        return retVal;
    }

    /**
     * @param map
     * @return
     */
    private HashMap getCopyMap(HashMap map) {
        HashMap retVal = new HashMap();
        if(map != null){
            Set entries = map.entrySet();
            for (Iterator it = entries.iterator(); it.hasNext();) {
                Entry entry = (Entry) it.next();
                if (entry.getValue() instanceof List) {
                    retVal.put(entry.getKey(), getCopyBookmarks((List) entry.getValue()));
                } else {
                    retVal.put(entry.getKey(), entry.getValue());
                }
            }
        }
        return retVal;
    }
}
