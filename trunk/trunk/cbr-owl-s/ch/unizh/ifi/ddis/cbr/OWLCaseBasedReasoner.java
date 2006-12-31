/**
 * 
 */
package ch.unizh.ifi.ddis.cbr;

import impl.owls.process.InputListImpl;
import impl.owls.process.OutputListImpl;
import impl.owls.process.ProcessListImpl;

import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLType;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.service.Service;

import simpack.accessor.string.StringAccessor;
import simpack.api.ISequenceAccessor;
import simpack.api.impl.SimilarityMeasure;
import simpack.exception.InvalidSimilarityMeasureNameException;
import simpack.measure.external.owlsmx.CosineSimilarity;
import simpack.measure.external.owlsmx.ExtendedJaccardMeasure;
import simpack.measure.external.owlsmx.JensenShannonMeasure;
import simpack.measure.graph.GraphIsomorphism;
import simpack.measure.graph.MaxCommonSubgraphIsoValiente;
import simpack.measure.string.AveragedStringMatching;
import simpack.tokenizer.SplittedStringTokenizer;

import examples.Matchmaker.Match;

/**
 * @author simonl
 *
 */
public class OWLCaseBasedReasoner {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private OWLKnowledgeBase kb;
	private ArrayList cases = new ArrayList();

	// cache directory for OWL Knowledgebase
	private String cacheDir = "C:\\Hacks\\workspace\\Ontologies\\ont_cache";
	
	// Trail List URL
	private String trailList = "http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/TrailList";
	
	// default weighting for matching
	//
	private double semanticInputsWeight = 0.6, semanticOutputsWeight = 0.3, semanticProcessWeight = 0.1;
	private double syntacticInputsWeigth = 0.6, syntacticOutputsWeight = 0.3, syntacticProcessWeight = 0.1;
	private double semanticWeight = 0.5, syntacticWeight = 0.25, graphWeight = 0.25;

	/**
	 * Constructor for the CBR system  
	 * @throws URISyntaxException 
	 * @throws FileNotFoundException 
	 */
	public OWLCaseBasedReasoner() throws FileNotFoundException, URISyntaxException {
		init();
	}

	// create OWL Knowledge Base, set reasoner and load trails
	//
	public void init() throws FileNotFoundException, URISyntaxException {
		kb = OWLFactory.createKB();
		kb.setReasoner("Pellet");
		
	    if(!cacheDir.equals("")) {
	    	kb.getReader().getCache().setLocalCacheDirectory(cacheDir);
	    }

		List services = kb.readAllServices(trailList);
		//logger.info("Trails::"+services);
		Service service;
		OWLOntology ont;
		Iterator i = services.iterator();
		while(i.hasNext()) {
			service = (Service) i.next();
			ont = service.getOntology();
			RecordedCase rc = new RecordedCase(ont, kb);
			if(!containsCase(rc)) {
				cases.add(rc);
			}
		}
	    logger.info("Initialized Case Base with " + cases.size());
	}
	
	public void setCacheDir(String cacheDir) {
		this.cacheDir = cacheDir;
	}

	public OWLCaseBasedReasoner(OWLKnowledgeBase kb) {
		this.kb = kb;
	}
	
	public void setKB(OWLKnowledgeBase kb) {
		this.kb = kb;
	}
	
	public OWLKnowledgeBase getKB() {
		return kb;
	}
	
	// allow customization of weights
	public void setWeights(Double semanticWeight, Double syntacticWeight, Double graphWeight) {
		// only set values that are != null
		if(semanticWeight != null) this.semanticWeight = semanticWeight.doubleValue();
		if(syntacticWeight != null) this.syntacticWeight = syntacticWeight.doubleValue();
		if(graphWeight != null) this.graphWeight = graphWeight.doubleValue();
	}
	
	// allow customization of weights	
	public void setSemanticWeights(Double semanticInputsWeight, Double semanticOutputsWeight, Double semanticProcessWeight) {
		if(semanticInputsWeight != null) this.semanticInputsWeight = semanticInputsWeight.doubleValue();
		if(semanticOutputsWeight != null) this.semanticOutputsWeight = semanticOutputsWeight.doubleValue();
		if(semanticProcessWeight != null) this.semanticProcessWeight = semanticProcessWeight.doubleValue();
	}

	// allow customization of weights
	public void setSyntacticWeights(Double syntacticInputsWeigth, Double syntacticOutputsWeight, Double syntacticProcessWeight) {
		if(syntacticInputsWeigth != null) this.syntacticInputsWeigth = syntacticInputsWeigth.doubleValue();
		if(syntacticOutputsWeight != null) this.syntacticOutputsWeight = syntacticOutputsWeight.doubleValue();
		if(syntacticProcessWeight != null) this.syntacticProcessWeight = syntacticProcessWeight.doubleValue();
	}
	
	public ArrayList getSimilar(OWLOntology ont) throws FileNotFoundException, URISyntaxException {
		ArrayList bestCases = new ArrayList();
		
		logger.info("getSimilar");
		
		// load ontology into the kb
		kb.load(ont);
		
		RecordedCase newCase = new RecordedCase(ont);
		
		//logger.info(newCase.getGraph().getNodeSet());
		
		RecordedCase oldCase;
		Iterator i = cases.iterator();
		OWLSSimilarityMeasure similarity;
		while(i.hasNext()) {
			oldCase = (RecordedCase) i.next();
			similarity = compare(newCase, oldCase);
			bestCases.add(similarity);
			logger.info("match:: " + similarity.getSimilarity() + "::size::" + bestCases.size());
		}
		
		return bestCases;
	}
	
	private OWLSSimilarityMeasure compare(RecordedCase newCase, RecordedCase oldCase) {
		logger.info("compare\n new::" + newCase + "\nto old::\n" + oldCase);
		
		OWLSSimilarityMeasure similarity = new OWLSSimilarityMeasure(oldCase, newCase);
		similarity.calculate();
		
		return similarity;
	}

	/*
	 * helper method to avoid duplicates as the kb returns somehow duplicates
	 * 
	 */
	private boolean containsCase(RecordedCase rc) {
		Iterator i = cases.iterator();
		RecordedCase temp;
		while(i.hasNext()) {
			temp = (RecordedCase) i.next();
			if(temp.getURI().equals(rc.getURI())) return true;
		}
		return false;
	}
	
    // insertion sort
    // 
	private Hashtable rankHashtable(Hashtable hashtable) {
		//ArrayList a = new ArrayList();
		Hashtable rankedHashtable = new Hashtable();
		System.out.println("rank hashtable::" + hashtable.size());
		
		//a.addAll(hashtable.keySet());
		
		int [] a = new int[hashtable.size()];
		int x = 0;
		for (Enumeration i = hashtable.keys(); i.hasMoreElements() ;) {
			Integer match = (Integer) i.nextElement();
			a[x++] = match.intValue();
		}
		
		for(int i = 1; i < a.length; i++) {
			int j = i;
			int temp = a[j];
			while (j>0 && a[j-1] > temp) {
				a[j] = a[j-1];
				j--;
			}
			a[j] = temp;
		}
		
		for(int i = 0; i<a.length; i++) {
			Integer match = new Integer(a[i]);
			RecordedCase rc = (RecordedCase) hashtable.get(match);
			rankedHashtable.put(match, rc);
		}
		
		return rankedHashtable;
	}

}
