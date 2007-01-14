package ch.unizh.ifi.ddis.cbr.similarity;

public class SimilarityMatch {
    private double inputs;
    private double outputs;
    private double processes;
    private double graph;

    public SimilarityMatch(double inputs, double outputs, double processes, double graph) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.processes = processes;
        this.graph = graph;
    }
    
    public double inputs() {
    	return inputs;
    }
    
    public double outputs() {
    	return outputs;
    }
    
    public double processes() {
    	return processes;
    }
    
    public double graph() {
    	return graph;
    }
}

