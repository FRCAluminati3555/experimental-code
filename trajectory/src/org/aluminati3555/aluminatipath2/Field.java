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

package org.aluminati3555.aluminatipath2;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/**
 * This class holds dimensions and ratios of the field
 * 
 * @author Caleb Heydon
 */
public class Field {
	public static final double FIELD_WIDTH_INCHES = 54 * 12;
	
	// The ratio between inches and pixels
	public static double ratio; // Should be 1.75
	
	static {
		try {
			int pixels = ImageIO.read(new File(FieldPane.FIELD_IMAGE)).getWidth();
			ratio = FIELD_WIDTH_INCHES / pixels;
		} catch (IOException e) {
			ratio = 1.75;
		}
	}
}
