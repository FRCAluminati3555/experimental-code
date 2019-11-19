/**
 * FRC Team 3555
 */

package org.aluminati3555.mp.file.generator;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;

/**
 * This program takes a csv file with a motion profile and converts it into an
 * mp file
 * 
 * @author Caleb Heydon
 */

public class MPFileGenerator {
	private static Scanner scanner;

	private static String csv;
	private static String mp;
	private static double wheelDiameter;

	private static double[][] readCSV() throws FileNotFoundException {
		// Get number of lines
		scanner = new Scanner(new File(csv));

		int i = 0;
		while (scanner.hasNextLine()) {
			if (!scanner.nextLine().equals("")) {
				i++;
			}
		}

		scanner.close();
		scanner = new Scanner(new File(csv));

		String[] lines = new String[i];
		for (int j = 0; j < i; j++) {
			lines[j] = scanner.nextLine().replaceAll(",", "");
		}

		double[][] profile = new double[i][4];
		for (int j = 0; j < profile.length; j++) {
			String[] numbers = null;

			if (lines[j].contains("\t")) {
				numbers = lines[j].split("\t");
			} else if (lines[j].contains(" ")) {
				numbers = lines[j].split(" ");
			} else {
				System.err.println("Invalid file");
				System.exit(-1);
			}

			profile[j][0] = Double.parseDouble(numbers[0]);
			profile[j][1] = Double.parseDouble(numbers[1]);
			profile[j][2] = Double.parseDouble(numbers[2]);
			profile[j][3] = Double.parseDouble(numbers[3]);
		}
		scanner.close();

		return profile;
	}

	private static void writeMP(double[][] profile) throws IOException {
		DataOutputStream output = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(new File(mp))));

		// Write length
		output.writeInt(profile.length);

		// Write profile
		for (int i = 0; i < profile.length; i++) {
			output.writeDouble(profile[i][0]);
			output.writeDouble(profile[i][1]);
			output.writeDouble(profile[i][2]);
			output.writeDouble(profile[i][3]);
		}

		output.close();
	}

	public static void main(String[] args) throws IOException {
		// Check arguments
		if (args.length < 2) {
			System.err.println("Usage: java -jar mp_file_generator.jar <csv>.csv <mp>.mp <optional_wheel_diameter>");
			System.exit(-1);
		}

		csv = args[0];
		mp = args[1];

		if (!new File(csv).exists()) {
			System.err.println("CSV file does not exist");
			System.exit(-1);
		}

		if (args.length >= 3) {
			if (args[2].equals("1/3")) {
				wheelDiameter = 1 / 3.0;
			} else {
				try {
					wheelDiameter = Double.parseDouble(args[2]);
				} catch (NumberFormatException e) {
					System.err.println("Invalid diameter: " + args[2]);
					System.exit(-1);
				}
			}
		}

		// Setup console
		scanner = new Scanner(System.in);

		if (new File(mp).exists()) {
			System.out.println("MP file already exists.  Press enter to continue or Ctrl+C to exit....");
			scanner.nextLine();
		}

		scanner.close();

		// Read CSV
		double[][] profile = readCSV();

		// Convert
		double circ = Math.PI * wheelDiameter;
		for (int i = 0; i < profile.length && wheelDiameter > 0; i++) {
			profile[i][0] = profile[i][0] / circ;
			profile[i][1] = profile[i][1] / circ;
		}

		double angle = 0;
		double lastAbsolute = 0;
		for (int i = 0; i < profile.length; i++) {
			double absoluteRadians = profile[i][3];
			double absoluteDegrees = absoluteRadians * 180 / Math.PI;
			
			boolean negative = false;
			double delta = 0;
			if ((lastAbsolute >= 350) && (absoluteDegrees <= 10)) {
				delta = (360 - lastAbsolute) + absoluteDegrees;
			} else if ((lastAbsolute <= 10) && (absoluteDegrees >= 350)) {
				negative = true;
				delta = lastAbsolute + (360 - absoluteDegrees);
			} else {
				delta = absoluteDegrees - lastAbsolute;
				
				if (delta < 0) {
					delta = -delta;
				} else {
					negative = true;
				}
			}
			
			if (negative) {
				angle -= delta;
			} else {
				angle += delta;
			}
			
			profile[i][3] = angle;
			
			lastAbsolute = absoluteDegrees;
		}

		// Write mp
		writeMP(profile);
	}
}
