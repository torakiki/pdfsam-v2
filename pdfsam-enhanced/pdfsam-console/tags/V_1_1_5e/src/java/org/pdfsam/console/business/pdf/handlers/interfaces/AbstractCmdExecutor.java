/*
 * Created on 18-Oct-2007
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
package org.pdfsam.console.business.pdf.handlers.interfaces;

import java.util.Observable;

import org.pdfsam.console.business.dto.WorkDoneDataModel;
import org.pdfsam.console.business.dto.commands.AbstractParsedCommand;
import org.pdfsam.console.exceptions.console.ConsoleException;
/**
 * Abstract command executor
 * @author Andrea Vacondio
 *
 */
public abstract class AbstractCmdExecutor extends Observable implements CmdExecutor {
	
	private WorkDoneDataModel workDone = null;
	
	/**
	 * set the percentage of work done for the current execution and notify the observers
	 * @param percentage
	 */
	protected final void setPercentageOfWorkDone(int percentage){
		if(workDone == null){
			workDone = new WorkDoneDataModel();
		}
		workDone.setPercentage(percentage);
		setChanged();
		notifyObservers(workDone);
	}

	/**
	 * set the percentage of work done to indeterminate for the current execution and notify the observers
	 */
	protected final void setWorkIndeterminate(){
		setPercentageOfWorkDone(WorkDoneDataModel.INDETERMINATE);
	}

	/**
	 * set the work completed for the current execution and notify the observers
	 */
	protected final void setWorkCompleted(){
		setPercentageOfWorkDone(WorkDoneDataModel.MAX_PERGENTAGE);
	}
	
	/**
	 * reset the percentage of work done
	 */
	protected final void resetPercentageOfWorkDone(){
		if(workDone != null){
			workDone.resetPercentage();
		}
	}

	public abstract void execute(AbstractParsedCommand parsedCommand) throws ConsoleException;
}
