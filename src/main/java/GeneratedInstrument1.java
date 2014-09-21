import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Patch;
import javax.sound.midi.Receiver;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.sampled.AudioFormat;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.sun.media.sound.AudioFloatConverter;
import com.sun.media.sound.FFT;
import com.sun.media.sound.SF2Instrument;
import com.sun.media.sound.SF2InstrumentRegion;
import com.sun.media.sound.SF2Layer;
import com.sun.media.sound.SF2LayerRegion;
import com.sun.media.sound.SF2Region;
import com.sun.media.sound.SF2Sample;
import com.sun.media.sound.SF2Soundbank;
import com.sun.media.sound.SoftSynthesizer;

public class GeneratedInstrument1 extends JApplet {

    float dbRange = 100;

    ArrayList<SfGeneratorEditor> generators = new ArrayList<SfGeneratorEditor>();

    private class Preset {
        private String name;

        private int[] gains = new int[gain_sliders.length];

        private int[] widths = new int[gain_sliders.length];

        private double[] harmonic = new double[gain_sliders.length];

        private HashMap<Integer, Integer> gens = new HashMap<Integer, Integer>();

        public Preset(String name) {
            this.name = name;
            gains[0] = 100;
            for (int i = 0; i < harmonic.length; i++) {
                harmonic[i] = i + 1;
            }
        }

        public String toString() {
            return name;
        }

        public void setHarmonic(int id, int gain, int width) {
            if (gain < -100)
                gain = -100;
            if (gain > 0)
                gain = 0;
            if (width < 0)
                width = 0;
            if (width > 100)
                width = 100;
            gains[id - 1] = 100 + gain;
            widths[id - 1] = width;
        }

        public void setHarmonic(int id, double harmonic, int gain, int width) {
            if (gain < -100)
                gain = -100;
            if (gain > 0)
                gain = 0;
            if (width < 0)
                width = 0;
            if (width > 100)
                width = 100;
            gains[id - 1] = 100 + gain;
            widths[id - 1] = width;
            this.harmonic[id - 1] = harmonic;
        }

        public void setGenerator(int genid, int value) {
            gens.put(genid, value);
        }

        public void select() {
            haltScreenUpdates = true;

            for (int i = 0; i < gain_sliders.length; i++) {
                gain_sliders[i].setValue(gains[i]);
            }
            for (int i = 0; i < width_sliders.length; i++) {
                width_sliders[i].setValue(widths[i]);
            }
            for (int i = 0; i < width_sliders.length; i++) {
                if (Math.abs(harmonic[i] - (int) harmonic[i]) < 0.00001)
                    harmonic_field[i].setText("" + (int) harmonic[i]);
                else
                    harmonic_field[i].setText("" + harmonic[i]);
            }

            for (SfGeneratorEditor gen : generators) {
                Integer v = gens.get(gen.gid);
                if (v != null)
                    gen.setValue(v.intValue());
                else
                    gen.setValue(gen.getDefaultValue());
            }

            haltScreenUpdates = false;
            dirty = true;
            designInstrument(false);
            sv.repaint();

        }

    }

    private class SfGeneratorEditor extends JPanel {

        private static final long serialVersionUID = 1L;

        private int gid;

        private JSlider slider;

        private JTextField textfield;

        private short getSFDefaultValue(int generator) {
            if (generator == 8)
                return (short) 13500;
            if (generator == 21)
                return (short) -12000;
            if (generator == 23)
                return (short) -12000;
            if (generator == 25)
                return (short) -12000;
            if (generator == 26)
                return (short) -12000;
            if (generator == 27)
                return (short) -12000;
            if (generator == 28)
                return (short) -12000;
            if (generator == 30)
                return (short) -12000;
            if (generator == 33)
                return (short) -12000;
            if (generator == 34)
                return (short) -12000;
            if (generator == 35)
                return (short) -12000;
            if (generator == 36)
                return (short) -12000;
            if (generator == 38)
                return (short) -12000;
            if (generator == 43)
                return (short) 0x7F00;
            if (generator == 44)
                return (short) 0x7F00;
            if (generator == 46)
                return (short) -1;
            if (generator == 47)
                return (short) -1;
            if (generator == 56)
                return (short) 100;
            if (generator == 58)
                return (short) -1;
            return 0;
        }

        public int getDefaultValue() {
            return getSFDefaultValue(gid);
        }

        private short getSFMinValue(int generator) {
            switch (generator) {
            case 0:
                return 0;
            case 1:
                return -32768;
            case 2:
                return -32768;
            case 3:
                return -32768;
            case 4:
                return 0;
            case 5:
                return -12000;
            case 6:
                return -12000;
            case 7:
                return -12000;
            case 8:
                return 1500;
            case 9:
                return 0;
            case 10:
                return -12000;
            case 11:
                return -12000;
            case 12:
                return -32768;
            case 13:
                return -960;
            case 15:
                return 0;
            case 16:
                return 0;
            case 17:
                return -500;
            case 21:
                return -16000;
            case 22:
                return -12000;
            case 23:
                return -16000;
            case 24:
                return -12000;
            case 25:
                return -12000;
            case 26:
                return -12000;
            case 27:
                return -12000;
            case 28:
                return -12000;
            case 29:
                return 0;
            case 30:
                return -12000;
            case 31:
                return -1200;
            case 32:
                return -1200;
            case 33:
                return -12000;
            case 34:
                return -12000;
            case 35:
                return -12000;
            case 36:
                return -12000;
            case 37:
                return 0;
            case 38:
                return -12000;
            case 39:
                return -1200;
            case 40:
                return -1200;
            case 43:
                return 0;
            case 44:
                return 0;
            case 45:
                return -32768;
            case 46:
                return 0;
            case 47:
                return 0;
            case 48:
                return 0;
            case 50:
                return -32768;
            case 51:
                return -120;
            case 52:
                return -99;
            case 54:
                return -32768;
            case 56:
                return 0;
            case 57:
                return 1;
            case 58:
                return 0;
            default:
                return Short.MIN_VALUE;
            }
        }

