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

public class TuningApplet2 extends JApplet {

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

    public static void sendScaleOctaveTunings(Receiver recv, int bank, int preset,
            String name, double[] tunings) throws IOException,
            InvalidMidiDataException {
        int[] itunings = new int[12];
        for (int i = 0; i < itunings.length; i++) {
            itunings[i] = (int) (tunings[i] * 8192.0 / 100.0);
        }
        SysexMessage msg = UniversalSysExBuilder.MidiTuningStandard
                .scaleOctaveTuningDump2ByteForm(UniversalSysExBuilder.ALL_DEVICES, bank, preset, name, itunings);                
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
                synth.getChannels()[4].programChange(pgm);
            }
        });
        JPanel instrumentpanel = new JPanel();
        instrumentpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        instrumentpanel.setOpaque(false);
        instrumentpanel.add(new JLabel("Instrument: "));
        instrumentpanel.add(instrumentcombobox);
        boxpanel.add(instrumentpanel);

        try {
            
            // Just Intonation
            double[] tunings;
            tunings = new double[] {
                    0,
                    111.73-100.0,
                    203.91-200.0,
                    315.64-300.0,
                    386.31-400.0,
                    498.04-500.0,
                    582.51-600.0,
                    701.96-700.0,
                    813.69-800.0,
                    884.36-900.0,
                    968.826-1000.0,
                    1088.27-1100.0,
                    1200.0-1200.0};            
            sendScaleOctaveTunings(recv, 0, 100, "Just", tunings);
            sendTuningChange(recv, 0, 100);

            tunings = new double[] {
                    0,
                    90.22-100.0,
                    203.91-200.0,
                    294.13-300.0,
                    407.82-400.0,
                    498.04-500.0,
                    611.73-600.0,
                    701.96-700.0,
                    792.18-800.0,
                    905.87-900.0,
                    996.09-1000.0,
                    1109.78-1100.0};          
            sendScaleOctaveTunings(recv, 0, 101, "Pyth", tunings);
            sendTuningChange(recv, 1, 101);           
            
            tunings = new double[] {
                    0.0,
                    76.0-100.0,
                    193.2-200.0,
                    310.3-300.0,
                    386.3-400.0,
                    503.4-500.0,
                    579.5-600.0,
                    696.6-700.0,
                    772.6-800.0,
                    889.7-900.0,
                    1006.8-1000.0,
                    1082.9-1100.0};      
            sendScaleOctaveTunings(recv, 0, 102, "Meantone", tunings);
            sendTuningChange(recv, 2, 102);          
            
            tunings = new double[] {
                    0-0,
                    90-100,
                    192-200,
                    294-300,
                    390-400,
                    498-500,
                    588-600,
                    696-700,
                    792-800,
                    888-900,
                    996-1000,
                    1092-1100};
            sendScaleOctaveTunings(recv, 0, 103, "WellTemp", tunings);
            sendTuningChange(recv, 3, 103);              
           
            
            
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

        for (int i = 0; i < 5; i++) {
            
            String title = "";
            if(i == 0) title = "Just Intonation";
            if(i == 1) title = "Pythagorean Tuning";
            if(i == 2) title = "Meantone Temperament";
            if(i == 3) title = "Well Temperament";
            if(i == 4) title = "Equal Temperament";
            
            Dimension vdim12 = new Dimension(1000, 50);
            VirtualKeyboard12 vkeyboard12 = new VirtualKeyboard12();
            vkeyboard12.setSize(vdim12);
            vkeyboard12.setPreferredSize(vdim12);
            vkeyboard12.setMinimumSize(vdim12);
            vkeyboard12.setMaximumSize(vdim12);
            vkeyboard12.setChannel(i);
            vkeyboard12.setReceiver(recv);  

            JScrollPane scrollpane12 = new JScrollPane(vkeyboard12);
            scrollpane12.setPreferredSize(new Dimension(500, 80));
            scrollpane12.getViewport().setViewPosition(new Point(200, 0));
            
            JPanel panel12 = new JPanel(new BorderLayout());
            panel12.setOpaque(false);
            panel12.setBorder(BorderFactory
                    .createTitledBorder(title));
            panel12.add(scrollpane12);
            boxpanel.add(panel12);            
        }

    }

}
