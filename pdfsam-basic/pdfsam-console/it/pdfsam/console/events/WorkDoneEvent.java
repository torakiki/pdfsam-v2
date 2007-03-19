/*
 * Created on 04-Jul-2006
 * Copyright (C) 2006 by Andrea Vacondio.
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
package it.pdfsam.console.events;

import it.pdfsam.console.interfaces.WorkDoneListener;

import java.util.EventObject;
/**
 * Event class to tell the listeners how much of the work has been done
 * @author Andrea Vacondio
 * @see it.pdfsam.console.interfaces.WorkDoneListener
 */
public class WorkDoneEvent extends EventObject {
    
    /**
     * 
     */
    private static final long serialVersionUID = 1006299604590401518L;
    public static final int PERCENTAGE_CHANGE = 0x01;
    public static final int WORK_DONE = 0x02;
    
    /**
     * Message of the event
     */
    private String event_message;
    /**
     * Percentage of work done
     */
    private int percentage_done;
    /**
     * Event type
     */
    private int event_type;

    public WorkDoneEvent(Object source, int event_type) {
        this(source, event_type, "", 0);
    }

    public WorkDoneEvent(Object source, int event_type,String message) {
        this(source, event_type, message, 0);
    }

    public WorkDoneEvent(Object source, int event_type, int percentage) {
        this(source, event_type, "", percentage);
    }

    public WorkDoneEvent(Object source, int event_type, String message, int percentage) {
        super(source);
        event_message = message;
        percentage_done = percentage;
        this.event_type = event_type;
    }

    /**
     * @return Returns the event_message.
     */
    public String getEventMessage() {
        return event_message;
    }

    /**
     * @return Returns the percentage_done.
     */
    public int getPercentageDone() {
        return percentage_done;
    }

    /**
     * @return Returns the event_type.
     */
    public int getType() {
        return event_type;
    }
    
    /**
     * Dispatch the event to listener calling the appropriate method
     * @param listener
     */
    public void dispatch(WorkDoneListener listener) {
        switch (event_type) {
        case PERCENTAGE_CHANGE:
            ((WorkDoneListener)listener).percentageOfWorkDoneChanged(this);
            break;

        case WORK_DONE:
            ((WorkDoneListener)listener).workCompleted(this);
            break;
        }
        }
    
}
