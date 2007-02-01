package ch.unizh.ifi.ddis.cbr.adaption;

import impl.owls.process.InputListImpl;
import impl.owls.process.OutputListImpl;
import impl.owls.process.ProcessListImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.ResultList;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.ValueOf;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.utils.URIUtils;

import ch.unizh.ifi.ddis.cbr.CBRFactory;
import ch.unizh.ifi.ddis.cbr.OWLWrapper;
import ch.unizh.ifi.ddis.cbr.similarity.Similarity;

public abstract class AdaptionDefault implements Adaption {
	
	public OWLWrapper oldCase, newCase, mergedCase = null;
	
	public OWLOntology ont;
	
	public OWLKnowledgeBase kb = CBRFactory.getKB();

	public URI baseURI;

	private Service mergeService;
	private Profile mergeProfile;
	private CompositeProcess mergeProcess;
	private Sequence sequence;
	private Process previousProcess = null;
	
	private int iInput = 0, iOutput = 0, iSequence = 0, iPerform = 0, iProcess = 0, iResult = 0;
	// constants used to count new elements created for the trail
	private static final String SEQUENCE = "Sequence", INPUT = "Input", OUTPUT = "Output", PROCESS = "Process", RESULT = "Result", PERFORM = "Perform";

	// URL where merged cases are stored
	// (must end in a slash)
	private static final String MERGEURL = "http://www.ifi.unizh.ch/ddis/ont/owl-s/adaptions/";
	private static final String MERGEONTOLOGY = "Adapt";
	private static final String MERGEPROCESS = "AdaptProcess", MERGESERVICE = "AdaptService", MERGEPROFILE = "AdaptProfile";

	//Similarity strategies
	private Similarity[] similarityStrategies;
	
	// threshold
	public double similarityThreshold = 0.5;

	public AdaptionDefault() {
		init();
	}
	
	public void init() {
		loadSimilarityStrategies();
		
		baseURI = URI.create(MERGEURL + MERGEONTOLOGY + "-" + createTimeStamp() + ".owl#");
		
		// create an empty ontology in this KB which will hold the merged case ontology
		ont = kb.createOntology(baseURI);
		mergeService = ont.createService(URIUtils.createURI(baseURI, MERGESERVICE));
		mergeProfile = ont.createProfile(URIUtils.createURI(baseURI, MERGEPROFILE));
		mergeProcess = ont.createCompositeProcess(URIUtils.createURI(baseURI, MERGEPROCESS));

		mergeService.setProfile(mergeProfile);
		mergeService.setProcess(mergeProcess);

		sequence = ont.createSequence(createIdentifier(SEQUENCE));
		mergeProcess.setComposedOf(sequence);

	}
	
	private void loadSimilarityStrategies() {
		similarityStrategies = CBRFactory.getSimilarityStrategies();
	}

	public void addProcess(Process newProcess) throws NoBindingException {
		// add this process to sequence
		Process process = addToSequence(newProcess);
		// add to sequence
		sequence.addComponent(process.getPerform());
		
		if(previousProcess != null) {
			addBindings(previousProcess, newProcess);
		} else {
			// first process, add inputs to parent
			addFirstBindings(process);
		}
		
		previousProcess = process;
	}
	
	private void addFirstBindings(Process process) {
		InputList inputs = process.getInputs();
		Iterator i = inputs.iterator();
		Input input;
		while(i.hasNext()) {
			input = (Input) i.next();
			Input newInput = ont.createInput(createIdentifier(INPUT));
			newInput.setLabel(input.getURI().toString());
			newInput.setParamType(input.getParamType());
			//newInput.setConstantValue(input.getConstantValue());

			Perform perform = process.getPerform();
			perform.addBinding(input, Perform.TheParentPerform, newInput);
		}
		
	}

	private void addBindings(Process previousProcess, Process newProcess) throws NoBindingException {
		OutputList outputs = previousProcess.getOutputs();
		InputList inputs = newProcess.getInputs();
		Iterator i = outputs.iterator();
		ArrayList used = new ArrayList();
		Output output;
		Input input;
		Input bestMatch = null;
		while(i.hasNext()) {
			output = (Output) i.next();
			Iterator j = inputs.iterator();
			double similarity = 0;
			while(j.hasNext()) {
				input = (Input) j.next();
				double tmpsimilarity = compareParameter(input, output);
				if(tmpsimilarity > similarity && !used.contains(bestMatch)) {
					bestMatch = input;
				}
			}
			Perform perform = newProcess.getPerform();
			System.out.println("previous Process:: " + previousProcess);
			if(similarity >= similarityThreshold) {
				perform.addBinding(bestMatch, previousProcess.getPerform(), output);	
			} else {
				System.err.println("no bindings");
				throw new NoBindingException("NoBindingException: " + previousProcess + "::" + newProcess);
			}
			
			used.add(bestMatch);
		}
		
		
	}
	
