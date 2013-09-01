package time.travelers.audio;

/**
 * Provides an abstract interface to dealing with specific sounds. 
 * Please note that holding a reference to a interface whose {@code isRunning()} returns false may prohibit garbage collection.
 * @author Grevor
 *
 */
public interface SoundControl {
	public void stopSound();
	public void pauseSound();
	public void resumeSound();
	public void setVolume(double newVolume);
	public boolean isRunning();
}
