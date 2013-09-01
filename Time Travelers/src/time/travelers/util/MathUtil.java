package time.travelers.util;

public class MathUtil {
	
	/**
	 * Gets the value closest to value, while being clamped (inclusively) between the high-bound and the low-bound.
	 * @param value - The value to correct.
	 * @param highBound - The high bound.
	 * @param lowBound - The low bound.
	 * @return
	 * The value clamped between the two bounds.
	 */
	public static int getValueFittingBounds(int value, int highBound, int lowBound)
	{
		if(highBound < lowBound)
			throw new IllegalArgumentException("High bound must be greater than or equal to the lower bound.");
		
		if (value >= highBound)
			return highBound;
		else if (value <= lowBound)
			return lowBound;
		else
			return value;
	}
	
	/**
	 * Gets the value closest to value, while being clamped (inclusively) between the high-bound and the low-bound.
	 * @param value - The value to correct.
	 * @param highBound - The high bound.
	 * @param lowBound - The low bound.
	 * @return
	 * The value clamped between the two bounds.
	 */
	public static double getValueFittingBounds(double value, double highBound, double lowBound) {
		if(highBound < lowBound)
			throw new IllegalArgumentException("High bound must be greater than or equal to the lower bound.");
		
		if (value >= highBound)
			return highBound;
		else if (value <= lowBound)
			return lowBound;
		else
			return value;
	}
}
