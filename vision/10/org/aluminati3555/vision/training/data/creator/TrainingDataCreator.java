/**
 * FRC Team 3555
 * Deep Space
 */

package org.aluminati3555.vision.training.data.creator;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

/**
 * This class is for creating training data for vision tracking in the Deep
 * Space FRC game. The training data consists of two different sets: left
 * targets and right targets. The targets on the left are angled to the left at
 * about a 14.5 degree angle, and the ones are the right are mirrors of the
 * left. This class will generate the training data as two folders of 16x16
 * pixel images. These images will be used to train a neural network which will
 * be used to differentiate between left and right-side reflective tape so that
 * the vision targets can be located.
 * 
 * @author Caleb Heydon
 */

public class TrainingDataCreator {
	// The dimensions of the training images.
	public static final double WIDTH = 16;
	public static final double HEIGHT = 16;

	// The dimensions for the original image
	public static final double ORIGINAL_WIDTH = 64;
	public static final double ORIGINAL_HEIGHT = 64;

	// The nominal angle of the reflective tape in degrees.
	public static final double NOMINAL_ANGLE = 14.5;

	// The variation of the angle in the training data in degrees.
	public static final double MAX_ANGLE_VARIATION = 5;

	// A unitless nominal length of the reflective tape.
	public static final double NOMINAL_LENGTH = 52;

	// The maximum length variation in either direction of the reflective tape.
	public static final double MAX_LENGTH_VARIATION = 15;

	// A unitless nominal width of the reflective tape.
	public static final double NOMINAL_WIDTH = 20;

	// The maximum width variation in either direction of the reflective tape.
	public static final double MAX_WIDTH_VARIATION = 5;

	/**
	 * This method returns a random angle for the reflective tape in the training
	 * data. It will return a value of NOMINAL_ANGLE +/- MAX_ANGLE_VARIATION.
	 * 
	 * @return The angle for the reflective tape.
	 */
	public double getRandomTrainingAngle() {
		// Get a random number between -1 and 1.
		double random = 2 * Math.random() - 0.5;

		// Find the difference in the angle
		double angleDifference = MAX_ANGLE_VARIATION * random;

		// Return the nominal angle plus the angle difference
		return NOMINAL_ANGLE + angleDifference;
	}

	/**
	 * This method returns a random length for the reflective tape in the training
	 * data. It will return a value of NOMINAL_LENGTH +/- MAX_LENGTH_VARIATION.
	 * 
	 * @return The length of the reflective tape.
	 */
	public double getRandomTrainingLength() {
		// Get a random number between -1 and 1.
		double random = 2 * Math.random() - 0.5;

		// Find the difference in the length
		double lengthDifference = MAX_LENGTH_VARIATION * random;

		// Return the nominal length plus the difference
		return NOMINAL_LENGTH + lengthDifference;
	}

	/**
	 * This method returns a random width for the reflective tape in the training
	 * data. It will return a value of NOMINAL_WIDTH +/- MAX_WIDTH_VARIATION.
	 * 
	 * @return The width of the reflective tape.
	 */
	public double getRandomTrainingWidth() {
		// Get a random number between -1 and 1.
		double random = 2 * Math.random() - 0.5;

		// Find the difference in the width
		double widthDifference = MAX_WIDTH_VARIATION * random;

		// Return the nominal width plus the difference
		return NOMINAL_WIDTH + widthDifference;
	}

	/**
	 * This method returns a new blank buffered image of the specified size.
	 * 
	 * @param width  The width of the image.
	 * @param height The height of the image.
	 * @return The image.
	 */
	public BufferedImage getNewBlankImage(int width, int height) {
		// Create image
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Set all pixels to black
		int[] black = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		for (int y = 0; y < image.getRaster().getHeight(); y++) {
			for (int x = 0; x < image.getRaster().getWidth(); x++) {
				image.getRaster().setPixel(x, y, black);
			}
		}

		return image;
	}

