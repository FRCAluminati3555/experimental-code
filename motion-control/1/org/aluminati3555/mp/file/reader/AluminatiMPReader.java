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

package org.aluminati3555.mp.file.reader;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * This class reads an encoded motion profile from the filesystem
 * 
 * @author Caleb Heydon
 */

public class AluminatiMPReader {
	// Name of the file
	private String file;

	// Was there an error loading the file?
	private boolean error;

	// The motion profile
	private double[][] profile;

	/**
	 * Returns the path of the file
	 * 
	 * @return
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Returns true if an error was encountered
	 * 
	 * @return
	 */
	public boolean getError() {
		return error;
	}

	/**
	 * Returns the motion profile
	 * 
	 * @return
	 */
	public double[][] getProfile() {
		return profile;
	}

	/**
	 * Loads the motion profile from the disk
	 */
	private boolean loadMP() {
		boolean error = false;

		try {
			DataInputStream input = new DataInputStream(new GZIPInputStream(new FileInputStream(new File(file))));

			// Read length
			int length = input.readInt();
			profile = new double[length][4];

			for (int i = 0; i < length; i++) {
				profile[i][0] = input.readDouble();
				profile[i][1] = input.readDouble();
				profile[i][2] = input.readDouble();
				profile[i][3] = input.readDouble();
			}

			input.close();
		} catch (IOException e) {
			error = true;
		}

		return error;
	}

	public AluminatiMPReader(String file) {
		this.file = file;
		this.error = loadMP();
	}
}
