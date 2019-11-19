import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;

public class VisionTest {
	/**
	 * The input necessary for XOR.
	 */
	public static double XOR_INPUT[][] = { { 0.0, 0.0 }, { 1.0, 0.0 }, { 0.0, 1.0 }, { 1.0, 1.0 } };

	/**
	 * The ideal data necessary for XOR.
	 */
	public static double XOR_IDEAL[][] = { { 0.0 }, { 1.0 }, { 1.0 }, { 0.0 } };

	public static void main(String[] args) {
		BasicNetwork neuralNetwork = new BasicNetwork();
		neuralNetwork.addLayer(new BasicLayer(null, true, 2));
		neuralNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, 5));
		neuralNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 1));
		neuralNetwork.getStructure().finalizeStructure();
		neuralNetwork.reset();

		MLDataSet dataSet = new BasicMLDataSet(XOR_INPUT, XOR_IDEAL);
		ResilientPropagation resilientPropagation = new ResilientPropagation(neuralNetwork, dataSet);

		int epoch = 1;

		do {
			resilientPropagation.iteration();
			System.out.println("Epoch #" + epoch + " Error: " + resilientPropagation.getError());
			epoch++;
		} while (resilientPropagation.getError() > 0.001);
		resilientPropagation.finishTraining();

		for (int i = 0; i < dataSet.size(); i++) {
			MLDataPair dataPair = dataSet.get(i);
			MLData output = neuralNetwork.compute(dataPair.getInput());

			System.out.println(dataPair.getInput().getData(0) + ", " + dataPair.getInput().getData(1) + ", actual = "
					+ output.getData(0) + ", ideal = " + dataPair.getIdeal().getData(0));
		}
		
		Encog.getInstance().shutdown();
	}
}