package time.travelers.audio;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import time.travelers.util.MathUtil;
import time.travelers.util.MemUtil;

public class AudioFile extends AudioBus {
	private static ArrayList<AudioFile> allAudioFiles = new ArrayList<AudioFile>(300);
	private ArrayList<AsyncPlay> currentlyPlaying = new ArrayList<AsyncPlay>(5);
	
	private byte[] audioData;
	private AudioFormat format;
	private final String filename;
	private long loopStart, loopEnd;
	
	
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
		super(output,gain);
		this.filename = filename;
		this.setGain(gain);
		this.loadAudio(true);
		AudioFile.addToList(this);
	}
	
	//
	// Getters and setters
	//
	
	/**
	 * Sets the output of this AudioFile to the selected bus.<br>
	 * If null is passed, the master-bus is selected as output.
	 */
	public void setOutput(AudioBus newOutput) {
		super.setOutput(newOutput);
		this.setVolumeForAllPlayingSounds();
	}
	
	/**
	 * Sets the gain of this sound to the specified value.
	 */
	@Override
	public void setGain(double newGain) {
		super.setGain(newGain);
		this.setVolumeForAllPlayingSounds();
	}
	
	//
	// Public Functions.
	//
	
	public boolean isLoaded() {
		return audioData != null;
	}
	
	/**
	 * Plays this sound, causing it to loop the specified number of times. 
	 * Any non-positive number will cause the audio to play straight through to the end-point once.
	 * @param loops - The number of times to loop this audio.
	 * @return
	 * A Interface allowing control of this specific sound instance.<br>
	 * If this is not needed, please do NOT store this in a variable, as it may inhibit garbage collection.
	 * Also, make sure to check regularly if the SoundControl is still running. If it is not, remove any reference to it.
	 * @see SoundControl
	 */
	public SoundControl playLooping(int loops) {
		if(!this.isLoaded())
			this.loadAudio();
		AsyncPlay p = new AsyncPlay(this.audioData, this.loopStart, this.loopEnd, this);
		p.start();
		this.currentlyPlaying.add(p);
		return p;
	}
	
	/**
	 * Stops all currently playing instances of this sound, and then plays a new instance.
	 * @param loops - The number of times to loop the new sound.
	 * @return
	 * A SoundControl to enable controlling this specific instance of the sound. 
	 * @see playLooping
	 */
	public SoundControl stopAndPlay(int loops) {
		this.stopAllInstances();
		return this.playLooping(loops);
	}
	
	/**
	 * Plays the sound if and only if no other instances is playing.
	 * @param loops - The number of times to loop the new sound.
	 * @return
	 * A SoundControl to enable controlling this specific instance of the sound. 
	 * @see playLooping
	 */
	public SoundControl timidPlay(int loops) {
		if(this.hasPlayingInstances())
			return null;
		else
			return playLooping(loops);
	}
	
	/**
	 * Plays this sound once, from start to the loop end point.
	 * @return
	 * A SoundControl to enable controlling this specific instance of the sound. 
	 * @see playLooping
	 */
	public SoundControl play() {
		return this.playLooping(0);
	}
	
	/**
	 * Stops all currently playing instances of this sound.
	 */
	public void stopAllInstances() {
		for(int i = 0; i < this.currentlyPlaying.size(); ) {
			this.currentlyPlaying.get(i).stopSound();
			this.currentlyPlaying.remove(i);
		}
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
	// Protected Functions
	//
	
	protected void propagateChange() {
		super.propagateChange();
		setVolumeForAllPlayingSounds();
	}
	
	//
	// Private functions.
	//
	
	/**
	 * Sets the volume of all currently playing sounds to the current gain of this AudioFile.
	 */
	private void setVolumeForAllPlayingSounds() {
		if(this.currentlyPlaying != null)
			for(int i = 0; i < this.currentlyPlaying.size(); i++)
				this.currentlyPlaying.get(i).setVolume(this.getTotalGain());
	}

	private boolean hasPlayingInstances() {
		return this.currentlyPlaying.size() != 0;
	}
	
	/**
	 * Buffers audio from file. This may throw memory exceptions.
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
	
	/**
	 * Release the buffered sound data of this AudioFile if it has no currently playing instances.<br>
	 * This will make sure that sounds used often will not be unbuffered as often, should someone choose to use this.
	 */
	private void releaseMemory() {
		if(!this.hasPlayingInstances())
			this.audioData = null;
	}
	
	//
	// Private classes
	//
	
	private class AsyncPlay extends Thread implements SoundControl {
		final byte[] audioData;
		private int loopsRemaining;
		private long loopStart, loopEnd;
		private SourceDataLine playingLine;
		private int bufferSize;
		private boolean isRunning = true;
		private int currentByte = 0;
		/**
		 * The container class representing the audio playing.
		 */
		private final AudioFile parent;
		
		public AsyncPlay(byte[] audioData, long loopStart, long loopEnd, AudioFile parent) {
			this.audioData = audioData;
			this.loopStart = loopStart;
			this.loopEnd = loopEnd;
			this.parent = parent;
		}
		@Override
		public void run() {
			try {
				playingLine = AudioSystem.getSourceDataLine(format);
				bufferSize = playingLine.getBufferSize();
				playingLine.open();
				playingLine.start();
			} catch (LineUnavailableException e) {
				//If something goes wrong, clean up the mess and never start the playback.
				e.printStackTrace();
				this.cleanUp();
				return;
			}
			//Sets the gain.
			this.setVolume(parent.getTotalGain());
			
			while(isRunning) {
				int written = -1;
				if(this.hasOpenPlayingLine() && currentByte < audioData.length)
					written = playingLine.write(audioData, currentByte, this.getBytesToRead());
				
				//If we have reached the end of the file or the loop point, check for looping.
				if(written == -1 || audioData.length - (currentByte + written) == 0 || (this.loopEnabled() && currentByte == loopEnd)) {
					if(this.loopsRemaining > 0) {
						//If we detect a loop, set the variables up and force the next iteration instantly.
						this.setupForLoop();
						continue;
					} else {
						//Else, we are done playing, as no more loops can be detected.
						//The loop will thus exit on the next iteration. 
						this.isRunning = false;
					}
				}
				
				currentByte += written;
				try {Thread.sleep(getSleepTime());}
				catch (InterruptedException e) {/*This will never happen, but to ensure that no strange exception is thrown...*/}
			} //!isRunning
			
			this.cleanUp();
			return;
		}
		
		/**
		 * Gets the amount of bytes to write into the buffer. For performance, this should not block.
		 * @return
		 * The number of bytes to write into the playingLine.
		 */
		private int getBytesToRead() {
			if(this.loopEnabled() && loopEnd - currentByte <= bufferSize)
				return (int) (loopEnd - currentByte);
			else
				return Math.min(bufferSize, audioData.length - currentByte);
		}
		
		/**
		 * Sets the sound up for a new loop.
		 */
		private void setupForLoop() {
			this.currentByte = (int) this.loopStart;
			this.loopsRemaining--;
		}
		
		/**
		 * Gets the time, in milliseconds, to sleep. This will give a lag-leeway of about half the buffers size. 
		 * If significant lag occurs, may want to change this.
		 * @return
		 * The time, in milliseconds, to sleep the thread.
		 */
		private int getSleepTime() {
			//The "500" is actually 1000/2, to accommodate for milliseconds, and half the buffer.
			return (int) (500 * playingLine.available() / (playingLine.getFormat().getFrameRate() * format.getFrameSize()));
		}
		
		/**
		 * Checks if looping is enabled.
		 * @return
		 * True if it is, else false.
		 */
		private boolean loopEnabled() {
			return loopEnd != loopStart;
		}
		
		/**
		 * Sets the volume of this sound. This function will clamp the value given to fit the control.
		 * @param newVolume - The requested new volume.
		 */
		public synchronized void setVolume(double newVolume) {
			if(this.hasOpenPlayingLine()) {
				FloatControl vol = (FloatControl)playingLine.getControl(FloatControl.Type.MASTER_GAIN);
				double realVolume = MathUtil.getValueFittingBounds(newVolume, vol.getMaximum(), vol.getMinimum());
				vol.setValue((float) realVolume);
			}
		}
		
		/**
		 * Cleans up any used resource, and dereference the object from the currentlyPlaying-list.
		 * This must be called before returning, and must always be directly followed by a "return;" call.
		 */
		private synchronized void cleanUp() {
			if(this.hasOpenPlayingLine()) {
				playingLine.stop();
				playingLine.close();
			}
			parent.currentlyPlaying.remove(this);
		}
		
		/**
		 * Checks if the playingLine is open.
		 * @return
		 * true if it is, else false.
		 */
		private boolean hasOpenPlayingLine() {
			return this.playingLine != null && this.playingLine.isOpen();
		}
		
		/**
		 * Stops the sound and requests cleanup. Memory might not be free'd up right away.
		 */
		public void stopSound() {
			this.isRunning = false;
			this.stopPlayingLine();
		}
		
		/**
		 * Stops the line from playing, if a line is even available.
		 */
		private void stopPlayingLine() {
			if(this.hasOpenPlayingLine())
				playingLine.stop();
		}
		
		/**
		 * Pauses the sound. A paused sound still uses system resources. 
		 * If resources are needed, please call stopSound().
		 */
		public void pauseSound() {
			this.stopPlayingLine();
		}
		
		/**
		 * Resumes a paused sound.
		 */
		public void resumeSound() {
			if(this.playingLine != null && this.isRunning)
				playingLine.start();
		}
		@Override
		public boolean isRunning() {
			return this.isRunning;
		}
	}
	
	//
	// Static functions
	//
	/**
	 * Attempts to free memory used by any non-playing audio. 
	 * This may cause slight lag if sounds are needed later, as the computer have to load resources. 
	 * <br>
	 * If certain sounds are needed soon afterwards, call the {@code prefetchAudio()} function on them to alleviate some lag.
	 * <br><br>
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
	
	
	//For testing.
	public static void main(String args[]) throws InterruptedException {
		AudioFile f = new AudioFile("C:/Users/Grevor/Desktop/Revised Audio/Epic Theme n.wav");
		f.playLooping(0);
		Thread.sleep(6000);
		f.playLooping(0);
		AudioBus.getMasterBus().setGain(-70);
	}
}

