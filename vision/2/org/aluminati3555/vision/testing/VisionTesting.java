/**
 * FRC Team 3555
 * Deep Space
 */

package org.aluminati3555.vision.testing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.encog.Encog;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * This is the main class of the vision testing program created for the Deep
 * Space FRC game. This program uses a testing set of images to evaluate the
 * performance of a neural network.
 * 
 * @author Caleb Heydon
 */

public class VisionTesting {
	// The dimensions of the training images
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

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
	 * This method loads the left images from the resources.
	 * 
	 * @return The ArrayList of images.
	 * @throws IOException
	 */
	public ArrayList<BufferedImage> loadLeftImages() throws IOException {
		// Get directory list
		File[] files = new File(resourcePath + "left/").listFiles();

		// Image files
		ArrayList<File> imageFiles = new ArrayList<File>();

		// Locate the image files
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().toLowerCase().endsWith(".png")) {
				imageFiles.add(files[i]);
			}
		}

		// Images
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

		// Load images
		for (int i = 0; i < imageFiles.size(); i++) {
			images.add(ImageIO.read(imageFiles.get(i)));
		}

		return images;
	}

	/**
	 * This method loads the right images from the resources.
	 * 
	 * @return The ArrayList of images.
	 * @throws IOException
	 */
	public ArrayList<BufferedImage> loadRightImages() throws IOException {
		// Get directory list
		File[] files = new File(resourcePath + "right/").listFiles();

		// Image files
		ArrayList<File> imageFiles = new ArrayList<File>();

		// Locate the image files
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().toLowerCase().endsWith(".png")) {
				imageFiles.add(files[i]);
			}
		}

		// Images
		ArrayList<BufferedImage> images = new ArrayList<BufferedImage>();

		// Load images
		for (int i = 0; i < imageFiles.size(); i++) {
			images.add(ImageIO.read(imageFiles.get(i)));
		}

		return images;
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
	 * This method tests a neural network using the testing images.
	 * 
	 * @param leftImages  The ArrayList of the left testing images.
	 * @param rightImages The ArrayList of the right testing images.
	 * @return The accuracy of the neural network. A value between 0 and 1.
	 */
	public double testNetwork(ArrayList<BufferedImage> leftImages, ArrayList<BufferedImage> rightImages) {
		// The total number of testing images
		double total = leftImages.size() + rightImages.size();
		int count = 0;

		// Create new dataset
		double[][] inputs = new double[leftImages.size() + rightImages.size()][WIDTH * HEIGHT];
		double[][] outputs = new double[leftImages.size() + rightImages.size()][2];

		// Create rows for the left images
		double[] leftOutput = { 1, 0 };
		for (int i = 0; i < leftImages.size(); i++) {
			inputs[i] = getImageAsArray(leftImages.get(i));
			outputs[i] = leftOutput;
		}

		// Create rows for the right images
		double[] rightOutput = { 0, 1 };
		for (int i = 0; i < rightImages.size(); i++) {
			inputs[leftImages.size() - 1 + i] = getImageAsArray(rightImages.get(i));
			outputs[leftImages.size() - 1 + i] = rightOutput;
		}

		MLDataSet dataSet = new BasicMLDataSet(inputs, outputs);

		for (int i = 0; i < inputs.length; i++) {
			MLDataPair dataPair = dataSet.get(i);
			MLData outputData = neuralNetwork.compute(dataPair.getInput());

			if (dataPair.getIdeal().getData(0) > dataPair.getIdeal().getData(1)) {
				// The test is for a left image
				if (outputData.getData(0) > outputData.getData(1)) {
					count++;
				}
			} else {
				// The test is for a right image
				if (outputData.getData(0) < outputData.getData(1)) {
					count++;
				}
			}
		}

		// Calculate network accuracy
		double accuracy = count / total;
		return accuracy;
	}

	/**
	 * This constructor allows the resource path to be set on initialization.
	 * 
	 * @param resourcePath
	 */
	public VisionTesting(String resourcePath) {
		this.resourcePath = resourcePath;
	}

	public static void main(String[] args) throws IOException {
		// Read arguments
		if (args.length < 2) {
			System.err.println("Usage: java -jar <jarname> <resource_directory> <input_filename>");
			System.exit(1);
		}
		String resourcePath = args[0];
		String fileName = args[1];

		// Correct input
		if (!resourcePath.endsWith("/") && !resourcePath.endsWith("\\")) {
			resourcePath += "/";
		}

		VisionTesting visionTesting = new VisionTesting(resourcePath);

		// Load left training images
		System.out.println("Loading left testing images...");
		ArrayList<BufferedImage> leftImages = visionTesting.loadLeftImages();
		System.out.println(leftImages.size() + " images loaded");

		// Load right training images
		System.out.println("Loading right testing images...");
		ArrayList<BufferedImage> rightImages = visionTesting.loadRightImages();
		System.out.println(rightImages.size() + " images loaded");

		// Load neural network
		System.out.println("Loading neural network...");
		visionTesting.loadNeuralNetwork(fileName);

		// Test network
		System.out.println("Testing neural network...");
		double accuracy = visionTesting.testNetwork(leftImages, rightImages);
		System.out.println("Network accuracy: " + accuracy);
		System.out.println("Done");

		// Shutdown encog
		Encog.getInstance().shutdown();
	}
}