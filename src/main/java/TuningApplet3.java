import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.io.IOException;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.SysexMessage;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.sun.media.sound.SoftSynthesizer;

public class TuningApplet3 extends JApplet {

    private static final long serialVersionUID = 1L;

    private Synthesizer synth = new SoftSynthesizer();

    private Receiver recv;

    JPanel firstpanel;

    JLabel infolabel;

    String error_text = "";

    public void destroy() {
        synth.close();
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

    public void retune(int bank, int preset, int note, double tuning)
            throws IOException, InvalidMidiDataException {
        int[] key_numbers = new int[1];
        int[] key_tunings = new int[1];

        key_numbers[0] = note;
        key_tunings[0] = (int) (tuning * 16384.0 / 100.0);

        SysexMessage msg = UniversalSysExBuilder.MidiTuningStandard
                .singleNoteTuningChange(UniversalSysExBuilder.ALL_DEVICES,
                        true, bank, preset, key_numbers, key_tunings);
        recv.send(msg, -1);
    }

    public class SoundPainter extends JComponent {

        private static final long serialVersionUID = 1L;

        boolean was_prev_selected = false;

        int was_prev_selected_x;

        int was_prev_selected_y;

        int activenote = -1;

        MidiChannel channel;

        boolean[] activenotes = new boolean[32];

        int[] activenotes_x = new int[32];

        int[] activenotes_y = new int[32];

        public SoundPainter() {
            channel = synth.getChannels()[0];
            addMouseListener(new MouseListener() {

                public void mousePressed(MouseEvent e) {

                    // Check if we clicked on existing note
                    for (int i = 0; i < activenotes.length; i++) {
                        if (activenotes[i]) {
                            was_prev_selected = true;
                            was_prev_selected_x = e.getX();
                            was_prev_selected_y = e.getY();

                            int x_delta = activenotes_x[i] - e.getX();
                            int y_delta = activenotes_y[i] - e.getY();
                            int delta = x_delta * x_delta + y_delta * y_delta;
                            if (delta < 10 * 10) {
                                // Existing note found
                                activenote = i;
                                repaint();
                                return;
                            }
                        }
                    }

                    // Find free note
                    for (int i = 0; i < activenotes.length; i++) {
                        if (!activenotes[i]) {
                            was_prev_selected = false;
                            activenote = i;
                            activenotes[i] = true;
                            activenotes_x[i] = e.getX();
                            activenotes_y[i] = e.getY();
                            try {
                                double cent = (e.getX() * 12800.0) / getWidth();
                                retune(0, 99, 36 + i, cent);
                            } catch (Exception e1) {
                                e1.printStackTrace();
                            }
                            channel.noteOn(36 + i, 80);
                            repaint();
                            return;
                        }
                    }

                }

                public void mouseReleased(MouseEvent e) {

                    if (activenote == -1)
                        return;

                    if (was_prev_selected) {
                        int x_delta = was_prev_selected_x - e.getX();
                        int y_delta = was_prev_selected_y - e.getY();
                        int delta = x_delta * x_delta + y_delta * y_delta;
                        if (delta < 3 * 3) {
                            channel.noteOff(36 + activenote);
                            activenotes[activenote] = false;
                            repaint();
                        }
                    }

                    activenote = -1;

                }

                public void mouseClicked(MouseEvent e) {
                }

                public void mouseEntered(MouseEvent e) {
                }

                public void mouseExited(MouseEvent e) {
                }
            });

            addMouseMotionListener(new MouseMotionListener()
            {
                public void mouseDragged(MouseEvent e) {
                    if (activenote != -1) {
                        // Existing note found
                        int i = activenote;
                        activenotes_x[i] = e.getX();
                        activenotes_y[i] = e.getY();
                        try {
                            double cent = (e.getX() * 12800.0) / getWidth();
                            retune(0, 99, 36 + i, cent);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        repaint();
                    }
                }

                public void mouseMoved(MouseEvent e) {                    
                }                
            });
        }

        public void paint(Graphics g) {
            super.paint(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
                    RenderingHints.VALUE_FRACTIONALMETRICS_ON);

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth(), getHeight());

            int w = getWidth();
            int h = getHeight();
            int th = 30;

            float nw = w / 128f;
            float cx = 0;

            g2.setColor(Color.BLACK);
            g2.drawLine(0, th, w, th);

            Color light_gray1 = new Color(0.9f, 0.9f, 0.9f);
            Color light_gray2 = new Color(0.8f, 0.8f, 0.8f);

            Rectangle2D rect = new Rectangle2D.Double();
            for (int i = 0; i < 128; i++) {
                int b = i % 12;
                boolean a = (b == 1 || b == 3 | b == 6 | b == 8 | b == 10);

                if (!a) {
                    g2.setColor(light_gray1);
                    g2.drawLine((int) cx, th, (int) cx, h);
                } else {
                    g2.setColor(light_gray2);
                    g2.drawLine((int) cx, th, (int) cx, h);
                    g2.drawLine(1 + (int) cx, th, 1 + (int) cx, h);
                }

                if (!a) {
                    // rect.setRect(cx, 0, nw, h);
                    // g2.setColor(Color.WHITE);
                    // g2.fill(rect);

                    g2.setColor(Color.BLACK);
                    // g2.draw(rect);
                    g2.drawLine((int) cx, 0, (int) cx, th);

                    // cx += nw;

                }
                cx += nw;
            }
            cx = 0;
            nw = w / 128f;
            float black_note_width = nw;
            for (int i = 0; i < 128; i++) {
                int b = i % 12;
                boolean a = (b == 1 || b == 3 | b == 6 | b == 8 | b == 10);
                if (!a) {

                } else {
                    rect.setRect(cx, th - th * 4.0 / 7.0, black_note_width,
                            th * 4.0 / 7.0);
                    g2.setColor(Color.BLACK);
                    g2.fill(rect);
                    g2.setColor(Color.BLACK);
                    g2.draw(rect);
                }
                cx += nw;
            }
            
            // Draw harmonics
            for (int j = 0; j < 12; j++) {
                             
              int ra = j;
              int rb = j-1;
              if(j == 0) { ra = 16; rb = 15; }
              if(j == 1) { ra = 9; rb = 8; }
              if(j == 2) { ra = 6; rb = 5; }
              if(j == 3) { ra = 5; rb = 4; }
              if(j == 4) { ra = 4; rb = 3; }
              if(j == 5) { ra = 7; rb = 5; }
              if(j == 6) { ra = 3; rb = 2; }             
              if(j == 7) { ra = 8; rb = 5; }
              if(j == 8) { ra = 5; rb = 3; }
              if(j == 9) { ra = 7; rb = 4; }
              if(j == 10) { ra = 15; rb = 8; }
              if(j == 11) { ra = 2; rb = 1; }
                             
              double ratio = ((double)ra) / ((double)rb);
              double cent = 12.0 * Math.log(ratio) / Math.log(2.0);
              int jx = (int)(cent * (getWidth() / 128.0));
              String s_r = ra + "/" + rb;
                
              for (int i = 0; i < activenotes.length; i++) {
                if (activenotes[i]) {
                    if (i == activenote)
                        g2.setColor(Color.BLUE);
                    else
                        g2.setColor(Color.LIGHT_GRAY);
                    g2.fillOval(activenotes_x[i] - 2 + jx, activenotes_y[i] - 2,
                            4, 4);
                    if((j % 2) == 0)
                        g2.drawString(s_r, activenotes_x[i] - 2 + jx, activenotes_y[i] - 3-6);
                    else
                        g2.drawString(s_r, activenotes_x[i] - 2 + jx, activenotes_y[i] + 13+6);
                    
                    g2.drawLine(activenotes_x[i]+ jx, activenotes_y[i]-4, activenotes_x[i]+ jx, activenotes_y[i]+3);
                }
              }
            }
            
            // Draw notes
            for (int i = 0; i < activenotes.length; i++) {
                if (activenotes[i]) {
                    if (i == activenote)
                        g2.setColor(Color.BLUE);
                    else
                        g2.setColor(Color.LIGHT_GRAY);
                    g2.fillOval(activenotes_x[i] - 10, activenotes_y[i] - 10,
                            20, 20);
                    if (i == activenote)
                    {
                        g2.drawLine(activenotes_x[i], 0, activenotes_x[i], getHeight());
                    }
                    g2.setColor(Color.BLACK);
                    g2.drawOval(activenotes_x[i] - 1, activenotes_y[i] - 1, 2,
                            2);
                }
            }
        }

        public void allOff() {
            for (int i = 0; i < activenotes.length; i++) {
                if (activenotes[i]) {
                    channel.noteOff(36 + i);
                    activenotes[i] = false;
                }
            }
            repaint();
        }
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

        final SoundPainter soundpainter = new SoundPainter();

        Soundbank sbk = synth.getDefaultSoundbank();
        String[] instruments = new String[128];
        for (int i = 0; i < instruments.length; i++) {
            instruments[i] = i + " "
                    + sbk.getInstrument(new Patch(0, i)).getName();
        }
        final JComboBox instrumentcombobox = new JComboBox(instruments);
        instrumentcombobox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int pgm = instrumentcombobox.getSelectedIndex();
                synth.getChannels()[0].programChange(pgm);
                soundpainter.allOff();
            }
        });

        try {
            sendTuningChange(recv, 0, 99);
        } catch (Exception e1) {
        }
        instrumentcombobox.setSelectedIndex(48);

        JPanel instrumentpanel = new JPanel();
        instrumentpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        instrumentpanel.setOpaque(false);
        instrumentpanel.add(new JLabel("Instrument: "));
        instrumentpanel.add(instrumentcombobox);
        boxpanel.add(instrumentpanel);

        Dimension vdim12 = new Dimension(3000, 50);
        soundpainter.setSize(vdim12);
        soundpainter.setPreferredSize(vdim12);
        soundpainter.setMinimumSize(vdim12);
        soundpainter.setMaximumSize(vdim12);
        // soundpainter.setChannel(0);
        // soundpainter.setReceiver(recv);

        JScrollPane scrollpane12 = new JScrollPane(soundpainter);
        scrollpane12.setPreferredSize(new Dimension(500, 160));
        scrollpane12.getViewport().setViewPosition(new Point(1200, 0));

        JPanel panel12 = new JPanel(new BorderLayout());
        panel12.setOpaque(false);
        panel12.add(scrollpane12);
        boxpanel.add(panel12);

    }

}
