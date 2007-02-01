package ch.unizh.ifi.ddis.cbr.similarity;

import java.net.URI;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLType;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public class SimilaritySemantic extends SimilarityDefault {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	// constants used for semantic matching
    public static double EXACT   =  1; //0;
    public static double SUBSUME =  0.75;  //1;
    public static double PLUGIN =  0.5;  //2;
    public static double FAIL    = 0;    //3; 

	// whether to include constants in the matching process
	// default is false
	private boolean constants = false;
	
	
    // NIL
    private URI NIL = URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil");
    
	public double compareInput(Input oldInput, Input newInput) {
		OWLType oldInputType = oldInput.getParamType();
		OWLType newInputType = newInput.getParamType();
		
		double match = getMatchType(oldInputType, newInputType);
		
   		if(constants && compareConstants(oldInput.getConstantValue(), newInput.getConstantValue())) {
   			match += 0.25;		
    	}
		
		return match;
	}

	public double compareOutput(Output oldOutput, Output newOutput) {
		OWLType oldOutputType = oldOutput.getParamType();
		OWLType newOutputType = newOutput.getParamType();
		
		double match = getMatchType(oldOutputType, newOutputType);
		
   		if(constants && compareConstants(oldOutput.getConstantValue(), newOutput.getConstantValue())) {
   			match += 0.25;		
    	}
		
		return match;
	}

	public double compareProcess(Process oldProcess, Process newProcess) {
		double match = 0;
		if(oldProcess.getLabel() != null) {
			if(oldProcess.getLabel().equals(newProcess.getURI().toString()) || newProcess.getURI().equals(NIL)) {
				match++;
			}
		}
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
	
	private double getMatchType(OWLType oldCaseType, OWLType newCaseType) {
		double matchtype = FAIL;
        if(oldCaseType.isEquivalent(newCaseType))
           matchtype = EXACT;
        else if(newCaseType.isSubTypeOf(oldCaseType))
        	matchtype = SUBSUME;        
        else if(oldCaseType.isSubTypeOf(newCaseType)) 
        	matchtype = PLUGIN;
        else if(newCaseType.getURI().equals(NIL))
        	matchtype = EXACT;
        else
        	matchtype = FAIL;
        //logger.info("match type::" + matchtype+ "::" + oldCaseType + " " + newCaseType);
        return matchtype;
	}

	public double compareParameter(Parameter oldParameter, Parameter newParameter) {
		OWLType oldOutputType = oldParameter.getParamType();
		OWLType newOutputType = newParameter.getParamType();
		
		double match = getMatchType(oldOutputType, newOutputType);
		
   		if(constants && compareConstants(oldParameter.getConstantValue(), newParameter.getConstantValue())) {
   			match += 0.25;		
    	}
		
		return match;
	}

}
