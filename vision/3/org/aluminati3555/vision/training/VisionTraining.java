/**
 * FRC Team 3555
 * Deep Space
 */

package org.aluminati3555.vision.training;

import java.awt.image.BufferedImage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.mathutil.randomize.ConsistentRandomizer;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.persist.EncogDirectoryPersistence;

/**
 * This is the main class of the vision training program created for the Deep
 * Space season of FRC. Uses sets of images to train a neural network. See the
 * documentation for the Vision Training Data Creator for more information.
 * 
 * @author Caleb Heydon
 */

public class VisionTraining {
	// The dimensions of the training images
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

	// The maximum error for the neural network
	public static final double MAX_ERROR = 0.0001;

	// The working directory for the training images
	String resourcePath;

	// This is the neural network that the program is using
	BasicNetwork neuralNetwork;

	// The number of hidden layers
	int hiddenLayers;

	// The number of neurons per hidden layer;
	int neuronsPerLayer;

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
	 * This method constructs a new neural network for the vision training. See the
	 * method for details about the neural network.
	 */
	public void constructNewNeuralNetwork() {
		// Create a new neural network
		BasicNetwork neuralNetwork = new BasicNetwork();
		neuralNetwork.addLayer(new BasicLayer(null, true, WIDTH * HEIGHT));

		// Add hidden layers
		for (int i = 0; i < hiddenLayers; i++) {
			neuralNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, neuronsPerLayer));
		}

		// Add output layer
		neuralNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));

		// Finalize network
		neuralNetwork.getStructure().finalizeStructure();
		neuralNetwork.reset();

		// Randomize weights
		new ConsistentRandomizer(-1, 1, 100).randomize(neuralNetwork);

		// Set the neural network
		this.neuralNetwork = neuralNetwork;
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
	 * This method trains the network using two ArrayLists of images.
	 * 
	 * @param leftImages  The left images.
	 * @param rightImages The right images.
	 */
	public void trainNetwork(ArrayList<BufferedImage> leftImages, ArrayList<BufferedImage> rightImages) {
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

		// Train network
		ResilientPropagation resilientPropagation = new ResilientPropagation(neuralNetwork, dataSet);

		double currentError = 1;
		do {
			resilientPropagation.iteration();

			currentError = resilientPropagation.getError();
			System.out.println("Current error: " + currentError);
		} while (currentError > MAX_ERROR);
		resilientPropagation.finishTraining();
	}

	/**
	 * This default constructor allows a value for the resource location to be set.
	 * 
	 * @param resourcePath The path to the training images.
	 */
	public VisionTraining(String resourcePath, int hiddenLayers, int neuronsPerLayer) {
		this.resourcePath = resourcePath;
		this.hiddenLayers = hiddenLayers;
		this.neuronsPerLayer = neuronsPerLayer;
	}

	public static void main(String[] args) throws IOException {
		// Read arguments
		if (args.length < 4) {
			System.err.println(
					"Usage: java -jar <jarname> <resource_directory> <output_filename> <hidden_layers> <neurons_per_layer>");
			System.exit(1);
		}
		String resourcePath = args[0];
		String fileName = args[1];
		int hiddenLayers = Integer.parseInt(args[2]);
		int neuronsPerLayer = Integer.parseInt(args[3]);

		// Correct input
		if (!resourcePath.endsWith("/") && !resourcePath.endsWith("\\")) {
			resourcePath += "/";
		}

		VisionTraining visionTraining = new VisionTraining(resourcePath, hiddenLayers, neuronsPerLayer);

		// Create a new neural network
		System.out.println("Creating new neural network...");
		visionTraining.constructNewNeuralNetwork();

		// Load left training images
		System.out.println("Loading left training images...");
		ArrayList<BufferedImage> leftImages = visionTraining.loadLeftImages();
		System.out.println(leftImages.size() + " images loaded");

		// Load right training images
		System.out.println("Loading right training images...");
		ArrayList<BufferedImage> rightImages = visionTraining.loadRightImages();
		System.out.println(rightImages.size() + " images loaded");

		// Train network
		System.out.println("Training neural network...");
		visionTraining.trainNetwork(leftImages, rightImages);

		// Save network
		System.out.println("Saving neural network...");
		visionTraining.saveNeuralNetwork(fileName);
		System.out.println("Done");

		// Shutdown encog
		Encog.getInstance().shutdown();
	}
}