/**
 * FRC Team 3555
 */

package frc.robot;

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

public class MPReader {
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
			profile = new double[length][3];
			
			for (int i = 0; i < length; i++) {
				profile[i][0] = input.readDouble();
				profile[i][1] = input.readDouble();
				profile[i][2] = input.readDouble();
			}
			
			input.close();
		} catch (IOException e) {
			error = true;
		}
		
		return error;
	}
	
	public MPReader(String file) {
		this.file = file;
		this.error = loadMP();
	}
}
