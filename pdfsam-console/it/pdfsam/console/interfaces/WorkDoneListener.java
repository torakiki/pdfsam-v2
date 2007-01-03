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
package it.pdfsam.console.interfaces;

import it.pdfsam.console.events.WorkDoneEvent;

import java.util.EventListener;

/**
 * Interface for listeners of WorkDoneEvent
 * @author Andrea Vacondio
 * @see it.pdfsam.console.events.WorkDoneEvent
 */
public interface WorkDoneListener extends EventListener {
    
    void percentageOfWorkDoneChanged(WorkDoneEvent wde);
    void workingIndeterminate(WorkDoneEvent wde);
    void workCompleted(WorkDoneEvent wde);
}
