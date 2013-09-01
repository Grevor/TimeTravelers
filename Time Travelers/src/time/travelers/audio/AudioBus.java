package time.travelers.audio;

import java.util.ArrayList;

import time.travelers.util.MathUtil;

public class AudioBus {
	public static final double maxGain = 6, minGain = -80, mutedGain = -10000;
	
	/**
	 * The static master-bus existing in every project. It is from this bus that all sound originate.
	 */
	private static AudioBus master = new AudioBus(0, true);
	private double gain;
	private boolean isMuted = false;
	private AudioBus output;
	private ArrayList<AudioBus> inputs = new ArrayList<AudioBus>(20);
	
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
		if(output == null)
			output = AudioBus.getMasterBus();
		
		this.output = output;
		output.addAsInputBus(this);
		this.setGain(gain);
	}
	
	/**
	 * Used ONLY to create the master-bus.<br>
	 * DO NOT USE THIS FOR ANYTHING ELSE.
	 */
	private AudioBus(double gain, boolean foo) {
		this.output = null;
		this.gain = gain;
	}
	
	/**
	 * Gets the total gain from this bus-chain, starting from this bus, and stepping through to the master bus. 
	 * Please note that this value will not be clamped, and for safe usage this value must be clamped to fit the 
	 * Audio Implementations min-max values.
	 * @return
	 * The total gain from the master-bus to this bus.
	 * If this, or any higher-level bus is muted, returns {@code mutedGain}.
	 */
	public double getTotalGain() {
		if(this.isInMutedChain())
			return AudioBus.mutedGain;
		
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
		
		if(this.output != newOutput) {
			this.output.removeAsInputBus(this);
			this.output = newOutput;
			newOutput.addAsInputBus(this);
		}
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
		if(this.gain != newGain) {
			this.gain = MathUtil.getValueFittingBounds(newGain, maxGain, minGain);
			propagateChange();
		}
	}
	
	public double getGain() {
		return this.gain;
	}
	
	public void setMute(boolean b) {
		if(b != this.isMuted) {
			this.isMuted = b;
			this.propagateChange();
		}
	}
	
	public boolean isMuted() {
		return this.isMuted;
	}
	
	//
	// Private Methods
	//
	
	private void addAsInputBus(AudioBus bus) {
		if(!this.inputs.contains(bus))
			this.inputs.add(bus);
	}
	
	private void removeAsInputBus(AudioBus bus) {
		this.inputs.remove(bus);
	}
	
	/**
	 * Propagates a change to this bus down to child buses, and ultimately to playing sounds.
	 */
	protected void propagateChange() {
		for(AudioBus a : this.inputs)
			a.propagateChange();
	}
	
	
	//
	// Static methods
	//
	
	public static AudioBus getMasterBus() {
		return master;
	}
}
