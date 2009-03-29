package jshm.wts;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import jshm.Config;
import jshm.JSHManager;
import jshm.util.Util;
import jshm.wts.gui.GUI;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * @author Tim Mullin
 * 
 */
public class JWTSubmitter {
	public static class Version {
		public static final String NAME = "JWTSubmitter";
		public static final int MAJOR = 0;
		public static final int MINOR = 0;
		public static final int POINT = 2;
		public static final boolean IS_BETA = false;
		
		public static final String
		MIN_JSHM_VERSION = "0.2.4",
		VERSION =
			String.format("%s.%s.%s", MAJOR, MINOR, POINT),
		STRING =
			String.format("%s%s", VERSION, IS_BETA ? " beta" : "");
		
		public static final String LAST = "0.0.1";
		public static final int LAST_REVISION = 269;
		
		public static final java.util.Date DATE = initDate("$Date$");
		public static final int REVISION = initRevision("$Revision$");
		
		private static java.util.Date initDate(final String _APP_DATE) {
			try {
				// $Date$
				return new java.text.SimpleDateFormat("$'Date': yyyy-MM-dd HH:mm:ss Z (EE, dd MMM yyyy) $")
					.parse(_APP_DATE);
			} catch (java.text.ParseException e) {}
			
			return new java.util.Date();
		}
			
		private static int initRevision(final String _APP_REVISION) {
			try {
				// $Revision$
				return Integer.parseInt(_APP_REVISION.replaceAll("[^\\d]+", ""));
			} catch (NumberFormatException e) {}
			
			return 0;
		}
	}
	
	
	static final Logger LOG = Logger.getLogger(JWTSubmitter.class.getName());
	static GUI gui = null;
	
	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		try {			
			// Ensure any uncaught exceptions are logged so that bugs
			// can be found more easily.
			Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
				public void uncaughtException(Thread thread, Throwable thrown) {
					LOG.log(Level.WARNING, "Exception in thread \"" + thread.getName() + "\"", thrown);
				}
			});
			
			checkJSHMVersion();
			
			for (String dir : new String[] {"logs", "wts"}) {
				File f = new File("data/" + dir);
				if (f.exists()) continue;
				
				LOG.fine("Creating data folder: " + f.getPath());
				
				try {
					f.mkdirs();
				} catch (Throwable e) {
					fail("Failed to create data folders", e, -1);
				}
			}
			
			// have to do this after creating the log folder because
			// the logger won't make the dirs for us
			try {
				jshm.logging.Log.reloadConfig();
			} catch (Throwable e) {
				fail("Unable to load logger configuration", e, -3);
			}
			
			Config.init();

	        try {
	        	WTSong.load();
	        } catch (Throwable e) {
	        	LOG.log(Level.WARNING, "Unable to load song cache", e);
	        }
			
	        final List<WTScore> scores = new ArrayList<WTScore>();
	        
	        try {
	        	scores.addAll(WTScore.load());
	        } catch (Throwable e) {
	        	LOG.log(Level.WARNING, "Unable to load score cache", e);
	        }
	        
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {	
					try {
						gui = new GUI();
						gui.setVisible(true);
						gui.toFront();
						gui.setScores(scores);
					} catch (Throwable t) {
						fail(t.toString(), t, -43);
					}
				}
			});
		} catch (Throwable t) {
			fail("Unknown error", t, -42);
		}
	}
	
	/**
	 * This should be called when we want to exit the program.
	 * It will handling cleaning up stuff.
	 */
	public static void dispose() {
		if (null != gui) {
			WTScore.save(gui.getScores());
			WTSong.save();
			gui.dispose();
		}
		
		Config.write();
		
		System.exit(0);
	}
	
	private static void fail(String message, Throwable thrown, int exitCode) {
		LOG.log(Level.SEVERE, message, thrown);
		
		try {
			ErrorInfo ei = new ErrorInfo("Error",
				message, null, null, thrown, null, null);
			JXErrorPane.showDialog(null, ei);
		} catch (Exception t) {
			// in case there was a ClassNotFound or something
			
			LOG.log(Level.SEVERE, "Error displaying JXErrorPane", t);
			
			StringWriter sw = new StringWriter();
			thrown.printStackTrace(new PrintWriter(sw));
			
			JOptionPane.showMessageDialog(null,
				sw.toString(),
				message, JOptionPane.ERROR_MESSAGE);
		}
		
		System.exit(exitCode);
	}
	
	
	static void checkJSHMVersion() {
		if (Util.versionCompare(JSHManager.Version.VERSION, Version.MIN_JSHM_VERSION) < 0) {
			JOptionPane.showMessageDialog(null,
				"JWTSubmitter requires JSHManager " +
				Version.MIN_JSHM_VERSION + " but only " +
				JSHManager.Version.VERSION + " was found.\n" +
				"Please update to the latest version of JSHMangaer.", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(-4);
		}
	}
}
