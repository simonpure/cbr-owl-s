package ch.unizh.ifi.ddis.cbr.merge;

import java.net.URI;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public interface Merger {
	
   // NIL pointer
    public static final URI NIL = URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil");

	// merges new case with old case
	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase);

}
