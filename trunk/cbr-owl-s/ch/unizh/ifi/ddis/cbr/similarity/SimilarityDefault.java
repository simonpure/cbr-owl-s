package ch.unizh.ifi.ddis.cbr.similarity;

import ch.unizh.ifi.ddis.cbr.OWLSGraph;
import ch.unizh.ifi.ddis.cbr.OWLWrapper;
import simpack.measure.graph.MaxCommonSubgraphIsoValiente;

public abstract class SimilarityDefault implements Similarity {

	private OWLWrapper oldCase, newCase;
	
	/*
	public SimilarityDefault(OWLWrapper oldCase, OWLWrapper newCase) {
		this.oldCase = oldCase;
		this.newCase = newCase;
	}
	*/
	
	public double compareGraph(OWLSGraph oldGraph, OWLSGraph newGraph) {
		MaxCommonSubgraphIsoValiente compareGraph = new MaxCommonSubgraphIsoValiente(oldGraph, newGraph, 2, 0.5, 0.5, "big");
		return compareGraph.getSimilarity();
	}
	
}
