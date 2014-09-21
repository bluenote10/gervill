/*
 * Copyright (c) 2007 by Karl Helgason
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package simplemidiplayer;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.UnsupportedEncodingException;

import javax.sound.midi.Instrument;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.Patch;
import javax.sound.midi.Sequence;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Track;
import javax.sound.midi.VoiceStatus;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class InfoFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	SimpleMidiPlayer midiplayer;

	DefaultTableModel seqmodel;

	DefaultTableModel sbkmodel;

	DefaultTableModel chmodel;

	DefaultTableModel vocmodel;

	Sequence seq = null;

	Soundbank sbk = null;

	JTabbedPane tabs;

	JPanel seqtab;

	JPanel sbktab;

	JLabel sbkinfolab;

	JPanel chtab;

	JPanel voctab;

	public InfoFrame(SimpleMidiPlayer midiplayer) {

		this.midiplayer = midiplayer;
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		setContentPane(panel);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		panel.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		tabs = new JTabbedPane();
		panel.add(tabs);

		seqtab = new JPanel();
		seqtab.setLayout(new BorderLayout());
		JTable seqtable = new JTable();

		seqmodel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		seqmodel.addColumn("Track");
		seqmodel.addColumn("Channel");
		seqmodel.addColumn("Patch");
		seqmodel.addColumn("Instrument");
		seqmodel.addColumn("Name");
		seqtable.setModel(seqmodel);
		seqtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		seqtable.getColumnModel().getColumn(0).setPreferredWidth(65);
		seqtable.getColumnModel().getColumn(1).setPreferredWidth(65);
		seqtable.getColumnModel().getColumn(2).setPreferredWidth(65);
		seqtable.getColumnModel().getColumn(3).setPreferredWidth(100);
		seqtable.getColumnModel().getColumn(4).setPreferredWidth(200);

		seqtab.add(new JScrollPane(seqtable));
		seqtab.setOpaque(false);
		tabs.addTab("Sequence", seqtab);

		sbktab = new JPanel();
		sbkinfolab = new JLabel();
		sbkinfolab.setFont(sbkinfolab.getFont().deriveFont(Font.PLAIN));
		sbkinfolab.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
		sbktab.setLayout(new BorderLayout());
		sbktab.add(sbkinfolab, BorderLayout.NORTH);
		JTable sbktable = new JTable();

		sbkmodel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		sbkmodel.addColumn("Patch");
		sbkmodel.addColumn("Name");
		sbkmodel.addColumn("Type");
		sbktable.setModel(sbkmodel);
		sbktable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		sbktable.getColumnModel().getColumn(0).setPreferredWidth(80);
		sbktable.getColumnModel().getColumn(1).setPreferredWidth(200);
		sbktable.getColumnModel().getColumn(2).setPreferredWidth(200);

		sbktab.add(new JScrollPane(sbktable));
		sbktab.setOpaque(false);
		tabs.addTab("Soundbank", sbktab);

		chtab = new JPanel();
		chtab.setLayout(new BorderLayout());
		JTable chtable = new JTable();

		chmodel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		chmodel.addColumn("Channel");
		chmodel.addColumn("Instrument");
		chmodel.addColumn("Pitch");
		chmodel.addColumn("Volume");
		chmodel.addColumn("Pan");
		chmodel.addColumn("Reverb");
		chmodel.addColumn("Chorus");
		chtable.setModel(chmodel);
		chtable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		chtable.getColumnModel().getColumn(0).setPreferredWidth(65);
		chtable.getColumnModel().getColumn(1).setPreferredWidth(100);
		chtable.getColumnModel().getColumn(2).setPreferredWidth(65);
		chtable.getColumnModel().getColumn(3).setPreferredWidth(65);
		chtable.getColumnModel().getColumn(4).setPreferredWidth(65);
		chtable.getColumnModel().getColumn(5).setPreferredWidth(65);
		chtable.getColumnModel().getColumn(6).setPreferredWidth(65);
		chmodel.addRow(new Object[] { "1", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "2", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "3", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "4", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "5", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "6", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "7", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "8", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "9", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "10", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "11", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "12", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "13", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "14", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "15", "", 0, 100, 0, 64, 0 });
		chmodel.addRow(new Object[] { "16", "", 0, 100, 0, 64, 0 });

		chtab.add(new JScrollPane(chtable));
		chtab.setOpaque(false);
		tabs.addTab("Channels", chtab);

		voctab = new JPanel();
		voctab.setLayout(new BorderLayout());
		JTable voctable = new JTable();

		vocmodel = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		vocmodel.addColumn("Active");
		vocmodel.addColumn("Channel");
		vocmodel.addColumn("Bank");
		vocmodel.addColumn("Program");
		vocmodel.addColumn("Note");
		vocmodel.addColumn("Volume");
		voctable.setModel(vocmodel);
		voctable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		voctable.getColumnModel().getColumn(0).setPreferredWidth(65);
		voctable.getColumnModel().getColumn(1).setPreferredWidth(64);
		voctable.getColumnModel().getColumn(2).setPreferredWidth(65);
		voctable.getColumnModel().getColumn(3).setPreferredWidth(65);
		voctable.getColumnModel().getColumn(4).setPreferredWidth(65);
		voctable.getColumnModel().getColumn(5).setPreferredWidth(65);

		voctab.add(new JScrollPane(voctable));
		voctab.setOpaque(false);
		tabs.addTab("Voices", voctab);

		ImageIcon swan = new javax.swing.ImageIcon(getClass().getResource(
				"/simplemidiplayer/swan.png"));
		setIconImage(swan.getImage());
		setTitle("Gervill - MIDI Player - Info");
		setSize(550, 350);
		// setLocationByPlatform(true);

		SimpleMidiPlayer.centerWindow(this);

	}

	public void updateDisplay() {
		if (!isVisible())
			return;
		if (!midiplayer.player_running)
			return;

		if (tabs.getSelectedComponent() == seqtab) {
			if (midiplayer.seq != seq) {
				seq = midiplayer.seq;
				while (seqmodel.getRowCount() != 0)
					seqmodel.removeRow(0);

				int row = 0;
				for (Track track : seq.getTracks()) {

					int channel = 0;
					int program = 0;
					int bank_lsb = -1;
					int bank_msb = -1;
					String instext = "";
					String tracktext = "";

					int evcount = track.size();
					for (int i = 0; i < evcount; i++) {
						MidiEvent event = track.get(i);
						if (event.getTick() != 0)
							break;
						if (event.getMessage() instanceof MetaMessage) {
							MetaMessage mmsg = (MetaMessage) event.getMessage();
							try {
								if (mmsg.getType() == 3)
									tracktext = new String(mmsg.getData(),
											"Latin1");
								if (mmsg.getType() == 4)
									instext = new String(mmsg.getData(),
											"Latin1");
							} catch (UnsupportedEncodingException e) {
							}
						}
						if (event.getMessage() instanceof ShortMessage) {
							ShortMessage smsg = (ShortMessage) event
									.getMessage();
							channel = smsg.getChannel() + 1;

							if (smsg.getCommand() == ShortMessage.PROGRAM_CHANGE)
								program = smsg.getData1();
							if (smsg.getCommand() == ShortMessage.CONTROL_CHANGE) {
								if (smsg.getData1() == 0)
									bank_msb = smsg.getData2();
								if (smsg.getData1() == 32)
									bank_lsb = smsg.getData2();
							}

						}

					}

					String[] rowdata = new String[5];

					if (instext.length() == 0)
						if (midiplayer.sbk != null) {
							int bank = 0;
							if (bank_msb != -1)
								bank += bank_msb * 128;
							if (bank_lsb != -1)
								bank += bank_lsb;
							Patch patch = new Patch(bank, program);
							Instrument ins = midiplayer.sbk
									.getInstrument(patch);
							if (ins != null)
								instext = ins.getName();
						}

					rowdata[0] = "" + row;
					rowdata[1] = "" + channel;
					rowdata[2] = "0," + program;
					rowdata[3] = instext;
					rowdata[4] = tracktext;

					if (bank_msb != -1 || bank_lsb != -1) {
						if (bank_msb == -1)
							bank_msb = 0;
						if (bank_lsb == -1)
							bank_lsb = 0;
						rowdata[2] = (bank_msb * 128 + bank_lsb) + ","
								+ program;
					}

					seqmodel.addRow(rowdata);
					row++;
				}

			}
		}

		if (tabs.getSelectedComponent() == sbktab) {
			if (midiplayer.sbk != sbk)
				if (midiplayer.sbk != null) {
					sbk = midiplayer.sbk;
					while (sbkmodel.getRowCount() != 0)
						sbkmodel.removeRow(0);

					sbkinfolab.setText("<html><body><table>"
							+ "<tr><td><b>Name:</b></td><td>" + sbk.getName()
							+ "</td>" + "<td><b>  Description:</b></td><td>"
							+ sbk.getDescription() + "</td></tr>"
							+ "<tr><td><b>Version:</b></td><td>"
							+ sbk.getVersion() + "</td>"
							+ "<td><b>  Vendor:</b></td><td>" + sbk.getVendor()
							+ "</td></tr></table>");

					for (Instrument ins : sbk.getInstruments()) {
						String[] rowdata = new String[3];
						rowdata[0] = ins.getPatch().getBank() + ","
								+ ins.getPatch().getProgram();
						rowdata[1] = ins.getName();
						rowdata[2] = ins.getClass().getSimpleName();
						sbkmodel.addRow(rowdata);
					}
				}
		}

		if (tabs.getSelectedComponent() == chtab) {
			Soundbank sbk = midiplayer.sbk;
			MidiChannel[] channels = midiplayer.softsynth.getChannels();
			for (int i = 0; i < 16; i++) {
				MidiChannel channel = channels[i];
				if (sbk != null) {
					Patch patch = new Patch(channel.getController(0) * 128
							+ channel.getController(32), channel.getProgram());
					Instrument ins = sbk.getInstrument(patch);
					if (ins != null)
						chmodel.setValueAt(channel.getProgram() + ": "
								+ ins.getName(), i, 1);
				}

				chmodel.setValueAt(channel.getPitchBend() - 8192, i, 2);
				chmodel.setValueAt(channel.getController(7), i, 3);
				chmodel.setValueAt(channel.getController(10) - 64, i, 4);
				chmodel.setValueAt(channel.getController(91), i, 5);
				chmodel.setValueAt(channel.getController(93), i, 6);
			}
		}

		if (tabs.getSelectedComponent() == voctab) {
			{
				VoiceStatus[] voices = midiplayer.softsynth.getVoiceStatus();
				while (vocmodel.getRowCount() > voices.length)
					vocmodel.removeRow(vocmodel.getRowCount() - 1);
				while (vocmodel.getRowCount() < voices.length)
					vocmodel.addRow(new Object[] { false, 0, 0, 0, 0, 0 });
				for (int i = 0; i < voices.length; i++) {
					VoiceStatus voc = voices[i];
					vocmodel.setValueAt(voc.active, i, 0);
					vocmodel.setValueAt(voc.channel + 1, i, 1);
					vocmodel.setValueAt(voc.bank, i, 2);
					vocmodel.setValueAt(voc.program, i, 3);
					vocmodel.setValueAt(voc.note, i, 4);
					vocmodel.setValueAt(voc.volume, i, 5);
				}
			}
		}
	}
}
