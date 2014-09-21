import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.sun.media.sound.SoftSynthesizer;

public class SimpleApplet1 extends JApplet {

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
