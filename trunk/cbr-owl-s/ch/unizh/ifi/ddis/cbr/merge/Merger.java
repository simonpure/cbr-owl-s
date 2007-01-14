package ch.unizh.ifi.ddis.cbr.merge;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public interface Merger {
	
	// merges new case with old case
	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase) throws FileNotFoundException, URISyntaxException;

}
