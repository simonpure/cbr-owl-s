package ch.unizh.ifi.ddis.cbr.merge;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public class MergerCopy extends MergerDefault {

	
	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException {
		
		Process newProcess = newCase.getService().getProcess();
		Process oldProcess = oldCase.getService().getProcess();
		
		addProcess(newProcess);
		addProcess(oldProcess);
		
		return getMergedCase();
	}

}
