
/**
 * FRC Team 3555
 * Deep Space
 */

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.aluminati3555.vision.api.Side;
import org.aluminati3555.vision.api.VisionAPI;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 
 * This class tests the vision API with OpenCV
 * 
 * @author Caleb Heydon
 *
 */

public class OpenCVTest {

	// Load native library
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	/**
	 * This method converts a mat to a buffered image
	 * 
	 * @param mat The mat
	 * @return The buffered image
	 */
	public static BufferedImage getBufferedImage(Mat mat) {
		try {
			MatOfByte matOfByte = new MatOfByte();
			Imgcodecs.imencode(".jpg", mat, matOfByte);

			byte[] buffer = matOfByte.toArray();

			InputStream in = new ByteArrayInputStream(buffer);
			BufferedImage image = ImageIO.read(in);

			in.close();
			return image;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * This method converts a buffered image to a mat
	 * 
	 * @param image The buffered image
	 * @return The mat
	 */
	public static Mat getMat(BufferedImage image) {
		Mat mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC3);
		byte[] buffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		mat.put(0, 0, buffer);

		return mat;
	}

	public static void main(String[] args) throws IOException {
		// Load vision api
		VisionAPI visionAPI = new VisionAPI("C:\\Users\\C\\Desktop\\deep_space_vision.dat");

		// Load original image
		BufferedImage image = ImageIO.read(new File("C:\\Users\\C\\Desktop\\original.jpg"));
		Mat mat = getMat(image);

		// Setup pipeline and process image
		DeepSpaceVisionPipeline visionPipeline = new DeepSpaceVisionPipeline();
		visionPipeline.process(mat);

		image = getBufferedImage(visionPipeline.maskOutput());

		BufferedImage hslImage = getBufferedImage(visionPipeline.hslThresholdOutput());
		ImageIO.write(hslImage, "png", new File("C:\\Users\\C\\Desktop\\hsl_filter.png"));

		ArrayList<MatOfPoint> contours = visionPipeline.filterContoursOutput();

		System.out.println("Contours found: " + contours.size());

		// Go through targets found
		ArrayList<BufferedImage> targets = new ArrayList<BufferedImage>();
		for (int i = 0; i < contours.size(); i++) {
			Rect boundingBox = Imgproc.boundingRect(contours.get(i));

			int x = boundingBox.x;
			int y = boundingBox.y;
			int width = boundingBox.width;
			int height = boundingBox.height;
			
			if (width != height) {
				width = height;
				x -= (boundingBox.height - boundingBox.width) / 2;

				if (x < 0 || x + width > image.getWidth()) {
					continue;
				}
			}

			targets.add(image.getSubimage(x, y, width, height));
		}
		
		System.out.println("Targets found: " + targets.size());

		// Save images of vision targets
		for (int i = 0; i < targets.size(); i++) {
			ImageIO.write(targets.get(i), "png", new File("C:\\Users\\C\\Desktop\\target_" + i + ".png"));

			visionAPI.filterGreen(targets.get(i));
			ImageIO.write(visionAPI.scaleImage(targets.get(i)), "png",
					new File("C:\\Users\\C\\Desktop\\filtered_target_" + i + ".png"));
		}

		// Read original image again to draw
		image = ImageIO.read(new File("C:\\Users\\C\\Desktop\\original.jpg"));
		Graphics graphics = image.getGraphics().create();

		for (int i = 0; i < targets.size(); i++) {
			Side result = visionAPI.runNetwork(targets.get(i));
			Rect boundingBox = Imgproc.boundingRect(contours.get(i));

			if (result == null) {
				continue;
			} else if (result == Side.LEFT) {
				graphics.setColor(Color.YELLOW);
			} else if (result == Side.RIGHT) {
				graphics.setColor(Color.RED);
			}

			graphics.drawRect(boundingBox.x - 5, boundingBox.y - 5, boundingBox.width + 10, boundingBox.height + 10);
		}

		graphics.setColor(Color.GRAY);
		graphics.fillRect(0, 0, 90, 50);

		graphics.setColor(Color.YELLOW);
		graphics.drawString("Yellow = Left", 10, 20);

		graphics.setColor(Color.RED);
		graphics.drawString("Red = Right", 10, 40);

		graphics.dispose();

		// Output image
		ImageIO.write(image, "png", new File("C:\\Users\\C\\Desktop\\output.png"));
	}
}