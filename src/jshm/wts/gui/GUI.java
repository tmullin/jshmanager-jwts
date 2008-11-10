/*
 * GUI.java
 *
 * Created on November 8, 2008, 7:20 PM
 */

package jshm.wts.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SwingWorker;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.netbeans.spi.wizard.Wizard;

import jshm.Config;
import jshm.JSHManager;
import jshm.Platform;
import jshm.Difficulty;
import jshm.gui.GuiUtil;
import jshm.gui.ProgressDialog;
import jshm.gui.TextFileViewerDialog;
import jshm.util.PasteBin;
import jshm.wts.gui.wizards.csvimport.CsvImportWizard;
import jshm.wts.gui.wizards.scoreupload.ScoreUploadWizard;
import jshm.wts.Instrument;
import jshm.wts.JWTSubmitter;
import jshm.wts.WTScore;
import jshm.wts.WTSong;
import jshm.wts.gui.components.MyJXTable;
import jshm.wts.gui.editors.SongCellEditor;
import jshm.wts.gui.models.WTScoreTableModel;
import jshm.wts.scraper.WTSongScraper;


/**
 *
 * @author  Tim
 */
public class GUI extends javax.swing.JFrame {
	static final Logger LOG = Logger.getLogger(GUI.class.getName());
	
	WTScoreTableModel tableModel;
	TextFileViewerDialog textFileViewer;
	Timer autoSaveTimer;
	
    /** Creates new form GUI */
    public GUI() {
    	GuiUtil.init();
    	
        initComponents();
        
        setTitle("");
        
        textFileViewer = new TextFileViewerDialog(this, true);
        
        try {
			setSize(Config.getInt("wts.window.width"), Config.getInt("wts.window.height"));
	        setLocation(Config.getInt("wts.window.x"), Config.getInt("wts.window.y"));
	        
	        if (Config.getBool("wts.window.maximized")) {
				setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
	        }
        } catch (NullPointerException e) {}
		
        try {
        	platformCombo.setSelectedItem(Platform.valueOf(Config.get("wts.platform")));
        	instrumentCombo.setSelectedItem(Instrument.valueOf(Config.get("wts.instrument")));
        	diffCombo.setSelectedItem(Difficulty.valueOf(Config.get("wts.difficulty")));
        } catch (NullPointerException e) {
        } catch (IllegalArgumentException e) {
        }
        
        tableModel = new WTScoreTableModel();
        table.setModel(tableModel);
        tableModel.setParent(table);
                
//        Random r = new Random();
//        for (int i = 0; i < 10; i++) {
//        	WTScore s = new WTScore();
//        	s.setPlatform(Platform.values()[r.nextInt(4)]);
//        	s.setDifficulty(Difficulty.values()[r.nextInt(4)]);
//        	s.setSong(WTSong.getList().get(r.nextInt(WTSong.getList().size())));
//        	s.setInstrument(Instrument.values()[r.nextInt(5)]);
//        	s.setScore(r.nextInt(999999) + 1);
//        	s.setRating(r.nextInt(3) + 3);
//        	s.setPercent(r.nextInt(99) + 1);
//        	s.setStreak(r.nextInt(2999) + 1);
//        	tableModel.addScore(s);
//        }
        
        
        // remove key mappings for events that are bound to menu items
        
        final List<KeyStroke> keyStokesToRemove = new ArrayList<KeyStroke>();
        
        for (Component c : fileMenu.getMenuComponents()) {
        	if (c instanceof JMenuItem && null != ((JMenuItem) c).getAccelerator()) {
//        		System.out.println("Removing: " + ((JMenuItem) c).getAccelerator());
        		keyStokesToRemove.add(((JMenuItem) c).getAccelerator());
        	}
    	}

        ((MyJXTable) table).setStartEditorIgnoreKeyStrokes(keyStokesToRemove);
        
        
        // create auto save timer
        autoSaveTimer = new Timer("autosave");
        autoSaveTimer.scheduleAtFixedRate(new TimerTask() {
			public void run() {
				LOG.finer("Autosaving data");
				
				try {
					WTSong.save();
					WTScore.save(getScores());
				} catch (Throwable t) {
					LOG.log(Level.WARNING, "Exception in autosave thread", t);
				}
			}
        }, 60000, 60000);
    }
    
