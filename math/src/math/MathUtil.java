/**
 * Copyright (c) 2019 Caleb Heydon
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package math;

import java.util.ArrayList;

/**
 * Utility methods for the math library
 * 
 * @author Caleb Heydon
 */
public class MathUtil {
	/**
	 * Finds the points for use in the trapezoid method
	 * 
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	public static double[] computeTrapezoidPoints(double min, double max, int n) {
		if (min > max) {
			throw new MathException("Invalid min/max");
		}

		if (n < 1) {
			throw new MathException("n less than 1");
		}

		ArrayList<Double> points = new ArrayList<Double>();

		points.add(min);
		double step = (max - min) / n;
		for (double i = min + step; i < max; i += step) {
			points.add(i);
		}

		points.add(max);

		double[] pointsArray = new double[points.size()];
		for (int i = 0; i < points.size(); i++) {
			pointsArray[i] = points.get(i);
		}

		return pointsArray;
	}

	/**
	 * Computes the trapezoid area of a function
	 * 
	 * @param function
	 * @param min
	 * @param max
	 * @param n
	 * @return
	 */
	public static double computeTrapezoidArea(MathFunction function, double min, double max, int n) {
		double[] points = MathUtil.computeTrapezoidPoints(min, max, n);

		double sum = 0;
		sum += function.compute(points[0]);
		sum += function.compute(points[points.length - 1]);

		for (int i = 1; i < points.length - 1; i++) {
			sum += 2 * function.compute(points[i]);
		}

		double area = ((max - min) / (2 * n)) * sum;
		return area;
	}

	/**
	 * Computes a zero using Newton's Method
	 * 
	 * @param function
	 * @param derivative
	 * @param seed
	 * @param iterations
	 * @return
	 */
	public static double computeZero(MathFunction function, MathFunction derivative, double seed, int iterations) {
		double x = seed;

		for (int i = 0; i < iterations; i++) {
			x -= function.compute(x) / derivative.compute(x);
		}

		return x;
	}
}
