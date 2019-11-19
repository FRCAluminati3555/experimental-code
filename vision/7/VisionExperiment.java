import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class VisionExperiment {
	public static final int MIN_GREEN = 190;
	public static final int MAX_WHITE = 160;
	
	public static void filterWhite(BufferedImage image) {
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int[] pixel = new int[10];
				image.getRaster().getPixel(x, y, pixel);

				int average = (pixel[0] + pixel[1] + pixel[2]) / 3;
				
				int[] black = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
				if (average > MAX_WHITE) {
					image.getRaster().setPixel(x, y, black);
				}
			}
		}
	}
	
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
	public static BufferedImage scaleImage(BufferedImage image, int width, int height) {
		Image scaledImage = image.getScaledInstance(width, height, BufferedImage.SCALE_FAST);
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// Draw scaled image on buffered image
		Graphics graphics = bufferedImage.getGraphics();
		graphics.drawImage(scaledImage, 0, 0, null);
		graphics.dispose();

		return bufferedImage;
	}

	public static void main(String[] args) throws IOException {
		BufferedImage image = ImageIO.read(new File("C:\\Users\\C\\Desktop\\Original.jpg"));
		
		filterWhite(image);
		ImageIO.write(image, "png", new File("C:\\Users\\C\\Desktop\\filters\\white_filter.png"));
		
		filterGreen(image);
		ImageIO.write(image, "png", new File("C:\\Users\\C\\Desktop\\filters\\green_filter.png"));
	}
}