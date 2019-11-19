/**
 * FRC Team 3555
 * Deep Space
 */

package org.aluminati3555.vision.api;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * This is the main class of the vision recognition api for the 2019 frc game.
 * 
 * @author Caleb Heydon
 */

public class VisionAPI {
	// The target dimensions of the images
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

	// The minimum green value to be kept
	public static final int MIN_GREEN = 64;

	// The working directory for the training images
	String resourcePath;

	// This is the neural network that the program is using
	BasicNetwork neuralNetwork;

	/**
	 * This method returns the neural network that is currently in use.
	 * 
	 * @return The neural network.
	 */
	public BasicNetwork getNeuralNetwork() {
		return neuralNetwork;
	}

	/**
	 * This method loads a previously saved neural network.
	 * 
	 * @param path The path of the neural network.
	 */
	public void loadNeuralNetwork(String path) {
		neuralNetwork = (BasicNetwork) EncogDirectoryPersistence.loadObject(new File(path));
	}

	/**
	 * This method saves the neural network in use as a file.
	 * 
	 * @param path The path of the file.
	 */
	public void saveNeuralNetwork(String path) {
		EncogDirectoryPersistence.saveObject(new File(path), neuralNetwork);
	}

	/**
	 * This method preps an image for input into a neural network.
	 * 
	 * @param image The image.
	 * @return The array of inputs from the image.
	 */
	public double[] getImageAsArray(BufferedImage image) {
		double[] buffer = new double[WIDTH * HEIGHT];

		for (int y = 0; y < image.getRaster().getHeight(); y++) {
			for (int x = 0; x < image.getRaster().getWidth(); x++) {
				// Load the pixel into the buffer
				int[] pixelBuffer = new int[10];
				image.getRaster().getPixel(x, y, pixelBuffer);

				if (pixelBuffer[1] > 0) {
					buffer[y * WIDTH + x] = 0.5;
				} else {
					buffer[y * WIDTH + x] = 1;
				}
			}
		}

		return buffer;
	}

	/**
	 * This method filters the image so that there is only green.
	 * 
	 * @param image The image.
	 */
	public void filterGreen(BufferedImage image) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int[] pixel = new int[10];
				image.getRaster().getPixel(x, y, pixel);

				// Is the pixel green
				if (!(pixel[1] > pixel[0] && pixel[1] > pixel[2])) {
					int[] black = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
					image.getRaster().setPixel(x, y, black);
					continue;
				}

				// Get green value
				int green = pixel[1];

				// Filter min green
				if (green < MIN_GREEN) {
					int[] black = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
					image.getRaster().setPixel(x, y, black);
					continue;
				}

				// Maximize remaining
				int[] greenPixel = { 0, 255, 0, 0, 0, 0, 0, 0, 0, 0 };
				image.getRaster().setPixel(x, y, greenPixel);
			}
		}
	}

	/**
	 * This method will scale the training image to a standard size.
	 * 
	 * @param image The original image.
	 * @return The scaled image.
	 */
	public BufferedImage scaleImage(BufferedImage image) {
		Image scaledImage = image.getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST);
		BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		// Draw scaled image on buffered image
		Graphics graphics = bufferedImage.getGraphics();
		graphics.drawImage(scaledImage, 0, 0, null);
		graphics.dispose();

		return bufferedImage;
	}

	/**
	 * This method runs the neural network on the image that is given. It will
	 * filter the image and then scale it to 16x16.
	 * 
	 * @param image The original image.
	 * @return The side that the target belongs to.
	 */
	public Side runNetwork(BufferedImage image) {
		// Filter the image
		filterGreen(image);

		// Scale the image
		BufferedImage scaledImage = scaleImage(image);

		// Run the network

		double[][] inputs = new double[1][WIDTH * HEIGHT];
		inputs[0] = getImageAsArray(scaledImage);

		MLDataSet dataSet = new BasicMLDataSet(inputs, new double[][] { { 0, 0 } });
		MLDataPair dataPair = dataSet.get(0);
		
		MLData outputData = neuralNetwork.compute(dataPair.getInput());
		
		if (outputData.getData()[0] > outputData.getData()[1]) {
			// Left
			return Side.LEFT;
		}
		
		if (outputData.getData()[0] < outputData.getData()[1]) {
			// Right
			return Side.RIGHT;
		}
		
		// Network error
		return null;
	}

	/**
	 * This constructor allows the neural network file to be loaded from it.
	 * 
	 * @param neuralNetworkFilePath
	 */
	public VisionAPI(String neuralNetworkFilePath) {
		// Load the neural network
		loadNeuralNetwork(neuralNetworkFilePath);
	}
}