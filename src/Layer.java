public class Layer {

    public int size;
    public double[] neurons;
    public double[] biases;
    public double[][] weights;

    public Layer(int size, int nextSize) {
        this.size = size;
        neurons = new double[size];
        biases = new double[size];
        weights = new double[size][nextSize];
    }

    public Layer(Layer layer){
        size = layer.size;
        neurons = new double[layer.neurons.length];
        System.arraycopy(layer.neurons, 0, neurons, 0, layer.neurons.length);
        biases = new double[layer.biases.length];
        System.arraycopy(layer.biases, 0, biases, 0, layer.biases.length);
        weights = new double[layer.weights.length][];
        System.arraycopy(layer.weights, 0, weights, 0, layer.weights.length);
        for (int i = 0; i < layer.weights.length; i++) {
            System.arraycopy(layer.weights[i], 0, weights[i], 0, layer.weights[i].length);
        }



    }

    @Override
    public String toString() {
//        String text = "neuronos ";
//
//        for (int i = 0; i < neurons.length; i++) {
//            text+=" "+neurons[i];
//        }
//        text+="\n biases";
//        for (int i = 0; i < biases.length; i++) {
//            text+=" "+biases[i];
//        }

        String text ="\n weights";
        for (int i = 0; i < weights.length; i++) {
            text+="\t";
            for (int j = 0; j < weights[i].length; j++) {
                text+=" "+weights[i][j];
            }
            text+="\n";
        }
        return text;
    }
}