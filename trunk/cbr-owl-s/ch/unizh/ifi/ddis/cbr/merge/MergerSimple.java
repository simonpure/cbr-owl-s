package ch.unizh.ifi.ddis.cbr.merge;

import ch.unizh.ifi.ddis.cbr.OWLWrapper;

/*
 * simply return old case
 */
public class MergerSimple implements Merger {

	public OWLWrapper merge(OWLWrapper oldCase, OWLWrapper newCase) {
		return oldCase;
	}

}
