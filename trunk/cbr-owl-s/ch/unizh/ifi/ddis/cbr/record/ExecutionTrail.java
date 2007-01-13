/*
 * Implementation of Execution Trail
 * @author simonl
 * 
 * @date 2006/11/13 - Initial version
 * @date 2006/12/25 - Refactoring & clean-up
 *  
 */
package ch.unizh.ifi.ddis.cbr.record;

import impl.owls.process.InputImpl;
import impl.owls.process.InputListImpl;
import impl.owls.process.OutputImpl;
import impl.owls.process.OutputListImpl;
import impl.owls.process.ParameterImpl;
import impl.owls.process.ProcessListImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLDataProperty;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.grounding.AtomicGrounding;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputBinding;
import org.mindswap.owls.process.InputBindingList;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.ParameterValue;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.ResultList;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.ValueOf;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.swrl.Variable;
import org.mindswap.utils.URIUtils;

import sun.awt.geom.AreaOp.AddOp;

public class ExecutionTrail {
	private Logger logger = Logger.getLogger(this.getClass().getName());

	private ArrayList processes = new ArrayList();

	//private ArrayList inputs = new ArrayList();

	//private ArrayList outputs = new ArrayList();
	
	private ArrayList executionrecords = new ArrayList();

	private OWLOntology ont;

	private Service serviceTrail;

	private ProcessExecutionEngine exec;

	private OWLKnowledgeBase kb;

	private boolean finished;
	
	private boolean failed;
	
	private ExecutionException e;
	
	private URI baseURI;

	private int iInput = 0, iOutput = 0, iSequence = 0, iPerform = 0, iProcess = 0, iResult = 0;
	// constants used to count new elements created for the trail
	private static final String SEQUENCE = "Sequence", INPUT = "Input", OUTPUT = "Output", PROCESS = "Process", RESULT = "Result", PERFORM = "Perform";

	// URL to where trails are stored
	// (must end in a slash)
	private static final String TRAILURL = "http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/";
	private static final String TRAILONTOLOGY = "ExecutionTrail";
	private static final String TRAILPROCESS = "ExecutionTrailProcess", TRAILSERVICE = "ExecutionTrailService", TRAILPROFILE = "ExecutionTrailProfile";
	
	public ExecutionTrail(OWLKnowledgeBase kb) {
		this.kb = kb;
		baseURI = URI.create(TRAILURL + TRAILONTOLOGY + "-" + createTimeStamp() + ".owl#");
	}

	public ExecutionTrail(OWLKnowledgeBase kb, ProcessExecutionEngine exec) {
		this.kb = kb;
		this.exec = exec;
		baseURI = URI.create(TRAILURL + TRAILONTOLOGY + "-" + createTimeStamp() + ".owl#");
	}

	public ExecutionTrail(OWLKnowledgeBase kb, ProcessExecutionEngine exec, String name) {
		this.kb = kb;
		this.exec = exec;
		baseURI = URI.create(TRAILURL + TRAILONTOLOGY + "-" + name + "-" + createTimeStamp() + ".owl#");
	}
	
	private String createTimeStamp() {
		return String.valueOf(new java.util.Date().getTime());
	}

	public URI getBaseURI() {
		return baseURI;
	}

	public void setExec(ProcessExecutionEngine exec) {
		this.exec = exec;
		this.kb = this.exec.getKB();
		System.out.println(this.kb);
	}

	public void setKB(OWLKnowledgeBase kb) {
		this.kb = kb;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}

	public boolean finished() {
		return this.finished;
	}

	public void setFailed(boolean failed) {
		this.failed = failed;
	}
	
	public void setFailed(boolean failed, ExecutionException e) {
		this.failed = failed;
		this.e = e;
	}
	
	public boolean failed() {
		return failed;
	}

	public void processStarted(Process process, ValueMap inputs){
		ExecutionRecord executionrecord = new ExecutionRecord(process, inputs);
		executionrecords.add(executionrecord);
	}
	
	public void processesFinished(Process process, ValueMap inputs, ValueMap outputs) {
		Iterator i = executionrecords.iterator();
		ExecutionRecord executionrecord;
		while(i.hasNext()) {
			executionrecord = (ExecutionRecord) i.next();
			if(process == executionrecord.getProcess()) {
				executionrecord.setOutput(outputs);
			}
		}
	}
	