        private short getSFMaxValue(int generator) {
            switch (generator) {
            case 0:
                return 32767;
            case 1:
                return 0;
            case 2:
                return 32767;
            case 3:
                return 32767;
            case 4:
                return 32767;
            case 5:
                return 12000;
            case 6:
                return 12000;
            case 7:
                return 12000;
            case 8:
                return 13500;
            case 9:
                return 960;
            case 10:
                return 12000;
            case 11:
                return 12000;
            case 12:
                return 0;
            case 13:
                return 960;
            case 15:
                return 1000;
            case 16:
                return 1000;
            case 17:
                return 500;
            case 21:
                return 5000;
            case 22:
                return 4500;
            case 23:
                return 5000;
            case 24:
                return 4500;
            case 25:
                return 5000;
            case 26:
                return 8000;
            case 27:
                return 5000;
            case 28:
                return 8000;
            case 29:
                return 1000;
            case 30:
                return 8000;
            case 31:
                return 1200;
            case 32:
                return 1200;
            case 33:
                return 5000;
            case 34:
                return 8000;
            case 35:
                return 5000;
            case 36:
                return 8000;
            case 37:
                return 1440;
            case 38:
                return 8000;
            case 39:
                return 1200;
            case 40:
                return 1200;
            case 43:
                return 127;
            case 44:
                return 127;
            case 45:
                return 32767;
            case 46:
                return 127;
            case 47:
                return 127;
            case 48:
                return 1440;
            case 50:
                return 32767;
            case 51:
                return 120;
            case 52:
                return 99;
            case 54:
                return 32767;
            case 56:
                return 1200;
            case 57:
                return 127;
            case 58:
                return 127;
            default:
                return Short.MAX_VALUE;
            }
        }

        private String cent = "cent";

        private String cb = "0.1dB";

        private String timcent = "timcent";

        private String semitone = "semitone";

        private String mpercent = "0.1%";

        private String negative_mpercent = "-0.1%";

        private String getSFType(int generator) {
            switch (generator) {
            case 5:
                return cent;
            case 6:
                return cent;
            case 7:
                return cent;
            case 8:
                return cent;
            case 9:
                return cb;
            case 10:
                return cent;
            case 11:
                return cent;
            case 13:
                return cb;
            case 15:
                return mpercent;
            case 16:
                return mpercent;
            case 17:
                return mpercent;
            case 21:
                return timcent;
            case 22:
                return cent;
            case 23:
                return timcent;
            case 24:
                return cent;
            case 25:
                return timcent;
            case 26:
                return timcent;
            case 27:
                return timcent;
            case 28:
                return timcent;
            case 29:
                return negative_mpercent;
            case 30:
                return timcent;
            case 31:
                return timcent;
            case 32:
                return timcent;
            case 33:
                return timcent;
            case 34:
                return timcent;
            case 35:
                return timcent;
            case 36:
                return timcent;
            case 37:
                return cb;
            case 38:
                return timcent;
            case 39:
                return timcent;
            case 40:
                return timcent;
            case 48:
                return cb;
            case 51:
                return semitone;
            case 52:
                return cent;
            case 56:
                return cent;
            default:
                return "";
            }

        }

        public SfGeneratorEditor(final int gid) {
            this.gid = gid;

            setOpaque(false);
            setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));

