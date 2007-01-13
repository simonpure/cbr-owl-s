/**
 * 
 */
package ch.unizh.ifi.ddis.cbr;

import impl.owls.process.InputListImpl;
import impl.owls.process.OutputListImpl;
import impl.owls.process.ProcessListImpl;

import java.lang.reflect.Constructor;
import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLType;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;

import simpack.accessor.string.StringAccessor;
import simpack.api.ISequenceAccessor;
import simpack.api.impl.SimilarityMeasure;
import simpack.measure.graph.GraphIsomorphism;
import simpack.measure.graph.MaxCommonSubgraphIsoValiente;
import simpack.tokenizer.SplittedStringTokenizer;

/**
 * @author simonl
 *
 */
public class OWLSSimilarityMeasure {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private OWLWrapper oldCase;
	private OWLWrapper newCase;
	//private OWLWrapper combinedCase;

	// state of calculation
	private boolean calculated = false;
	// the overall similarity measure 
	private double similarity;

	// the different similarities
	private double semanticmatch, semanticinputs, semanticoutputs, semanticprocesses;
	private double syntacticmatch, syntacticinputs, syntacticoutputs, syntacticprocessses;
	private double graphmatch; 

	// default weighting for matching
	//
	private double semanticInputsWeight = 0.6, semanticOutputsWeight = 0.3, semanticProcessWeight = 0.1;
	private double syntacticInputsWeigth = 0.6, syntacticOutputsWeight = 0.3, syntacticProcessWeight = 0.1;
	private double semanticWeight = 0.8, syntacticWeight = 0.1, graphWeight = 0.1;

	// whether to include constants in the matching process
	// default is false
	private boolean constants = false;
	
	// constants used for semantic matching
    public static String[] MATCHES = {"EXACT", "SUBSUME", "RELAXED", "FAIL"};
    public static int EXACT   = 0;
    public static int SUBSUME = 1;
    public static int RELAXED = 2;
    public static int FAIL    = 3; 

    // NIL
    private URI NIL = URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil");
    
	// similarity methods for syntactic measures
	// must extend simpack.api.impl.SimilarityMeasure 
	// must have a constructor that accepts String, String or ISequenceAccessor, ISequenceAccessor
	// see Simpack for details (http://www.ifi.unizh.ch/ddis/simpack.html)
	private String[] methods = {"simpack.measure.external.owlsmx.CosineSimilarity", "simpack.measure.external.owlsmx.ExtendedJaccardMeasure", 
			"simpack.measure.external.owlsmx.JensenShannonMeasure"};
    
	// set default method for sintactic matching
	private String defaultMethod = "simpack.measure.external.owlsmx.ExtendedJaccardMeasure";

	/*
	 * constructor
	 */
	public OWLSSimilarityMeasure(OWLWrapper oldCase, OWLWrapper newCase) {
		this.oldCase = oldCase;
		this.newCase = newCase;
	}

	// allow customization of weights
	public void setWeights(Double semanticWeight, Double syntacticWeight, Double graphWeight) {
		// only set values that are != null
		if(semanticWeight != null) this.semanticWeight = semanticWeight.doubleValue();
		if(syntacticWeight != null) this.syntacticWeight = syntacticWeight.doubleValue();
		if(graphWeight != null) this.graphWeight = graphWeight.doubleValue();
	}
	
	// allow customization of weights	
	public void setSemanticWeights(Double semanticInputsWeight, Double semanticOutputsWeight, Double semanticProcessWeight) {
		if(semanticInputsWeight != null) this.semanticInputsWeight = semanticInputsWeight.doubleValue();
		if(semanticOutputsWeight != null) this.semanticOutputsWeight = semanticOutputsWeight.doubleValue();
		if(semanticProcessWeight != null) this.semanticProcessWeight = semanticProcessWeight.doubleValue();
	}

	// allow customization of weights
	public void setSyntacticWeights(Double syntacticInputsWeigth, Double syntacticOutputsWeight, Double syntacticProcessWeight) {
		if(syntacticInputsWeigth != null) this.syntacticInputsWeigth = syntacticInputsWeigth.doubleValue();
		if(syntacticOutputsWeight != null) this.syntacticOutputsWeight = syntacticOutputsWeight.doubleValue();
		if(syntacticProcessWeight != null) this.syntacticProcessWeight = syntacticProcessWeight.doubleValue();
	}
	
	// return ontology associated with this similarity measure
	public OWLWrapper getOldCase() {
		return oldCase;
	}

	// return ontology associated with this similarity measure
	public OWLWrapper getNewCase() {
		return newCase;
	}
	
	public double [] getSemanticSimilarities() {
		calculate();
		double [] tmp = {semanticmatch, semanticinputs, semanticoutputs, semanticprocesses};
		return tmp;
	}
	
