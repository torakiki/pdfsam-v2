/*
 * Created on 27-Aug-2009
 * Copyright (C) 2009 by Andrea Vacondio.
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

import java.util.Map;

import de.intarsys.cwt.environment.IGraphicsContext;
import de.intarsys.pdf.platform.cwt.rendering.CSPlatformRenderer;
/**
 * Jpod renderer set to "RenderQuality"
 * @author Andrea Vacondio
 *
 */
public class JPodRenderer extends CSPlatformRenderer {

	public JPodRenderer(Map paramOptions, IGraphicsContext igc) {
		super(paramOptions, igc);
		setInterruptible(true);
		prepareRenderQuality(igc);
	}

}
