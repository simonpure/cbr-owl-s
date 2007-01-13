/*
 * Created on 16.04.2005
 */
package ch.unizh.ifi.ddis.cbr.record;

import impl.jena.OWLDataTypeImpl;

import java.net.URI;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.vocabulary.XSD;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.JavaAtomicGrounding;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.URIUtils;


/**
 * <p>
 * This example shows how to create a service grounded to a simple Java method. 
 * A sample service is generated and a grounding is created, which matches to
 * the following method call:
 * <br>
 * 		<pre>public String testIt(int i, Double y)</pre>
 * </p>
 * 
 * @author Michael Daenzer, University of Zurich
 */
public class CreateJavaGrounding {
	private ExecutionTrail trail;
    public final URI baseURI = URI.create("http://www.example.org/BookPrice.owl#");
	

	public static void main(String[] args) throws Exception {
		CreateJavaGrounding test = new CreateJavaGrounding();
		test.run();
	}
	
	public void run() throws Exception  {
		String baseURL = "http://www.ifi.unizh.ch/ddis/ont/owl-s/MyTestService.owl";
		URI baseURI = URI.create(baseURL + "#");
		
		OWLKnowledgeBase kb = OWLFactory.createKB();
	    kb.getReader().getCache().setLocalCacheDirectory( "C:\\Hacks\\workspace\\Ontologies\\ont_cache");

		//trial.setKB(kb);
		OWLOntology ont = kb.createOntology(true);
		//OWLOntology ont = OWLFactory.createOntology(URI.create(baseURI));
		
		Service service = ont.createService(URIUtils.createURI(baseURI, "MyService"));
		Profile profile = ont.createProfile(URIUtils.createURI(baseURI, "MyProfile"));
		
		AtomicProcess process = ont.createAtomicProcess(URIUtils.createURI(baseURI, "MyProcess"));
		Perform perform = ont.createPerform();
		
		service.setProfile(profile);
		service.setProcess(process);
//		process.setService(service);
		
		Input input1 = ont.createInput(URIUtils.createURI(baseURI, "myInput1"));
		input1.setParamType(new OWLDataTypeImpl(XSD.nonNegativeInteger));
		input1.setProcess(process);
		input1.setLabel("Input1");

		Input input2 = ont.createInput(URIUtils.createURI(baseURI, "myInput2"));
		input2.setParamType(new OWLDataTypeImpl(XSD.nonNegativeInteger));
		input2.setProcess(process);
		input2.setLabel("Input2");
		
		Output output = ont.createOutput(URIUtils.createURI(baseURI, "myOutput")); 
		output.setParamType(new OWLDataTypeImpl(XSD.nonNegativeInteger));
		output.setProcess(process);
		output.setLabel("Result");
		
	    perform.addBinding(input1, Perform.TheParentPerform, output);
	    perform.addBinding(input2, Perform.TheParentPerform, output);
		perform.setProcess(process);

		profile.addInput(input1);
		profile.addInput(input2);
		profile.addOutput(output);
		
		JavaAtomicGrounding jAtomicGround = ont.createJavaAtomicGrounding(URIUtils.createURI(baseURI, "MyJAtomGround"));
		jAtomicGround.setOutputVar(baseURI + "JPar1", "java.lang.String", output);
		jAtomicGround.setInputVar(baseURI + "JIn1", "int", 1, input1);
		jAtomicGround.setInputVar(baseURI + "JIn2", "java.lang.Double", 2, input2);
		jAtomicGround.setClaz("examples.CreateJavaGrounding");
		jAtomicGround.setMethod("testIt");
		jAtomicGround.setProcess(process);
			
		Grounding jGrounding = ont.createJavaGrounding(URIUtils.createURI(baseURI, "MyJGrounding"));
		jGrounding.addGrounding(jAtomicGround);
		jGrounding.setService(service);
		
		ont.write(System.out, baseURI);
		Service s = process.getService();
		System.out.println(s.getURI());
		
		System.out.println();
		System.out.println( "Executing service:" );
		
		ValueMap values = new ValueMap();
		ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		
		// get the parameter using the local name
		values.setDataValue(process.getInput("myInput1"), "2");		
		values.setDataValue(process.getInput("myInput2"), "3");
		
		exec.setKB(kb);
//		trial.setExec(exec);
		trail = new ExecutionTrail(kb, exec);
		
		exec.addMonitor(new ExecutionMonitor(trail));
		
		values = exec.execute(process, values);
		
		System.out.println(trail.showExecutionTrail());
        trail.createOntologyTrail().write(System.out, trail.getBaseURI());

	}
	
	public String testIt(int i, Double y) throws Exception {
		// wait some time to show interruption feature
		//Thread.sleep(8000);
		// calc some value
        double s = i * y.doubleValue();
    	// something to show correct invocation
    	System.out.println("FirstParameter * SecondParameter = " + s);
    	// provoke exception
    	//s = Double.parseDouble("ThisThrowsAnException");
    	return new String("Return value of JavaGrounding " + s);
    }
}
