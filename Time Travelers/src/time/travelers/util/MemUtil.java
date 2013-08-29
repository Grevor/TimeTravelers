package time.travelers.util;

public class MemUtil {
	private static Runtime runtime = Runtime.getRuntime();
	/**
	 * Gets the allocated memory of the JVM. This is the allocated memory, and not the actually used memory.
	 * @return
	 */
	public static synchronized long getAllocatedMemory() {
	    return runtime.totalMemory();
	}
	
	/**
	 * Gets the used memory of the JVM.
	 * @return
	 */
	public static synchronized long getUsedMemory() {
		return runtime.totalMemory() - runtime.freeMemory();
	}
	
	/**
	 * Gets the max memory of the JVM. Further allocations will throw exceptions.
	 * @return
	 */
	public static synchronized long getMaxMemory() {
		return runtime.maxMemory();
	}
	
	/**
	 * Gets the free memory remaining of the JVM.
	 * @return
	 */
	public static synchronized long getFreeMemory() {
		return runtime.freeMemory() + (getMaxMemory() - getAllocatedMemory());
	}

}
