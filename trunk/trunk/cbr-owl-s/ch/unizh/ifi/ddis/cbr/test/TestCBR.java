package ch.unizh.ifi.ddis.cbr.test;


import impl.jena.OWLDataTypeImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.service.Service;

import ch.unizh.ifi.ddis.cbr.CBRKnowledgeBase;
import ch.unizh.ifi.ddis.cbr.OWLCaseBasedReasoner;
import ch.unizh.ifi.ddis.cbr.OWLSSimilarityMeasure;
import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public class TestCBR {
	
	public TestCBR() {
		
	}

	private void createTrailList() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
	    kb.getReader().getCache().setLocalCacheDirectory( "C:\\Hacks\\workspace\\Ontologies\\ont_cache");
		
		String baseURI = "http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/TrailList";

		OWLCaseBasedReasoner cbr = new OWLCaseBasedReasoner();
		OWLOntology newCase = kb.createOntology(URI.create(baseURI));
		
		OWLOntology ont;
		ArrayList trails = new ArrayList();
		trails.add("http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/ExecutionTrail-1167165220043.owl#");
		trails.add("http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/ExecutionTrail-1167215985364.owl");
		
		Iterator i = trails.iterator();
		String trail;
		while(i.hasNext()) {
			trail = (String) i.next();
			try {
				ont = kb.read(trail);
				newCase.addImport(ont);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		newCase.write(System.out);
		Set ontologies = newCase.getImports();
		System.out.println(ontologies);		

	}
	private void run() throws FileNotFoundException, URISyntaxException {
		
		OWLKnowledgeBase kb = OWLFactory.createKB();
	    kb.getReader().getCache().setLocalCacheDirectory( "C:\\Hacks\\workspace\\Ontologies\\ont_cache");
		
		OWLOntology ont = null;
		Service s = null;
		try {
			//s = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/AmazonBookPrice.owl");
			//s = kb.readService("http://www.example.org/FindCheaperBook.owl");
			//s = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/BookPrice.owl");
			//s = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/Dictionary.owl");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
//		newCase = s.getOntology();
		/*
		System.out.println("ont URI::"+newCase.getURI());
		System.out.println("service URI::"+s.getURI());
		System.out.println("service ont URI::"+newCase.getService().getURI());
		System.out.println(newCase.getService(newCase.getURI()));
		System.out.println(newCase.getBaseOntology());
		
		newCase.write(System.out);
		*/
		
		//System.out.println(s.toRDF());
		
		//Service s = ont.getService();
	//	Process p = s.getProcess();
	//	Input i = p.getInput();
		
//		Input newInput = newCase.createInput(URI.create(baseURI + "#Input"));
//		newInput.setParamType(i.getParamType());

//		System.out.println("figger::"+ont.getURI());
//		ont.write(System.out);
		
		String baseURI = "http://www.example.org/Test.owl";
		OWLOntology newCase = kb.createOntology(URI.create(baseURI));
		
		Service service = newCase.createService(URI.create(baseURI + "#TrailService"));
		AtomicProcess p = newCase.createAtomicProcess(URI.create(baseURI + "#MyProcess"));
		Perform perform = newCase.createPerform();

		service.setProcess(p);
		
		Input input1 = newCase.createInput(URI.create(baseURI + "#myInput1"));
		input1.setParamType(new OWLDataTypeImpl(XSD.nonNegativeInteger));
		input1.setProcess(p);
		input1.setLabel("Input1");
		
	    perform.addBinding(input1, Perform.TheParentPerform, input1);
		perform.setProcess(p);


		newCase.write(System.out);
		

		OWLCaseBasedReasoner cbr = new OWLCaseBasedReasoner();
		ArrayList bestCases = cbr.getSimilar(newCase);

		System.out.println("# best cases::" + bestCases.size());
		Iterator i = bestCases.iterator();
		OWLSSimilarityMeasure similarity;
		while(i.hasNext()) {
			similarity = (OWLSSimilarityMeasure) i.next();
			System.out.println("match::" + similarity.getSimilarity());
			System.out.println("case::" + similarity.getOldCase());
			
		}
		
/*
 
 			System.out.println("-------------");
			System.out.println("input names::");
			System.out.println(rc.getInputNames());
			System.out.println("-------------");
			
 		
		//cbr.getKB().getReader().getCache().setLocalCacheDirectory( "C:\\Hacks\\workspace\\Ontologies\\ont_cache");
		
		Set ontologies = kb.getOntologies();
		
		System.out.println("size::"+ontologies.size());
		OWLOntology ont;
		Iterator i = ontologies.iterator();
		while(i.hasNext()) {
			ont = (OWLOntology) i.next();
			//ont.write(System.out);
			try {
				kb.readAllServices(ont.getURI());
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(ont.getURI());
		}
		
		ontologies = kb.getOntologies();
		System.out.println("size::"+ontologies.size());
		i = ontologies.iterator();
		while(i.hasNext()) {
			ont = (OWLOntology) i.next();
			//ont.write(System.out);
			System.out.println(ont.getURI());
		}
		
		
		System.out.println(kb.getOntologies());
	*/
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TestCBR test = new TestCBR();
		try {
			test.run();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