            JButton defaulter = new JButton();
            defaulter.setText("R");
            defaulter.setMargin(new Insets(0, 0, 0, 0));
            defaulter.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    slider.setValue(getSFDefaultValue(gid));
                }
            });

            slider = new JSlider();
            slider.setOpaque(false);
            slider.setMinimum(getSFMinValue(gid));
            slider.setMaximum(getSFMaxValue(gid));
            slider.setValue(getSFDefaultValue(gid));
            slider.setMaximumSize(new Dimension(85, 20));
            slider.setPreferredSize(new Dimension(85, 20));
            slider.addChangeListener(new ChangeListener() {
                public void stateChanged(ChangeEvent arg0) {
                    dirty = true;
                    updateDisplay();
                }
            });
            add(slider);
            textfield = new JTextField("");
            textfield.setMaximumSize(new Dimension(50, 20));
            textfield.setPreferredSize(new Dimension(50, 20));
            textfield.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent arg0) {
                    if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                        slider.setValue(Integer.parseInt(textfield.getText()));
                        arg0.consume();
                    }
                }

                public void keyReleased(KeyEvent arg0) {
                }

                public void keyTyped(KeyEvent arg0) {
                }
            });
            textfield.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                }

                public void focusLost(FocusEvent arg0) {
                    int x = Integer.parseInt(textfield.getText());
                    if (slider.getValue() != x)
                        slider.setValue(x);
                    updateDisplay();
                }
            });
            add(textfield);
            updateDisplay();
            add(defaulter);
            add(new JLabel(getSFType(gid)));
            generators.add(this);
        }

        public void setValue(int i) {
            if (slider.getValue() != i)
                slider.setValue(i);
        }

        public int getValue() {
            return slider.getValue();
        }

        private void updateDisplay() {
            if (textfield == null)
                return;
            textfield.setText("" + slider.getValue());
        }

        public void process(SF2Region region) {
            if (slider.getValue() != getSFDefaultValue(gid)) {
                region.putInteger(gid, slider.getValue());
            }
        }

    }

    private class SpecturmViewer extends JComponent {
        private static final long serialVersionUID = 1L;

        public void paint(Graphics g) {
            super.paint(g);

            int fftlen = data.length / 25;

            double base = 8 * 15;

            Graphics2D g2 = (Graphics2D) g;
            /*
             * g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
             * RenderingHints.VALUE_ANTIALIAS_ON);
             * g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
             * RenderingHints.VALUE_FRACTIONALMETRICS_ON);
             */
            int w = getWidth();
            int h = getHeight() - 20;

            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, w, h);

            if (data != null) {

                g2.setStroke(new BasicStroke(1f));

                g2.setColor(Color.LIGHT_GRAY);
                for (int i = 0; i <= dbRange; i += 10) {
                    float db = -i;
                    float y = 10 + ((h - 20) * (-db / dbRange));
                    if (y > (h - 10))
                        y = h - 10;
                    g2.draw(new Line2D.Float(20, y, w, y));
                }

                for (int i = 0; i <= gain_sliders.length; i++) {
                    float x = (float) (20 + (i * base * (w - 20))
                            / (float) fftlen);
                    g2.draw(new Line2D.Float(x, 10, x, h - 10));
                }

                g2.setColor(Color.BLACK);
                for (int i = 10; i < dbRange; i += 10) {
                    float db = -i;
                    float y = 10 + ((h - 20) * (-db / dbRange));
                    if (y > (h - 10))
                        y = h - 10;
                    g2.drawString("-" + i, 0, 4 + (int) y);
                }

                for (int i = 1; i <= gain_sliders.length; i++) {
                    float x = (float) (20 + (i * base * (w - 20))
                            / (float) fftlen);
                    g2.drawString("" + i, x - 4, h + 3);
                }

                g2.setStroke(new BasicStroke(1.5f));

                float lastx = 20;
                float lasty = h - 10;
                for (int i = 0; i < fftlen; i++) {
                    float x = 20 + (i * (w - 20)) / (float) fftlen;

                    float db = (float) (20f * Math.log10(data[i * 2]));
                    float y = 10 + ((h - 20) * (-db / dbRange));

                    if (y > (h - 10))
                        y = h - 10;
                    g2.draw(new Line2D.Float(lastx, lasty, x, y));

                    // g2.drawLine((int)lastx, (int)lasty, (int)x, y);
                    lastx = x;
                    lasty = y;
                }
            }

        }
    }

    private static final long serialVersionUID = 1L;

    private Synthesizer synth = new SoftSynthesizer();

    private Receiver recv;

    boolean stereo_mode = false;

    JPanel firstpanel;

    JLabel infolabel;

    String error_text = "";

    JTextField[] harmonic_field = new JTextField[20];

    JSlider[] width_sliders = new JSlider[20];

    JSlider[] gain_sliders = new JSlider[20];

    SpecturmViewer sv;

    boolean dirty = false;

    Soundbank sbk = null;

    double[] data = null;

    double[] data_audio;

    double[] data_audio2;

    ArrayList<Preset> presets = new ArrayList<Preset>();

    boolean haltScreenUpdates = false;

    public void complexGaussianDist(double[] cdata, double m, double s, double v) {
        if (s < 0.5) {
            int im = (int) m;
            cdata[im * 2] = v;
            return;
        }
        for (int x = 0; x < cdata.length / 4; x++) {
            cdata[x * 2] += v
                    * Math.exp((-1.0 / 2.0) * Math.pow((x - m) / s, 2.0));
        }
    }

    public double[] realPart(double[] in) {
        double[] out = new double[in.length / 2];
        for (int i = 0; i < out.length; i++) {
            out[i] = in[i * 2];
        }
        return out;
    }

    public void randomPhase(double[] data) {
        for (int i = 0; i < data.length; i += 2) {
            double phase = Math.random() * 2 * Math.PI;
            double d = data[i];
            data[i] = Math.sin(phase) * d;
            data[i + 1] = Math.cos(phase) * d;
        }
    }

    FFT ifft_obj = null;

    int ifft_obj_len = 0;

    public void ifft(double[] data) {
        if (ifft_obj == null || ifft_obj_len != data.length / 2) {
            ifft_obj_len = data.length / 2;
            ifft_obj = new FFT(ifft_obj_len, 1);

        }
        ifft_obj.transform(data);
    }

    public SF2Sample newSimpleFFTSample(SF2Soundbank sf2, String name,
            double[] data, double base) {
        return newSimpleFFTSample(sf2, name, data, base, 10);
    }

    public float[] toFloat(double[] in) {
        float[] out = new float[in.length];
        for (int i = 0; i < out.length; i++) {
            out[i] = (float) in[i];
        }
        return out;
    }

    public float[] loopExtend(float[] data, int newsize) {
        float[] outdata = new float[newsize];
        int p_len = data.length;
        int p_ps = 0;
        for (int i = 0; i < outdata.length; i++) {
            outdata[i] = data[p_ps];
            p_ps++;
            if (p_ps == p_len)
                p_ps = 0;
        }
        return outdata;
    }

    public void normalize(double[] data, double target) {
        double maxvalue = 0;
        for (int i = 0; i < data.length; i++) {
            if (data[i] > maxvalue)
                maxvalue = data[i];
            if (-data[i] > maxvalue)
                maxvalue = -data[i];
        }
        if (maxvalue == 0)
            return;
        double gain = target / maxvalue;
        for (int i = 0; i < data.length; i++)
            data[i] *= gain;
    }

    public void fadeUp(float[] data, int samples) {
        double dsamples = samples;
        for (int i = 0; i < samples; i++)
            data[i] *= i / dsamples;
    }

    public byte[] toBytes(float[] in, AudioFormat format) {
        byte[] out = new byte[in.length * format.getFrameSize()];
        return AudioFloatConverter.getConverter(format).toByteArray(in, out);
    }

    public SF2Sample newSimpleFFTSample(SF2Soundbank sf2, String name,
            double[] data, double base, int fadeuptime) {

        int fftsize = data.length / 2;
        AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
        double basefreq = (base / fftsize) * format.getSampleRate() * 0.5;

        randomPhase(data);
        ifft(data);
        data = realPart(data);
        normalize(data, 0.9);
        float[] fdata = toFloat(data);
        fdata = loopExtend(fdata, fdata.length + 512);
        fadeUp(fdata, fadeuptime);
        byte[] bdata = toBytes(fdata, format);

        /*
         * Create SoundFont2 sample.
         */
        SF2Sample sample = new SF2Sample(sf2);
        sample.setName(name);
        sample.setData(bdata);
        sample.setStartLoop(256);
        sample.setEndLoop(fftsize + 256);
        sample.setSampleRate((long) format.getSampleRate());
        double orgnote = (69 + 12)
                + (12 * Math.log(basefreq / 440.0) / Math.log(2));
        sample.setOriginalPitch((int) orgnote);
        sample.setPitchCorrection((byte) (-(orgnote - (int) orgnote) * 100.0));
        sf2.addResource(sample);

        return sample;
    }

    public Soundbank designInstrument(boolean createsbk) {

        int x = 8;
        int fftsize = 4096 * x;
        if (data == null || data.length != fftsize * 2)
            data = new double[fftsize * 2];

        Arrays.fill(data, 0);

        double[] data = this.data;
        double base = x * 15;

        for (int i = 0; i < width_sliders.length; i++) {
            double h = i + 1;
            try {
                h = Double.parseDouble(harmonic_field[i].getText());
            } catch (NumberFormatException e) {
                harmonic_field[i].setText("" + (i + 1));
            }
            if (gain_sliders[i].getValue() > 0) {
                double width = (width_sliders[i].getValue()) / 4;
                double db = -(100 - gain_sliders[i].getValue());
                double gain = Math.pow(10, db / 20.0);
                complexGaussianDist(data, base * h, width, gain);
            }
        }

        if (!createsbk)
            return null;

        SF2Soundbank sf2 = new SF2Soundbank();
        sf2.setName("My SoundFont");
        sf2.setVendor("Generated");
        sf2.setDescription("A newly created soundfont");

        SF2Layer layer1 = null;
        SF2Layer layer2 = null;

        for (int i = 0; i < 2; i++) {

            double[] data_audio = i == 0 ? this.data_audio : this.data_audio2;
            if (data_audio == null || data_audio.length != fftsize * 2) {
                data_audio = new double[fftsize * 2];
                if (i == 0)
                    this.data_audio = data_audio;
                else
                    this.data_audio2 = data_audio;
            }
            System.arraycopy(data, 0, data_audio, 0, data_audio.length);

            SF2Sample sample = newSimpleFFTSample(sf2, "FFT Sample " + i,
                    data_audio, base, 200);
            SF2Layer layer = newLayer(sf2, "FFT Layer " + i, sample);
            SF2Region region = layer.getRegions().get(0);
            region.putInteger(SF2Region.GENERATOR_SAMPLEMODES, 1); // set loop
            if (stereo_mode)
                if (i == 0)
                    region.putInteger(SF2Region.GENERATOR_PAN, -500);
                else
                    region.putInteger(SF2Region.GENERATOR_PAN, 500);

            for (SfGeneratorEditor gen : generators) {
                gen.process(region);
            }

            if (i == 0)
                layer1 = layer;
            else
                layer2 = layer;

            if (!stereo_mode)
                break;

        }

        if (!stereo_mode)
            newInstrument(sf2, "FFT Instrument", new Patch(0, 0), layer1);
        else
            newInstrument(sf2, "FFT Instrument", new Patch(0, 0), layer1,
                    layer2);

        return sf2;

    }

    public SF2Layer newLayer(SF2Soundbank sf2, String name, SF2Sample sample) {
        SF2LayerRegion region = new SF2LayerRegion();
        region.setSample(sample);

        SF2Layer layer = new SF2Layer(sf2);
        layer.setName(name);
        layer.getRegions().add(region);
        sf2.addResource(layer);

        return layer;
    }

    public SF2Instrument newInstrument(SF2Soundbank sf2, String name,
            Patch patch, SF2Layer... layers) {

        /*
         * Create SoundFont2 instrument.
         */
        SF2Instrument ins = new SF2Instrument(sf2);
        ins.setPatch(patch);
        ins.setName(name);
        sf2.addInstrument(ins);

        /*
         * Create region for instrument.
         */
        for (int i = 0; i < layers.length; i++) {
            SF2InstrumentRegion insregion = new SF2InstrumentRegion();
            insregion.setLayer(layers[i]);
            ins.getRegions().add(insregion);
        }

        return ins;
    }

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

        Dimension vdim12 = new Dimension(1000, 50);
        VirtualKeyboard12 vkeyboard12 = new VirtualKeyboard12();
        vkeyboard12.setSize(vdim12);
        vkeyboard12.setPreferredSize(vdim12);
        vkeyboard12.setMinimumSize(vdim12);
        vkeyboard12.setMaximumSize(vdim12);
        vkeyboard12.setChannel(0);

        final MidiChannel channel1 = synth.getChannels()[0];
        channel1.controlChange(7, 127);

        Receiver recv2 = new Receiver() {
            public void close() {
            }

            public void send(MidiMessage arg0, long arg1) {
                if (dirty) {
                    synth.unloadAllInstruments(sbk);
                    sbk = designInstrument(true);
                    synth.loadAllInstruments(sbk);
                    channel1.programChange(0);
                    dirty = false;

                }
                recv.send(arg0, arg1);
            }
        };

        vkeyboard12.setReceiver(recv2);

        JScrollPane scrollpane12 = new JScrollPane(vkeyboard12);
        scrollpane12.setPreferredSize(new Dimension(500, 80));
        scrollpane12.getViewport().setViewPosition(new Point(200, 0));

        JPanel panel12 = new JPanel(new BorderLayout());
        panel12.setOpaque(false);
        panel12.add(scrollpane12);

        JPanel panelslides = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.gridy = 0;
        c.gridx = 0;
        panelslides.add(new JLabel("Harmonic"), c);
        c.gridx = 1;
        panelslides.add(new JLabel("Amplitude"), c);
        c.gridx = 2;
        panelslides.add(new JLabel("Bandwidth"), c);

        final ChangeListener changelistener = new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                if (haltScreenUpdates)
                    return;
                dirty = true;
                if (sv != null) {
                    designInstrument(false);
                    sv.repaint();
                }
            }
        };

        for (int i = 0; i < width_sliders.length; i++) {

            JSlider amp_slider = new JSlider();
            amp_slider.setOpaque(false);
            amp_slider.setMinimum(0);
            amp_slider.setMaximum(100);
            amp_slider.setValue(0);
            amp_slider.addChangeListener(changelistener);

            JSlider w_slider = new JSlider();
            w_slider.setOpaque(false);
            w_slider.setMinimum(0);
            w_slider.setMaximum(100);
            w_slider.setValue(0);
            w_slider.addChangeListener(changelistener);

            JTextField tf = new JTextField("" + (i + 1));
            tf.setMaximumSize(new Dimension(50, 20));
            tf.setPreferredSize(new Dimension(50, 20));
            tf.addKeyListener(new KeyListener() {
                public void keyPressed(KeyEvent arg0) {
                    if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
                        changelistener.stateChanged(null);
                    }
                }

                public void keyReleased(KeyEvent arg0) {
                }

                public void keyTyped(KeyEvent arg0) {
                }
            });
            tf.addFocusListener(new FocusListener() {
                public void focusGained(FocusEvent arg0) {
                }

                public void focusLost(FocusEvent arg0) {
                    changelistener.stateChanged(null);
                }
            });

            harmonic_field[i] = tf;
            width_sliders[i] = w_slider;
            gain_sliders[i] = amp_slider;

            c.gridy = i + 1;
            c.gridx = 0;
            // panelslides.add(new JLabel("" + (i + 1)), c);
            panelslides.add(tf, c);
            c.gridx = 1;
            panelslides.add(amp_slider, c);
            c.gridx = 2;
            panelslides.add(w_slider, c);

        }
        gain_sliders[0].setValue(100);
        GridBagConstraints ec = new GridBagConstraints();
        ec.gridy = 100;
        ec.gridwidth = 2;
        ec.weightx = 10;
        ec.weighty = 10;

        c.anchor = GridBagConstraints.LINE_START;
        JPanel volenv = new JPanel(new GridBagLayout());
        volenv.setBorder(BorderFactory.createTitledBorder("Volume Envelope"));
        volenv.setOpaque(false);
        c.gridy = 0;
        c.gridx = 0;
        volenv.add(new JLabel("Delay"), c);
        c.gridx = 1;
        volenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_DELAYVOLENV), c);
        c.gridy = 1;
        c.gridx = 0;
        volenv.add(new JLabel("Attack"), c);
        c.gridx = 1;
        volenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_ATTACKVOLENV), c);
        c.gridy = 2;
        c.gridx = 0;
        volenv.add(new JLabel("Hold"), c);
        c.gridx = 1;
        volenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_HOLDVOLENV), c);
        c.gridy = 3;
        c.gridx = 0;
        volenv.add(new JLabel("Decay"), c);
        c.gridx = 1;
        volenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_DECAYVOLENV), c);
        c.gridy = 4;
        c.gridx = 0;
        volenv.add(new JLabel("Sustain"), c);
        c.gridx = 1;
        volenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_SUSTAINVOLENV), c);
        c.gridy = 5;
        c.gridx = 0;
        volenv.add(new JLabel("Release"), c);
        c.gridx = 1;
        volenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_RELEASEVOLENV), c);
        c.gridy = 6;
        c.gridx = 0;
        volenv.add(new JLabel("Attenuation"), c);
        c.gridx = 1;
        volenv.add(
                new SfGeneratorEditor(SF2Region.GENERATOR_INITIALATTENUATION),
                c);
        volenv.add(new JLabel(), ec);

        JPanel modenv = new JPanel(new GridBagLayout());
        modenv.setBorder(BorderFactory
                .createTitledBorder("Modulation Envelope"));
        modenv.setOpaque(false);
        c.gridy = 0;
        c.gridx = 0;
        modenv.add(new JLabel("Delay"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_DELAYMODENV), c);
        c.gridy = 1;
        c.gridx = 0;
        modenv.add(new JLabel("Attack"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_ATTACKMODENV), c);
        c.gridy = 2;
        c.gridx = 0;
        modenv.add(new JLabel("Hold"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_HOLDMODENV), c);
        c.gridy = 3;
        c.gridx = 0;
        modenv.add(new JLabel("Decay"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_DECAYMODENV), c);
        c.gridy = 4;
        c.gridx = 0;
        modenv.add(new JLabel("Sustain"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_SUSTAINMODENV), c);
        c.gridy = 5;
        c.gridx = 0;
        modenv.add(new JLabel("Release"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_RELEASEMODENV), c);
        c.gridy = 6;
        c.gridx = 0;
        modenv.add(new JLabel("To Filter Cutoff"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_MODENVTOFILTERFC),
                c);
        c.gridy = 7;
        c.gridx = 0;
        modenv.add(new JLabel("To Pitch"), c);
        c.gridx = 1;
        modenv.add(new SfGeneratorEditor(SF2Region.GENERATOR_MODENVTOPITCH), c);
        modenv.add(new JLabel(), ec);

        JPanel filter_gen = new JPanel(new GridBagLayout());
        filter_gen.setBorder(BorderFactory.createTitledBorder("Filter"));
        filter_gen.setOpaque(false);
        c.gridy = 0;
        c.gridx = 0;
        filter_gen.add(new JLabel("Cutoff Freq."), c);
        c.gridx = 1;
        filter_gen.add(new SfGeneratorEditor(
                SF2Region.GENERATOR_INITIALFILTERFC), c);
        c.gridy = 1;
        c.gridx = 0;
        filter_gen.add(new JLabel("Q"), c);
        c.gridx = 1;
        filter_gen.add(
                new SfGeneratorEditor(SF2Region.GENERATOR_INITIALFILTERQ), c);
        filter_gen.add(new JLabel(), ec);

        JPanel lfo_mod = new JPanel(new GridBagLayout());
        lfo_mod.setBorder(BorderFactory.createTitledBorder("Modulation LFO"));
        lfo_mod.setOpaque(false);
        c.gridy = 0;
        c.gridx = 0;
        lfo_mod.add(new JLabel("Delay"), c);
        c.gridx = 1;
        lfo_mod.add(new SfGeneratorEditor(SF2Region.GENERATOR_DELAYMODLFO), c);
        c.gridy = 1;
        c.gridx = 0;
        lfo_mod.add(new JLabel("Freq"), c);
        c.gridx = 1;
        lfo_mod.add(new SfGeneratorEditor(SF2Region.GENERATOR_FREQMODLFO), c);
        c.gridy = 2;
        c.gridx = 0;
        lfo_mod.add(new JLabel("To Filter Cutoff"), c);
        c.gridx = 1;
        lfo_mod.add(
                new SfGeneratorEditor(SF2Region.GENERATOR_MODLFOTOFILTERFC), c);
        c.gridy = 3;
        c.gridx = 0;
        lfo_mod.add(new JLabel("To Pitch"), c);
        c.gridx = 1;
        lfo_mod
                .add(new SfGeneratorEditor(SF2Region.GENERATOR_MODLFOTOPITCH),
                        c);
        lfo_mod.add(new JLabel(), ec);

        JPanel lfo_vib = new JPanel(new GridBagLayout());
        lfo_vib.setBorder(BorderFactory.createTitledBorder("Vibration LFO"));
        lfo_vib.setOpaque(false);
        c.gridy = 0;
        c.gridx = 0;
        lfo_vib.add(new JLabel("Delay"), c);
        c.gridx = 1;
        lfo_vib.add(new SfGeneratorEditor(SF2Region.GENERATOR_DELAYVIBLFO), c);
        c.gridy = 1;
        c.gridx = 0;
        lfo_vib.add(new JLabel("Freq"), c);
        c.gridx = 1;
        lfo_vib.add(new SfGeneratorEditor(SF2Region.GENERATOR_FREQVIBLFO), c);
        c.gridy = 2;
        c.gridx = 0;
        lfo_vib.add(new JLabel("To Pitch"), c);
        c.gridx = 1;
        lfo_vib
                .add(new SfGeneratorEditor(SF2Region.GENERATOR_VIBLFOTOPITCH),
                        c);
        lfo_vib.add(new JLabel(), ec);

        GridBagConstraints c2 = new GridBagConstraints();
        c2.fill = GridBagConstraints.BOTH;
        JPanel generators = new JPanel(new GridBagLayout());
        generators.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        generators.setOpaque(false);
        c2.gridx = 0;
        c2.gridy = 0;
        generators.add(volenv, c2);
        c2.gridx = 1;
        c2.gridy = 0;
        generators.add(modenv, c2);
        c2.gridx = 0;
        c2.gridy = 1;
        generators.add(lfo_vib, c2);
        c2.gridx = 1;
        c2.gridy = 1;
        generators.add(lfo_mod, c2);
        c2.gridx = 0;
        c2.gridy = 2;
        generators.add(filter_gen, c2);
        generators.add(new JLabel(), ec);

        sbk = designInstrument(true);

        synth.unloadAllInstruments(synth.getDefaultSoundbank());
        synth.loadAllInstruments(sbk);
        channel1.programChange(0);

        panelslides.setOpaque(false);
        panelslides.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        sv = new SpecturmViewer();
        Dimension dim = new Dimension(300, 200);
        sv.setMinimumSize(dim);
        sv.setPreferredSize(dim);

        initPresets();
        final JComboBox presetSelector = new JComboBox(presets.toArray());
        presetSelector.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((Preset) presetSelector.getSelectedItem()).select();
            }

        });

        JPanel instrumentpanel = new JPanel();
        instrumentpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        instrumentpanel.setOpaque(false);
        instrumentpanel.add(new JLabel("Preset: "));
        instrumentpanel.add(presetSelector);
        final JCheckBox reberbcheckbox = new JCheckBox("Reverb");
        reberbcheckbox.setOpaque(false);
        reberbcheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                channel1.controlChange(91, reberbcheckbox.isSelected() ? 127
                        : 0);
            }
        });
        channel1.controlChange(91, 0);
        instrumentpanel.add(reberbcheckbox);
        final JCheckBox choruscheckbox = new JCheckBox("Chorus");
        choruscheckbox.setOpaque(false);
        choruscheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                channel1.controlChange(93, choruscheckbox.isSelected() ? 127
                        : 0);
            }
        });
        channel1.controlChange(93, 0);
        instrumentpanel.add(choruscheckbox);
        final JCheckBox portamentocheckbox = new JCheckBox("Portamento");
        portamentocheckbox.setOpaque(false);
        portamentocheckbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {

                if (portamentocheckbox.isSelected()) {
                    channel1.controlChange(126, 1); // Mono Mode
                    channel1.controlChange(65, 127); // Set Portamento On
                    channel1.controlChange(5, 67); // Set portamento time
                } else {
                    channel1.controlChange(127, 1); // Poly Mode
                    channel1.controlChange(65, 0); // Set Portamento off
                }
            }
        });
        instrumentpanel.add(portamentocheckbox);

        final JCheckBox stereo_mode_checkbox = new JCheckBox("Stereo");
        stereo_mode_checkbox.setOpaque(false);
        stereo_mode_checkbox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                changelistener.stateChanged(null);
                stereo_mode = stereo_mode_checkbox.isSelected();
            }
        });
        instrumentpanel.add(stereo_mode_checkbox);

        boxpanel.add(instrumentpanel);
        boxpanel.add(panel12);
        boxpanel.add(sv);

        JPanel presets_panel = new JPanel();
        presets_panel.setLayout(new BoxLayout(presets_panel, BoxLayout.Y_AXIS));
        presets_panel.setOpaque(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Harmonic Profile", panelslides);
        tabs.addTab("Instrument Settings", generators);
        boxpanel.add(tabs);

    }

    public void initPresets() {
        Preset preset;

        preset = new Preset("Sine wave");
        presets.add(preset);

        preset = new Preset("Square wave");
        for (int i = 1; i <= 20; i += 2) {
            double x = 1.0 / (double) i;
            preset.setHarmonic(i, (int) (20 * Math.log10(x)), 0);
        }
        presets.add(preset);

        preset = new Preset("Triangle wave");
        for (int i = 1; i <= 20; i += 2) {
            double x = 1.0 / (double) (i * i);
            preset.setHarmonic(i, (int) (20 * Math.log10(x)), 0);
        }
        presets.add(preset);
        preset = new Preset("Sawtooth wave");
        for (int i = 1; i <= 20; i++) {
            double x = 1.0 / (double) i;
            preset.setHarmonic(i, (int) (20 * Math.log10(x)), 0);
        }
        presets.add(preset);

        preset = new Preset("Synth Piano");
        preset.setHarmonic(1, 0, 2);
        preset.setHarmonic(2, -20, 3);
        preset.setHarmonic(3, -40, 4);
        preset.setHarmonic(4, -15, 5);
        preset.setHarmonic(5, -10, 6);
        preset.setHarmonic(6, -40, 7);
        preset.setHarmonic(7, -35, 8);
        preset.setHarmonic(8, -45, 9);
        preset.setHarmonic(9, -55, 10);
        preset.setHarmonic(10, -60, 11);
        preset.setHarmonic(11, -65, 12);
        preset.setHarmonic(12, -55, 13);
        preset.setHarmonic(13, -65, 14);
        preset.setHarmonic(14, -70, 15);
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -12000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, -1000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, 1440);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERFC, 9500);
        presets.add(preset);
        
        preset = new Preset("Flute");
        preset.setHarmonic(1, 0, 0);
        preset.setHarmonic(2, 0, 0);
        for (int i = 2; i < 20; i++) {
            preset.setHarmonic(i + 1, -(i-2) * 5 - 10, 0);
        }        
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -6000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, -1000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, -100);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERFC, 9500);
        presets.add(preset);
        

        preset = new Preset("Flute 2");
        preset.setHarmonic(1, 0, 0);
        preset.setHarmonic(2, -25, 0);
        preset.setHarmonic(3, -15, 0);
        preset.setHarmonic(4, -30, 0);
        preset.setHarmonic(5, -25, 0);
        preset.setHarmonic(6, -40, 0);
        preset.setHarmonic(7, -35, 0);
        preset.setHarmonic(8, -45, 0);
        preset.setHarmonic(9, -55, 0);
        preset.setHarmonic(10, -60, 0);
        preset.setHarmonic(11, -65, 0);
        preset.setHarmonic(12, -55, 0);
        preset.setHarmonic(13, -65, 0);
        preset.setHarmonic(14, -70, 0);
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -6000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, -1000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, -100);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERFC, 9500);
        presets.add(preset);

        preset = new Preset("Trumpet");
        preset.setHarmonic(1, -20, 0);
        preset.setHarmonic(2, -15, 0);
        preset.setHarmonic(3, -8, 0);
        for (int i = 3; i < 20; i++) {
            preset.setHarmonic(i + 1, -i * 4, 0);
        }
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -10000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 0);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, -100);

        preset.setGenerator(SF2Region.GENERATOR_ATTACKMODENV, -4000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEMODENV, -2500);
        preset.setGenerator(SF2Region.GENERATOR_MODENVTOFILTERFC, 5000);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERFC, 4500);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERQ, 10);

        presets.add(preset);

        preset = new Preset("Horn");
        preset.setHarmonic(1, -10, 0);
        for (int i = 1; i < 20; i++) {
            preset.setHarmonic(i + 1, -i * 3, 0);
        }
        preset.setGenerator(SF2Region.GENERATOR_SAMPLEMODES, 1);
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -6000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, -1000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, -100);

        preset.setGenerator(SF2Region.GENERATOR_ATTACKMODENV, -500);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEMODENV, 12000);
        preset.setGenerator(SF2Region.GENERATOR_MODENVTOFILTERFC, 5000);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERFC, 4500);

        presets.add(preset);

        preset = new Preset("Strings");
        for (int i = 0; i < 20; i++) {
            preset.setHarmonic(i + 1, -i * 4, i * 4 + 5);
        }
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -4000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 2000);

        presets.add(preset);

        preset = new Preset("Choir");
        for (int i = 0; i < 20; i++) {
            preset.setHarmonic(i + 1, -i * 4, i * 4 + 10);
        }
        preset.setHarmonic(5 + 1, -40, 5 * 4);
        preset.setHarmonic(6 + 1, -50, 6 * 4);
        preset.setHarmonic(7 + 1, -60, 7 * 4);
        preset.setHarmonic(8 + 1, -70, 8 * 4);

        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -4000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 2000);

        presets.add(preset);
        
        preset = new Preset("Choir 2");
        for (int i = 0; i < 4; i++) {
            preset.setHarmonic(i + 1, -i * 7, i * 4 + 10);
        }
        for (int i = 10; i < 20; i++) {
            preset.setHarmonic(i + 1, -i * 4, i * 4 + 10);
        }
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -4000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 2000);
        presets.add(preset);        

        preset = new Preset("Bell");
        for (int i = 0; i < 20; i++) {
            preset.setHarmonic(i + 1, (i + 1) + (i == 0 ? 0 : 0.05 + (i == 1 ? 0.15 : 0)), -i * 5,
                    i * 1 + 5);
        }
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, 1440);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_INITIALFILTERFC, 9500);

        presets.add(preset);

        preset = new Preset("Musical Bell");
        for (int i = 0; i < 20; i++) {
            preset.setHarmonic(i + 1, (i + 1) + (i == 0 ? 0 : 0.2), (i == 0 ? 0
                    : -35)
                    + (i == 16 ? 30 : 0)
                    + (int) (20 * Math.log10(1.0 / (i * i + 1.0))),
                    (i == 16 ? 20 : 0) + i);
        }
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, 1440);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, 4000);

        presets.add(preset);

        preset = new Preset("New Age");
        for (int i = 0; i < 20; i++) {
            preset.setHarmonic(i + 1, -i * 4, i * 4 + 5);
        }
        preset.setHarmonic(20, -30, 85);
        preset.setGenerator(SF2Region.GENERATOR_ATTACKVOLENV, -4000);
        preset.setGenerator(SF2Region.GENERATOR_RELEASEVOLENV, 4000);
        preset.setGenerator(SF2Region.GENERATOR_DECAYVOLENV, -1200);
        preset.setGenerator(SF2Region.GENERATOR_SUSTAINVOLENV, 150);

        presets.add(preset);

    }

}