	public Process getAtomicProcess(int position) {
		Iterator i = executionrecords.iterator();
		ExecutionRecord executionrecord;
		int atomic = -1;
		Process process = null;
		while(i.hasNext()) {
			executionrecord = (ExecutionRecord) i.next();
			if(executionrecord.isAtomic()) {
				atomic++;
				if(atomic == position) {
					process = executionrecord.getProcess();
				}
			}
		}
		return process;
	}
	
	public ExecutionRecord getAtomicExecutionRecord(int position) {
		Iterator i = executionrecords.iterator();
		ExecutionRecord executionrecord;
		int atomic = -1;
		ExecutionRecord atomicExecutionrecord = null;
		while(i.hasNext()) {
			executionrecord = (ExecutionRecord) i.next();
			if(executionrecord.isAtomic()) {
				atomic++;
				if(atomic == position) {
					atomicExecutionrecord = executionrecord;
				}
			}
		}
		return atomicExecutionrecord;
	}
	
	public int getAtomicProcessSize() {
		Iterator i = executionrecords.iterator();
		ExecutionRecord executionrecord;
		int atomic = 0;
		while(i.hasNext()) {
			executionrecord = (ExecutionRecord) i.next();
			if(executionrecord.isAtomic()) {
				atomic++;
			}
		}
		return atomic;
	}
	
	public int getCompositeProcessSize() {
		Iterator i = executionrecords.iterator();
		ExecutionRecord executionrecord;
		int composite = 0;
		while(i.hasNext()) {
			executionrecord = (ExecutionRecord) i.next();
			if(executionrecord.isComposite()) {
				composite++;
			}
		}
		return composite;
	}
	
	private ExecutionRecord getExecutionRecord(Process p) {
		Iterator i = executionrecords.iterator();
		ExecutionRecord executionrecord;
		while(i.hasNext()) {
			executionrecord = (ExecutionRecord) i.next();
			//logger.info("ER::"+executionrecord.toString());
			if(executionrecord.getProcess().equals(p)) {
			//logger.info("match!");
				return executionrecord;
			}
		}
		return null;
	}
	
