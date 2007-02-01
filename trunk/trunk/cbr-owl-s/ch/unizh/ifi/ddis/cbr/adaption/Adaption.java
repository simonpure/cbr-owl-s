package ch.unizh.ifi.ddis.cbr.adaption;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public interface Adaption {
	
	// merges new case with old case
	public OWLWrapper adapt(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException, NoBindingException;

}
