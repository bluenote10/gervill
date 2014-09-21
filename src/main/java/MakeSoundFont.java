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

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.*;

import com.sun.media.sound.*;

public class MakeSoundFont {

	public static void main(String[] args)
			throws UnsupportedAudioFileException, IOException {
		/*
		 * Create new SoundFont2 soundbank
		 */
		SF2Soundbank sf2 = new SF2Soundbank();

		/*
		 * Select audio file.
		 */
		File audiofile = new File("ding.wav");
		AudioInputStream audiosream = AudioSystem
				.getAudioInputStream(audiofile);

		/*
		 * Make sure the audio stream is in 
		 * correct format for soundfonts 
		 * e.g.16 bit signed, little endian
		 */
		AudioFormat format = new AudioFormat(audiosream.getFormat()
				.getSampleRate(), 16, 1, true, false);
		AudioInputStream convaudiosream = AudioSystem.getAudioInputStream(
				format, audiosream);

		/*
		 * Read the content of the file into a byte array.
		 */
		int datalength = (int) convaudiosream.getFrameLength()
				* format.getFrameSize();
		byte[] data = new byte[datalength];
		convaudiosream.read(data, 0, data.length);
		audiosream.close();

		/*
		 * Create SoundFont2 sample.
		 */
		SF2Sample sample = new SF2Sample(sf2);
		sample.setName("Ding Sample");
		sample.setData(data);
		sample.setSampleRate((long) format.getSampleRate());
		sample.setOriginalPitch(75);
		sf2.addResource(sample);

		/*
		 * Create SoundFont2 layer.
		 */
		SF2Layer layer = new SF2Layer(sf2);
		layer.setName("Ding Layer");
		sf2.addResource(layer);

		/*
		 * Create region for layer.
		 */
		SF2LayerRegion region = new SF2LayerRegion();
		region.putInteger(SF2Region.GENERATOR_RELEASEVOLENV, 12000);
		region.setSample(sample);
		layer.getRegions().add(region);

		/*
		 * Create SoundFont2 instrument.
		 */
		SF2Instrument ins = new SF2Instrument(sf2);
		ins.setName("Ding Instrument");
		sf2.addInstrument(ins);

		/*
		 * Create region for instrument.
		 */
		SF2InstrumentRegion insregion = new SF2InstrumentRegion();
		insregion.setLayer(layer);
		ins.getRegions().add(insregion);

		/*
		 * Save soundbank to disk.
		 */
		sf2.save("ding.sf2");
	}

}
