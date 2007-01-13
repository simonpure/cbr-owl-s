package ch.unizh.ifi.ddis.cbr;

import impl.owls.process.InputListImpl;
import impl.owls.process.OutputListImpl;

import java.net.URI;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLType;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;

public class SimilarityStatic {
	
	private static Logger logger = Logger.getLogger("SimilarityStatic");
	
	// default weighting for matching
	//
	private static double semanticInputsWeight = 0.6, semanticOutputsWeight = 0.3, semanticProcessWeight = 0.1;
	private static double syntacticInputsWeigth = 0.6, syntacticOutputsWeight = 0.3, syntacticProcessWeight = 0.1;
	private static double semanticWeight = 0.8, syntacticWeight = 0.1, graphWeight = 0.1;

	// whether to include constants in the matching process
	// default is false
	private static boolean constants = false;
	
	// constants used for semantic matching
    public static String[] MATCHES = {"EXACT", "SUBSUME", "RELAXED", "FAIL"};
    public static int EXACT   = 0;
    public static int SUBSUME = 1;
    public static int RELAXED = 2;
    public static int FAIL    = 3; 

    // NIL
    private static URI NIL = URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil");
    
	// similarity methods for syntactic measures
	// must extend simpack.api.impl.SimilarityMeasure
	// must have a constructor that accepts String, String or ISequenceAccessor,
	// ISequenceAccessor
	// see Simpack for details (http://www.ifi.unizh.ch/ddis/simpack.html)
	private static String[] methods = {"simpack.measure.external.owlsmx.CosineSimilarity", "simpack.measure.external.owlsmx.ExtendedJaccardMeasure", 
			"simpack.measure.external.owlsmx.JensenShannonMeasure"};
    
	// set default method for sintactic matching
	private static String defaultMethod = "simpack.measure.external.owlsmx.ExtendedJaccardMeasure";
	
	public static boolean compareInputs(InputList oldInputs, InputList newInputs) {
		boolean match = false;
		Iterator i = oldInputs.iterator();
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
		    		// inputs.add(oldInput);
		    		// TODO should constant values be included in matching
					// by default?
		    		if(constants) {
		    			if(compareConstants(oldInput.getConstantValue(), newInput.getConstantValue())) {
		    				match = true;		
		    			}
		    		} else {
		    			match = true;;
		    		}
		    		// logger.info("added::" + oldInput);
		    	} else {
		    		match = false;
		    	}
		    	if(!match) break;
		    }
		    if(!match) break;
		}
		return match;
	}
	
	public static boolean compareOutputs(OutputList oldOutputs, OutputList newOutputs) {
		boolean match = false;
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
		    				match = true;		
		    			}
		    		} else {
		    			match = true;
		    		}
		    	} else {
		    		match = false;
		    	}
		    	if(!match) break;
		    }
		    if(!match) break;
		}
		return match;
	}
	
	   private static int getMatchType(OWLType oldCaseType, OWLType newCaseType) {
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
	   
	private static boolean compareConstants(OWLValue oldValue, OWLValue newValue) {
			logger.info("new value::" + newValue);
			if(newValue != null) {
				return oldValue.equals(newValue);
			}
			// return true if no constant value is specified
			return true;
		}

}
