package org.aluminati3555.vision.api;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VisionTest {
	// The target dimensions of the images
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;

	// The minimum green value to be kept
	public static final int MIN_GREEN = 64;

	/**
	 * This method filters the image so that there is only green.
	 * 
	 * @param image The image.
	 */
	public static void filterGreen(BufferedImage image) {
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
	 * This method crops the training image so that there is no excess space.
	 * 
	 * @param image The original image.
	 * @return The final image.
	 */
	public static BufferedImage cropImage(BufferedImage image) {
		// Find bounds
		int minX;
		int minY = image.getHeight();

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
	public static BufferedImage scaleImage(BufferedImage image) {
		Image scaledImage = image.getScaledInstance(WIDTH, HEIGHT, BufferedImage.SCALE_FAST);
		BufferedImage bufferedImage = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);

		// Draw scaled image on buffered image
		Graphics graphics = bufferedImage.getGraphics();
		graphics.drawImage(scaledImage, 0, 0, null);
		graphics.dispose();

		return bufferedImage;
	}

	public static void main(String[] args) throws IOException {
		BufferedImage image = ImageIO.read(new File("C:\\Users\\C\\Desktop\\Data.png"));
		ImageIO.write(image, "png", new File("C:\\Users\\C\\Desktop\\output.png"));

		VisionAPI visionAPI = new VisionAPI("C:\\Users\\C\\Desktop\\Encog Deep Space Vision\\deep_space_vision.dat");
		Side side = visionAPI.runNetwork(image);
		
		System.out.println(side);
		
		filterGreen(image);
		image = cropImage(image);
		image = scaleImage(image);
		ImageIO.write(image, "png", new File("C:\\Users\\C\\Desktop\\output2.png"));
	}
}