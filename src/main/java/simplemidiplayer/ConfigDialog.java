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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Properties;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

public class ConfigDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	boolean isok = false;

	private String getValueFromList(JComboBox box) {
		if (box.isEditable())
			return box.getEditor().getItem().toString();
		return box.getSelectedItem().toString();
	}

	private void selectValueInList(JComboBox box, String value) {
		if (value == null)
			return;
		if (box.isEditable()) {
			box.getEditor().setItem(value);
		} else {
			for (int i = 0; i < box.getItemCount(); i++) {
				if (box.getItemAt(i).equals(value)) {
					box.setSelectedIndex(i);
					return;
				}
			}
		}
	}

	public boolean isOK() {
		return isok;
	}

	public ConfigDialog(JFrame parent) {
		super(parent);
		setSize(550, 400);
		// setLocationByPlatform(true);
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setTitle("Gervill - MIDI Player - Config");

		ArrayList<String> dev_list = new ArrayList<String>();
		dev_list.add("(default)");
		for (Mixer.Info info : AudioSystem.getMixerInfo()) {
			Mixer mixer = AudioSystem.getMixer(info);
			boolean hassrcline = false;
			for (Line.Info linfo : mixer.getSourceLineInfo())
				if (linfo instanceof javax.sound.sampled.DataLine.Info)
					hassrcline = true;
			if (hassrcline) {
				dev_list.add(info.getName());
			}
		}

		String[] devlist = new String[dev_list.size()];
		dev_list.toArray(devlist);

		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		setContentPane(panel);

		JPanel optpanel = new JPanel();
		optpanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		panel.add(optpanel);
		optpanel.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(3, 3, 3, 3);
		c.anchor = GridBagConstraints.WEST;

		final JComboBox co_devname = new JComboBox(devlist);
		final JComboBox co_samplerate = new JComboBox(new String[] { "44100",
				"22050", "11025" });
		co_samplerate.setSelectedIndex(0);
		co_samplerate.setEditable(true);
		final JComboBox co_channels = new JComboBox(new String[] { "1", "2" });
		co_channels.setSelectedIndex(1);
		final JComboBox co_bits = new JComboBox(new String[] { "8", "16" });
		co_bits.setSelectedIndex(1);
		final JComboBox co_latency = new JComboBox(new String[] { "100", "200",
				"400", "800" });
		co_latency.setSelectedIndex(2);
		co_latency.setEditable(true);
		final JComboBox co_polyphony = new JComboBox(new String[] { "32", "64",
				"96", "128", "256" });
		co_polyphony.setSelectedIndex(1);
		co_polyphony.setEditable(true);
		final JComboBox co_interp = new JComboBox(new String[] { "linear",
				"cubic", "sinc", "point" });
		co_interp.setSelectedIndex(0);
        
        final JComboBox co_largemode = new JComboBox(new String[] { "true",
                "false" });
        co_largemode.setSelectedIndex(1);        

		c.gridy = 0;
		c.gridx = 0;
		optpanel.add(new JLabel("Device name:"), c);
		c.gridy = 0;
		c.gridx = 1;
		optpanel.add(co_devname, c);
		c.gridy = 1;
		c.gridx = 0;
		optpanel.add(new JLabel("Sample rate (Hz):"), c);
		c.gridy = 1;
		c.gridx = 1;
		optpanel.add(co_samplerate, c);
		c.gridy = 2;
		c.gridx = 0;
		optpanel.add(new JLabel("Channels:"), c);
		c.gridy = 2;
		c.gridx = 1;
		optpanel.add(co_channels, c);
		c.gridy = 3;
		c.gridx = 0;
		optpanel.add(new JLabel("Bits:"), c);
		c.gridy = 3;
		c.gridx = 1;
		optpanel.add(co_bits, c);
		c.gridy = 4;
		c.gridx = 0;
		optpanel.add(new JLabel("Latency (msec):"), c);
		c.gridy = 4;
		c.gridx = 1;
		optpanel.add(co_latency, c);
		c.gridy = 5;
		c.gridx = 0;
		optpanel.add(new JLabel("Max polyphony:"), c);
		c.gridy = 5;
		c.gridx = 1;
		optpanel.add(co_polyphony, c);
		c.gridy = 6;
		c.gridx = 0;
		optpanel.add(new JLabel("Interpolation mode:"), c);
		c.gridy = 6;
		c.gridx = 1;
		optpanel.add(co_interp, c);
        c.gridy = 7;
        c.gridx = 0;
        optpanel.add(new JLabel("Large mode:"), c);
        c.gridy = 7;
        c.gridx = 1;
        optpanel.add(co_largemode, c);        

		JButton okbutton = new JButton("OK");
		okbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				Properties p = SimpleMidiPlayer.getConfig();

				if (co_devname.getSelectedIndex() == 0)
					p.remove("devicename");
				else
					p.setProperty("devicename", getValueFromList(co_devname));

				p.setProperty("samplerate", getValueFromList(co_samplerate));
				p.setProperty("channels", getValueFromList(co_channels));
				p.setProperty("bits", getValueFromList(co_bits));
				p.setProperty("latency", getValueFromList(co_latency));
				p.setProperty("polyphony", getValueFromList(co_polyphony));
                p.setProperty("interpolation", getValueFromList(co_interp));
				p.setProperty("largemode", getValueFromList(co_largemode));
				SimpleMidiPlayer.storeConfig(p);
				isok = true;

				ConfigDialog.this.dispose();

			}
		});
		okbutton.setDefaultCapable(true);
		JButton cancelbutton = new JButton("Cancel");
		cancelbutton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ConfigDialog.this.dispose();
			}
		});

		JPanel buttonpanel = new JPanel();
		panel.add(buttonpanel, BorderLayout.SOUTH);
		buttonpanel.add(okbutton);
		buttonpanel.add(cancelbutton);

		KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		panel.registerKeyboardAction(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		}, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

		Properties p = SimpleMidiPlayer.getConfig();

		selectValueInList(co_devname, p.getProperty("devicename"));
		selectValueInList(co_samplerate, p.getProperty("samplerate"));
		selectValueInList(co_channels, p.getProperty("channels"));
		selectValueInList(co_bits, p.getProperty("bits"));
		selectValueInList(co_latency, p.getProperty("latency"));
		selectValueInList(co_polyphony, p.getProperty("polyphony"));
		selectValueInList(co_interp, p.getProperty("interpolation"));
        selectValueInList(co_largemode, p.getProperty("largemode"));

		pack();

		SimpleMidiPlayer.centerWindow(this);
	}

}
