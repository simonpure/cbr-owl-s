package ch.unizh.ifi.ddis.cbr;

import impl.owls.process.InputListImpl;
import impl.owls.process.ProcessListImpl;

import java.net.URI;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLType;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.process.Process;


public class CombinedCase {
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private OWLWrapper oldCase;
	private OWLWrapper newCase;
	
	private OWLWrapper combinedCase;
	
    // NIL
    private URI NIL = URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil");


	/*
	 * constructor
	 */
	public CombinedCase(OWLWrapper oldCase, OWLWrapper newCase) {
		this.oldCase = oldCase;
		this.newCase = newCase;
	}

	public CombinedCase(OWLSSimilarityMeasure similarity) {
		this.oldCase = similarity.getOldCase();
		this.newCase = similarity.getNewCase();
	}

	public void getCombinedCase() {
		logger.info("combined processes::" + getProcess());
		
	}

	private void createProcesses() {
		
	}
	
	private void createService() {
		
	}
	
	private void createProfile() {
		
	}
	
	private ProcessList getProcess() {
		ProcessList plNew = newCase.getProcesses();
		Process previousProcess = null;
		Process process = null;
		
		Iterator i = plNew.iterator();
		while(i.hasNext()) {
			process = (Process) i.next();
			if(process.getURI().equals(NIL)) break;
			previousProcess = process;
		}
		if(previousProcess == null) previousProcess = process;
		
		// grab inputs & outputs from process we are trying to complete
		InputList ilNew = process.getInputs();
		OutputList olNew = process.getOutputs();
		
		ProcessList plOld = oldCase.getProcesses();
		// find last matching process
		int j;
		for(j = 0; j < plOld.size(); j++) {
			process = (Process) plOld.get(j);
			if(previousProcess.getURI().equals(NIL)) break;
			if(process.getLabel() != null) if(process.getLabel().equals(previousProcess.getURI().toString())) {
				// don't use this process
				j++;
				break;
			}
		}
		Process inputProcess = null;
		Process outputProcess = null;
		ProcessListImpl processes = new ProcessListImpl();
		
		// continue scanning through the process list reusing last process if previousProcess was nil
		for(int k = j; k < plOld.size(); k++) {
			process = (Process) plOld.get(k);
			InputList ilOld = process.getInputs();
			OutputList olOld = process.getOutputs();
			if(SimilarityStatic.compareInputs(ilOld, ilNew) && inputProcess == null) {
				inputProcess = process;
				if(!processes.contains(process)) processes.add(process);
			}
			if(SimilarityStatic.compareOutputs(olOld, olNew) && inputProcess != null) {
				outputProcess = process;
				if(!processes.contains(process)) processes.add(process);
			}
		}
		
		return processes;
		
	}

	
	private ProcessList getProcess1() {
		ProcessList plNew = newCase.getProcesses();
		Process previousProcess = null;
		Process process = null;
		Iterator i = plNew.iterator();
		while(i.hasNext()) {
			process = (Process) i.next();
			if(process.getURI().equals(NIL)) break;
			previousProcess = process;
		}
		if(previousProcess == null) previousProcess = process;
		
		// grab inputs & outputs from process we are trying to complete
		InputList ilNew = process.getInputs();
		OutputList olNew = process.getOutputs();
		
		ProcessList plOld = oldCase.getProcesses();
		Iterator j = plOld.iterator();
		// find last matching process
		while(j.hasNext()) {
			process = (Process) j.next();
			if(previousProcess.getURI().equals(NIL)) break;
			if(process.getLabel() != null) if(process.getLabel().equals(previousProcess.getURI().toString())) break;
		}
		Process inputProcess = null;
		Process outputProcess = null;
		ProcessListImpl processes = new ProcessListImpl();
		while(j.hasNext()) {
			process = (Process) j.next();
			InputList ilOld = process.getInputs();
			OutputList olOld = process.getOutputs();
			if(SimilarityStatic.compareInputs(ilOld, ilNew) && inputProcess == null) {
				inputProcess = process;
				if(!processes.contains(process)) processes.add(process);
			}
			if(SimilarityStatic.compareOutputs(olOld, olNew) && inputProcess != null) {
				outputProcess = process;
				if(!processes.contains(process)) processes.add(process);
			}
		}
		
		return processes;
		
	}
	

	
}
