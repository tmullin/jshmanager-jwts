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
import jshm.wts.gui.GUI;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * @author Tim Mullin
 *
 */
public class JWTSubmitter {
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
}