	private List getExecutionRecords(ProcessList pl) {
		Iterator i = pl.iterator();
		ArrayList executionrecords = new ArrayList();
		ExecutionRecord executionrecord;
		Process p;
		while(i.hasNext()) {
			p = (Process) i.next();
			executionrecord = getExecutionRecord(p);
			if(executionrecord != null) {
			//	logger.info("add::"+executionrecord.toString());
				executionrecords.add(executionrecord);
			//	logger.info("added");
			}
		}
	//	logger.info("list size::"+executionrecords.size());
		return executionrecords;
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
	
	public Service getServiceTrail() {
		return serviceTrail;
	}
	/*
	 * creates an ontology containing the recorded execution of this trail
	 * @return an OWL-S ontology with all processes, bindings and constant values
	 * 
	 */
	public OWLOntology createOntologyTrail() {
		// logger.info("createOntology");
		try {
			serviceTrail = createServiceTrail();
		} catch (Exception e) {
			System.out.println(e.toString());
			ont = null;
		}
		return ont;
	}

	private String saveService, saveProfile;
	/*
	 * main method to create the trail based on the recorded processes
	 * @return service 
	 */
	private Service createServiceTrail() throws Exception {

		// create an empty ontology in this KB which will hold the Trail ontology
		try {
			ont = kb.createOntology(true);
		} catch (Exception e) {
			System.out.println("create ont " + e.toString());
		}

//		OWLOntology base = kb.read(p.getOntology().getFileURI());
//		logger.info(base);
//		ont.addImport(base);
		
		Service service = ont.createService(URIUtils.createURI(baseURI, TRAILSERVICE));
		Profile profile = ont.createProfile(URIUtils.createURI(baseURI, TRAILPROFILE));
		//CompositeProcess process = ont.createCompositeProcess(URIUtils.createURI(baseURI, TRAILPROCESS));

		ExecutionRecord er = (ExecutionRecord) executionrecords.get(0);
		Process p = er.getProcess();

		try {
			service.setLabel(p.getService().getURI().toString());
		} catch(Exception e) {
			// null pointer
		}
		try {
			profile.setLabel(p.getProfile().getURI().toString());
		} catch(Exception e) {
			// null pointer
		}

		/*
		Process trailProcess;
		if(p.canCastTo(CompositeProcess.class)) {
			trailProcess = (CompositeProcess) ont.createCompositeProcess(URIUtils.createURI(baseURI, TRAILPROCESS));
		} else {
			trailProcess = (AtomicProcess) ont.createAtomicProcess(URIUtils.createURI(baseURI, TRAILPROCESS));
		}
		 */
		
		Process trailProcess = createSequenceProcess(p);
		
		System.out.println("trailprocess::" + trailProcess);
		
		createBindings(p, trailProcess);

		//logger.info("sequence ending");

		// set profile and process of the trail
		service.setProfile(profile);
		service.setProcess(trailProcess);

		//logger.info("profile starting");
		createProfile(profile, trailProcess);
		//logger.info("profile ending");

		//logger.info("label starting");
//		profile.setLabel(createLabel(processes));
		profile.setLabel(createLabel(executionrecords));

		//logger.info("label ending");

		profile.setTextDescription(profile.getLabel());

		service.setProfile(profile);
		service.setProcess(trailProcess);
		//service.setLabel(saveService);
		//profile.setLabel(saveProfile);
		
		// service.setGrounding(grounding);

		return service;
	}
	
	
	private Hashtable inputs = new Hashtable();
	
	/*
	 * create a new input for the trail
	 * 
	 */
	private Input createInput(Input input, ValueMap vmInput) {
			OWLValue value = getValue(vmInput, input);
			String inputName = vmInput.getStringValue(input);
			Input newInput;
			//logger.info("before if");
			if(inputs.containsKey(input.getURI())) {
				//logger.info("contains");
				newInput = (Input) inputs.get(input.getURI());
			} else {
				//logger.info("nope");
				//Input newInput = (Input) ont.createInput(URIUtils.createURI(baseURI, inputName));
				newInput = ont.createInput(createIdentifier(INPUT));
				newInput.setLabel(input.getURI().toString());
				//logger.info("before param");
				newInput.setParamType(input.getParamType());
				//logger.info("before constant");
				newInput.setConstantValue(value);
				
				if (value.canCastTo(OWLIndividual.class)) {
					OWLIndividual individual = (OWLIndividual) value
							.castTo(OWLIndividual.class);
	//				logger.info("individual::" + individual.toRDF(false, true));
					//newInput.setLabel(newInput.getLabel() + "<IMPORT>" + individual.getOntology().getURI().toString() + "<\\IMPORT>");
					ont.addImport(individual.getOntology());
				} else if (value.isDataValue()) {
					OWLDataValue datavalue = (OWLDataValue) value
							.castTo(OWLDataValue.class);
					//logger.info("value:" + datavalue.toString());
				}
				
				inputs.put(input.getURI(), newInput);
				
			}
			return newInput;
	}

	private Hashtable outputs = new Hashtable();
	
	private Output createOutput(Output output, ValueMap vmOutput) {
			OWLValue value = getValue(vmOutput, output);
			String outputName = vmOutput.getStringValue(output);
			Output newOutput;
			if(outputs.containsKey(output.getURI())) {
				newOutput = (Output) outputs.get(output.getURI());
			} else {

				newOutput = ont.createOutput(createIdentifier(OUTPUT));
				newOutput.setLabel(output.getURI().toString());
				newOutput.setParamType(output.getParamType());
				newOutput.setConstantValue(value);
				if (value.canCastTo(OWLIndividual.class)) {
					OWLIndividual individual = (OWLIndividual) value
							.castTo(OWLIndividual.class);
		//			logger.info("individual::" + individual.toRDF(true, true));
					ont.addImport(individual.getOntology());

				} else if (value.isDataValue()) {
					OWLDataValue datavalue = (OWLDataValue) value
							.castTo(OWLDataValue.class);
					//logger.info("value:" + datavalue.toString());
				}
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
			//System.out.println(process.getURI());
			
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
	 *  Creates a composite process which wrappes all executed processes
	 * 
	 * @version 0.9
	 * @author simonl
	 * 
	 * @param composite process
	 * @param execution records
	 * @return composite process
	 * @throws Exception in case something goes wrong
	 */
	private Process createSequenceProcess(Process oldProcess) {

		if(oldProcess.canCastTo(CompositeProcess.class)) {
			// Composite Process
			CompositeProcess cp = (CompositeProcess) oldProcess.castTo(CompositeProcess.class);
			ExecutionRecord executionrecord = getExecutionRecord(cp);
		
			CompositeProcess newCompositeProcess = ont.createCompositeProcess(createIdentifier(PROCESS));
			newCompositeProcess.setLabel(cp.getURI().toString());
		
			Perform perform = ont.createPerform(createIdentifier(PERFORM));
			perform.setProcess(newCompositeProcess);
			
			Sequence sequence = ont.createSequence(createIdentifier(SEQUENCE));
			newCompositeProcess.setComposedOf(sequence);
		
			InputList il = cp.getInputs(); 
			ValueMap vmInput = (ValueMap) executionrecord.getInput();
	  
			Iterator iterator = il.iterator(); 
			Input input;
			while(iterator.hasNext()) {
				input = (Input) iterator.next();
				Input newInput = createInput(input, vmInput);
				newInput.setProcess(newCompositeProcess);
				
				//performs[i].addBinding(input, prevPerform, newInput);
				// TODO not sure we actually need the parent perform
				// who knows?
				//addInputBindings(cp, input, newCompositeprocess, newInput);
				//perform.addBinding(newInput, Perform.TheParentPerform, input);
			}
			
			OutputList ol = cp.getOutputs();
			ValueMap vmOutput = executionrecord.getOutput();
			Iterator outputI = ol.iterator();
			Output output;
			while(outputI.hasNext()){
				output = (Output) outputI.next();
				OWLValue value = getValue(vmOutput, output);
				Output newOutput = createOutput(output, vmOutput);
				newOutput.setProcess(newCompositeProcess);
				//Result result = ont.createResult(createIdentifier(RESULT));
				//newCompositeProcess.setResult(result);
				//
				// TODO not sure if we need this really  
				//result.addBinding(newOutput, Perform.TheParentPerform, output);
				
			}
			
		    ProcessList pl = getProcesses(cp, false);
		    //System.out.println("processes::" + pl.size());
		    Iterator i = pl.iterator();
		    while(i.hasNext()) {
		    	Process newProcess = createSequenceProcess((Process) i.next());
		    	sequence.addComponent(newProcess.getPerform());
		    }
		    return newCompositeProcess;
			
		} else {
			// Atomic Process
			
			ExecutionRecord executionrecord = getExecutionRecord(oldProcess);
			
			Process newProcess = ont.createAtomicProcess(createIdentifier(PROCESS));
			newProcess.setLabel(oldProcess.getURI().toString());

			Perform newPerform = ont.createPerform(createIdentifier(PERFORM));
			newPerform.setProcess(newProcess);
			
			InputList il = oldProcess.getInputs(); 
			ValueMap vmInput = (ValueMap) executionrecord.getInput();
	  
			Iterator iterator = il.iterator(); 
			Input input;
			while(iterator.hasNext()) {
				input = (Input) iterator.next();
				Input newInput = createInput(input, vmInput);
				newInput.setProcess(newProcess);
			}
			
			OutputList ol = oldProcess.getOutputs();
			ValueMap vmOutput = executionrecord.getOutput();
			Iterator outputI = ol.iterator();
			Output output;
			while(outputI.hasNext()){
				output = (Output) outputI.next();
				Output newOutput = createOutput(output, vmOutput);
				newOutput.setProcess(newProcess);
			}
			
			return newProcess;
		}
	}

	/**
	 * 
	 * Create a label for the composite service based on the labels of services.
	 * Basically return the string [Service1 + Service2 + ... + ServiceN] as the
	 * label
	 * 
	 * @param services
	 *            List of Servie objects
	 * @return
	 * @throws Exception
	 */
	private String createLabel(List executionrecords) throws Exception {
		String label = "[";
		
		ExecutionRecord executionrecord;

		//for (int i = 0; i < processes.size(); i++) {
		for (int i = 0; i < executionrecords.size(); i++) {
			executionrecord = (ExecutionRecord) executionrecords.get(i);
			Process p = executionrecord.getProcess();
//			Process p = (Process) processes.get(i);
			
			// Service s = p.getService();
			// Service s = kb.readService(p.getService().getURI());
			try {
				Service s = p.getService();
				if (i > 0) {
					label += " + ";
				}
				label += s.getLabel();

			} catch (Exception e) {
				//logger.info("no label for process::"+p.toRDF()+"\nError::"+e.toString());
			}
		}

		label += "]";

		return label;
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

	private OWLValue getValue(ValueMap vm, Parameter parameter) {
		try {
			OWLIndividual individual = vm.getIndividualValue(parameter);
			return individual;
		} catch (Exception e) {
			OWLDataValue value = vm.getDataValue(parameter);
			return value;
		}
	}


	public String showExecutionTrail() {
		String s = new String();
		Iterator i = executionrecords.iterator();
		ExecutionRecord er;
		while(i.hasNext()) {
			er = (ExecutionRecord) i.next();
			s += er.toString(); 
		}
		return s;

	}


}
