/**
 * 
 */
package ch.unizh.ifi.ddis.cbr;

import impl.owls.process.InputListImpl;
import impl.owls.process.OutputListImpl;
import impl.owls.process.ProcessListImpl;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLType;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.service.Service;

import ch.unizh.ifi.ddis.cbr.similarity.Similarity;

import simpack.accessor.string.StringAccessor;
import simpack.api.ISequenceAccessor;
import simpack.api.impl.SimilarityMeasure;
import simpack.exception.InvalidSimilarityMeasureNameException;
import simpack.measure.external.owlsmx.CosineSimilarity;
import simpack.measure.external.owlsmx.ExtendedJaccardMeasure;
import simpack.measure.external.owlsmx.JensenShannonMeasure;
import simpack.measure.graph.GraphIsomorphism;
import simpack.measure.graph.MaxCommonSubgraphIsoValiente;
import simpack.measure.string.AveragedStringMatching;
import simpack.tokenizer.SplittedStringTokenizer;

import examples.Matchmaker.Match;

/**
 * @author simonl
 *
 */
public class OWLSCaseBasedReasoner {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private OWLKnowledgeBase kb;
	private ArrayList cases = new ArrayList();

	
	// Trail List URL
	private String trailList = "http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/TrailList";
	
	// default weighting for matching
	//
	private double inputsWeight = 0.6;
	private double outputsWeight = 0.3;
	private double processesWeight = 0.2;

	
	// threshold to add matches
	private double IOPsThreshold = 0.6, graphThreshold = 0.3;

	
	//Similarity strategies
	private Similarity[] similarityStrategies;
	
	/*
	 * class to get different similarities break down
	 */
	public static class SimilarityMatch {
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
   
	
	/**
	 * Constructor for the CBR system  
	 * @throws URISyntaxException 
	 * @throws FileNotFoundException 
	 */
	public OWLSCaseBasedReasoner() throws FileNotFoundException, URISyntaxException {
		init();
	}

	// create OWL Knowledge Base, set reasoner and load trails
	//
	public void init() throws FileNotFoundException, URISyntaxException {
		
		kb = CBRFactory.getKB();
	    
	    loadCaseBase();
	    loadSimilarityStrategies();
	}
	
	private void loadCaseBase() throws FileNotFoundException, URISyntaxException {
		List services = kb.readAllServices(trailList);
		//logger.info("Trails::"+services);
		Service service;
		OWLOntology ont;
		Iterator i = services.iterator();
		while(i.hasNext()) {
			service = (Service) i.next();
			ont = service.getOntology();
			OWLWrapper rc = new OWLWrapper(ont, kb);
			if(!containsCase(rc)) {
				cases.add(rc);
			}
		}
	    logger.info("Initialized Case Base with " + cases.size());
	}
	
	private void loadSimilarityStrategies() {
		similarityStrategies = CBRFactory.getSimilarityStrategies();
	}
	
	public OWLSCaseBasedReasoner(OWLKnowledgeBase kb) {
		this.kb = kb;
	}
	
	public void setKB(OWLKnowledgeBase kb) {
		this.kb = kb;
	}
	
	public OWLKnowledgeBase getKB() {
		return kb;
	}
	

	
	public ArrayList retrieve(OWLOntology ont) throws FileNotFoundException, URISyntaxException {
		ArrayList retrievedCases = new ArrayList();

		// load ontology to this kb
		kb.load(ont);
		OWLWrapper newCase = new OWLWrapper(ont);

		// compare new case against old cases and add matches
		OWLWrapper oldCase;
		Iterator i = cases.iterator();
		SimilarityMatch[] matches;
		while(i.hasNext()) {
			oldCase = (OWLWrapper) i.next();
			logger.info("compare\n new::" + newCase + "\nto old::\n" + oldCase);
			
			matches = getSimilarity(oldCase, newCase);
			
			for(int j = 0; j < matches.length; j++) {
				double similarityInputs = matches[j].inputs();
				double similarityOutputs = matches[j].outputs();
				double similarityProcesses = matches[j].processes();
				double similarityGraph = matches[j].graph();
				
				double similarityIOPs = (similarityInputs * inputsWeight) + (similarityOutputs * outputsWeight)
					+ (similarityProcesses * processesWeight);
				
				logger.info("IOPs::" + similarityIOPs + " Inputs::" + similarityInputs + " Outputs::" +  similarityOutputs + " p::" 
						+  similarityProcesses + " g::" + similarityGraph);
				
				// check if one of the strategies was successful to consider old case
				if(similarityGraph >= graphThreshold && similarityIOPs >= IOPsThreshold) {
					retrievedCases.add(oldCase);
					break;
				}
			}
		}
		return retrievedCases;
	}
	

