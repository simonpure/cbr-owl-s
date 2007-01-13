package ch.unizh.ifi.ddis.cbr.record;

import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;
import org.mindswap.utils.Utils;
import org.mindswap.wsdl.WSDLOperation;
import org.mindswap.wsdl.WSDLService;


public class RunServiceSimple {
    Service service;
    Profile profile;
    Process process;
    WSDLService s;
    WSDLOperation op;
    String inValue;
    String outValue;
    ValueMap values;
    ProcessExecutionEngine exec;
    ExecutionTrail trail;

    public static void main(String[] args) throws Exception {
        RunServiceSimple test = new RunServiceSimple();
	       try {
	           test.runBookFinder();
	        } catch(Exception e) {
	        	System.out.println(e.toString());
	        }
    }
	public RunServiceSimple() {
	       // create an execution engine
        exec = OWLSFactory.createExecutionEngine();

        // Attach a listener to the execution engine
     //   exec.addMonitor(new DefaultProcessMonitor());	
	}
	   public void runBookFinder() throws Exception {
	        OWLKnowledgeBase kb = OWLFactory.createKB();
	        	        
	        kb.getReader().getCache().setLocalCacheDirectory( "C:\\Hacks\\workspace\\Ontologies\\ont_cache");
	        
	        trail = new ExecutionTrail(kb);
	        exec.addMonitor(new ExecutionMonitor(trail));
	        
	        System.out.println("start reading");

	        // read the service description
	        service = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/BookFinder.owl");
	        process = service.getProcess();

	        System.out.println("done reading");
	        
	        // initialize the input values to be empty
	        values = new ValueMap();

	        // use any book name
	        inValue = "City of Glass";

	        // get the parameter using the local name
	        values.setDataValue(process.getInput("BookName"), inValue);
	        values = exec.execute(process, values);

	        // get the output param using the index
	        OWLIndividual out = values.getIndividualValue(process.getOutput());

	        // do something with output
	        System.out.println(Utils.formatRDF(out.toRDF()));
	        
	        System.out.println(trail.showExecutionTrail());
	        trail.createOntologyTrail().write(System.out, trail.getBaseURI());

	    }

}
