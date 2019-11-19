/**
 * FRC Team 3555
 * Deep Space
 * 
 * @author Caleb Heydon
 */

package org.aluminati3555.vision.training;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

public class VisionTraining {
	/**
	 * This is the main class of the vision training program created for the Deep
	 * Space season of FRC. Uses sets of images to train a neural network. See the
	 * documentation for the Vision Training Data Creator for more information.
	 */

	// The dimensions of the training images
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

	// The working directory for the training images
	String resourcePath;

	// This is the neural network that the program is using
	NeuralNetwork<?> neuralNetwork;

	// The number of hidden layers
	int hiddenLayers;

	// The number of neurons per hidden layer;
	int neuronsPerLayer;

	/**
	 * This method returns the neural network that is currently in use.
	 * 
	 * @return The neural network.
	 */
	public NeuralNetwork<?> getNeuralNetwork() {
		return neuralNetwork;
	}

	/**
	 * This method loads a previously saved neural network.
	 * 
	 * @param path The path of the neural network.
	 */
	public void loadNeuralNetwork(String path) {
		neuralNetwork = NeuralNetwork.createFromFile(path);
	}

	/**
	 * This method saves the neural network in use as a file.
	 * 
	 * @param path The path of the file.
	 */
	public void saveNeuralNetwork(String path) {
		neuralNetwork.save(path);
	}

	/**
	 * This method returns a new layer.
	 * 
	 * @param neurons The number of neurons.
	 * @return The layer.
	 */
	public Layer getNewLayer(int neurons) {
		Layer layer = new Layer(neurons);
		for (int i = 0; i < neurons; i++) {
			layer.addNeuron(new Neuron());
		}

		return layer;
	}

	/**
	 * This method constructs a new neural network for the vision training. See the
	 * method for details about the neural network.
	 */
	public void constructNewNeuralNetwork() {
		// Generate layer numbers
		int[] layers = new int[hiddenLayers + 2];
		layers[0] = WIDTH * HEIGHT;
		for (int i = 1; i < layers.length - 1; i++) {
			layers[i] = neuronsPerLayer;
		}
		layers[layers.length - 1] = 2;

		// Create the neural network
		NeuralNetwork<?> neuralNetwork = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, layers);

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
		DataSet dataSet = new DataSet(WIDTH * HEIGHT, 2);

		// Create rows for the left images
		double[] leftOutput = { 1, 0 };
		for (int i = 0; i < leftImages.size(); i++) {
			DataSetRow row = new DataSetRow();

			// Set input
			row.setInput(getImageAsArray(leftImages.get(i)));

			// Set output
			row.setDesiredOutput(leftOutput);

			// Add row
			dataSet.add(row);
		}

		// Create rows for the right images
		double[] rightOutput = { 0, 1 };
		for (int i = 0; i < rightImages.size(); i++) {
			DataSetRow row = new DataSetRow();

			// Set input
			row.setInput(getImageAsArray(rightImages.get(i)));

			// Set output
			row.setDesiredOutput(rightOutput);

			// Add row
			dataSet.add(row);
		}

		// Train network
		BackPropagation backPropagation = new BackPropagation();
		backPropagation.setMaxIterations(1000);
		backPropagation.setNeuralNetwork(neuralNetwork);
		backPropagation.learn(dataSet);
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
	}
}