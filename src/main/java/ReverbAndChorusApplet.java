import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.media.sound.SoftSynthesizer;

public class ReverbAndChorusApplet extends JApplet {

    private static final long serialVersionUID = 1L;

    private Synthesizer synth = new SoftSynthesizer();

    private Receiver recv;
    
    JPanel firstpanel;    
    JLabel infolabel;
    String error_text = "";
    
    public void destroy() {
        synth.close();
    }   

    public void init() {
        Runnable runnable = new Runnable() {
            public void run() {
                try {

                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            firstpanel = new JPanel();
                            firstpanel.setBackground(Color.WHITE);
                            firstpanel.setLayout(new FlowLayout());
                            add(firstpanel);
                            
                            infolabel = new JLabel(
                                    "Loading synthesizer, please wait . . .");

                            firstpanel.add(infolabel);

                            validate();
                            invalidate();
                        }                        
                    });
                                        
                    synth.getDefaultSoundbank();                      
                    synth.open();
                    recv = synth.getReceiver();

                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            createGUI();
                            validate();
                            invalidate();
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }
    
    public JSlider createMidiControlSlider(final int control, int defaultvalue)
    {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMaximum(127);
        slider.setMinimum(0);
        slider.setValue(defaultvalue);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {                
                try {
                    ShortMessage sms = new ShortMessage();
                    sms.setMessage(ShortMessage.CONTROL_CHANGE, control, slider.getValue());
                    recv.send(sms, -1);
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        slider.setOpaque(false);
        return slider;
    }
    
    public JComboBox createReverbTypeCombobox()
    {
        String[] reverbtypes = {"Small Room", "Medium Room", "Large Room", "Medium Hall", "Large Hall", "Plate"};
        final JComboBox combobox = new JComboBox(reverbtypes);
        combobox.setSelectedIndex(4);
        combobox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                int reverbType = 0;
                if(combobox.getSelectedIndex() == 0) reverbType = UniversalSysExBuilder.DeviceControl.ReverbEffect.REVERB_TYPE_SMALL_ROOM; 
                if(combobox.getSelectedIndex() == 1) reverbType = UniversalSysExBuilder.DeviceControl.ReverbEffect.REVERB_TYPE_MEDIUM_ROOM; 
                if(combobox.getSelectedIndex() == 2) reverbType = UniversalSysExBuilder.DeviceControl.ReverbEffect.REVERB_TYPE_LARGE_ROOM; 
                if(combobox.getSelectedIndex() == 3) reverbType = UniversalSysExBuilder.DeviceControl.ReverbEffect.REVERB_TYPE_MEDIUM_HALL; 
                if(combobox.getSelectedIndex() == 4) reverbType = UniversalSysExBuilder.DeviceControl.ReverbEffect.REVERB_TYPE_LARGE_HALL;
                if(combobox.getSelectedIndex() == 5) reverbType = UniversalSysExBuilder.DeviceControl.ReverbEffect.REVERB_TYPE_PLATE;                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ReverbEffect.setReverbType(
                            UniversalSysExBuilder.ALL_DEVICES, reverbType);
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return combobox;
    }

    public JSlider createReverbTimeSlider()
    {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMaximum(127);
        slider.setMinimum(0);
        slider.setValue(64);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ReverbEffect.setReverbTime(
                            UniversalSysExBuilder.ALL_DEVICES, slider.getValue());
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        slider.setOpaque(false);
        return slider;
    }
        
    public JComboBox createChorusTypeCombobox()
    {
        String[] chorustypes = {"Chorus 1", "Chorus 2", "Chorus 3", "Chorus 4", "FB Chorus", "Flanger"};
        final JComboBox combobox = new JComboBox(chorustypes);
        combobox.setSelectedIndex(2);
        combobox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                int chorusType = 0;
                if(combobox.getSelectedIndex() == 0) chorusType = UniversalSysExBuilder.DeviceControl.ChorusEffect.CHORUS_TYPE_CHORUS1; 
                if(combobox.getSelectedIndex() == 1) chorusType = UniversalSysExBuilder.DeviceControl.ChorusEffect.CHORUS_TYPE_CHORUS2; 
                if(combobox.getSelectedIndex() == 2) chorusType = UniversalSysExBuilder.DeviceControl.ChorusEffect.CHORUS_TYPE_CHORUS3; 
                if(combobox.getSelectedIndex() == 3) chorusType = UniversalSysExBuilder.DeviceControl.ChorusEffect.CHORUS_TYPE_CHORUS4; 
                if(combobox.getSelectedIndex() == 4) chorusType = UniversalSysExBuilder.DeviceControl.ChorusEffect.CHORUS_TYPE_FB_CHORUS;
                if(combobox.getSelectedIndex() == 5) chorusType = UniversalSysExBuilder.DeviceControl.ChorusEffect.CHORUS_TYPE_FLANGER;                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ChorusEffect.setChorusType(
                            UniversalSysExBuilder.ALL_DEVICES, chorusType);
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
            }
        });
        return combobox;
    }
    
    public JSlider createChorusFeedbackSlider()
    {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMaximum(127);
        slider.setMinimum(0);
        slider.setValue(64);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ChorusEffect.setChorusFeedback(
                            UniversalSysExBuilder.ALL_DEVICES, slider.getValue());
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        slider.setOpaque(false);
        return slider;
    }
    
    public JSlider createChorusModDepthSlider()
    {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMaximum(127);
        slider.setMinimum(0);
        slider.setValue(64);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ChorusEffect.setChorusModDepth(
                            UniversalSysExBuilder.ALL_DEVICES, slider.getValue());
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        slider.setOpaque(false);
        return slider;
    }

    public JSlider createChorusModRateSlider()
    {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMaximum(127);
        slider.setMinimum(0);
        slider.setValue(64);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ChorusEffect.setChorusModRate(
                            UniversalSysExBuilder.ALL_DEVICES, slider.getValue());
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        slider.setOpaque(false);
        return slider;
    }

    public JSlider createChorusSendToReverbSlider()
    {
        final JSlider slider = new JSlider(JSlider.HORIZONTAL);
        slider.setMaximum(127);
        slider.setMinimum(0);
        slider.setValue(64);
        slider.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e) {                
                try {
                    MidiMessage msg = UniversalSysExBuilder.DeviceControl.ChorusEffect.setChorusSendToReverb(
                            UniversalSysExBuilder.ALL_DEVICES, slider.getValue());
                    recv.send(msg, -1);
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (InvalidMidiDataException e1) {
                    e1.printStackTrace();
                }
                
            }
        });
        slider.setOpaque(false);
        return slider;
    }
    
    public void createGUI() {
        remove(firstpanel);
        JPanel toppanel = new JPanel();
        toppanel.setBackground(Color.WHITE);
        toppanel.setLayout(new FlowLayout());
        add(toppanel);

        JPanel boxpanel = new JPanel();
        boxpanel.setOpaque(false);
        boxpanel.setLayout(new BoxLayout(boxpanel, BoxLayout.Y_AXIS));
        toppanel.add(boxpanel);
        
        Soundbank sbk = synth.getDefaultSoundbank();
        String[] instruments = new String[128];        
        for (int i = 0; i < instruments.length; i++) {
            instruments[i] = i + " " + sbk.getInstrument(new Patch(0, i)).getName();
        }
        final JComboBox instrumentcombobox = new JComboBox(instruments);
        instrumentcombobox.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                int pgm = instrumentcombobox.getSelectedIndex();
                synth.getChannels()[0].programChange(pgm);
            }
        });

        
        JPanel instrumentpanel = new JPanel();
        instrumentpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        instrumentpanel.setOpaque(false);
        instrumentpanel.add(new JLabel("Instrument: "));
        instrumentpanel.add(instrumentcombobox);
        boxpanel.add(instrumentpanel);  
        
        JPanel effectscontainer = new JPanel();
        effectscontainer.setLayout(new BoxLayout(effectscontainer, BoxLayout.X_AXIS));
        effectscontainer.setOpaque(false);
        boxpanel.add(effectscontainer);
                
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.WEST;
        c.insets = new Insets(2,2,2,2);
        
        JPanel reverbeffect = new JPanel();
        reverbeffect.setBorder(BorderFactory
                .createTitledBorder("Reverb"));
        reverbeffect.setOpaque(false);
        reverbeffect.setLayout(new GridBagLayout());
        
        c.gridx = 0; 
        c.gridy = 0;
        reverbeffect.add(new JLabel("Level"),c);
        c.gridx = 1; 
        reverbeffect.add(createMidiControlSlider(91, 40),c);        
        
        c.gridx = 0; 
        c.gridy = 1;
        reverbeffect.add(new JLabel("Type"),c);
        c.gridx = 1; 
        c.weightx = 1;
        reverbeffect.add(createReverbTypeCombobox(),c);
        
        c.gridx = 0; 
        c.gridy = 2;
        reverbeffect.add(new JLabel("Room size"),c);
        c.gridx = 1; 
        reverbeffect.add(createReverbTimeSlider(),c);
        
        c.gridx = 0;
        c.gridy = 3;
        c.weighty = 1;
        JPanel emptypanel = new JPanel();
        emptypanel.setOpaque(false);
        reverbeffect.add(emptypanel, c);
        c.weighty = 0;
        
        effectscontainer.add(reverbeffect);
        
        JPanel choruseffect = new JPanel();
        choruseffect.setBorder(BorderFactory
                .createTitledBorder("Chorus"));
        choruseffect.setOpaque(false);
        choruseffect.setLayout(new GridBagLayout());
        
        c.gridx = 0; 
        c.gridy = 0;
        choruseffect.add(new JLabel("Level"),c);
        c.gridx = 1; 
        choruseffect.add(createMidiControlSlider(93, 0),c);
        
        c.gridx = 0; 
        c.gridy = 1;
        choruseffect.add(new JLabel("Type"),c);
        c.gridx = 1;         
        choruseffect.add(createChorusTypeCombobox(),c);
        
        c.gridx = 0; 
        c.gridy = 2;
        choruseffect.add(new JLabel("Mod Rate"),c);
        c.gridx = 1; 
        choruseffect.add(createChorusModRateSlider(),c);

        c.gridx = 0; 
        c.gridy = 3;
        choruseffect.add(new JLabel("Mod Depth"),c);
        c.gridx = 1; 
        choruseffect.add(createChorusModDepthSlider(),c);
        
        c.gridx = 0; 
        c.gridy = 4;
        choruseffect.add(new JLabel("Feedback"),c);
        c.gridx = 1; 
        choruseffect.add(createChorusFeedbackSlider(),c);
        
        c.gridx = 0; 
        c.gridy = 5;
        choruseffect.add(new JLabel("Send to Reverb"),c);
        c.gridx = 1; 
        choruseffect.add(createChorusSendToReverbSlider(),c);
        
        effectscontainer.add(choruseffect);
      

        Dimension vdim12 = new Dimension(1000, 50);
        VirtualKeyboard12 vkeyboard12 = new VirtualKeyboard12();
        vkeyboard12.setSize(vdim12);
        vkeyboard12.setPreferredSize(vdim12);
        vkeyboard12.setMinimumSize(vdim12);
        vkeyboard12.setMaximumSize(vdim12);
        vkeyboard12.setChannel(0);
        vkeyboard12.setReceiver(recv);

        JScrollPane scrollpane12 = new JScrollPane(vkeyboard12);
        scrollpane12.setPreferredSize(new Dimension(500, 80));
        scrollpane12.getViewport().setViewPosition(new Point(200, 0));

        JPanel panel12 = new JPanel(new BorderLayout());
        panel12.setOpaque(false);
        panel12.add(scrollpane12);
        boxpanel.add(panel12);


    }

}
