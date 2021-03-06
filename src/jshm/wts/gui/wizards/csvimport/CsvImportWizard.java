/*
 * -----LICENSE START-----
 * JSHManager - A Java-based tool for managing one's ScoreHero account.
 * Copyright (C) 2008 Tim Mullin
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 * -----LICENSE END-----
 */
package jshm.wts.gui.wizards.csvimport;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jshm.Difficulty;
import jshm.Platform;
import jshm.wts.Instrument;
import jshm.wts.WTScore;
import jshm.wts.csv.CsvColumn;
import jshm.wts.csv.CsvParser;
import jshm.wts.gui.GUI;

//import jshm.gui.ProgressDialog;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;
import org.netbeans.spi.wizard.DeferredWizardResult;
import org.netbeans.spi.wizard.ResultProgressHandle;
import org.netbeans.spi.wizard.Summary;
import org.netbeans.spi.wizard.Wizard;
import org.netbeans.spi.wizard.WizardException;
import org.netbeans.spi.wizard.WizardPage;
import org.netbeans.spi.wizard.WizardPage.WizardResultProducer;

@SuppressWarnings("unchecked")
public class CsvImportWizard {
	static final Logger LOG = Logger.getLogger(CsvImportWizard.class.getName());
	
	public static Wizard createWizard(final GUI gui) {
		final CsvImportWizard me = new CsvImportWizard(gui);
		
		return WizardPage.createWizard("Import Scores from CSV File",
			new WizardPage[] {
				new OptionsPage(gui),
				new ColumnsPage()
			},
			me.resultProducer);
	}

	final GUI gui;
	
	private CsvImportWizard(final GUI gui) {
		this.gui = gui;
	}
	
	private WizardResultProducer resultProducer = new WizardResultProducer() {
		@Override
		public boolean cancel(Map settings) {
			return true;
//			return JOptionPane.YES_OPTION ==
//				JOptionPane.showConfirmDialog(null,
//					"Are you sure you want to cancel?", "Question",
//					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		}
	
		@Override
		public Object finish(Map wizardData) throws WizardException {
//			System.out.println("Keys: ");
//			
//			for (Object key : wizardData.keySet()) {
//				System.out.println("  " + key + ": " + wizardData.get(key));
//			}
//			
//			return null;
			return deferredResult;
		}
	};
	
	private DeferredWizardResult deferredResult = new DeferredWizardResult() {
		@Override
		public void start(Map settings, ResultProgressHandle progress) {
			try {
				progress.setBusy("Parsing CSV file...");
				
				File f = new File((String) settings.get("file"));
				boolean inferColumns = true;
				
				try {
					inferColumns = (Boolean) settings.get("inferColumns");
				} catch (Exception e) {}
					
				CsvColumn[] columns = null;
				
				if (!inferColumns) {
					Object[] columnObjs = (Object[]) settings.get("columns");
					
					columns = new CsvColumn[columnObjs.length];
					
					for (int i = 0; i < columnObjs.length; i++)
						columns[i] = (CsvColumn) columnObjs[i];
				}
				
				List<String> summary = new ArrayList<String>();
				List<WTScore> scores = CsvParser.parse(summary, f, columns, 
					(Platform) settings.get("platform"),
					(Instrument) settings.get("instrument"),
					(Difficulty) settings.get("difficulty"),
					(Instrument) settings.get("drums"));
				
				summary.add("Parsed " + scores.size() + " scores");
				
				progress.setBusy("Inserting scores...");
				gui.addScores(scores);
				
				summary.add(
					String.format("Done. Added %s scores", scores.size()));
				
				progress.finished(
					Summary.create(
						summary.toArray(new String[0]), null));
			} catch (Throwable e) {
				LOG.log(Level.WARNING, "Failed to parse CSV file", e);
				progress.failed("Failed to parse CSV file: " + e.getMessage(), false);
				ErrorInfo ei = new ErrorInfo("Error", "Failed to parse CSV file", null, null, e, null, null);
				JXErrorPane.showDialog(null, ei);
				return;
			}
		}
	};
}
