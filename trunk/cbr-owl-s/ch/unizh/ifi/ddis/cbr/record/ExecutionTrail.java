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
import impl.owls.process.OutputImpl;
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
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.swrl.Variable;
import org.mindswap.utils.URIUtils;

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
		//	logger.info("ER::"+executionrecord.toString());
			if(executionrecord.getProcess().equals(p)) {
			//	logger.info("match!");
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
			System.out.println("create ont" + e.toString());
		}
		ExecutionRecord er = (ExecutionRecord) executionrecords.get(0);
		Process p = er.getProcess();

//		OWLOntology base = kb.read(p.getOntology().getFileURI());
//		logger.info(base);
//		ont.addImport(base);
		
		Service service = ont.createService(URIUtils.createURI(baseURI, TRAILSERVICE));
		CompositeProcess process = ont.createCompositeProcess(URIUtils.createURI(baseURI, TRAILPROCESS));
		Profile profile = ont.createProfile(URIUtils.createURI(baseURI, TRAILPROFILE));
		
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
		
		
		
		//logger.info("Ontology::" + p.getOntology().getURI());
		
		// we don't need a grounding
		// Grounding grounding = ont.createGrounding(URIUtils.createURI(baseURI,
		// "TestGrounding"));
		
		// set profile and process of the trail
		service.setProfile(profile);
		service.setProcess(process);

		// service.setGrounding(grounding);

		//logger.info("sequence starting");
//		createSequenceProcess(process, processes);
		process = createSequenceProcess(process, executionrecords);

		//logger.info("sequence ending");

		//logger.info("profile starting");
		createProfile(profile, process);
		//logger.info("profile ending");

		//logger.info("label starting");
