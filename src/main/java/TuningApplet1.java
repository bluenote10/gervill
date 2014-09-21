import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.SysexMessage;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.sun.media.sound.SoftSynthesizer;

public class TuningApplet1 extends JApplet {

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

    public static void sendTuningChange(Receiver recv, int channel,
            int tuningpreset) throws InvalidMidiDataException {
        // Data Entry
        ShortMessage sm1 = new ShortMessage();
        sm1.setMessage(ShortMessage.CONTROL_CHANGE, channel, 0x64, 03);
        ShortMessage sm2 = new ShortMessage();
        sm2.setMessage(ShortMessage.CONTROL_CHANGE, channel, 0x65, 00);
        // Tuning program 19
        ShortMessage sm3 = new ShortMessage();
        sm3
                .setMessage(ShortMessage.CONTROL_CHANGE, channel, 0x06,
                        tuningpreset);

        // Data Increment
        ShortMessage sm4 = new ShortMessage();
        sm4.setMessage(ShortMessage.CONTROL_CHANGE, channel, 0x60, 0x7F);
        // Data Decrement
        ShortMessage sm5 = new ShortMessage();
        sm5.setMessage(ShortMessage.CONTROL_CHANGE, channel, 0x61, 0x7F);

        recv.send(sm1, -1);
        recv.send(sm2, -1);
        recv.send(sm3, -1);
        recv.send(sm4, -1);
        recv.send(sm5, -1);
    }

    public static void sendTunings(Receiver recv, int bank, int preset,
            String name, double[] tunings) throws IOException,
            InvalidMidiDataException {
        int[] itunings = new int[128];
        for (int i = 0; i < itunings.length; i++) {
            itunings[i] = (int) (tunings[i] * 16384.0 / 100.0);
        }
        SysexMessage msg = UniversalSysExBuilder.MidiTuningStandard
                .keyBasedTuningDump(UniversalSysExBuilder.ALL_DEVICES, bank,
                        preset, name, itunings);
        recv.send(msg, -1);
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
                synth.getChannels()[1].programChange(pgm);
                synth.getChannels()[2].programChange(pgm);
                synth.getChannels()[3].programChange(pgm);
            }
        });
        JPanel instrumentpanel = new JPanel();
        instrumentpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        instrumentpanel.setOpaque(false);
        instrumentpanel.add(new JLabel("Instrument: "));
        instrumentpanel.add(instrumentcombobox);
        boxpanel.add(instrumentpanel);

        try {

            double[] tunings = new double[128];

            for (int i = 0; i < tunings.length; i++)
                tunings[i] = 2400 + i * 1200.0 / 19.0;
            sendTunings(recv, 0, 19, "19-TET", tunings);
            sendTuningChange(recv, 0, 19);

            for (int i = 0; i < tunings.length; i++)
                tunings[i] = -2400 + i * 1200.0 / 7.0;
            sendTunings(recv, 0, 7, "7-TET", tunings);
            sendTuningChange(recv, 2, 7);

            for (int i = 0; i < tunings.length; i++)
                tunings[i] = -2400 + i * 1200.0 / 5.0;
            sendTunings(recv, 0, 5, "5-TET", tunings);
            sendTuningChange(recv, 3, 5);

        } catch (Exception e1) {
            e1.printStackTrace();
            return;
        }

        Dimension vdim19 = new Dimension(627, 50);
        VirtualKeyboard19 vkeyboard19 = new VirtualKeyboard19();
        vkeyboard19.setSize(vdim19);
        vkeyboard19.setPreferredSize(vdim19);
        vkeyboard19.setMinimumSize(vdim19);
        vkeyboard19.setMaximumSize(vdim19);
        vkeyboard19.setReceiver(recv);

        Dimension vdim12 = new Dimension(1000, 50);
        VirtualKeyboard12 vkeyboard12 = new VirtualKeyboard12();
        vkeyboard12.setSize(vdim12);
        vkeyboard12.setPreferredSize(vdim12);
        vkeyboard12.setMinimumSize(vdim12);
        vkeyboard12.setMaximumSize(vdim12);
        vkeyboard12.setChannel(1);
        vkeyboard12.setReceiver(recv);

        Dimension vdim7 = new Dimension(1700, 50);
        VirtualKeyboard7 vkeyboard7 = new VirtualKeyboard7();
        vkeyboard7.setSize(vdim7);
        vkeyboard7.setPreferredSize(vdim7);
        vkeyboard7.setMinimumSize(vdim7);
        vkeyboard7.setMaximumSize(vdim7);
        vkeyboard7.setChannel(2);
        vkeyboard7.setReceiver(recv);

        Dimension vdim5 = new Dimension(1700, 50);
        VirtualKeyboard5 vkeyboard5 = new VirtualKeyboard5();
        vkeyboard5.setSize(vdim5);
        vkeyboard5.setPreferredSize(vdim5);
        vkeyboard5.setMinimumSize(vdim5);
        vkeyboard5.setMaximumSize(vdim5);
        vkeyboard5.setChannel(3);
        vkeyboard5.setReceiver(recv);

        JScrollPane scrollpane19 = new JScrollPane(vkeyboard19);
        scrollpane19.setPreferredSize(new Dimension(500, 80));
        scrollpane19.getViewport().setViewPosition(new Point(107, 0));

        JScrollPane scrollpane12 = new JScrollPane(vkeyboard12);
        scrollpane12.setPreferredSize(new Dimension(500, 80));
        scrollpane12.getViewport().setViewPosition(new Point(200, 0));

        JScrollPane scrollpane7 = new JScrollPane(vkeyboard7);
        scrollpane7.setPreferredSize(new Dimension(500, 80));
        scrollpane7.getViewport().setViewPosition(new Point(320, 0));

        JScrollPane scrollpane5 = new JScrollPane(vkeyboard5);
        scrollpane5.setPreferredSize(new Dimension(500, 80));
        scrollpane5.getViewport().setViewPosition(new Point(320, 0));

        JPanel panel19 = new JPanel(new BorderLayout());
        panel19.setOpaque(false);
        panel19.setBorder(BorderFactory
                .createTitledBorder("19 equal temperament (19-TET)"));
        panel19.add(scrollpane19);
        boxpanel.add(panel19);

        JPanel panel12 = new JPanel(new BorderLayout());
        panel12.setOpaque(false);
        panel12.setBorder(BorderFactory
                .createTitledBorder("12 equal temperament (12-TET)"));
        panel12.add(scrollpane12);
        boxpanel.add(panel12);

        JPanel panel7 = new JPanel(new BorderLayout());
        panel7.setOpaque(false);
        panel7.setBorder(BorderFactory
                .createTitledBorder("7 equal temperament (7-TET)"));
        panel7.add(scrollpane7);
        boxpanel.add(panel7);

        JPanel panel5 = new JPanel(new BorderLayout());
        panel5.setOpaque(false);
        panel5.setBorder(BorderFactory
                .createTitledBorder("5 equal temperament (5-TET)"));
        panel5.add(scrollpane5);
        boxpanel.add(panel5);

    }

}
