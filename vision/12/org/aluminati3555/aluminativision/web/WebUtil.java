/**
 * Copyright (c) 2019 Team 3555
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

package org.aluminati3555.aluminativision.web;

/**
 * This class contains utilities for the web server
 * 
 * @author Caleb Heydon
 */
public class WebUtil {
	/**
	 * Returns / if .. { or } are found
	 * 
	 * @param input
	 * @return
	 */
	public static String sanitizeBlock(String input) {
		String output = input;

		if (output.contains("..") || output.contains("{") || output.contains("}")) {
			return "/";
		}

		return output;
	}

	/**
	 * Only allows A-Z, a-z, and -
	 * 
	 * @param input
	 * @return
	 */
	public static String sanitizeMax(String input) {
		String output = "";

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);

			if ((c > 'A' && c < 'Z') || (c > 'a' && c < 'z') || c == '-') {
				output += c;
			}
		}

		return output;
	}
}
