package ch.unizh.ifi.ddis.cbr;

import java.lang.reflect.Constructor;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;

import ch.unizh.ifi.ddis.cbr.similarity.Similarity;

/*
 * factory to produce the different objects used to retrieve and reuse cases 
 */
public class CBRFactory {
	
	// OWL Knowledge Base
	private static OWLKnowledgeBase kb = null;

	// cache directory for OWL Knowledgebase
	private static String cacheDir = "C:\\Hacks\\workspace\\Ontologies\\ont_cache";

	// different similarity measures
	private static String[] similarities = {"ch.unizh.ifi.ddis.cbr.similarity.SimilaritySemantic", "ch.unizh.ifi.ddis.cbr.similarity.SimilaritySyntactic"};

	public static Similarity[] getSimilarityStrategies() {
		Similarity[] similarityStrategies = new Similarity[similarities.length];
		for(int i = 0; i < similarities.length; i++) {
			try {
				Class clazz = Class.forName(similarities[i]);
				Constructor con = clazz.getConstructor(new Class[]{});
				Similarity similarity = (Similarity) con.newInstance();
				similarityStrategies[i] = similarity;
			} catch (Exception e) {
				// disregard -NoSuchMethodException e, ClassNotFoundException e1
			}
		}
		return similarityStrategies;
	}
	
	public static OWLKnowledgeBase getKB() {
		if(kb == null) {
			kb = OWLFactory.createKB();
			kb.setReasoner("Pellet");
			
		    if(!cacheDir.equals("")) {
		    	kb.getReader().getCache().setLocalCacheDirectory(cacheDir);
		    }
		}
		return kb;
	}
	
}
