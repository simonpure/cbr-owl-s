package ch.unizh.ifi.ddis.cbr.adaption;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public class AdaptionCopy extends AdaptionDefault {

	
	public OWLWrapper adapt(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException, NoBindingException {
		
		Process newProcess = newCase.getService().getProcess();
		Process oldProcess = oldCase.getService().getProcess();
		
		addProcess(newProcess);
		addProcess(oldProcess);
		
		return getMergedCase();
	}

}
