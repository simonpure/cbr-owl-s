package ch.unizh.ifi.ddis.cbr.adaption;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

/*
 * simply return old case
 */
public class AdaptionSimple extends AdaptionDefault {

	public OWLWrapper adapt(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException, NoBindingException {
		Process process = oldCase.getService().getProcess();
		addProcess(process);
		
		return getMergedCase();
	}

}
