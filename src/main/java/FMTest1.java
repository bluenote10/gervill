import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;

import com.sun.media.sound.ModelAbstractOscillator;


public class FMTest1 {
	
	// A implementation of very simple FM Oscillator
	// =============================================
	
	public static class MyOscillator extends ModelAbstractOscillator	
	{
		double ix = 0;
		double last_ix_step = -1;
		
		public int read(float[][] buffers, int offset, int len) throws IOException {
			
			// Grab channel 0 buffer from buffers
			float[] buffer = buffers[0];	
 
			// Calculate ix step so sin oscillirator is tuned so 6900 cents is 440 hz
			double target_ix_step = 
				Math.exp((getPitch()-6900) * (Math.log(2.0) / 1200.0))
				* (440 / getSampleRate()) * (Math.PI*2);		
			double ix_step = last_ix_step;
			if(ix_step == -1) ix_step = target_ix_step;			
			double ix_step_step = (target_ix_step - ix_step)/len;
			
			// Simple FM synthesizer implementation
			int endoffset = offset + len;
			for (int i = offset; i < endoffset; i++) {
				buffer[i] = (float)Math.sin(ix + Math.sin(ix*3));			
				ix += ix_step;
				// ix_step_step is used for
				// smooth pitch changes 
				ix_step += ix_step_step;  
			}
			
			last_ix_step = target_ix_step;
			
			return len;
		}

		
	}
		
	// This code is only for testing the instrument we defined/created above.
    // ======================================================================
	
	public static void main(String[] args) throws Exception {
				
		Synthesizer synth = MidiSystem.getSynthesizer();
		synth.open();
		synth.unloadAllInstruments(synth.getDefaultSoundbank());
		synth.loadAllInstruments(new MyOscillator());
		Sequence seq = MidiSystem.getSequence(FMTest1.class.getResource("/FMTest1.mid"));
		Sequencer seqr = MidiSystem.getSequencer(false);
		seqr.open();
		seqr.getTransmitter().setReceiver(synth.getReceiver());
		seqr.setSequence(seq);
		seqr.start();
		
		System.out.println();
		System.out.println("Is active, press enter to stop");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		br.readLine();
		System.out.println("Stop...");
		
		seqr.stop();
		seqr.close();
		synth.close();
		
		System.exit(0);
	}

}
