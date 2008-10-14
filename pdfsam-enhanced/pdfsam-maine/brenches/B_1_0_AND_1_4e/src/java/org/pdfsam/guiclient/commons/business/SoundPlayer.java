/*
 * Created on 12-Oct-2008
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
package org.pdfsam.guiclient.commons.business;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;

import org.apache.log4j.Logger;
import org.pdfsam.guiclient.configuration.Configuration;
import org.pdfsam.i18n.GettextResource;

/**
 * Plays sounds
 * @author Andrea Vacondio
 *
 */
public class SoundPlayer {

	private static final Logger log = Logger.getLogger(SoundPlayer.class.getPackage().getName());
	
	private static final String SOUND = "/resources/sounds/ok_sound.wav";
	private static final String ERROR_SOUND = "/resources/sounds/error_sound.wav";
	
	private static SoundPlayer player= null;
	private Clip errorClip;
	private Clip soundClip;
		
	public static synchronized SoundPlayer getInstance() { 
		if (player == null){
			player = new SoundPlayer();
		}
		return player;
	}
	
	/**
	 * Plays an error sound
	 */
	public void playErrorSound(){
		try{
			if(errorClip == null){
				AudioInputStream  sound = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(ERROR_SOUND));
				DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
				errorClip = (Clip) AudioSystem.getLine(info);
				errorClip.open(sound);
			}
			execute(new PlayThread(errorClip));
		}catch (Exception e){
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error playing sound"),e);
		}
	}
	
	/**
	 * Plays a sound
	 */
	public void playSound(){
		try{
			if(soundClip == null){
				AudioInputStream  sound = AudioSystem.getAudioInputStream(this.getClass().getResourceAsStream(SOUND));
				DataLine.Info info = new DataLine.Info(Clip.class, sound.getFormat());
				soundClip = (Clip) AudioSystem.getLine(info);
				soundClip.open(sound);
			}
			execute(new PlayThread(soundClip));
		}catch (Exception e){
			log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error playing sound"),e);
		}
	}
	
	/**
	 * executes r
	 * @param r
	 */
	private void execute(Runnable r){
		new Thread(r).start();
	}
	
	/**
	 * Plays the sound
	 * @author Andrea Vacondio
	 *
	 */
	private class PlayThread extends Thread {
		private Clip clip;
		
		
        /**
		 * @param clip
		 */
		public PlayThread(Clip clip) {
			super();
			this.clip = clip;
		}


		public void run() {
            try{
            	clip.start();
            }catch(Exception e){
            	log.error(GettextResource.gettext(Configuration.getInstance().getI18nResourceBundle(),"Error playing sound"),e);
            }
        }
    }
}
