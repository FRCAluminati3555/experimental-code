/**
 * FRC Team 3555
 */

package org.aluminati3555.mp.file.reader;

import java.io.File;

/**
 * This program dumps the contents of a mp file
 * 
 * @author Caleb Heydon
 *
 */

public class MPFileReader {
	private static AluminatiMPReader mpReader;

	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java -jar mp_file_Reader.jar <mp_file>.mp");
			System.exit(-1);
		}

		if (!new File(args[0]).exists()) {
			System.err.println(args[0] + " does not exist");
			System.exit(-1);
		}

		System.out.println("Position\tVelocity\tDuration\tHeading");
		mpReader = new AluminatiMPReader(args[0]);
		if (mpReader.getError()) {
			System.err.println("Unable to read file");
			System.exit(-1);
		}

		double[][] profile = mpReader.getProfile();

		for (int i = 0; i < profile.length; i++) {
			System.out.println(
					profile[i][0] + ",\t" + profile[i][1] + ",\t" + profile[i][2] + ",\t" + profile[i][3] + ",\t");
		}
	}
}
