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

/**
 * Class to manage function inputs
 * 
 * @author Caleb Heydon
 */
public class MathInput implements MathNumber {
	/**
	 * Returns PI
	 * 
	 * @return
	 */
	public static MathInput pi() {
		return new MathInput(Math.PI);
	}

	/**
	 * Returns e
	 * 
	 * @return
	 */
	public static MathInput e() {
		return new MathInput(Math.E);
	}

	/**
	 * Returns c in m/s
	 * 
	 * @return
	 */
	public static MathInput c() {
		return new MathInput(299792458);
	}

	private double value;

	@Override
	public String toString() {
		return "[" + value + "]";
	}

	/**
	 * Returns the value of the input
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value
	 * 
	 * @param value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	public MathInput(double value) {
		setValue(value);
	}

	public MathInput() {
		this(0);
	}
}