    public void addScore(WTScore score) {
    	((WTScoreTableModel) table.getModel()).addScore(score);
    }
    
    public void addScores(List<WTScore> scores) {
    	((WTScoreTableModel) table.getModel()).addScores(scores);
    }
    
    public void setScores(List<WTScore> scores) {
    	((WTScoreTableModel) table.getModel()).setScores(scores);
    }
    
    public List<WTScore> getScores() {
    	return ((WTScoreTableModel) table.getModel()).getScores();
    }
    
    /**
     * 
     * @param indicies indicies to remove in model coordinates
     */
    public void removeScores(int ... indicies) {
    	((WTScoreTableModel) table.getModel()).removeScores(indicies);
    }

    public Platform getCurrentPlatform() {
    	return
    	platformCombo.getSelectedIndex() == 0
    	? null
    	: (Platform) platformCombo.getSelectedItem();
    }
    
    public Instrument getCurrentInstrument() {
    	return
    	instrumentCombo.getSelectedIndex() == 0
    	? null
    	: (Instrument) instrumentCombo.getSelectedItem();
    }
    
    public Difficulty getCurrentDifficulty() {
    	return
    	diffCombo.getSelectedIndex() == 0
    	? null
    	: (Difficulty) diffCombo.getSelectedItem();
    }
    
    /**
     * 
     * @return true if the user has selected a platform, instrument, <i>and</i> difficulty, false otherwise
     */
    private boolean checkCombos() {
    	if (null == getCurrentPlatform() || null == getCurrentInstrument() || null == getCurrentDifficulty()) {
    		JOptionPane.showMessageDialog(this, "Please select a platform, instrument, and difficulty first.", "Error", JOptionPane.WARNING_MESSAGE);
    		return false;
    	}
    	
    	return true;
    }
    
    private boolean checkSongCount() {
    	if (0 == WTSong.getList().size()) {
    		JOptionPane.showMessageDialog(this, "Please download the song data (from the File menu) first.", "Error", JOptionPane.WARNING_MESSAGE);
    		return false;
    	}
    	
    	return true;
    }
    
    private boolean checkScoreCount() {
    	if (0 == getScores().size()) {
    		JOptionPane.showMessageDialog(this, "Please enter some scores first.", "Error", JOptionPane.WARNING_MESSAGE);
    		return false;
    	}
    	
    	return true;
    }
    
    @Override
    public void setTitle(String title) {
    	super.setTitle(
    		(title.isEmpty() ? "" : title + " - ") +
    		JWTSubmitter.Version.NAME + " " + JWTSubmitter.Version.STRING);
    }
    
