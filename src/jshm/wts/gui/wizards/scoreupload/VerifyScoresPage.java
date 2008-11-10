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
package jshm.wts.gui.wizards.scoreupload;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;

import jshm.wts.gui.GUI;

import org.netbeans.spi.wizard.WizardPage;

public class VerifyScoresPage extends WizardPage {
	public VerifyScoresPage(GUI gui) {
		super("verifyScores", "Verify scores", true);
		
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(
			new JLabel("<html>" + gui.getScores().size() + " scores are about to be uploaded.<br>" +
						"Click Finish to upload them.<br>" +
						"Do not ask me to remove this page to make for faster uploading."),
			BorderLayout.CENTER);
		
		setPreferredSize(new Dimension(600, 400));
		setLayout(new BorderLayout(0, 10));
		add(northPanel, BorderLayout.NORTH);
		
		putWizardData("scores", gui.getScores());
	}
}