	private SimilarityMatch[] getSimilarity(OWLWrapper oldCase, OWLWrapper newCase) {
		SimilarityMatch[] matches = new SimilarityMatch[similarityStrategies.length];
		
		for(int i = 0; i<similarityStrategies.length; i++) {
			Similarity similarityStrategy = similarityStrategies[i];
			double similarityInputs = getSimilarityInputs(similarityStrategy , oldCase, newCase);
			double similarityOutputs = getSimilarityOutputs(similarityStrategy , oldCase, newCase);
			double similarityProcesses = getSimilarityProcesses(similarityStrategy , oldCase, newCase);
			double similarityGraph = getSimilarityGraph(similarityStrategy, oldCase, newCase);
			SimilarityMatch similarity = new SimilarityMatch(similarityInputs, similarityOutputs, similarityProcesses, similarityGraph);
			matches[i] = similarity;
		}
		
		return matches;
	}
	
	private double getSimilarityGraph(Similarity strategy, OWLWrapper oldCase, OWLWrapper newCase) {
		double match = 0;
		match = strategy.compareGraph(oldCase.getGraph(), newCase.getGraph());
		return match;
	}
	
	private double getSimilarityInputs(Similarity strategy, OWLWrapper oldCase, OWLWrapper newCase) {
		double match = -1;
		InputList oldInputs = oldCase.getInputs();
		InputList newInputs = newCase.getInputs();
		if(oldInputs.size() == 0) {
			// no inputs
			match = 0;
		}
		if (newInputs.size() == 0) {
			// no inputs in new case, maximum match
			match = oldCase.getNumberOfInputs();
		} 
		if (match == -1) {
			match = 0;
			Iterator i = oldInputs.iterator();
			Iterator j = newInputs.iterator();
			Input oldInput;
			Input newInput;
			while(i.hasNext()) {
				oldInput = (Input) i.next();
				
			    while(j.hasNext()) {
			    	newInput = (Input) j.next();
			    	match += strategy.compareInput(oldInput, newInput);
			    }
			}
		}
		// normalize
		return (double) (match / (double) oldCase.getNumberOfInputs());
	}
	
	private double getSimilarityOutputs(Similarity strategy, OWLWrapper oldCase, OWLWrapper newCase) {
		double match = -1;
		OutputList oldOutputs = oldCase.getOutputs();
		OutputList newOutputs = newCase.getOutputs();
		if(oldOutputs.size() == 0) {
			// no inputs
			match = 0;
		}
		if (newOutputs.size() == 0) {
			// no inputs in new case, maximum match
			match = oldCase.getNumberOfOutputs();
		} 
		if (match == -1) {
			match = 0;
			Iterator i = oldOutputs.iterator();
			Iterator j = newOutputs.iterator();
			Output oldOutput;
			Output newOutput;
			while(i.hasNext()) {
				oldOutput = (Output) i.next();
				
			    while(j.hasNext()) {
			    	newOutput = (Output) j.next();
			    	match += strategy.compareOutput(oldOutput, newOutput);
			    }
			}
		}
		// normalize
		return (double) (match / (double) oldCase.getNumberOfOutputs());
	}