	/**
	 * This method will randomly generate the dimensions of a new strip of
	 * reflective tape. The angle is not generated.
	 * 
	 * @param angle The angle of the reflective tape.
	 * @return The strip of reflective tape.
	 */
	public ReflectiveTapeStrip getNewReflectiveTapeStrip(double angle) {
		// Adjust angle
		angle = 90 - angle;

		// Get random values
		double length = getRandomTrainingLength();
		double width = getRandomTrainingWidth();

		double centerX = ORIGINAL_WIDTH / 2;
		double centerY = ORIGINAL_HEIGHT / 2;

		// Generate the coefficients
		double sinCoefficient = Math.sin(Math.toRadians(angle));
		double cosCoefficient = Math.cos(Math.toRadians(angle));

		double reverseSinCoefficient = Math.sin(Math.toRadians(angle + 180));
		double reverseCosCoefficient = Math.cos(Math.toRadians(angle + 180));

		double leftSinCoefficient = Math.sin(Math.toRadians(angle + 90));
		double leftCosCoefficient = Math.cos(Math.toRadians(angle + 90));

		double rightSinCoefficient = Math.sin(Math.toRadians(angle - 90));
		double rightCosCoefficient = Math.cos(Math.toRadians(angle - 90));

		// Compute points
		ArrayList<Point2D> points = new ArrayList<Point2D>();

		// Upper left
		double x = centerX + leftCosCoefficient * width / 2 + cosCoefficient * length / 2;
		double y = centerY - leftSinCoefficient * width / 2 - sinCoefficient * length / 2;
		points.add(new Point2D(x, y));

		// Upper right
		x = centerX + rightCosCoefficient * width / 2 + cosCoefficient * length / 2;
		y = centerY - rightSinCoefficient * width / 2 - sinCoefficient * length / 2;
		points.add(new Point2D(x, y));

		// Lower right
		x = centerX + rightCosCoefficient * width / 2 + reverseCosCoefficient * length / 2;
		y = centerY - rightSinCoefficient * width / 2 - reverseSinCoefficient * length / 2;
		points.add(new Point2D(x, y));

		// Lower left
		x = centerX + leftCosCoefficient * width / 2 + reverseCosCoefficient * length / 2;
		y = centerY - leftSinCoefficient * width / 2 - reverseSinCoefficient * length / 2;
		points.add(new Point2D(x, y));

		return new ReflectiveTapeStrip(points);
	}

	/**
	 * This method adds some static to the image by randomly making black dots.
	 * 
	 * @param image The image.
	 */
	public void addNoise(BufferedImage image) {
		// Generate static in the image by going through every pixel
		int[] black = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

		for (int y = 0; y < image.getRaster().getHeight(); y++) {
			for (int x = 0; x < image.getRaster().getWidth(); x++) {
				double random = Math.random();

				if (random >= 0.98) {
					image.getRaster().setPixel(x, y, black);
				}
			}
		}
	}

