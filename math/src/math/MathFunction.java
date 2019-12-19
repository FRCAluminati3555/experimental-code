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
 * This class works like a math function
 * 
 * @author Caleb Heydon
 */
public class MathFunction {
	private MathNumber function;
	private MathInput[] inputs;

	/**
	 * Configures the function
	 * 
	 * @param function
	 * @param inputs
	 */
	public void set(MathNumber function, MathInput... inputs) {
		this.function = function;
		this.inputs = inputs;
	}

	/**
	 * Computes the function using the preset inputs
	 * 
	 * @return
	 */
	public double compute() {
		return function.getValue();
	}

	/**
	 * Computes the function using specified values
	 * 
	 * @param inputs
	 * @return
	 */
	public double compute(double... inputs) {
		if (this.inputs.length != inputs.length) {
			throw new MathException("Invalid number of inputs");
		}

		for (int i = 0; i < this.inputs.length; i++) {
			this.inputs[i].setValue(inputs[i]);
		}

		return function.getValue();
	}

	public MathFunction(MathNumber function, MathInput... inputs) {
		set(function, inputs);
	}
}
