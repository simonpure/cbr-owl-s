package ch.unizh.ifi.ddis.cbr.similarity;

import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.OWLSGraph;

/*
 * interface to implement the different ways to compare 
 */
public interface Similarity {

	// implements method to compare inputs
	public double compareInput(Input oldInput, Input newInput);
	
	// implements method to compare outputs
	public double compareOutput(Output output, Output newOutput);
	
	// implements method to compare processes
	public double compareProcess(Process oldProcess, Process newProcess);
	
	// implements method to compare OWLS Graph
	public double compareGraph(OWLSGraph oldGraph, OWLSGraph newGraph);
	
	
}
