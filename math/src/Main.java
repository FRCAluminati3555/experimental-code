import math.MathFunction;
import math.MathInput;
import math.MathPower;
import math.MathSec;
import math.MathUtil;

public class Main {
	public static void main(String[] args) {
		MathInput x = new MathInput();
		MathFunction function = new MathFunction(new MathPower(new MathSec(x), new MathInput(3)), x);
		
		double max = Math.PI / 4;
		double min = 0;
		int n = 4;
		
		System.out.println(MathUtil.computeTrapezoidArea(function, min, max, n));

//		MathInput x1 = new MathInput();
//		MathInput x2 = new MathInput();
//
//		MathFunction function = new MathFunction(
//				new MathSubtract(new MathPower(x1, new MathInput(4)), new MathInput(3)), x1);
//		MathFunction derivative = new MathFunction(
//				new MathMultiply(new MathInput(4), new MathPower(x1, new MathInput(3))), x2);
//
//		double seed = 1.5;
//		int iterations = 10;
//
//		System.out.println(MathUtil.computeZero(function, derivative, seed, iterations));
	}
}
