package time.travelers.audio;

import time.travelers.util.MathUtil;

public class AudioBus {
	public static final double maxGain = 6, minGain = -80;
	
	private static AudioBus master = new AudioBus(0, true);
	private double gain;
	private boolean isMuted = false;
	private AudioBus output;
	
	/**
	 * Creates a new AudioBus outputting to the master-bus.
	 */
	public AudioBus() {
		this(0);
	}
	/**
	 * Creates a new AudioBus outputting to the master-bus, with the specified gain.
	 * @param gain - The initial gain of this AudioBus.
	 */
	public AudioBus(double gain) {
		this(null, gain);
	}
	/**
	 * Creates a new AudioBus with the specified output and gain.
	 * @param output - The output AudioBus. If this is null, the master-bus is chosen.
	 * @param gain - The initial gain of this AudioBus.
	 */
	public AudioBus(AudioBus output, double gain) {
		if(this.output == null)
			output = AudioBus.getMasterBus();
		
		this.output = output;
		this.setGain(gain);
	}
	
	/**
	 * Used ONLY to create the master-bus.
	 */
	private AudioBus(double gain, boolean foo) {
		this.output = null;
		this.gain = gain;
	}
	
	/**
	 * Gets the total gain from this bus-chain, starting from this bus, and stepping through to the master bus. 
	 * Please note that this value will not be clamped, and for safe useage this value must be clamped to fit the 
	 * Audio Implementations min-max values.
	 * @return
	 * The total gain from the master-bus to this bus.
	 */
	public double getTotalGain() {
		if(this.isMaster())
			return this.gain;
		else
			return output.getTotalGain() + this.gain;
	}
	
	/**
	 * Checks if this bus is in a muted chain or not. 
	 * The general contract of a muted chain is that any sound connected to such a chain is to be silent. 
	 * The sound may still play inaudible, though.
	 * @return
	 * True if it is, else false.
	 */
	public boolean isInMutedChain() {
		if(isMaster())
			return this.isMuted;
		else
			return output.isInMutedChain() || this.isMuted;
	}
	
	/**
	 * Checks if this AudioBus is the master-bus.
	 * @return
	 * True if it is, else false.
	 */
	public boolean isMaster() {
		return this.output == null;
	}
	
	/**
	 * Sets the output of this AudioBus to the specified bus. If null is passed, defaults to master-bus.
	 * @param newOutput - The bus to output to.
	 */
	public void setOutput(AudioBus newOutput) {
		if(this.isMaster())
			return;
		if(newOutput == null)
			newOutput = AudioBus.getMasterBus();
		this.output = newOutput;
	}
	
	public AudioBus getOutput() {
		return this.output;
	}
	
	/**
	 * Sets the gain of this AudioBus to the specified value.
	 * <br>
	 * If the value is outside the max-min bounds, it will be clamped to fit.
	 * @param newGain - The new gain of this AudioBus.
	 */
	public void setGain(double newGain) {
		this.gain = MathUtil.getValueFittingBounds(newGain, maxGain, minGain);
	}
	
	public double getGain() {
		return this.gain;
	}
	
	public void setMute(boolean b) {
		this.isMuted = b;
	}
	
	public boolean isMuted() {
		return this.isMuted;
	}
	
	
	
	//
	// Static methods
	//
	
	public static AudioBus getMasterBus() {
		return master;
	}
}