//		profile.setLabel(createLabel(processes));
		profile.setLabel(createLabel(executionrecords));

		//logger.info("label ending");

		profile.setTextDescription(profile.getLabel());

		service.setProfile(profile);
		service.setProcess(process);
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


	/*
	 * get control constructs
	 */
	private ProcessList getConstructs(ControlConstruct cc) {
		if(cc instanceof Sequence) {
			return cc.getAllProcesses(true);
		}
		
		ProcessListImpl temp = new ProcessListImpl();

		List constructs = cc.getConstructs();
		//logger.info(constructs + " size::" + constructs.size());
		ControlConstruct controlconstruct;
		Iterator iterator = constructs.iterator();
		while(iterator.hasNext()) {
			controlconstruct = (ControlConstruct) iterator.next();
			temp.addAll(getConstructs(controlconstruct));
		}
		return temp;
	}


	/**
	 *  Creates a composite process which wrappes all executed processes
	 * 
	 * @version 0.5
	 * @author simonl
	 * 
	 * @param composite process
	 * @param execution records
	 * @return composite process
	 * @throws Exception in case something goes wrong
	 */
	private CompositeProcess createSequenceProcess(CompositeProcess compositeProcess, List executionrecords) throws Exception {
		//logger.info("sequence entered");
		Sequence sequence = ont.createSequence(createIdentifier(SEQUENCE));
		compositeProcess.setComposedOf(sequence);
//		logger.info("prepared - name" + compositeProcess.toRDF());

		Perform[] performs = new Perform[getAtomicProcessSize()];
		
		Process p;
		ExecutionRecord executionrecord;
		ExecutionRecord prevExecutionrecord = null;
		for (int i = 0; i < executionrecords.size(); i++) {
			
		//	logger.info("i::" + i + "size::" +  executionrecords.size());
			//p = (Process) processes.get(i);
			executionrecord = (ExecutionRecord) executionrecords.get(i);
		//	logger.info("execution record");
			
			if(!executionrecord.isUsed()) {
				
			executionrecord.setUsed(true);
			//p = getAtomicProcess(i);
			
		//	logger.info("compsite::" + executionrecord.isComposite());
			
			if(executionrecord.isComposite()) {
				CompositeProcess cp = (CompositeProcess) executionrecord.getProcess();
				ControlConstruct cc = cp.getComposedOf();
				
				//String processName = cp.getLocalName().toString();
//				logger.info("name::"+processName);
//				CompositeProcess newCompositeProcess = ont.createCompositeProcess(URIUtils.createURI(baseURI, processName));
				CompositeProcess newCompositeProcess = ont.createCompositeProcess(createIdentifier(PROCESS));
				
				// set the URI of the original composite process to reuse the original ontology
			//	logger.info("composite process URI" + cp.getURI().toString());
				newCompositeProcess.setLabel(cp.getURI().toString());
				
				// add composite process to composite process
				Perform perform = ont.createPerform(createIdentifier(PERFORM));
				perform.setProcess(newCompositeProcess);
				sequence.addComponent(perform);
				
				InputList il = cp.getInputs(); 
				ValueMap vmInput = (ValueMap) executionrecord.getInput();
			  
				Iterator iterator = il.iterator(); 
				Input input;
				while(iterator.hasNext()) {
					input = (Input) iterator.next();
					Input newInput = createInput(input, vmInput);
					newInput.setProcess(compositeProcess);
					//performs[i].addBinding(input, prevPerform, newInput);
					// TODO not sure we actually need the parent perform
					// who knows?
					perform.addBinding(newInput, Perform.TheParentPerform, input);
				 }

				 OutputList ol = cp.getOutputs();
				 ValueMap vmOutput = executionrecord.getOutput();
				 Iterator outputI = ol.iterator();
				 Output output;
				 while(outputI.hasNext()){
					 output = (Output) outputI.next();
					 OWLValue value = getValue(vmOutput, output);
					 Output newOutput = createOutput(output, vmOutput);
					 newOutput.setProcess(compositeProcess);
					 Result result = ont.createResult(createIdentifier(RESULT));
					 //
					 // TODO not sure if we need this really  
					 // 
					 result.addBinding(newOutput, Perform.TheParentPerform, output);
					 compositeProcess.setResult(result);
				 }
				 
			//	logger.info("construct::" + cc.getConstructName());
			//	logger.info("constructs::" + cc.getConstructs());
				
				ProcessList pl = null;
				//List constructs = cc.getConstructs();
				pl = getConstructs(cc);
				
				// ProcessList pl = cc.getAllProcesses(true);
//				 logger.info("processlist::"+pl.toString());
				 List executionrecordsCompositeProcess = getExecutionRecords(pl);
//				 logger.info("recursive");
				 if(executionrecordsCompositeProcess.size()>0) {
					 newCompositeProcess = createSequenceProcess(newCompositeProcess, executionrecordsCompositeProcess);
				 }
				
			} else if (executionrecord.isAtomic()) {
				
				p = executionrecord.getProcess();
				
				// save original service and description
				// should always be the same
				//logger.info(p.getService());
				//saveService = p.getService().getURI().toString();
				//saveProfile = p.getProfile().getURI().toString();
				
				Process process = ont.createAtomicProcess(createIdentifier(PROCESS));
				//Process process = ont.createAtomicProcess(p.getURI());
				process.setLabel(p.getURI().toString());
			//	logger.info(p.getURI().toString());
						
				performs[i] = ont.createPerform(createIdentifier(PERFORM));
			//
			// save Perform
			//	Perform performProcess = p.getPerform();
//logger.info(performProcess);

			// *BUG*
			// overrides getPerform().getBindings()
			// TODO fix setProcess to not override Perform
			//performs[i].setProcess(p);
			
				//TODO new process test
				performs[i].setProcess(process);

			// reset Perform
			/*try {
				p.setPerform(performProcess);
			} catch (Exception e) {
				// no perform
			}
*/
				sequence.addComponent(performs[i]);

				InputList il = p.getInputs(); 
//			 ValueMap vmInput = (ValueMap) inputs.get(i);
				ValueMap vmInput = (ValueMap) executionrecord.getInput();
		  
				Iterator iterator = il.iterator(); 
				Input input;
			  //Output output;
				while(iterator.hasNext()) {
		//			logger.info("input");
					input = (Input) iterator.next();
		//			logger.info("newinput" + input);
					Input newInput = createInput(input, vmInput);
		//			logger.info("newinput" + newInput);
					// TODO new process test
					newInput.setProcess(process);
					
				//	logger.info("param::"+performProcess.getBindingFor(input).getParameter());
			//		logger.info("param type::"+performProcess.getBindingFor(input).getParameter().getType());
					//logger.info("perform::"+performProcess.toRDF());
					//logger.info(performProcess.getBindingFor(input).toRDF());
//					performs[i].addBinding(input, prevPerform, performProcess.getBindingFor(input).getParameter());

					// TODO test binding
					performs[i].addBinding(newInput, performs[i], input);
					
						//performs[i].addBinding(input, performs[i], newInput);
						//	performs[i].addBinding(newInput, performs[i], performProcess.getBindingFor(input).getParameter());
		//			logger.info("before if");

					if(i == 0) {
						//newInput = (Input) ont.createInput(URIUtils.createURI(baseURI, inputName));
						newInput = createInput(input, vmInput);
						newInput.setProcess(compositeProcess);
						performs[0].addBinding(newInput, Perform.TheParentPerform,	input);
					}

					//performs[i].getBindingFor(input).setValue((ParameterValue) value);
		//			logger.info("prev perf::"+prevPerform);
			  }
				
//			  logger.info("end inputs");
			  
				OutputList ol = p.getOutputs();

				ValueMap vmOutput = executionrecord.getOutput();
			 
		//		logger.info("output size::"+ol.size());

				Iterator outputI = ol.iterator();
				Output output;
				while(outputI.hasNext()){
					output = (Output) outputI.next();
					Output newOutput = createOutput(output, vmOutput);
		//			logger.info("new Output?");
					//newOutput.setProcess(compositeProcess);
					// TODO new process test
					newOutput.setProcess(process);
					// newOutput.addProperty()
					// performs[i].addBinding(newOutput, prevPerform, performProcess.getBindingFor(input).getParameter());

					Result result = ont.createResult(createIdentifier(RESULT));
					result.addBinding(newOutput, performs[i],output);
					//
					// TODO where to add result to?
					//
					//compositeProcess.setResult(result);
					//p.setResult(result);
					process.setResult(result);
					logger.info("performs.length::" + performs.length);	
					if(i == (performs.length-1)) {
						newOutput = createOutput(output, vmOutput);
						
						newOutput.setProcess(compositeProcess);
						result = ont.createResult(createIdentifier(RESULT));
						result.addBinding(newOutput, Perform.TheParentPerform, output);
						compositeProcess.setResult(result);
					}
				 }
		//	  logger.info("done output");
	//		 } // end if
				//prevProcess = p;
				prevExecutionrecord = executionrecord;
		//		logger.info("done atomic process");
			} // end atomic process
			} // end isUsed
		} // end for
		
		//logger.info("for done");
		//logger.info("composite p::"+compositeProcess.debugString());
		return compositeProcess;
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
		logger.info("inputs size profile::"+process.getInputs().size());
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