	public double getSemanticSimilarity() {
		calculate();
		return semanticmatch;
	}

	public double [] getSyntacticSimilarities() {
		calculate();
		double [] tmp = {syntacticmatch, syntacticinputs, syntacticoutputs, syntacticprocessses};
		return tmp;
	}

	public double getSyntacticSimilarity() {
		calculate();
		return syntacticmatch;
	}

	public double getGraphSimilarity() {
		return graphmatch;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public double getSimilarity() {
		if(!calculated) {
			calculate();
		}
		return similarity;
	}
	
	public void setConsiderConstants(boolean constants) {
		this.constants = constants;
	}
	
	public boolean getConsiderConstants() {
		return constants;
	}

	public void calculate() {
		if(!calculated) {
			// semantic matches
			//
			
			// returns number of matches
			// returns max in case newCase provides no restriction
			// compute relative number of matches
			// and weight it
			
			//logger.info("inputs::" + compareSemanticInputs(newCase) + "weight::" + semanticInputsWeight + "#::" + oldCase.getNumberOfInputs());
			//logger.info("inputs::" + ((double) 1 / (double) 2)*0.6);

			//semanticinputs = ((double) compareSemanticInputs(newCase) / (double) oldCase.getNumberOfInputs());  //*semanticInputsWeight;
			//semanticoutputs = ((double) compareSemanticOutputs(newCase) / (double) oldCase.getNumberOfOutputs()); //*semanticOutputsWeight;
			//semanticprocesses = ((double) compareSemanticProcesses(newCase) / (double) oldCase.getNumberOfProcesses()); //*semanticProcessWeight;

			semanticinputs = ((double) compareSemanticInputs(newCase) / (double) oldCase.getNumberOfInputs())*semanticInputsWeight;
			semanticoutputs = ((double) compareSemanticOutputs(newCase) / (double) oldCase.getNumberOfOutputs())*semanticOutputsWeight;
			semanticprocesses = ((double) compareSemanticProcesses(newCase) / (double) oldCase.getNumberOfProcesses())*semanticProcessWeight;

			// normalize
			semanticmatch = ((semanticinputs) + (semanticoutputs) 
					+ (semanticprocesses) / 3 );
			
//			semanticmatch = ((semanticinputs*semanticInputsWeight) + (semanticoutputs*semanticOutputsWeight) 
//					+ (semanticprocesses*semanticProcessWeight) / 3);


			logger.info("Semantic:: " + semanticmatch);
			logger.info("comparing inputs::" + semanticinputs);
			logger.info("comparing outputs::" + semanticoutputs);
			logger.info("comparing processes::" + semanticprocesses);

			// sintactic matches
			// returns max in case newCase provides no restriction

			//syntacticinputs = (compareSyntacticInputs(newCase) / oldCase.getNumberOfInputs());  //*syntacticInputsWeigth;
			//syntacticoutputs = (compareSyntacticOutputs(newCase) / oldCase.getNumberOfOutputs()); //*syntacticOutputsWeight;
			//syntacticprocessses = (compareSyntacticProcesses(newCase) / oldCase.getNumberOfProcesses()); //*syntacticProcessWeight;

			syntacticinputs = (compareSyntacticInputs(newCase) / oldCase.getNumberOfInputs())*syntacticInputsWeigth;
			syntacticoutputs = (compareSyntacticOutputs(newCase) / oldCase.getNumberOfOutputs())*syntacticOutputsWeight;
			syntacticprocessses = (compareSyntacticProcesses(newCase) / oldCase.getNumberOfProcesses())*syntacticProcessWeight;

			// normalize
			syntacticmatch = ((syntacticinputs) + (syntacticoutputs) 
					+ (syntacticprocessses) / 3 );
			
//			syntacticmatch = ((syntacticinputs*syntacticInputsWeigth) + (syntacticoutputs*syntacticOutputsWeight) 
//					+ (syntacticprocessses*syntacticProcessWeight) / 3);

			logger.info("Syntactic:: " + syntacticmatch);
			logger.info("comparing inputs::" + syntacticinputs);
			logger.info("comparing outputs::" + syntacticoutputs);
			logger.info("comparing processes::" + syntacticprocessses);
			
			// graph match
			//
			
			graphmatch = compareGraph(newCase);
			logger.info("Graph::");
			logger.info("comparing graph::" + graphmatch);
			
			// compute overall match with weights
			//
			similarity = (semanticmatch*semanticWeight + syntacticmatch*syntacticWeight + graphmatch*graphWeight);
			calculated = true;
		}
	}
	
	private double compareGraph(OWLWrapper newCase) {
		OWLSGraph oldgraph = oldCase.getGraph();
		OWLSGraph newgraph = newCase.getGraph();
		
		logger.info("graph1:: " + oldgraph.size());
		logger.info("graph2:: " + newgraph.size());
		
		//GraphIsomorphism compareGraph = new GraphIsomorphism(oldgraph, newgraph);
		
		MaxCommonSubgraphIsoValiente compareGraph = new MaxCommonSubgraphIsoValiente(oldgraph, newgraph, 2, 0.5, 0.5, "big");
		return compareGraph.getSimilarity();
	}
	
	private double compareStrings(String method, String s1, String s2) {
		StringAccessor a1 = new StringAccessor(s1, new SplittedStringTokenizer(" "));
		StringAccessor a2 = new StringAccessor(s2, new SplittedStringTokenizer(" "));
		double similarity;
		try {
			Class clazz = Class.forName(method);		
			Constructor con;
			SimilarityMeasure similaritymeasure = null;
			try {
				con = clazz.getConstructor(new Class[]{ISequenceAccessor.class, ISequenceAccessor.class});
				similaritymeasure = (SimilarityMeasure) con.newInstance(a1, a2);

			} catch (NoSuchMethodException e) {
				try {
					con = clazz.getConstructor(new Class[]{String.class, String.class});
					similaritymeasure = (SimilarityMeasure) con.newInstance(s1, s2);
				} catch (NoSuchMethodException e1) {
					// hmmm.. shouldn't happen
					similarity = 0;
				}
			}
			similarity = similaritymeasure.getSimilarity();
		} catch (Exception e) {
			// ups...
			similarity = 0;
		}
		return similarity;
	}

	private double compareSyntacticInputs(OWLWrapper newCase) {
		double match = -1;
		ArrayList oldInputs = oldCase.getInputNames();
		ArrayList newInputs = newCase.getInputNames();
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
			String oldInput;
			String newInput;
			while(i.hasNext()) {
				oldInput = (String) i.next();
				
			    while(j.hasNext()) {
			    	newInput = (String) j.next();
			    	//logger.info("\nold::" + oldInput + "\n" + "new::" + newInput);
			    	//match += compareStrings(defaultMethod, oldInput, newInput);
			    	double tmpMatch = 0;
			    	for(int k = 0; k<methods.length; k++) {
						//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldInput, newInput));
						tmpMatch += compareStrings(methods[k], oldInput, newInput);
			    	}
					// normalize match and add to overall match
					match += (tmpMatch / methods.length);
			    }
			}
		}
		return match;
	}