	private double compareParameter(Input input, Output output) {
		double similarity = 0;
		for(int i = 0; i<similarityStrategies.length; i++) {
			Similarity similarityStrategy = similarityStrategies[i];
			double similarityInputs = similarityStrategy.compareParameter(input, output);
			if(similarityInputs > similarity) similarity = similarityInputs;
		}
		return similarity;
	}
	
	
	public OWLWrapper getMergedCase() throws FileNotFoundException, URISyntaxException {
		if(mergedCase == null) {
			createProfile(mergeProfile, mergeProcess);
			addResult();
			
			//ont.write(System.out);
			
			mergedCase = new OWLWrapper(ont); 
		}
		return mergedCase;
	}
	
	private void addResult() {
		OutputList outputs = previousProcess.getOutputs();
		Iterator i = outputs.iterator();
		Output output;
		while(i.hasNext()) {
			output = (Output) i.next();
			
			Output newOutput = ont.createOutput(createIdentifier(OUTPUT));
			newOutput.setLabel(output.getURI().toString());
			newOutput.setParamType(output.getParamType());
			//newOutput.setConstantValue(output.getConstantValue());
			newOutput.setProcess(mergeProcess);
			
			Result result = ont.createResult(createIdentifier(RESULT));
			result.addBinding(newOutput, previousProcess.getPerform(), output);
			
			mergeProcess.setResult(result);
		}
	}
	
	
	/**
	 *  adds a new process and it's children
	 * 
	 * @version 0.9
	 * @author simonl
	 * 
	 */
	private Process addToSequence(Process originalProcess) {

		if(originalProcess.canCastTo(CompositeProcess.class)) {
			// Composite Process
			CompositeProcess cp = (CompositeProcess) originalProcess.castTo(CompositeProcess.class);
		
			CompositeProcess newCompositeProcess = ont.createCompositeProcess(createIdentifier(PROCESS));
			newCompositeProcess.setLabel(cp.getURI().toString());
		
			Perform perform = ont.createPerform(createIdentifier(PERFORM));
			perform.setProcess(newCompositeProcess);
			
			Sequence sequence = ont.createSequence(createIdentifier(SEQUENCE));
			newCompositeProcess.setComposedOf(sequence);
		
			InputList il = cp.getInputs(); 
	  
			Iterator iterator = il.iterator(); 
			Input input;
			while(iterator.hasNext()) {
				input = (Input) iterator.next();
				Input newInput = createInput(input);
				newInput.setProcess(newCompositeProcess);
			}
			
			OutputList ol = cp.getOutputs();
			Iterator outputI = ol.iterator();
			Output output;
			while(outputI.hasNext()){
				output = (Output) outputI.next();
				Output newOutput = createOutput(output);
				newOutput.setProcess(newCompositeProcess);
			}
			
		    ProcessList pl = getProcesses(cp, false);
		    //System.out.println("processes::" + pl.size());
		    Iterator i = pl.iterator();
		    while(i.hasNext()) {
		    	Process newProcess = addToSequence((Process) i.next());
		    	sequence.addComponent(newProcess.getPerform());
		    }
		    
		    // add its bindings
			addInputBindings(originalProcess, newCompositeProcess);
			addResultBindings(originalProcess, newCompositeProcess);

		    return newCompositeProcess;
			
		} else {
			// Atomic Process
			
			Process newProcess = ont.createAtomicProcess(createIdentifier(PROCESS));
			newProcess.setLabel(originalProcess.getURI().toString());

			Perform newPerform = ont.createPerform(createIdentifier(PERFORM));
			newPerform.setProcess(newProcess);
			
			InputList il = originalProcess.getInputs(); 
	  
			Iterator iterator = il.iterator(); 
			Input input;
			while(iterator.hasNext()) {
				input = (Input) iterator.next();
				Input newInput = createInput(input);
				newInput.setProcess(newProcess);
			}
			
			OutputList ol = originalProcess.getOutputs();
			Iterator outputI = ol.iterator();
			Output output;
			while(outputI.hasNext()){
				output = (Output) outputI.next();
				Output newOutput = createOutput(output);
				newOutput.setProcess(newProcess);
			}
		    // add its bindings
			//addInputBindings(originalProcess, newProcess);
			//addResultBindings(originalProcess, newProcess);
			return newProcess;
		}
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
				//newInput.setConstantValue(input.getConstantValue());
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
				//newOutput.setConstantValue(output.getConstantValue());
				outputs.put(output.getURI(), newOutput);
			}
			return newOutput;
	}



	private void createBindings(Process originalProcess, Process mergedProcess) {
		ProcessList pl = getProcesses(originalProcess, true);
		Iterator i = pl.iterator();
		Process process;
		while(i.hasNext()) {
			process = (Process) i.next();
			System.out.println("binding for process::" + process.getURI());
			addInputBindings(process, mergedProcess);
			addResultBindings(process, mergedProcess);			
		}
	}
	
	private void addResultBindings(Process originalProcess, Process mergedProcess) {
		//System.out.println("output bindings::" + oldProcess.getURI());

	   	Process newProcess = getProcess(mergedProcess, originalProcess.getURI());

    	ResultList rl = originalProcess.getResults();
    	
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
		    	
		    	Output newOutput = getOutput(mergedProcess, output.getURI());
		    	Process newPerformProcess = getProcess(mergedProcess, uriProcess);
		    	Parameter newParameter = (Parameter) getParameter(mergedProcess, vo.getParameter().getURI());
				
		    	Result newResult = ont.createResult(createIdentifier(RESULT));
				
				Perform performProcess = newPerformProcess.getPerform();
		    	if(uriProcess.equals(Perform.TheParentPerform.getURI())) performProcess = Perform.TheParentPerform;
		    	
				result.addBinding(newOutput, performProcess, newParameter);
				newProcess.setResult(newResult);
				
		    	//System.out.println("output::" + output + "->" + newOutput);
		    	//System.out.println("fromProcess::" + uriProcess + "->" + performProcess);
		    	//System.out.println("theVar::" + vo.getParameter() + "->" + newParameter);
	    	}
    		
    	}
	}

	private void addInputBindings(Process originalProcess, Process mergedProcess) {
		//System.out.println("input bindings::" + oldProcess.getURI());
		Perform perform = originalProcess.getPerform();
    	if(perform == null) {
    		System.out.println("no perfom");
    		return;
    	}
    	
    	Process newProcess = getProcess(mergedProcess, originalProcess.getURI());
    	
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

//	    	System.out.println("input::" + input);
//	    	System.out.println("fromProcess::" + uriProcess);
//	    	System.out.println("theVar::" + vo.getParameter());

	    	Input newInput = getInput(mergedProcess, input.getURI());
	    	Process newPerformProcess = getProcess(mergedProcess, uriProcess);
	    	Parameter newParameter = (Parameter) getParameter(mergedProcess, vo.getParameter().getURI());

	    	
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
	
	public InputList getInputs(Process originalProcess) {
		ProcessList pl = getProcesses(originalProcess, true);
		Iterator i = pl.iterator();
		Process process;
		InputListImpl il = new InputListImpl();
		while(i.hasNext()) {
			process = (Process) i.next();
			getAllInputs(process, il);
			//il.addAll(getAllInputs(process));
		}
		return (InputList) il;
	}

	private void getAllInputs(Process process, InputList temp) {
		InputList il = process.getInputs();
		Iterator i = il.iterator();
		Input input;
		while(i.hasNext()) {
			input = (Input) i.next();
			if(!temp.contains(input)) {
				temp.add(input);
			}
		}
	}

	public Output getOutput(Process newProcess, URI uri) {
		OutputList ol = getOutputs(newProcess);
		Iterator i = ol.iterator();
		Output output = null;
		while(i.hasNext()) {
			output = (Output) i.next();
			if(output.getLabel().equals(uri.toString())) break;
		}
		return output;
	}

	public OutputList getOutputs(Process originalProcess) {
		ProcessList pl = getProcesses(originalProcess, true);
		Iterator i = pl.iterator();
		Process process;
		OutputListImpl ol = new OutputListImpl();
		while(i.hasNext()) {
			process = (Process) i.next();
			getAllOutputs(process, ol);
		}
		return (OutputList) ol;
		
	}
	
	private void getAllOutputs(Process process, OutputList temp) {
		OutputList ol = process.getOutputs();
		Iterator i = ol.iterator();
		Output output;
		while(i.hasNext()) {
			output = (Output) i.next();
			if(!temp.contains(output)) {
				temp.add(output);
			}
		}
	}
	
	private Process getProcess(Process originalProcess, URI uri) {
		ProcessList pl = getProcesses(originalProcess, true);
		Iterator i = pl.iterator();
		Process process = null;
		while(i.hasNext()) {
			process = (Process) i.next();
			if(process.getLabel().equals(uri.toString())) break;
		}
		return process;
	}
	
	public ProcessList getProcesses(Process process, boolean first) {
		ProcessListImpl processes = new ProcessListImpl();
		processes.add(process);
		ProcessList pl = getAllProcesses(processes);
		//logger.info("done");
		if (!first) pl.remove(0);
		return pl;
	}

	private ProcessList getAllProcesses(ProcessList pl) {
		ProcessListImpl temp = new ProcessListImpl();
		Iterator i = pl.iterator();
		Process process;
		while(i.hasNext()) {
			process = (Process) i.next();
			//System.out.println("process:: " + process.getURI());
			
			temp.add(process);
			if (process.canCastTo(CompositeProcess.class)) {
				CompositeProcess cp = (CompositeProcess) process.castTo(CompositeProcess.class);
				//logger.info(cp);
				ControlConstruct cc = cp.getComposedOf();
				if(cc != null) {
					//logger.info(cc);
					pl = (ProcessListImpl) cc.getAllProcesses();
					if(pl != null) {
						//logger.info(pl.size());
						temp.addAll(getAllProcesses(pl));	
					}
				}
			}
		}
		return temp;
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
	
	private String createTimeStamp() {
		return String.valueOf(new java.util.Date().getTime());
	}
}
