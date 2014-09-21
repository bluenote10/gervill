import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.sound.midi.MidiSystem;
import javax.sound.midi.Receiver;
import javax.sound.midi.ShortMessage;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

import com.sun.media.sound.AudioSynthesizer;

public class AudioRender {

	public static void main(String[] args) throws Exception
	{
		/*
		* Open synthesizer in pull mode in the format 96000hz 24 bit stereo
		* using Sinc interpolation for highest quality.
		* With 1024 in max polyphony.
		*/
		AudioFormat format = new AudioFormat(96000, 24, 2, true, false);
		AudioSynthesizer synthesizer = (AudioSynthesizer)MidiSystem.getSynthesizer();
		Map<String,Object> info = new HashMap<String,Object>();
		info.put("resampletType", "sinc");
		info.put("maxPolyphony", "1024");
		AudioInputStream stream = synthesizer.openStream(format, info);
		 
		/*
		* Play midi note 60 on channel 1 for 1 sec.
		*/
		ShortMessage msg = new ShortMessage();
		Receiver recv = synthesizer.getReceiver();
		msg.setMessage(ShortMessage.PROGRAM_CHANGE, 0, 48, 0);
		recv.send(msg, 0);
		msg.setMessage(ShortMessage.NOTE_ON, 0, 60, 80);
		recv.send(msg, 0);
		msg.setMessage(ShortMessage.NOTE_ON, 0, 60, 0);
		recv.send(msg, 1000000);
		 
		/*
		* Calculate how many bytes 4 seconds are.
		*/
		long len = (long)(format.getFrameRate() * 4);
		 
		/*
		* Write 10 second into output file.
		*/
		stream = new AudioInputStream(stream, format, len);
		AudioSystem.write(stream, AudioFileFormat.Type.WAVE, new File("output.wav"));
		 
		/*
		* Close all resources.
		*/
		synthesizer.close();
	}
}
