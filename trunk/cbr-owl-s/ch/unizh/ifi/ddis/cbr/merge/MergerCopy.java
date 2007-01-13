package ch.unizh.ifi.ddis.cbr.merge;

import impl.owls.process.ProcessListImpl;

import java.util.Iterator;

import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;
import ch.unizh.ifi.ddis.cbr.SimilarityStatic;

public class MergerCopy extends MergerDefault {

	
	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase) {
		this.oldCase = oldCase;
		this.newCase = newCase;
		
		ProcessList newPL = newCase.getProcesses();
		ProcessList oldPL = oldCase.getProcesses();
		
		// TODO Auto-generated method stub
		return null;
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
}
