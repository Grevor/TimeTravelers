package time.travelers.util;

public class MathUtil {
	
	public static int getValueFittingBounds(int value, int highBound, int lowBound)
	{
		if (value >= highBound)
			return highBound;
		else if (value <= lowBound)
			return lowBound;
		else
			return value;
	}
	
	public static double getValueFittingBounds(double value, double highBound, double lowBound) {
		if (value >= highBound)
			return highBound;
		else if (value <= lowBound)
			return lowBound;
		else
			return value;
	}
}
