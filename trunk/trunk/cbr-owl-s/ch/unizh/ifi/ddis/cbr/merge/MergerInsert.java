package ch.unizh.ifi.ddis.cbr.merge;

import impl.owls.process.ProcessListImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;
import ch.unizh.ifi.ddis.cbr.SimilarityStatic;

public class MergerInsert extends MergerDefault {

	// NIL pointer
    public static final URI NIL = URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil");

	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException {
		doInsertion(oldCase, newCase);
		return getMergedCase();
	}

	
	private void doInsertion(OWLWrapper oldCase, OWLWrapper newCase) {
		ProcessList plNew = newCase.getProcesses();
		Process previousProcess = null;
		Process process = null;
		
		Iterator i = plNew.iterator();
		while(i.hasNext()) {
			process = (Process) i.next();
			if(process.getURI().equals(NIL)) break;
			// add process
			addProcess(process);
			previousProcess = process;
		}
		
		if(previousProcess == null) previousProcess = process;
		
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
		
		// continue scanning through the process list reusing last process if previousProcess was nil
		for(int k = j; k < plOld.size(); k++) {
			process = (Process) plOld.get(k);
			addProcess(process);
		}
		
	}

	
	
}
