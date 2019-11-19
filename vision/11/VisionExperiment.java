import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class VisionExperiment {
	public static final int WIDTH = 16;
	public static final int HEIGHT = 16;
	
	/**
	 * This method preps an image for input into a neural network.
	 * 
	 * @param image The image.
	 * @return The array of inputs from the image.
	 */
	public static double[] getImageAsArray(BufferedImage image) {
		double[] buffer = new double[WIDTH * HEIGHT];

		for (int y = 0; y < image.getRaster().getHeight(); y++) {
			for (int x = 0; x < image.getRaster().getWidth(); x++) {
				// Load the pixel into the buffer
				int[] pixelBuffer = new int[10];
				image.getRaster().getPixel(x, y, pixelBuffer);
				
				if (pixelBuffer[1] > 0) {
					buffer[y * WIDTH + x] = 1;
				} else {
					buffer[y * WIDTH + x] = 0;
				}
			}
		}

		return buffer;
	}
	
	public static void main(String[] args) throws IOException {
		BufferedImage image = ImageIO.read(new File("C:\\Users\\C\\Desktop\\output.png"));
		double[] buffer = getImageAsArray(image);
	}
}