import java.util.function.UnaryOperator;

public class NeuralNetwork {

    private double learningRate;
    private Layer[] layers;
    private UnaryOperator<Double> activation;
    private UnaryOperator<Double> derivative;



    public NeuralNetwork(double learningRate, UnaryOperator<Double> activation, UnaryOperator<Double> derivative, int... sizes) {
        this.learningRate = learningRate;
        this.activation = activation;
        this.derivative = derivative;
        layers = new Layer[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            int nextSize = 0;
            if(i < sizes.length - 1) nextSize = sizes[i + 1];
            layers[i] = new Layer(sizes[i], nextSize);
            for (int j = 0; j < sizes[i]; j++) {
                layers[i].biases[j] = Math.random() * 2.0 - 1.0;
                for (int k = 0; k < nextSize; k++) {
                    layers[i].weights[j][k] = Math.random() * 2.0 - 1.0;
                }
            }
        }
    }

    public synchronized double[] feedForward(double[] inputs) {
        //Layer[] layers = useLayers(false, null);
        System.arraycopy(inputs, 0, layers[0].neurons, 0, inputs.length);
        for (int i = 1; i < layers.length; i++)  {
            Layer l = layers[i - 1];
            Layer l1 = layers[i];
            for (int j = 0; j < l1.size; j++) {
                l1.neurons[j] = 0;
                for (int k = 0; k < l.size; k++) {
                    l1.neurons[j] += l.neurons[k] * l.weights[k][j];
                }
                l1.neurons[j] += l1.biases[j];
                l1.neurons[j] = activation.apply(l1.neurons[j]);
            }
        }
        return layers[layers.length - 1].neurons;
//        return layers;
    }

    boolean isWriting = false;
//    int readCount = 0;

    private boolean read(boolean start){
        if(start){
            if(isWriting) {
                return false;
            }else {
//                readCount++;
//                System.out.println(readCount+" + "+ Thread.currentThread().getName());
                return true;
            }
        }else {
//            readCount--;
//            System.out.println(readCount+" - "+ Thread.currentThread().getName());
            return true;
        }
    }

    private boolean wantWrite(Layer[] newLayers){
        isWriting = true;
//        if (readCount>0) return true;

            System.arraycopy(newLayers, 0, this.layers, 0, newLayers.length);

        isWriting = false;
        return false;
    }

    private Layer[] useLayers(boolean write, Layer[] newLayers) {
        if(!write){
            boolean access = false;
            while (!access){
                access = read(true);
            }
            Layer[] layers = this.layers;
            Layer[] freezedLayers = new Layer[layers.length];

            for (int i = 0; i < layers.length; i++) {
                freezedLayers[i] = new Layer(layers[i]);
            }

            read(false);

            return freezedLayers;
        }else {
            while(wantWrite(newLayers));
//                System.out.println();;
            return null;
        }
    }

    public synchronized void backpropagation( /*Layer[] layers, */double[] targets) {
        double[] errors = new double[layers[layers.length - 1].size];
        for (int i = 0; i < layers[layers.length - 1].size; i++) {
            errors[i] = targets[i] - layers[layers.length - 1].neurons[i];
        }
        for (int k = layers.length - 2; k >= 0; k--) {
            Layer l = layers[k];
            Layer l1 = layers[k + 1];
            double[] errorsNext = new double[l.size];
            double[] gradients = new double[l1.size];
            for (int i = 0; i < l1.size; i++) {
                gradients[i] = errors[i] * derivative.apply(layers[k + 1].neurons[i]);
                gradients[i] *= learningRate;
            }
            double[][] deltas = new double[l1.size][l.size];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    deltas[i][j] = gradients[i] * l.neurons[j];
                }
            }
            for (int i = 0; i < l.size; i++) {
                errorsNext[i] = 0;
                for (int j = 0; j < l1.size; j++) {
                    errorsNext[i] += l.weights[i][j] * errors[j];
                }
            }
            errors = new double[l.size];
            System.arraycopy(errorsNext, 0, errors, 0, l.size);
            double[][] weightsNew = new double[l.weights.length][l.weights[0].length];
            for (int i = 0; i < l1.size; i++) {
                for (int j = 0; j < l.size; j++) {
                    weightsNew[j][i] = l.weights[j][i] + deltas[i][j];
                }
            }
            l.weights = weightsNew;
            for (int i = 0; i < l1.size; i++) {
                l1.biases[i] += gradients[i];
            }
        }
        System.out.print("");
        useLayers(true,layers);
        System.out.print("");
    }
}