	@Override
    public void dispose() {    	
		autoSaveTimer.cancel();
		
    	Dimension size = getSize();
    	Point loc = getLocation();
    	final boolean isMaximized = JFrame.MAXIMIZED_BOTH ==
    		(getExtendedState() & JFrame.MAXIMIZED_BOTH);
    	
    	LOG.finer(
    		String.format("Saving GUI's location %s and size %s, maximized=%s",
    		loc, size, isMaximized));
    	
    	// no need to save this stuff if the frame is maximized
    	if (!isMaximized) {
    		Config.set("wts.window.maximized", false);
    		
    		Config.set("wts.window.width", size.width);
    		Config.set("wts.window.height", size.height);
    		Config.set("wts.window.x", loc.x);
    		Config.set("wts.window.y", loc.y);
    	} else {
    		Config.set("wts.window.maximized", true);
    	}	
    	
    	size = textFileViewer.getSize();
    	LOG.finer("Saving TextFileViewer's size " + size);
		Config.set("window.textfileviewer.width", size.width);
		Config.set("window.textfileviewer.height", size.height);
    	
    	LOG.finer(
    		String.format("Saving platform=%s, instrument=%s, difficulty=%s",
    		getCurrentPlatform(), getCurrentInstrument(), getCurrentDifficulty()));
    		
    	if (null != getCurrentPlatform())
    		Config.set("wts.platform", getCurrentPlatform().name());
    	if (null != getCurrentInstrument())
    		Config.set("wts.instrument", getCurrentInstrument().name());
    	if (null != getCurrentDifficulty())
    		Config.set("wts.difficulty", getCurrentDifficulty());
    	
    	super.dispose();
	}
	
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
//    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        table = new MyJXTable();
        jPanel1 = new javax.swing.JPanel();
        platformCombo = new JComboBox(new Object[] {"Select Platform", Platform.PS2, Platform.XBOX360, Platform.PS3, Platform.WII});
        instrumentCombo = new JComboBox(new Object[] {"Select Instrument", Instrument.GUITAR, Instrument.BASS, Instrument.GHWT_DRUMS, Instrument.RB_DRUMS, Instrument.VOCALS});
        diffCombo = new JComboBox(new Object[] {"Select Difficulty", Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD, Difficulty.EXPERT});
        jLabel1 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        downloadSongDataMenuItem = new javax.swing.JMenuItem();
        uploadMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        addScoreMenuItem = new javax.swing.JMenuItem();
        deleteSelectedScoresMenuItem = new javax.swing.JMenuItem();
        csvImportMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        saveMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        viewLogMenu = new javax.swing.JMenu();
        uploadLogsMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("JWTSubmitter");
        setIconImage(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/wts/resources/images/GHWT_32.png")).getImage());
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        table.setAutoCreateRowSorter(true);
        table.setColumnControlVisible(true);
        table.setHorizontalScrollEnabled(true);
        table.setIntercellSpacing(new java.awt.Dimension(0, 0));
        table.setShowGrid(false);
        jScrollPane1.setViewportView(table);

        getContentPane().add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Defaults:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(platformCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(instrumentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(diffCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(571, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(platformCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(instrumentCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addComponent(diffCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        fileMenu.setMnemonic('F');
        fileMenu.setText("File");

        downloadSongDataMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/down32.png")));
        downloadSongDataMenuItem.setMnemonic('D');
        downloadSongDataMenuItem.setText("Download Song Data...");
        downloadSongDataMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadSongDataMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(downloadSongDataMenuItem);

        uploadMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        uploadMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/up32.png")));
        uploadMenuItem.setMnemonic('U');
        uploadMenuItem.setText("Upload to ScoreHero...");
        uploadMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(uploadMenuItem);
        fileMenu.add(jSeparator2);

        addScoreMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_INSERT, 0));
        addScoreMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/add32.png")));
        addScoreMenuItem.setText("Add New Score");
        addScoreMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addScoreMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(addScoreMenuItem);

        deleteSelectedScoresMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_DELETE, 0));
        deleteSelectedScoresMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/delete32.png")));
        deleteSelectedScoresMenuItem.setText("Delete Selected Score(s)");
        deleteSelectedScoresMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedScoresMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(deleteSelectedScoresMenuItem);

        csvImportMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/addfile32.png")));
        csvImportMenuItem.setMnemonic('I');
        csvImportMenuItem.setText("Import from CSV File...");
        csvImportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                csvImportMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(csvImportMenuItem);
        fileMenu.add(jSeparator3);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/save32.png")));
        saveMenuItem.setText("Save Data");
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        exitMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/close32.png")));
        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        jMenuBar1.add(fileMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");

        viewLogMenu.setMnemonic('L');
        viewLogMenu.setText("View Log");

        uploadLogsMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/up32.png")));
        uploadLogsMenuItem.setMnemonic('U');
        uploadLogsMenuItem.setText("Upload for Debugging...");
        uploadLogsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadLogsMenuItemActionPerformed(evt);
            }
        });
        viewLogMenu.add(uploadLogsMenuItem);
        viewLogMenu.add(jSeparator4);

        initViewLogMenu();

        helpMenu.add(viewLogMenu);
        helpMenu.add(jSeparator5);

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(JSHManager.class.getResource("/jshm/resources/images/toolbar/infoabout32.png")));
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void initViewLogMenu() {
		try {
			final File logDir = new File("data/logs");
			String[] files = logDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			});
			
			for (final String s : files) {
				JMenuItem item = new JMenuItem(s);
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						showTextFileViewer(logDir.getPath() + "/" + s);
					}
				});
				
				viewLogMenu.add(item);
			}
		} catch (Exception e) {
			LOG.log(Level.WARNING, "Error initializing View Log menu", e);
		}
    }
    
    public void showTextFileViewer(final String file) {
    	try {
    		textFileViewer.setVisible(new File(file));
    	} catch (Exception e) {
    		LOG.log(Level.WARNING, "Unknown error displaying TextFileViewer", e);
    		ErrorInfo ei = new ErrorInfo("Error", "Unknown error displaying TextFileViewer", null, null, e, null, null);
    		JXErrorPane.showDialog(GUI.this, ei);
    	}
    }
    