	private double compareSyntacticOutputs(OWLWrapper newCase) {
		double match = -1;
		ArrayList oldOutputs = oldCase.getOutputNames();
		ArrayList newOutputs = newCase.getOutputNames();
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
			String oldOutput;
			String newOutput;
			while(i.hasNext()) {
				oldOutput = (String) i.next();
				
			    while(j.hasNext()) {
			    	newOutput = (String) j.next();
			    	//logger.info("\nold::" + oldOutput + "\n" + "new::" + newOutput);
			    	//match += compareStrings(defaultMethod, oldOutput, newOutput);
			    	double tmpMatch = 0;
			    	for(int k = 0; k<methods.length; k++) {
						//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldOutput, newOutput));
						tmpMatch += compareStrings(methods[k], oldOutput, newOutput);
			    	}
					// normalize match and add to overall match
					match += (tmpMatch / methods.length);
			    }
			}
		}
		return match;
	}

	private double compareSyntacticProcesses(OWLWrapper newCase) {
		double match = -1;
		ArrayList oldProcesses = oldCase.getOutputNames();
		ArrayList newProcesses = newCase.getOutputNames();
		if(oldProcesses.size() == 0) {
			// no inputs
			match = 0;
		}
		if (newProcesses.size() == 0) {
			// no inputs in new case, maximum match
			match = oldCase.getNumberOfProcesses();
		} 
		if (match == -1) {
			match = 0;
			Iterator i = oldProcesses.iterator();
			Iterator j = newProcesses.iterator();
			String oldProcess;
			String newProcess;
			while(i.hasNext()) {
				oldProcess = (String) i.next();
				
			    while(j.hasNext()) {
			    	newProcess = (String) j.next();
			    	//logger.info("\nold::" + oldProcesse + "\n" + "new::" + newProcesse);
			    	//match += compareStrings(defaultMethod, oldProcesse, newProcesse);
			    	double tmpMatch = 0;
			    	for(int k = 0; k<methods.length; k++) {
						//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldProcesse, newProcesse));
						tmpMatch += compareStrings(methods[k], oldProcess, newProcess);
			    	}
					// normalize match and add to overall match
					match += (tmpMatch / methods.length);
			    }
			}
		}
		return match;
	}

	private int compareSemanticInputs(OWLWrapper newCase) {
		int match = -1;
		InputList oldInputs = oldCase.getInputs();
		InputList newInputs = newCase.getInputs();

		if(oldInputs.size() == 0) {
			// no inputs in old case
			match = 0;
		}
		if (newInputs.size() == 0) {
			// no inputs in new case equals max match
			match = oldCase.getNumberOfInputs();
		} 
		// only compare when there are any paramters present in both cases
		if(match == -1) {
			match = 0;
			Iterator i = oldInputs.iterator();
			InputListImpl inputs = new InputListImpl();
			Input oldInput;
			Input newInput;
			while(i.hasNext()) {
				oldInput = (Input) i.next();
			    OWLType oldInputType = oldInput.getParamType();

				Iterator j = newInputs.iterator();
			    while(j.hasNext()) {
			    	newInput = (Input) j.next();
				    OWLType newInputType = newInput.getParamType();
			    	if(getMatchType(oldInputType, newInputType)<3) {
			    		//inputs.add(oldInput);
			    		// TODO should constant values be included in matching by default?
			    		if(constants) {
			    			if(compareConstants(oldInput.getConstantValue(), newInput.getConstantValue())) {
			    				match++;		
			    			}
			    		} else {
			    			match++;
			    		}
			    		//logger.info("added::" + oldInput);
			    	}
			    }
			}
		}
		//logger.info("match::" + match);
		//return (InputList) inputs;
		return match;
	}
	
	private boolean compareConstants(OWLValue oldValue, OWLValue newValue) {
		logger.info("new value::" + newValue);
		if(newValue != null) {
			return oldValue.equals(newValue);
		}
		// return true if no constant value is specified
		return true;
	}

	private int compareSemanticOutputs(OWLWrapper newCase) {
		int match = -1;
		OutputList oldOutputs = oldCase.getOutputs();
		OutputList newOutputs = newCase.getOutputs();
		
		if (oldOutputs.size() == 0) {
			// no Outputs in old case
			match = 0;
		} 
		if (newOutputs.size() == 0) {
			// no Outputs in new case equals max match
			match = oldCase.getNumberOfOutputs();
			//logger.info("no outputs::match outputs::" + match);
		} 
		if (match == -1) {
			match = 0;
			Iterator i = oldOutputs.iterator();
			OutputListImpl Outputs = new OutputListImpl();
			Output oldOutput;
			Output newOutput;
			while(i.hasNext()) {
				oldOutput = (Output) i.next();
			    OWLType oldOutputType = oldOutput.getParamType();
			    
			    Iterator j = newOutputs.iterator();
			    while(j.hasNext()) {
			    	newOutput = (Output) j.next();
				    OWLType newOutputType = newOutput.getParamType();
			    	if(getMatchType(oldOutputType, newOutputType)<3) {
			    		//Outputs.add(oldOutput);
			    		// TODO should constant values be included in matching by default?
			    		if(constants) {
			    			if(compareConstants(oldOutput.getConstantValue(), newOutput.getConstantValue())) {
			    				match++;		
			    			}
			    		} else {
			    			match++;
			    		}
			    	}
			    }
			}
		}
		//return (OutputList) Outputs;
		return match;
	}
	
	private int compareSemanticProcesses(OWLWrapper newCase) {
		int match = -1;
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
				//OWLType oldProcessType = oldProcess.getType();

				Iterator j = newProcesses.iterator();
				while(j.hasNext()) {
					newProcess = (Process) j.next();
					//OWLType newProcessType = newProcess.getType();
					//if(getMatchType(oldProcessType, newProcessType)<3) {
					//logger.info("Process::" + oldProcess.getLabel());
					if(oldProcess.getLabel() != null) {
					if(oldProcess.getLabel().equals(newProcess.getURI().toString()) || newProcess.getURI().equals(NIL)) {
						//processes.add(oldProcess);
						match++;
					}
					}
				}
			}
		}
		//return (ProcessList) processes;
		return match;
	}
	
	   private int getMatchType(OWLType oldCaseType, OWLType newCaseType) {
		   int matchtype = 3;
	        if(oldCaseType.isEquivalent(newCaseType))
	           matchtype = EXACT;
	        else if(newCaseType.isSubTypeOf(oldCaseType))
	        	matchtype = SUBSUME;        
	        else if(oldCaseType.isSubTypeOf(newCaseType)) 
	        	matchtype = RELAXED;
	        else if(newCaseType.getURI().equals(NIL))
	        	matchtype = EXACT;
	        else
	        	matchtype = FAIL;
	        logger.info("match type::" + matchtype+ "::" + oldCaseType + " " + newCaseType);
	        return matchtype;
	    }
	   
	   public OWLWrapper getCombinedCase() {
		   Iterator i = oldCase.getProcesses().iterator();
		   Process p;
		   while(i.hasNext()) {
			   p = (Process) i.next();
			   logger.info("P::" + p.getURI());
		   }
		   return null;
	   }
	
}
