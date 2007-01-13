package ch.unizh.ifi.ddis.cbr.merge;

import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;

import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.ResultList;
import org.mindswap.owls.process.ValueOf;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.URIUtils;

import ch.unizh.ifi.ddis.cbr.CBRFactory;
import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public abstract class MergerDefault implements Merger {
	
	public OWLWrapper oldCase, newCase, mergedCase;
	
	public OWLOntology ont;
	
	public OWLKnowledgeBase kb = CBRFactory.getKB();

	public URI baseURI;

	private Service mergeService;
	private Profile mergeProfile;
	private CompositeProcess mergeProcess;
	
	
	
	private int iInput = 0, iOutput = 0, iSequence = 0, iPerform = 0, iProcess = 0, iResult = 0;
	// constants used to count new elements created for the trail
	private static final String SEQUENCE = "Sequence", INPUT = "Input", OUTPUT = "Output", PROCESS = "Process", RESULT = "Result", PERFORM = "Perform";

	// URL where merged cases are stored
	// (must end in a slash)
	private static final String MERGEURL = "http://www.ifi.unizh.ch/ddis/ont/owl-s/mergers/";
	private static final String MERGEONTOLOGY = "Merge";
	private static final String MERGEPROCESS = "MergeProcess", MERGESERVICE = "MergeService", MERGEPROFILE = "MergeProfile";

	
	public void init() {
		// create an empty ontology in this KB which will hold the merged case ontology
		ont = kb.createOntology(true);
		mergeService = ont.createService(URIUtils.createURI(baseURI, MERGESERVICE));
		mergeProfile = ont.createProfile(URIUtils.createURI(baseURI, MERGEPROFILE));
		mergeProcess = ont.createCompositeProcess(URIUtils.createURI(baseURI, MERGEPROCESS));

		mergeService.setProfile(mergeProfile);
		mergeService.setProcess(mergeProcess);
		
	}
	

	public void addProcesses(ProcessList oldProcesses, ProcessList newProcesses) {
		
	}
	
	
	private URI createIdentifier(String name) {
		int type = 0;
		if(name.equals(INPUT)) {
			type = iInput++;
		} else if (name.equals(OUTPUT)) {
			type = iOutput++;
		} else if (name.equals(SEQUENCE)) {
			type = iSequence++;
		} else if (name.equals(PERFORM)) {
			type = iPerform++;
		} else if (name.equals(PROCESS)) {
			type = iProcess++;
		} else if (name.equals(RESULT)) {
			type = iResult++;
		}
		type++;
		return URIUtils.createURI(baseURI, name + String.valueOf(type));
	}
	
	
	private Hashtable inputs = new Hashtable();
	
	/*
	 * create a new input for the merged case
	 * 
	 */
	private Input createInput(Input input) {
			Input newInput;
			//logger.info("before if");
			if(inputs.containsKey(input.getURI())) {
				//logger.info("contains");
				newInput = (Input) inputs.get(input.getURI());
			} else {
				newInput = ont.createInput(createIdentifier(INPUT));
				newInput.setLabel(input.getURI().toString());
				//logger.info("before param");
				newInput.setParamType(input.getParamType());
				//logger.info("before constant");
				newInput.setConstantValue(input.getConstantValue());
				inputs.put(input.getURI(), newInput);
			}
			return newInput;
	}

	private Hashtable outputs = new Hashtable();
	
	private Output createOutput(Output output) {
			Output newOutput;
			if(outputs.containsKey(output.getURI())) {
				newOutput = (Output) outputs.get(output.getURI());
			} else {
				newOutput = ont.createOutput(createIdentifier(OUTPUT));
				newOutput.setLabel(output.getURI().toString());
				newOutput.setParamType(output.getParamType());
				newOutput.setConstantValue(output.getConstantValue());
				outputs.put(output.getURI(), newOutput);
			}
			return newOutput;
	}



	private void createBindings(Process oldProcess, Process trailProcess) {
		ProcessList pl = getProcesses(oldProcess, true);
		Iterator i = pl.iterator();
		Process process;
		while(i.hasNext()) {
			process = (Process) i.next();
			System.out.println("binding for process::" + process.getURI());
			addInbutBindings(process, trailProcess);
			addResultBindings(process, trailProcess);			
		}
	}
	
	private void addResultBindings(Process oldProcess, Process trailProcess) {
		//System.out.println("output bindings::" + oldProcess.getURI());

	   	Process newProcess = getProcess(trailProcess, oldProcess.getURI());

    	ResultList rl = oldProcess.getResults();
    	
    	if(rl.size() == 0) System.out.println("no results");
    	
    	Iterator l = rl.iterator();
    	Result result;
    	while(l.hasNext()) {
    		result = (Result) l.next();
    		//System.out.println("result::" + result.toRDF());
    		OWLIndividualList withOutputs = result.getProperties(OWLS.Process.withOutput);
	    	Iterator k = withOutputs.iterator();
	    	OWLIndividual hasDataFrom;
	    	while(k.hasNext()) {
	    		hasDataFrom = (OWLIndividual) k.next();
		    	OWLIndividual valueSource = hasDataFrom.getProperty(OWLS.Process.valueSource);
		    	OWLIndividual outputBinding = hasDataFrom.getProperty(OWLS.Process.toParam);
		    	
		    	Output output = (Output) outputBinding.castTo(Output.class);
		    	ValueOf vo = (ValueOf) valueSource.castTo(ValueOf.class);
    		
		    	Perform fromProcess = vo.getPerform();
		    	URI uriProcess = null;
		    	if(fromProcess.getProcess() != null) {
		    		Process performProcess = (Process) fromProcess.getAllProcesses().get(0);
		    		uriProcess = performProcess.getURI();
		    	} else {
		    		uriProcess = fromProcess.getURI();
		    	}
		    	
		    	Output newOutput = getOutput(trailProcess, output.getURI());
		    	Process newPerformProcess = getProcess(trailProcess, uriProcess);
		    	Parameter newParameter = (Parameter) getParameter(trailProcess, vo.getParameter().getURI());
				
		    	Result newResult = ont.createResult(createIdentifier(RESULT));
				
				Perform performProcess = newPerformProcess.getPerform();
		    	if(uriProcess.equals(Perform.TheParentPerform.getURI())) performProcess = Perform.TheParentPerform;
		    	
				result.addBinding(newOutput, performProcess, newParameter);
				newProcess.setResult(newResult);
				
		    	System.out.println("output::" + output + "->" + newOutput);
		    	System.out.println("fromProcess::" + uriProcess + "->" + performProcess);
		    	System.out.println("theVar::" + vo.getParameter() + "->" + newParameter);
	    	}
    		
    	}
	}

	private void addInbutBindings(Process oldProcess, Process trailProcess) {
		//System.out.println("input bindings::" + oldProcess.getURI());
		Perform perform = oldProcess.getPerform();
    	if(perform == null) {
    		System.out.println("no perfom");
    		return;
    	}
    	
    	Process newProcess = getProcess(trailProcess, oldProcess.getURI());
    	
    	//System.out.println("properties::" + perform.getProperty(OWLS.Process.fromProcess).toRDF());
    	
    	//OWLIndividual hasDataFrom = perform.getProperty(OWLS.Process.hasDataFrom);
    	
    	OWLIndividualList froms = perform.getProperties(OWLS.Process.hasDataFrom);
    	Iterator k = froms.iterator();
    	OWLIndividual hasDataFrom;
    	while(k.hasNext()) {
    		hasDataFrom = (OWLIndividual) k.next();
	    	OWLIndividual valueSource = hasDataFrom.getProperty(OWLS.Process.valueSource);
	    	OWLIndividual inputBinding = hasDataFrom.getProperty(OWLS.Process.toParam);
	    	
	    	Input input = (Input) inputBinding.castTo(Input.class);
	    	ValueOf vo = (ValueOf) valueSource.castTo(ValueOf.class);
	    	
	    	Perform fromProcess = vo.getPerform();
	    	URI uriProcess = null;
	    	if(fromProcess.getProcess() != null) {
	    		Process performProcess = (Process) fromProcess.getAllProcesses().get(0);
	    		uriProcess = performProcess.getURI();
	    	} else {
	    		uriProcess = fromProcess.getURI();
	    	}

	    	System.out.println("input::" + input);
	    	System.out.println("fromProcess::" + uriProcess);
	    	System.out.println("theVar::" + vo.getParameter());

	    	Input newInput = getInput(trailProcess, input.getURI());
	    	Process newPerformProcess = getProcess(trailProcess, uriProcess);
	    	Parameter newParameter = (Parameter) getParameter(trailProcess, vo.getParameter().getURI());

	    	
	    	Perform performProcess = newPerformProcess.getPerform();
	    	if(uriProcess.equals(Perform.TheParentPerform.getURI())) performProcess = Perform.TheParentPerform;
	    	
			Perform newPerform = newProcess.getPerform();
			newPerform.addBinding(newInput, performProcess, newParameter);			

    	}
    	
	}

	public Parameter getParameter(Process newProcess, URI uri) {
		Parameter parameter = (Parameter) getInput(newProcess, uri);
		if(parameter.getLabel().equals(uri)) return parameter;
		parameter = (Parameter) getOutput(newProcess, uri);
		return parameter;
	}
	
	public Input getInput(Process newProcess, URI uri) {
		InputList il = getInputs(newProcess);
		Iterator i = il.iterator();
		Input input = null;
		while(i.hasNext()) {
			input = (Input) i.next();
			if(input.getLabel().equals(uri.toString())) break;
		}
		return input;
	}

	/**
	 * 
	 * Create a Profile for the composite service. We only set the input and
	 * output of the profile based on the process.
	 * 
	 * @param profile
	 * @param process
	 * @return
	 */
	private Profile createProfile(Profile profile, Process process) {
		//logger.info("inputs size profile::"+process.getInputs().size());
		for (int i = 0; i < process.getInputs().size(); i++) {
			Input input = process.getInputs().inputAt(i);

			profile.addInput(input);
		}

		for (int i = 0; i < process.getOutputs().size(); i++) {
			Output output = process.getOutputs().outputAt(i);

			profile.addOutput(output);
		}

		return profile;
	}
}
