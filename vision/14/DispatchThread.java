private class DispatchThread extends Thread {
		private Vector<Mat> frames;
		private Hashtable<Mat, Double> fpsList;

		public synchronized void addFrame(Mat frame, double fps) {
			frames.add(frame);
			fpsList.put(frame, fps);
		}

		@Override
		public void run() {
			while (true) {
				if (frames.size() > 0) {
					Mat workingFrame;
					double fps;
					synchronized (this) {
						workingFrame = frames.get(0);
						fps = fpsList.get(workingFrame);
					}

					VisionUtil.resize(workingFrame, STREAMING_WIDTH, STREAMING_HEIGHT);

					double outputFPS = Double.parseDouble(decimalFormat.format(fps));
					Imgproc.putText(workingFrame, outputFPS + " FPS", new Point(5, 10), 0, 0.25, new Scalar(0, 255, 0));

					// See this link for crosshair
					// https://answers.opencv.org/question/22960/how-to-draw-crosshairsmarked-axes/

					Imgproc.line(workingFrame, new Point(workingFrame.width() / 2.0, workingFrame.height() / 2.0 - 8),
							new Point(workingFrame.width() / 2.0, workingFrame.height() / 2.0 + 8),
							new Scalar(0, 255, 0), 2);

					Imgproc.line(workingFrame, new Point(workingFrame.width() / 2.0 - 8, workingFrame.height() / 2.0),
							new Point(workingFrame.width() / 2.0 + 8, workingFrame.height() / 2.0),
							new Scalar(0, 255, 0), 2);

					compress(workingFrame);

					byte[] buffer = getJPEGBytes(workingFrame);
					workingFrame.release();

					for (int i = 0; i < clients.size(); i++) {
						try {
							clients.get(i).sendFrame(buffer);
						} catch (IOException e) {
							clients.remove(i);
							i--;
						}
					}

					frames.remove(0);
					fpsList.remove(workingFrame);
				}
			}
		}

		public DispatchThread() {
			frames = new Vector<Mat>();
			fpsList = new Hashtable<Mat, Double>();
		}
	}