	private double getSimilarityProcesses(Similarity strategy, OWLWrapper oldCase, OWLWrapper newCase) {
		double match = -1;
		ProcessList oldProcesses = oldCase.getProcesses();
		ProcessList newProcesses = newCase.getProcesses();
		
		if(oldProcesses.size() == 0) {
			// no Processes in old case
			match = 0;
		} 
		if (newProcesses.size() == 0) {
			// no Processes in new case equals max match
			match = oldCase.getNumberOfProcesses();
		} 
		if (match == -1) {
			match = 0;
			Iterator i = oldProcesses.iterator();
			//ProcessListImpl processes = new ProcessListImpl();
			Process oldProcess;
			Process newProcess;
			while(i.hasNext()) {
				oldProcess = (Process) i.next();

				Iterator j = newProcesses.iterator();
				while(j.hasNext()) {
					newProcess = (Process) j.next();
					//logger.info("old:: " + oldProcess + " new:: " + newProcess);
					match += strategy.compareProcess(oldProcess, newProcess);
				}
			}
		}
		// normalize
		return (double) (match / (double) oldCase.getNumberOfProcesses());
	}
	
	/*
	public ArrayList getSimilar(OWLOntology ont) throws FileNotFoundException, URISyntaxException {
		ArrayList bestCases = new ArrayList();
		
		logger.info("getSimilar");
		
		// load ontology into the kb
		kb.load(ont);
		
		OWLWrapper newCase = new OWLWrapper(ont);
		
		//logger.info(newCase.getGraph().getNodeSet());
		
		OWLWrapper oldCase;
		Iterator i = cases.iterator();
		OWLSSimilarityMeasure similarity;
		while(i.hasNext()) {
			oldCase = (OWLWrapper) i.next();
			similarity = compare(newCase, oldCase);
			if(similarity.getGraphSimilarity() >= graphThreshold || similarity.getSemanticSimilarity() >= semanticThreshold 
					|| similarity.getSyntacticSimilarity() >= syntacticThreshold) {
				bestCases.add(similarity);
			}
			logger.info("match:: " + similarity.getSimilarity());
		}
		
		return bestCases;
	}
	*/
	
	private OWLSSimilarityMeasure compare(OWLWrapper newCase, OWLWrapper oldCase) {
		logger.info("compare\n new::" + newCase + "\nto old::\n" + oldCase);
		
		OWLSSimilarityMeasure similarity = new OWLSSimilarityMeasure(oldCase, newCase);
		similarity.calculate();
		
		return similarity;
	}

	/*
	 * helper method to avoid duplicates as the kb returns somehow duplicates
	 * 
	 */
	private boolean containsCase(OWLWrapper rc) {
		Iterator i = cases.iterator();
		OWLWrapper temp;
		while(i.hasNext()) {
			temp = (OWLWrapper) i.next();
			if(temp.getURI().equals(rc.getURI())) return true;
		}
		return false;
	}
	
	public ArrayList getCases() {
		return cases;
	}
	
    // insertion sort
    // 
	private Hashtable rankHashtable(Hashtable hashtable) {
		//ArrayList a = new ArrayList();
		Hashtable rankedHashtable = new Hashtable();
		System.out.println("rank hashtable::" + hashtable.size());
		
		//a.addAll(hashtable.keySet());
		
		int [] a = new int[hashtable.size()];
		int x = 0;
		for (Enumeration i = hashtable.keys(); i.hasMoreElements() ;) {
			Integer match = (Integer) i.nextElement();
			a[x++] = match.intValue();
		}
		
		for(int i = 1; i < a.length; i++) {
			int j = i;
			int temp = a[j];
			while (j>0 && a[j-1] > temp) {
				a[j] = a[j-1];
				j--;
			}
			a[j] = temp;
		}
		
		for(int i = 0; i<a.length; i++) {
			Integer match = new Integer(a[i]);
			OWLWrapper rc = (OWLWrapper) hashtable.get(match);
			rankedHashtable.put(match, rc);
		}
		
		return rankedHashtable;
	}

}