private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
	JWTSubmitter.dispose();
}//GEN-LAST:event_exitMenuItemActionPerformed

private void downloadSongDataMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downloadSongDataMenuItemActionPerformed
	if (!checkCombos()) return;

	if (null != table.getCellEditor())
		table.getCellEditor().stopCellEditing();
	
	final ProgressDialog progress = new ProgressDialog(this);
	progress.setBusy("Downloading song data...");
	
	new SwingWorker<Void, Void>() {
		Throwable thrown = null;
		List<WTSong> songs = null;
		
		@Override
		protected Void doInBackground() throws Exception {
			try {
				WTSong.clear();
				songs = WTSongScraper.scrape(getCurrentPlatform());
				SongCellEditor.updateSongs();
			} catch (Throwable t) {
				thrown = t;
			}
			
			return null;
		}
		
		@Override
		public void done() {
			progress.dispose();
			
			if (null != songs) {
				if (songs.size() == 0)
					thrown = new Exception("No songs were scraped for an unknown reason");
			}
			
			if (null != thrown) {
				LOG.log(Level.WARNING, "Unable to download song data", thrown);
				ErrorInfo ei = new ErrorInfo("Error",
					"Unable to download song data", null, null, thrown, null, null);
				JXErrorPane.showDialog(null, ei);
			}
		}
	}.execute();
}//GEN-LAST:event_downloadSongDataMenuItemActionPerformed

private void csvImportMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_csvImportMenuItemActionPerformed
	if (!(checkSongCount() && checkCombos())) return;
	
	Wizard wiz = CsvImportWizard
		.createWizard(this);
	wiz.show();
}//GEN-LAST:event_csvImportMenuItemActionPerformed

private void uploadMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadMenuItemActionPerformed
	if (!(checkScoreCount() && checkSongCount() && checkCombos())) return;
	
	Wizard wiz = ScoreUploadWizard
		.createWizard(this);
	wiz.show();
}//GEN-LAST:event_uploadMenuItemActionPerformed

private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
	JWTSubmitter.dispose();
}//GEN-LAST:event_formWindowClosing

private void addScoreMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addScoreMenuItemActionPerformed
	if (!(checkSongCount() && checkCombos())) return;
	
	WTScore s = new WTScore();
	s.setPlatform(getCurrentPlatform());
	s.setInstrument(getCurrentInstrument());
	s.setDifficulty(getCurrentDifficulty());
		
	s.setSong(table.getRowCount() > 0
	? getScores().get(
		table.convertRowIndexToModel(
			table.getSelectedRow() >= 0
			? table.getSelectedRow()
			: table.getRowCount() - 1)).getSong()
	: WTSong.getList().get(0));
	
	addScore(s);
}//GEN-LAST:event_addScoreMenuItemActionPerformed

