package time.travelers.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import time.travelers.util.MathUtil;
import time.travelers.util.MemUtil;

public class AudioFile {
	private static ArrayList<AudioFile> allAudioFiles = new ArrayList<AudioFile>(300);
	private ArrayList<AsyncPlay> currentlyPlaying = new ArrayList<AsyncPlay>(5);
	
	private byte[] audioData;
	private AudioFormat format;
	private final String filename;
	private long loopStart, loopEnd;
	private double gain;
	AudioBus output;
	
	public AudioFile(String filename) {
		this(filename, 0);
	}
	
	public AudioFile(String filename, double gain) {
		this(filename, AudioBus.getMasterBus(), gain);
	}
	
	public AudioFile(String filename, AudioBus output) {
		this(filename, output, 0);
	}
	
	public AudioFile(String filename, AudioBus output, double gain) {
		this.filename = filename;
		this.output = output;
		this.setGain(gain);
		this.loadAudio(true);
	}
	
	//
	// Getters and setters
	//
	
	public void setOutput(AudioBus newOutput) {
		if(newOutput == null)
			newOutput = AudioBus.getMasterBus();
		this.output = newOutput;
	}
	
	public AudioBus getOutput() {
		return this.output;
	}
	
	public void setGain(double newGain) {
		this.gain = MathUtil.getValueFittingBounds(newGain, AudioBus.maxGain, AudioBus.minGain);
	}
	
	public double getGain() {
		return this.gain;
	}
	
	//
	// Public Functions.
	//
	
	public boolean isLoaded() {
		return audioData != null;
	}
	
	/**
	 * 
	 * @param loops
	 */
	public void playLooping(int loops) {
		if(!this.isLoaded())
			this.loadAudio();
		AsyncPlay p = new AsyncPlay(this.audioData, this.loopStart, this.loopEnd);
		p.start();
		this.currentlyPlaying.add(p);
	}
	
	/**
	 * Requests that audio data should be fetched for this AudioFile.
	 * @return
	 * True if this AudioFile is now buffered and ready, else false.
	 */
	public boolean prefetchAudio() {
		if(!this.isLoaded())
			return this.loadAudio(true);
		else return true;
	}
	
	//
	// Private functions.
	//

	private boolean hasPlayingInstances() {
		return this.currentlyPlaying.size() != 0;
	}
	
	/**
	 * Buffers audio from file.
	 * @return
	 */
	private boolean loadAudio() {
		return loadAudio(false);
	}
	
	/**
	 * Buffers audio from file.
	 * @return
	 */
	private boolean loadAudio(boolean checkMemoryFirst) {
		try (AudioInputStream s = AudioSystem.getAudioInputStream(new File(filename))){
			this.format = s.getFormat();
			
			//Checks the memory if needed. If the flag is true and the memory is not sufficient, 
			//this sound will refrain from loading until it needs to.
			if(checkMemoryFirst && MemUtil.getFreeMemory() < s.getFrameLength() * format.getFrameSize())
				return false;
			
			this.audioData = new byte[(int) (s.getFrameLength() * format.getFrameSize())];
			//Load all data.
			int curr = 0;
			while(curr < audioData.length) {
				curr += s.read(this.audioData, curr, audioData.length - curr);
			}
			
			s.close();
			return true;
		} catch (UnsupportedAudioFileException | IOException e) {
			System.out.println("Could not read AudioData.");
			return false;
		}
	}
	
	private void releaseMemory() {
		if(!this.hasPlayingInstances())
			this.audioData = null;
	}
	
	//
	// Private classes
	//
	
	@SuppressWarnings("unused")
	private class AsyncPlay extends Thread {
		final byte[] audioData;
		private int loopsRemaining;
		private long loopStart, loopEnd;
		private SourceDataLine playingLine;
		private int bufferSize;
		private boolean isRunning = true;
		private int currentByte = 0;
		
		public AsyncPlay(byte[] audioData, long loopStart, long loopEnd) {
			this.audioData = audioData;
			this.loopStart = loopStart;
			this.loopEnd = loopEnd;
		}
		@Override
		public void run() {
			try {
				playingLine = AudioSystem.getSourceDataLine(format);
				bufferSize = playingLine.getBufferSize();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				return;
			}
			//Sets the gain.
			this.setVolume(output.getTotalGain() + gain);
			
			while(isRunning) {
				int written = playingLine.write(audioData, currentByte, this.getBytesToRead());
				if(written == -1 || audioData.length - currentByte == 0 || currentByte == loopEnd) {
					if(this.loopsRemaining > 0) {
						this.setupForLoop();
						continue;
					}
				}
				currentByte += written;
				try {this.sleep(getSleepTime());}
				catch (InterruptedException e) { }
			} //!isRunning
			
			this.cleanUp();
		}
		
		private int getBytesToRead() {
			if(loopEnd - currentByte <= bufferSize)
				return (int) (loopEnd - currentByte);
			else
				return bufferSize;
		}
		
		private void setupForLoop() {
			this.currentByte = (int) this.loopStart;
			this.loopsRemaining--;
		}
		
		private int getSleepTime() {
			return (int) (1000 * playingLine.available() / (playingLine.getFormat().getFrameRate() * format.getFrameSize()));
		}
		
		private boolean loopEnabled() {
			return loopEnd != loopStart;
		}
		
		public synchronized void setVolume(double newVolume) {
			((FloatControl)playingLine.getControl(FloatControl.Type.MASTER_GAIN)).setValue((float)MathUtil.getValueFittingBounds(newVolume, AudioBus.maxGain, AudioBus.minGain));
		}
		
		private synchronized void cleanUp() {
			if(this.hasOpenPlayingLine()) {
				playingLine.stop();
				playingLine.close();
			}
			currentlyPlaying.remove(this);
		}
		
		private boolean hasOpenPlayingLine() {
			return this.playingLine != null && this.playingLine.isOpen();
		}
	}
	
	//
	// Static functions
	//
	/**
	 * Attempts to free memory used by any non-playing audio. 
	 * This may cause slight lag if sounds are needed later, as the computer have to load resources. 
	 * Please note that this function will not guarantee instant reclamation of memory.
	 * @see {@link isLoaded()}
	 */
	public static void garbageCollectAudio() {
		for(int i = 0; i < allAudioFiles.size(); i++)
			allAudioFiles.get(i).releaseMemory();
			
	}
	
	/**
	 * Adds a AudioFile to the list of all audio files. This should only be done in the constructor.
	 * @param a
	 */
	private static void addToList(AudioFile a) {
		if(!allAudioFiles.contains(a))
			allAudioFiles.add(a);
	}
}

