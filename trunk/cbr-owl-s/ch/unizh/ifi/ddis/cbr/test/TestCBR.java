package ch.unizh.ifi.ddis.cbr.test;


import impl.jena.OWLDataTypeImpl;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;

import ch.unizh.ifi.ddis.cbr.OWLSCaseBasedReasoner;
import ch.unizh.ifi.ddis.cbr.OWLWrapper;

public class TestCBR {
	
	public TestCBR() {
		
	}

	/*
	private void createTrailList() {
		OWLKnowledgeBase kb = OWLFactory.createKB();
	    kb.getReader().getCache().setLocalCacheDirectory( "C:\\Hacks\\workspace\\Ontologies\\ont_cache");
		
		String baseURI = "http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/TrailList";

		OWLSCaseBasedReasoner cbr = new OWLSCaseBasedReasoner();
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
	*/
	
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
	
		/*
		Process p1 = s.getProcess();
		ControlConstruct cc = ((CompositeProcess) p1).getComposedOf();
		System.out.println("name::" + cc.getConstructName());
		System.out.println("processes::"+cc.getAllProcesses());
		*/
		//System.exit(0);
		
		
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
		
		Service service = newCase.createService(URI.create(baseURI + "#TestService"));
		Profile profile = newCase.createProfile(URI.create(baseURI + "#TestProfile"));
		//AtomicProcess p = newCase.createAtomicProcess(URI.create(baseURI + "#MyProcess"));
		
		service.setProfile(profile);
		
		CompositeProcess cp = newCase.createCompositeProcess(URI.create(baseURI + "#TestProcess"));
		
		//Perform perform = newCase.createPerform();
		cp.setLabel("Process");
		
		service.setProcess(cp);

		Sequence sequence = newCase.createSequence(URI.create(baseURI + "#TestSequence"));
		cp.setComposedOf(sequence);

		
		//AtomicProcess p = newCase.createAtomicProcess(URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil"));
		AtomicProcess p = newCase.createAtomicProcess(URI.create(baseURI + "#TestProcess1"));
		
		Perform perform = newCase.createPerform(URI.create(baseURI + "#TestPerform"));
		perform.setProcess(p);

		sequence.addComponent(perform);
		
		Input input1 = newCase.createInput(URI.create(baseURI + "#myInput1"));
		input1.setParamType(new OWLDataTypeImpl(XSD.nonNegativeInteger));
		input1.setProcess(p);
		input1.setLabel("Input1");
		//input1.setConstantValue(newCase.createDataValue("3"));

		Input input2 = newCase.createInput(URI.create(baseURI + "#myInput2"));
		input2.setParamType(new OWLDataTypeImpl(XSD.nonNegativeInteger));
		input2.setProcess(p);
		input2.setLabel("Input2");
		//input1.setConstantValue(newCase.createDataValue("3"));

		Output output = newCase.createOutput(URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil"));
		output.setParamType(new OWLDataTypeImpl(URI.create("http://www.daml.org/services/owl-s/1.1/generic/ObjectList.owl#nil")));
		output.setProcess(p);
		output.setLabel("Output");
		
		cp.addInput(input1);
		cp.addInput(input2);
		cp.addOutput(output);
		
		profile.addInput(input1);
		profile.addInput(input2);
		profile.addOutput(output);
		
	    //perform.addBinding(input1, Perform.TheParentPerform, output);
		//perform.setProcess(p);

		newCase.write(System.out);
		

		OWLSCaseBasedReasoner cbr = new OWLSCaseBasedReasoner();
		
/*		
		ArrayList cases = cbr.getCases();
		
		Iterator j = cases.iterator();
		OWLWrapper oldCase;
		while(j.hasNext()) {
			oldCase = (OWLWrapper) j.next();
			System.out.println("process list:::" + oldCase.getURI());
			oldCase.printProcessList();
		}
		
		j = cases.iterator();
		while(j.hasNext()) {
			oldCase = (OWLWrapper) j.next();
			System.out.println("process list::");
			oldCase.printProcessList();
		}
	*/	
		//System.exit(0);
		
		/*
		ArrayList bestCases = cbr.retrieve(newCase);

		System.out.println("# best cases::" + bestCases.size());
		Iterator i = bestCases.iterator();
		OWLWrapper oldCase;
		while(i.hasNext()) {
			oldCase = (OWLWrapper) i.next();
			System.out.println("old case::" + oldCase);
			//new CombinedCase(similarity).getCombinedCase();
			
		}

*/
		
		cbr.reuse(newCase);
		
		
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
