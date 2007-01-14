package ch.unizh.ifi.ddis.cbr.merge;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

/*
 * simply return old case
 */
public class MergerSimple extends MergerDefault {

	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException {
		Process process = oldCase.getService().getProcess();
		System.out.println("simple::old process::" + process);
		addProcess(process);
		
		return getMergedCase();
	}

}