	/**
	 * This method crops the training image so that there is no excess space.
	 * 
	 * @param image The original image.
	 * @return The final image.
	 */
	public BufferedImage cropImage(BufferedImage image) {
		// Find bounds
		int minX;
		int minY = (int) ORIGINAL_HEIGHT;

		int maxX;
		int maxY = 0;

		// Find set pixels
		for (int y = 0; y < image.getRaster().getHeight(); y++) {
			for (int x = 0; x < image.getRaster().getWidth(); x++) {
				int[] buffer = new int[10];
				
				// Is the pixel set
				image.getRaster().getPixel(x, y, buffer);

				int green = buffer[1];

				if (green >= 255) {
					// The pixel is set

					if (y < minY) {
						minY = y;
					} else if (y > maxY) {
						maxY = y;
					}
				}
			}
		}

		// Correct size
		maxY += 1;

		minX = minY;
		maxX = maxY;

		// Crop image
		return image.getSubimage(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * This method will scale the training image to a standard size.
	 * 
	 * @param image The original image.
	 * @return The scaled image.
	 */
	public BufferedImage scaleImage(BufferedImage image) {
		Image scaledImage = image.getScaledInstance((int) WIDTH, (int) HEIGHT, BufferedImage.SCALE_FAST);
		BufferedImage bufferedImage = new BufferedImage((int) WIDTH, (int) HEIGHT, BufferedImage.TYPE_INT_RGB);

		// Draw scaled image on buffered image
		Graphics graphics = bufferedImage.getGraphics();
		graphics.drawImage(scaledImage, 0, 0, null);
		graphics.dispose();

		return bufferedImage;
	}

	/**
	 * This method randomly generates a new training image.
	 * 
	 * @param side The side the image is for.
	 * @return The training image.
	 */
	public BufferedImage getTrainingImage(Side side) {
		// Generate angle
		double angle = getRandomTrainingAngle();

		if (side == Side.RIGHT) {
			angle = -angle;
		}

		// Generate reflective tape strip
		ReflectiveTapeStrip reflectiveTapeStrip = getNewReflectiveTapeStrip(angle);

		// Create polygon
		Polygon shape = new Polygon();

		for (int i = 0; i < reflectiveTapeStrip.getPoints().size(); i++) {
			double x = reflectiveTapeStrip.getPoints().get(i).getX();
			double y = reflectiveTapeStrip.getPoints().get(i).getY();

			shape.addPoint((int) Math.round(x), (int) Math.round(y));
		}

		// Generate a new image
		BufferedImage image = getNewBlankImage((int) ORIGINAL_WIDTH, (int) ORIGINAL_HEIGHT);

		// Generate graphics
		Graphics graphics = image.getGraphics().create();

		// Draw shape
		graphics.setColor(Color.GREEN);
		graphics.fillPolygon(shape);
		graphics.dispose();

		// Add static
		addNoise(image);

		// Crop image
		image = cropImage(image);

		// Scale the image to a standard size
		image = scaleImage(image);

		return image;
	}

	/**
	 * This method generates a new set of training images
	 * 
	 * @param amount          The number of training images of each side to be
	 *                        generated.
	 * @param outputDirectory The directory that the images will be put in.
	 * @throws IOException
	 */
	public void generateTrainingData(int amount, String outputDirectory, boolean showOutput) throws IOException {
		// Correct outputDirectory
		if (!outputDirectory.endsWith("/") && !outputDirectory.endsWith("\\")) {
			outputDirectory += "/";
		}

		// Create directories if they do not already exist
		String leftDirectory = outputDirectory + "left/";
		String rightDirectory = outputDirectory + "right/";

		new File(leftDirectory).mkdirs();
		new File(rightDirectory).mkdirs();

		// Generate left images
		for (int i = 0; i < amount; i++) {
			String path = leftDirectory + "left_" + i + ".png";
			if (showOutput) {
				System.out.println("Generating " + path + "...");
			}

			BufferedImage image = getTrainingImage(Side.LEFT);

			ImageIO.write(image, "png", new File(path));
		}

		// Generate right images
		for (int i = 0; i < amount; i++) {
			String path = rightDirectory + "right_" + i + ".png";
			if (showOutput) {
				System.out.println("Generating " + path + "...");
			}

			BufferedImage image = getTrainingImage(Side.RIGHT);

			ImageIO.write(image, "png", new File(path));
		}
	}

	public static void main(String[] args) throws IOException {
		// Get arguments
		if (args.length < 2) {
			System.err.println("Usage: java -jar <jarname> <output_directory> <amount>");
			System.exit(1);
		}

		String outputDirectory = args[0];
		int amount = Integer.parseInt(args[1]);

		TrainingDataCreator trainingDataCreator = new TrainingDataCreator();
		trainingDataCreator.generateTrainingData(amount, outputDirectory, true);
	}
}