private void deleteSelectedScoresMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedScoresMenuItemActionPerformed
	int[] selected = table.getSelectedRows();
	if (selected.length == 0) return;
	
	for (int i = 0; i < selected.length; i++) {
//		System.out.print(selected[i] + " -> ");
		selected[i] = table.convertRowIndexToModel(selected[i]);
//		System.out.println(selected[i]);
	}
	
	removeScores(selected);
}//GEN-LAST:event_deleteSelectedScoresMenuItemActionPerformed

private void uploadLogsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uploadLogsMenuItemActionPerformed
	final ProgressDialog prog = new ProgressDialog(this);
	
	final StringBuilder sb = new StringBuilder(
		"Please copy and paste the following when replying in the JSHManager thread:\n\n"	
	);
	
	new SwingWorker<Boolean, Void>() {
		public Boolean doInBackground() throws Exception {
			final File logDir = new File("data/logs");
			String[] files = logDir.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".txt");
				}
			});

			int i = 0;
			for (final String s : files) {
				File f = new File(logDir, s);
				prog.setProgress(
					String.format("Uploading %s (%s of %s)", s, i + 1, files.length),
					i, files.length);
				LOG.finer("Uploading " + f.getAbsolutePath() + " to PasteBin");
				
				sb.append(f.getName());
				sb.append(" - ");
				sb.append(PasteBin.post(f));
				sb.append('\n');
				
				i++;
			}
							
			return true;
		}
		
		public void done() {
			try {
				if (get()) {
					prog.setVisible(false);
					textFileViewer.setVisible("Logs Uploaded", sb.toString());
				}
			} catch (Exception e) {
				LOG.log(Level.SEVERE, "Error uploading logs to PasteBin", e);
				ErrorInfo ei = new ErrorInfo("Error", "Error uploading logs", null, null, e, null, null);
				JXErrorPane.showDialog(GUI.this, ei);
			} finally {
				prog.dispose();
			}
		}
	}.execute();
}//GEN-LAST:event_uploadLogsMenuItemActionPerformed

private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
	try {
		WTSong.save();
		WTScore.save(getScores());
	} catch (Throwable t) {
		ErrorInfo ei = new ErrorInfo("Error",
			"Unable to save data", null, null, t, null, null);
		JXErrorPane.showDialog(null, ei);
	}
}//GEN-LAST:event_saveMenuItemActionPerformed

private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
	JOptionPane.showMessageDialog(this,
		String.format(
			"JWTSubmitter by Tim Mullin (DarylZero)\n\n" +
			"Version: %s\n" + 
			"Date: %s\n" +
			"Revision: %s\n\n" +
			"Using JSHManager %s",
			JWTSubmitter.Version.VERSION,
			new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss")
				.format(JWTSubmitter.Version.DATE),
			JWTSubmitter.Version.REVISION,
			JSHManager.Version.VERSION
		),
		"About",
		JOptionPane.INFORMATION_MESSAGE);
}//GEN-LAST:event_aboutMenuItemActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new GUI().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenuItem addScoreMenuItem;
    private javax.swing.JMenuItem csvImportMenuItem;
    private javax.swing.JMenuItem deleteSelectedScoresMenuItem;
    private javax.swing.JComboBox diffCombo;
    private javax.swing.JMenuItem downloadSongDataMenuItem;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JComboBox instrumentCombo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JComboBox platformCombo;
    private javax.swing.JMenuItem saveMenuItem;
    private org.jdesktop.swingx.JXTable table;
    private javax.swing.JMenuItem uploadLogsMenuItem;
    private javax.swing.JMenuItem uploadMenuItem;
    private javax.swing.JMenu viewLogMenu;
    // End of variables declaration//GEN-END:variables

}
