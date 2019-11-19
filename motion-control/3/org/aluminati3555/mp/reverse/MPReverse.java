/**
 * FRC Team 3555
 */

package org.aluminati3555.mp.reverse;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * This program adds negative signs to the position and velocity elements of a
 * motion profile (.csv)
 * 
 * @author Caleb Heydon
 */

public class MPReverse {
	/**
	 * Reads the file
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	private static ArrayList<String> readFile(String file) throws FileNotFoundException {
		Scanner scanner = new Scanner(new File(file));
		ArrayList<String> lines = new ArrayList<String>();

		while (scanner.hasNextLine()) {
			lines.add(scanner.nextLine());
		}

		scanner.close();
		return lines;
	}

	/**
	 * Writes the file
	 * 
	 * @param lines
	 * @param file
	 * @throws FileNotFoundException
	 */
	private static void writeFile(ArrayList<String> lines, String file) throws FileNotFoundException {
		PrintWriter output = new PrintWriter(file);

		for (int i = 0; i < lines.size(); i++) {
			output.println(lines.get(i));
		}

		output.close();
	}

	/**
	 * Processes a file
	 * 
	 * @param lines
	 */
	private static void process(ArrayList<String> lines) {
		for (int i = 0; i < lines.size(); i++) {
			if (lines.get(i).contains("-")) {
				lines.set(i, lines.get(i).replaceAll("-", ""));
				continue;
			}

			String[] params = lines.get(i).split(",");
			for (int j = 0; j < params.length; j++) {
				params[j] = params[j].trim();
			}

			params[0] = "-" + params[0];
			params[1] = "-" + params[1];

			lines.set(i, params[0] + ", " + params[1] + ", " + params[2] + ", ");
		}
	}

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 1) {
			System.err.println("Usage: java -jar mp_reverse.jar <filename>.csv");
			System.exit(-1);
		}

		String file = args[0];
		ArrayList<String> lines = readFile(file);
		process(lines);
		writeFile(lines, file);
	}
}
