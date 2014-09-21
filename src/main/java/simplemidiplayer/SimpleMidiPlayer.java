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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.MidiDevice.Info;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileFilter;

import com.sun.media.sound.AudioSynthesizer;
import com.sun.media.sound.EmergencySoundbank;

public class SimpleMidiPlayer extends JFrame {

	public class ImagePanel extends JPanel {

		private static final long serialVersionUID = 1L;

		Icon icon;

		public ImagePanel(Icon icon) {
			super();
			this.icon = icon;
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			icon.paintIcon(this, g, 0, 0);
		}

	}

	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		if (!configExists()) {
			ConfigDialog cd = new ConfigDialog(null);
			cd.setVisible(true);
			if (!cd.isOK())
				return;
		}
		new SimpleMidiPlayer().setVisible(true);
	}

	public JButton makeButton(String caption) {
		JButton butt = new JButton(caption);
		butt.setMargin(new Insets(2, 2, 2, 2));
		butt.setFocusable(false);
		butt.setFont(butt.getFont().deriveFont(Font.PLAIN));
		return butt;
	}

	JPopupMenu loadmenu;

	boolean synth_loaded = false;

	Sequencer seqr = null;

	Sequence seq = null;

	String seq_errmsg = null;

	File seqfile = null;

	Soundbank sbk = null;

	String sbk_errmsg = null;

	File sbkfile = null;

	Synthesizer softsynth = null;

	Mixer synthmixer = null;
	SourceDataLine line = null;

	InfoFrame infoframe;
	
	AudioFormat format;
    
	/*
	 * Find available AudioSynthesizer.
	 */
	public static AudioSynthesizer findAudioSynthesizer()
			throws MidiUnavailableException {
		// First check if default synthesizer is AudioSynthesizer.
		Synthesizer synth = MidiSystem.getSynthesizer();
		if (synth instanceof AudioSynthesizer)
			return (AudioSynthesizer) synth;

		// If default synhtesizer is not AudioSynthesizer, check others.
		Info[] infos = MidiSystem.getMidiDeviceInfo();
		for (int i = 0; i < infos.length; i++) {
			MidiDevice dev = MidiSystem.getMidiDevice(infos[i]);
			if (dev instanceof AudioSynthesizer)
				return (AudioSynthesizer) dev;
		}

		// No AudioSynthesizer was found, return null.
		return null;
	}	

	public void initMIDI() {
		try {
			
			final AudioSynthesizer synth = findAudioSynthesizer();

			Properties p = getConfig();
			Map<String, Object> ainfo = new HashMap<String, Object>();

			try {

				format = new AudioFormat(Float.parseFloat(p
						.getProperty("samplerate", "44100")), Integer
						.parseInt(p.getProperty("bits", "16")), Integer
						.parseInt(p.getProperty("channels", "2")), true, false);
				
				int latency = Integer.parseInt(p.getProperty("latency", "200")) * 1000;
				
				String devname = p.getProperty("devicename");
				if (devname != null) {
					Mixer.Info selinfo = null;
					for (Mixer.Info info : AudioSystem.getMixerInfo()) {
						Mixer mixer = AudioSystem.getMixer(info);
						boolean hassrcline = false;
						for (Line.Info linfo : mixer.getSourceLineInfo())
							if (linfo instanceof javax.sound.sampled.DataLine.Info)
								hassrcline = true;
						if (hassrcline) {
							if (info.getName().equals(devname)) {
								selinfo = info;
								break;
							}
						}
					}
					if (selinfo != null) {
						synthmixer = AudioSystem.getMixer(selinfo);
						try {
							synthmixer.open();
							
							int bufferSize = (int)							
								(format.getFrameSize() * format.getFrameRate() 
								* latency / 1000000f);
							if(bufferSize < 500) bufferSize = 500;
								
							DataLine.Info dataLineInfo = new DataLine.Info(
									SourceDataLine.class, format, bufferSize);
							if (synthmixer.isLineSupported(dataLineInfo))
								line = (SourceDataLine) synthmixer
										.getLine(dataLineInfo);
														
							line.open(format, bufferSize);
							line.start();
																					
						} catch (Throwable t) {
							t.printStackTrace();
							synthmixer = null;
						}
					}
				}
				
				//ainfo.put("multi threading", true);
				ainfo.put("format", format);
				ainfo.put("max polyphony", Integer.parseInt(p.getProperty(
						"polyphony", "64")));				
				ainfo.put("latency", Long.parseLong(p.getProperty("latency",
						"200")) * 1000L);

				ainfo.put("interpolation", p.getProperty("interpolation"));                
                String largemode = p.getProperty("largemode");
                if(largemode == null) largemode = "false";
                ainfo.put("large mode", largemode.equalsIgnoreCase("true"));

			} catch (Throwable t) {
				t.printStackTrace();
			}
            
			synth.open(line, ainfo);

			Runnable r = new Runnable() {
				public void run() {
					softsynth = synth;
					if (sbk == null)
						sbk = synth.getDefaultSoundbank();
					try {
						if (seqr == null) {
							try {
								seqr = MidiSystem.getSequencer(false);
							} catch (MidiUnavailableException e2) {
								e2.printStackTrace();
							}
						}
						if (seqr.isOpen())
							seqr.close();
						seqr.getTransmitter().setReceiver(
								softsynth.getReceiver());
						seqr.open();
					} catch (MidiUnavailableException e) {
						e.printStackTrace();
					}
					synth_loaded = true;
				}
			};

			if (SwingUtilities.isEventDispatchThread())
				r.run();
			else
				SwingUtilities.invokeLater(r);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void initMIDI_inThread() {
		synth_loaded = false;
		new Thread() {
			public void run() {
				initMIDI();
			}
		}.start();

	}

	public void closeMIDI() {
		if (synth_loaded) {
			seqr.close();
			softsynth.close();
			if (line != null) {
				line.close();
				line = null;
			}
			if (synthmixer != null) {
				synthmixer.close();
				synthmixer = null;
			}
		}
	}

	JLabel displayLab = new JLabel();

	public void updateDisplay() {

		if (!synth_loaded) {
			displayLab.setText("<html><body>Initializing . . .");
		} else {
			MidiDevice.Info info = softsynth.getDeviceInfo();
			
			String fmts = (int) format.getSampleRate() + "Hz "
					+ format.getSampleSizeInBits() + "bit "
					+ format.getChannels() + "ch";
			String line1 = "<b>" + info.getName() + " " + info.getVersion()
					+ "</b> &nbsp;" + fmts;
			String line2 = "";

			if (sbk == null) {
				line2 = "No SoundBank Loaded!";
			} else {
				if (sbk_errmsg != null)
					line2 = sbk_errmsg;
				else if (sbkfile == null)
					line2 = "Default SoundBank";
				else
					line2 = sbkfile.getName();
				if (line2.length() > 31)
					line2 = line2.substring(0, 31);
			}

			String line3 = "";
			if (seq == null) {
				line3 = "No Sequence";
			} else {
				if (seqr.isRunning() || seqr.getTickPosition() != 0) {

					long a = seqr.getTickPosition() / seq.getResolution();
					long b = seqr.getTickLength() / seq.getResolution();
					if (seqr.isRunning())
						line3 = "PLAY " + a + " of " + b;
					else
						line3 = "STOP " + a + " of " + b;

				} else {
					if (seq_errmsg != null)
						line3 = seq_errmsg;
					else
						line3 = seqfile.getName();
					if (line3.length() > 31)
						line3 = line3.substring(0, 31);
				}
			}
			displayLab.setText("<html><body>" + line1 + "<br>" + line2 + "<br>"
					+ line3);
		}

	}

	JFileChooser loadseq;

	JFileChooser loadsndbk;

	Thread actdisplay;

	boolean player_running = true;

	private static String CONFIG_FILE_NAME = "SimpleMidiPlayer.xml";

	private static File userDir = new File(System.getProperty("user.home"),
			".gervill");

	private static File configFile = new File(userDir, CONFIG_FILE_NAME);

	private static Properties configp = null;

	public static void centerWindow(Window w) {
		Rectangle windowSize;
		// Insets windowInsets;

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		GraphicsEnvironment ge = java.awt.GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice()
				.getDefaultConfiguration();
		if (gc == null)
			gc = w.getGraphicsConfiguration();

		if (gc != null) {
			windowSize = gc.getBounds();
		} else {
			windowSize = new java.awt.Rectangle(toolkit.getScreenSize());
		}

		Dimension size = w.getSize();
		Point parent_loc = w.getLocation();
		w.setLocation(parent_loc.x + windowSize.width / 2 - (size.width / 2),
				parent_loc.y + windowSize.height / 2 - (size.height / 2));

	}

	public static boolean configExists() {
		synchronized (configFile) {
			return configFile.exists();
		}
	}

	public static Properties getConfig() {
		synchronized (configFile) {

			if (configp != null) {
				Properties p = new Properties();
				p.putAll(configp);
				return p;
			}
			Properties p = new Properties();
			if (configFile.exists()) {
				FileInputStream fis;
				try {
					fis = new FileInputStream(configFile);
					try {
						p.loadFromXML(fis);
					} finally {
						fis.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return p;

		}
	}

	public static void storeConfig(Properties p) {
		synchronized (configFile) {

			try {
				configp = new Properties();
				configp.putAll(p);

				if (!userDir.exists())
					userDir.mkdirs();
				FileOutputStream fos = new FileOutputStream(configFile);
				try {
					p.storeToXML(fos, "GervillMidiPlayer");
				} finally {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	public void loadMidiSeq(File newseqfile) {
		try {
			seq_errmsg = null;
			Sequence newseq = MidiSystem.getSequence(newseqfile);

			seq = newseq;
			seqfile = newseqfile;
			// boolean running = seqr.isRunning();
			seqr.stop();
            
            // Reset All Channels
            for(MidiChannel c : softsynth.getChannels())
                c.resetAllControllers();
            
			seqr.setSequence(seq);
			seqr.setTickPosition(0);
			seqr.start();
		} catch (Throwable e1) {
			seq_errmsg = e1.toString();
		}

	}

	public void loadSoundbank(File newsbkfile) {
		try {
			sbk_errmsg = null;
			Soundbank newsbk = MidiSystem.getSoundbank(newsbkfile);
			if (sbk != null)
				softsynth.unloadAllInstruments(sbk);
			sbkfile = newsbkfile;
			sbk = newsbk;
			softsynth.loadAllInstruments(sbk);
		} catch (Throwable e1) {
			sbk_errmsg = e1.toString();
		}

	}

	public SimpleMidiPlayer() {

		loadseq = new JFileChooser();
		loadseq.setDialogTitle("Load MIDI Sequence");
		loadseq.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (!f.isFile())
					return true;
				return f.getName().toLowerCase().endsWith(".mid");
			}

			public String getDescription() {
				return "MIDI Sequence";
			}
		});
		loadsndbk = new JFileChooser();
		loadsndbk.setFileFilter(new FileFilter() {
			public boolean accept(File f) {
				if (!f.isFile())
					return true;
				String name = f.getName().toLowerCase();
				if (name.endsWith(".sf2"))
					return true;
				if (name.endsWith(".dls"))
					return true;
				if (name.endsWith(".pat"))
					return true;
				if (name.endsWith(".cfg"))
					return true;
				if (name.endsWith(".wav"))
					return true;
				if (name.endsWith(".au"))
					return true;
				if (name.endsWith(".aif"))
					return true;
				return false;
			}

			public String getDescription() {
				return "SoundBank (*.sf2,*.dls,*.pat,*.cfg,*.wav,*.au,*.aif)";
			}
		});

		// setLocationByPlatform(true);
		setResizable(false);
		setTitle("Gervill - MIDI Player");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				closeMIDI();
				player_running = false;
				try {
					actdisplay.join(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});

		infoframe = new InfoFrame(this);

		actdisplay = new Thread() {
			public void run() {
				boolean ok = true;
				while (ok) {
					synchronized (SimpleMidiPlayer.this) {
						ok = player_running;
					}
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							updateDisplay();
							infoframe.updateDisplay();
						}
					});
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
						return;
					}
				}
			}
		};
		actdisplay.start();

		initMIDI_inThread();

		ImageIcon backgr = new javax.swing.ImageIcon(getClass().getResource(
				"/simplemidiplayer/backgr.png"));
		ImageIcon swan = new javax.swing.ImageIcon(getClass().getResource(
				"/simplemidiplayer/swan.png"));
		setIconImage(swan.getImage());

		JPanel panel = new ImagePanel(backgr);
		Dimension size = new Dimension(443, 125);
		panel.setPreferredSize(size);
		panel.setMinimumSize(size);
		panel.setLayout(null);

		TransferHandler thandler = new TransferHandler() {
			private static final long serialVersionUID = 1L;

			public boolean canImport(JComponent comp,
					DataFlavor[] transferFlavors) {

				for (int i = 0; i < transferFlavors.length; i++) {
					if (transferFlavors[i]
							.equals(DataFlavor.javaFileListFlavor)) {
						return true;
					}
				}
				return false;
			}

			public boolean importData(JComponent comp, Transferable t) {

				List files = null;
				try {
					files = (List) t
							.getTransferData(DataFlavor.javaFileListFlavor);
				} catch (UnsupportedFlavorException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}

				if (files == null)
					return false;

				for (Object o : files) {
					File file = (File) o;
					if (file.isFile()) {
						if (file.getName().toLowerCase().endsWith(".mid"))
							loadMidiSeq(file);
						else
							loadSoundbank(file);
					}
				}

				return true;
			}
		};

		panel.setTransferHandler(thandler);

		setContentPane(panel);

		displayLab.setSize(225, 67);
		displayLab.setLocation(206, 20);
		displayLab.setFont(new Font("Monospaced", Font.PLAIN, 12));
		displayLab.setVerticalAlignment(JLabel.TOP);
		displayLab.setVerticalTextPosition(JLabel.TOP);
		displayLab.setTransferHandler(thandler);
		panel.add(displayLab);

		JPanel toolBar = new JPanel();
		toolBar.setLayout(new FlowLayout(FlowLayout.RIGHT, 2, 5));
		toolBar.setSize(429, 80);
		toolBar.setLocation(0, 82);
		toolBar.setOpaque(false);

		final JButton config = makeButton("CONFIG");
		final JButton info = makeButton("INFO");
		final JButton load = makeButton("LOAD");
		final JButton play = makeButton("PLAY");
		final JButton stop = makeButton("STOP");

		JMenuItem loadseq_menuitem = new JMenuItem("MIDI Sequence...");

		loadseq_menuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!synth_loaded)
					return;
				if (loadseq.showOpenDialog(SimpleMidiPlayer.this) == JFileChooser.APPROVE_OPTION) {
					loadMidiSeq(loadseq.getSelectedFile());
				}
			}
		});

		JMenuItem loadsndbk_menuitem = new JMenuItem("Soundbank...");

		loadsndbk_menuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!synth_loaded)
					return;
				if (loadsndbk.showOpenDialog(SimpleMidiPlayer.this) == JFileChooser.APPROVE_OPTION) {
					loadSoundbank(loadsndbk.getSelectedFile());
				}
			}
		});

		JMenuItem default_loadsndbk_menuitem = new JMenuItem(
				"Default Soundbank");

		default_loadsndbk_menuitem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (softsynth.getDefaultSoundbank() != null) {
					if (sbk != null)
						softsynth.unloadAllInstruments(sbk);
					sbk = softsynth.getDefaultSoundbank();
					sbkfile = null;
					softsynth.loadAllInstruments(sbk);
				}
			}
		});

		JMenuItem emerg_loadsndbk_menuitem = new JMenuItem(
		"Emergency Soundbank");

		emerg_loadsndbk_menuitem.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			
			Soundbank emsbk;
			try {
				emsbk = EmergencySoundbank.createSoundbank();
			} catch (Exception e1) {
				e1.printStackTrace();
				return;
			}
			if (sbk != null)
				softsynth.unloadAllInstruments(sbk);
			sbk = emsbk;
			sbkfile = null;
			softsynth.loadAllInstruments(sbk);
		}
		});
		
		loadmenu = new JPopupMenu();
		loadmenu.add(loadseq_menuitem);
		loadmenu.addSeparator();
		loadmenu.add(loadsndbk_menuitem);
		loadmenu.add(default_loadsndbk_menuitem);
		loadmenu.add(emerg_loadsndbk_menuitem);

		config.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!synth_loaded)
					return;

				ConfigDialog cd = new ConfigDialog(SimpleMidiPlayer.this);
				cd.setVisible(true);
				if (cd.isOK()) {
					Sequence pseq = seqr.getSequence();
					long ptick = seqr.getTickPosition();
					boolean prunning = seqr.isRunning();
					seqr.stop();
					softsynth.close();
					if (synthmixer != null) {
						synthmixer.close();
						synthmixer = null;
					}
					initMIDI();
					if (pseq != null) {
						try {
							seqr.setSequence(pseq);
						} catch (InvalidMidiDataException e1) {
							e1.printStackTrace();
						}
					}
					seqr.setTickPosition(ptick);
					if (prunning) {
						seqr.start();
					}

				}
			}
		});

		load.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!synth_loaded)
					return;
				loadmenu.show(load, 0, 0);
			}
		});

		info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				infoframe.setVisible(!infoframe.isVisible());
			}
		});

		play.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!synth_loaded)
					return;
				if (seq == null)
					return;
				seqr.start();
			}
		});

		stop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				if (!synth_loaded)
					return;
				if (seq == null)
					return;
				if (seqr.isRunning())
					seqr.stop();
				else
					seqr.setTickPosition(0);
			}
		});

		toolBar.add(config);
		toolBar.add(load);
		toolBar.add(info);
		toolBar.add(play);
		toolBar.add(stop);
		panel.add(toolBar);

		pack();

		centerWindow(this);

	}

}
