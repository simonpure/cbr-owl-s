package ch.unizh.ifi.ddis.cbr.record;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.mindswap.owl.EntityFactory;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owls.OWLSFactory;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;
import org.xml.sax.InputSource;

public class RunDictionary {
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		OWLKnowledgeBase kb = OWLFactory.createKB();

        kb.getReader().getCache().setLocalCacheDirectory( "C://eclipse//Next//ont_cache");
        Service s1;
        try {
			s1 = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/BookFinder.owl#");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 // create an execution engine
		   ProcessExecutionEngine exec = OWLSFactory.createExecutionEngine();
		   ExecutionTrail trail = new ExecutionTrail(kb, exec);
		   
		   exec.addMonitor(new ExecutionMonitor(trail));
		           
		   // create a kb
		  // OWLKnowledgeBase kb = OWLFactory.createKB();
		           
		   // read in the service description
		   Service aService = null;
		try {
			aService = kb.readService("http://www.mindswap.org/2004/owl-s/1.1/Dictionary.owl");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		   // get the process for the server
		   Process aProcess = aService.getProcess();
		   
		   // initialize the input values to be empty
		   ValueMap aInputValueMap = new ValueMap();
		   
		   // specify an input value
		   String inValue = "hello";
		   // set the value in the map
		   aInputValueMap.setDataValue(aProcess.getInput("InputString"), inValue);
		           
		   // run the process
		   ValueMap aOutputValueMap = null;
		try {
			aOutputValueMap = exec.execute(aProcess, aInputValueMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		   
		   // get the output
		   OWLDataValue out = (OWLDataValue) aOutputValueMap.getValue(aProcess.getOutput());

		   // display the value
		   System.err.println("Output = "+out.getValue());

		   trail.createOntologyTrail().write(System.out, trail.getBaseURI());


        
    //	for(int i = 0; i<100; i++) 
    //      	System.out.println("Preference " + i);
		
		System.out.println("Hello World!");

//		System.out.println("Done!");
		
	}
	
}
