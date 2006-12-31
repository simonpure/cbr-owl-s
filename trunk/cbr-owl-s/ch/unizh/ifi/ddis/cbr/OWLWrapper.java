/**
 * Represents a recorded execution by the OWL-S API using the modified process monitor (Execution Trail)
 * 
 * This will help facilitate the matching process to select the best matching cases
 * 
 */
package ch.unizh.ifi.ddis.cbr;

import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.ParameterList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;

import impl.owls.process.InputListImpl;
import impl.owls.process.OutputListImpl;
import impl.owls.process.ParameterListImpl;
import impl.owls.process.ProcessListImpl;
import org.mindswap.owls.process.ProcessList;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ValueMap;

/**
 * @author simonl
 *
 */
public class OWLWrapper {

	private Logger logger = Logger.getLogger(this.getClass().getName());
	private OWLOntology ont;
	private OWLKnowledgeBase kb;
	private boolean isTrail;
	private OWLSGraph graph;
	
	private static final String TRAILPROCESS = "ExecutionTrailProcess", TRAILSERVICE = "ExecutionTrailService", TRAILPROFILE = "ExecutionTrailProfile";
	private static final String TRAILURL = "http://www.ifi.unizh.ch/ddis/ont/owl-s/trails/";

	public OWLWrapper(OWLOntology ont) throws FileNotFoundException, URISyntaxException {
		this.ont = ont;
		isTrail = (ont.getURI().toString().indexOf(TRAILURL)==0);
		populateGraph();
		//System.out.println(ont.getURI());
		//ont.write(System.out);
	}

	public OWLWrapper(OWLOntology ont, OWLKnowledgeBase kb) throws FileNotFoundException, URISyntaxException {
		this.ont = ont;
		this.kb = kb;
		isTrail = (ont.getURI().toString().indexOf(TRAILURL)==0);
		populateGraph();
		//System.out.println(ont.getURI());
		//ont.write(System.out);
	}
	
	private void populateGraph() throws FileNotFoundException, URISyntaxException {
		//System.out.println(ont.getURI());
		//logger.info("ont.URI:: "+ont.getURI());
		Service service;
		//logger.info("service URI:: " + service + "ont URI:: " + ont.getURI());
		if(isTrail) {
			service = ont.getService();
			String uri = service.getLabel();
			//logger.info("service::" + uri);
			service = kb.readService(uri);
		} else {
			service = ont.getService(getServiceURI());
		}
		//logger.info("service URI:: " + service);
		graph = new OWLSGraph(service);
	}
	
	public void setKB(OWLKnowledgeBase kb) {
		this.kb = kb;
	}

	// process methods
	//
	public int getNumberOfProcesses() {
		ProcessList processes = getProcesses();
		return processes.size();
	}

	public String [] getProcessNamesLabel() {
		ProcessList processes = getProcesses();
		String [] names = new String [processes.size()];
		Iterator i = processes.iterator();
		int j = 0;
		Process process;
		String name;
		while(i.hasNext()) {
			process = (Process) i.next();
			if(isTrail) {
				name = process.getLabel();
			} else {
				name = process.getURI().toString();
			}
			names[j] = name; 
		}
		return names;
	}

	public ArrayList getProcessNames() {
		String [] processes = getProcessNamesLabel();
		return prepareStrings(processes);
	}

	public ProcessList getProcesses() {
		//ont.write(System.out);
		//System.out.println("Ont URI::"+ont.getBaseOntology());
		
		// TODO bug in OWL-S API? -> nope! multiple services associated with ontology...
		// returns same reference to service if not explicitly specified
		//Service service;
		//System.out.println("Trail match::" + ont.getURI().toString().indexOf(TRAILURL) + "\n" + TRAILURL +"\n" + ont.getURI().toString());
		/*
		if(isTrail) {
			// trail
			String serviceURI = ont.getURI().toString() + "#" + TRAILSERVICE;
			//System.out.println(serviceURI);
			service = ont.getService(URI.create(serviceURI));
		} else {
			// new case
			service = ont.getService(getServiceURI());
		} 
		*/
		
		Service service = ont.getService(getServiceURI());
		//service = ont.getService(ont.getURI());
		//System.out.println("service URI::"+service.getURI());
		//logger.info("service URI::"+service.getURI());
		
		Process process = service.getProcess();
		ProcessListImpl processes = new ProcessListImpl();
		processes.add(process);
		ProcessList pl = getAllProcesses(processes);
		return pl;

	}

	private ProcessList getAllProcesses(ProcessList pl) {
		ProcessListImpl temp = new ProcessListImpl();
		Iterator i = pl.iterator();
		Process process;
		while(i.hasNext()) {
			process = (Process) i.next();
			//System.out.println(process.getURI());
			
			temp.add(process);
			if (process.canCastTo(CompositeProcess.class)) {
				CompositeProcess cp = (CompositeProcess) process.castTo(CompositeProcess.class);
				ControlConstruct cc = cp.getComposedOf();
				pl = (ProcessListImpl) cc.getAllProcesses(true);
				temp.addAll(getAllProcesses(pl));
			}
		}
		return temp;
	}

	// input methods
	//
	
	public int getNumberOfInputs() {
		InputList inputs = getInputs();
		return inputs.size();
	}

	private String [] getInputNamesURI() {
		InputList inputs = getInputs();
		String [] names = new String [inputs.size()];
		Iterator i = inputs.iterator();
		int j = 0;
		Input input;
		String name;
		while(i.hasNext()) {
			input = (Input) i.next();
			if(isTrail) {
				name = input.getLabel();
			} else {
				name = input.getURI().toString();
			}
			names[j] = name; 
		}
		return names;
	}

	public ArrayList getInputNames() {
		String [] inputs = getInputNamesURI();
		return prepareStrings(inputs);
	}

	public InputList getInputs() {
		ProcessList pl = getProcesses();
		Iterator i = pl.iterator();
		Process process;
		InputListImpl il = new InputListImpl();
		while(i.hasNext()) {
			process = (Process) i.next();
			getAllInputs(process, il);
			//il.addAll(getAllInputs(process));
		}
		return (InputList) il;
	}

	private void getAllInputs(Process process, InputList temp) {
		InputList il = process.getInputs();
		Iterator i = il.iterator();
		Input input;
		while(i.hasNext()) {
			input = (Input) i.next();
			if(!temp.contains(input)) {
				temp.add(input);
			}
		}
	}

	// output methods
	//
	
	public int getNumberOfOutputs() {
		OutputList outputs = getOutputs();
		return outputs.size();
	}

	public String [] getOutputNamesLabel() {
		OutputList outputs = getOutputs();
		String [] names = new String [outputs.size()];
		Iterator i = outputs.iterator();
		int j = 0;
		Output output;
		String name;
		while(i.hasNext()) {
			output = (Output) i.next();
			if(isTrail) {
				name = output.getLabel();
			} else {
				name = output.getURI().toString();
			}
			names[j] = name; 
		}
		return names;
	}

	public ArrayList getOutputNames() {
		String [] outputs = getOutputNamesLabel();
		return prepareStrings(outputs);
	}
	
	public OutputList getOutputs() {
		ProcessList pl = getProcesses();
		Iterator i = pl.iterator();
		Process process;
		OutputListImpl ol = new OutputListImpl();
		while(i.hasNext()) {
			process = (Process) i.next();
			getAllOutputs(process, ol);
		}
		return (OutputList) ol;
		
	}
	
	private void getAllOutputs(Process process, OutputList temp) {
		OutputList ol = process.getOutputs();
		Iterator i = ol.iterator();
		Output output;
		while(i.hasNext()) {
			output = (Output) i.next();
			if(!temp.contains(output)) {
				temp.add(output);
			}
		}
	}

	
	private ParameterList dedup(ParameterList pl) {
		ParameterListImpl temp = new ParameterListImpl();
		Parameter parameter;
		Iterator i = pl.iterator();
		while(i.hasNext()) {
			parameter = (Parameter) i.next();
			if(!temp.contains(parameter)) {
				temp.add(parameter);
			}
		}
		
		return (ParameterList) temp;
	}
	
	// uses the porter stemmer to do some stemming
	private String doStemming(String string) {
		
		PorterStemmer stemmer = PorterStemmer.instanceOf();
		StringTokenizer words = new StringTokenizer(string);
		String stemmedString = new String();
		String tmp = new String();
		while(words.hasMoreTokens()) {
			tmp = stemmer.stem(words.nextToken());
			if(tmp != null) {
				stemmedString += tmp + " ";
			}
		}
		//logger.info(string + " -> " + stemmedString);
		return stemmedString;
	}
	
	private String prepareString(String string) {
		String temp = string;
		if(temp != null) {

		// strip useless info
		temp = temp.replaceAll("http://", "");
		temp = temp.replaceAll("http", "");
		temp = temp.replaceAll(":", "");
		temp = temp.replaceAll("www", "");
		temp = temp.replaceAll("\\.owl", "");
		temp = temp.replaceAll("0", "");
		temp = temp.replaceAll("9", "");
		temp = temp.replaceAll("8", "");
		temp = temp.replaceAll("7", "");
		temp = temp.replaceAll("6", "");
		temp = temp.replaceAll("5", "");
		temp = temp.replaceAll("4", "");
		temp = temp.replaceAll("3", "");
		temp = temp.replaceAll("2", "");
		temp = temp.replaceAll("1", "");

		// unfold string
		temp = temp.replaceAll("/", " ");
		temp = temp.replaceAll("#", " ");
		temp = temp.replaceAll("\\.", " ");
		temp = temp.replaceAll("_", " ");
		temp = temp.replaceAll("-", " ");
		
		String temp1 = new String();
		
		int spaces = 0;
		for(int i=0; i<temp.length(); i++) {
			//c[0] = temp.charAt(i);
			temp1 = temp1.concat(temp.substring(i, i+1));
			//System.out.println(temp.substring(i, i+1));

			if(i>1) {
//				if( (isUpperCase(temp.charAt(i-1)) != isUpperCase(temp.charAt(i))) ) {
				if( ((!isUpperCase(temp.charAt(i-1))) && (isUpperCase(temp.charAt(i)))) ) {
					//int j = i + spaces;
					//System.out.println("char::"+temp.charAt(i)+" i:: "+i+"::"+temp1.substring(0, i)+ "::'" + temp1.substring(i, i+1)+"'");
					
					temp1 = temp1.substring(0, i+spaces) + " " + temp1.substring(i+spaces, i+1+spaces);
					spaces++;
					//temp = temp.substring(0, i) + " " + temp.substring(i, i+1);
				}
			}
		}
		temp1 = temp1.replaceAll("\\s+", " ");
		temp1 = doStemming(temp1);
		temp = temp1.trim();
		
		}
		return temp;
		//return temp1;
	}

	// perpare strings to apply sintactic measures
	//
	private ArrayList prepareStrings(String [] strings) {
		//String [] temp = new String [strings.length];
		ArrayList temp = new ArrayList();
		for(int i = 0; i < strings.length; i++) {
			//temp[i] = prepareString(strings[i]);
			if(strings[i] != null) {
				temp.add(prepareString(strings[i]));
			}
		}
		return temp;
	}

	private boolean isUpperCase(char c) {
		char [] chars = new char [1];
		chars[0] = c;
		String t = new String(chars);
		if(t.equals(" ") || t.equals("-") || t.equals("_")) {
			return false;
		} else {
			return t.toUpperCase().equals(t);
		}
	}

	// get the service associated with this ontology
	//
	private URI getServiceURI() {
		Iterator i = ont.getServices().iterator();
		Service service;
		URI uri;
		while(i.hasNext()) {
			service = (Service) i.next();
			uri = service.getURI();
			if(uri.toString().indexOf(ont.getURI().toString()) == 0) {
				return uri;
			}
		}
		return ont.getService().getURI();
	}

	public boolean isTrail() {
		return isTrail;
	}
		
	public URI getURI() {
		return ont.getURI();
	}
	
	public OWLSGraph getGraph() {
		return graph;
	}
	
	public String toString() {
		String s = new String();
		if (isTrail) {
			s = "Trail URI::" + getURI().toString() + "\nprocesses::" + getProcessNames().toString() 
			+ "\nInputs::" + getInputNames().toString() + "\nOutputs::" + getOutputNames().toString();
		} else {
			s = "URI::" + getURI().toString() + "\nprocesses::" + getProcesses().toString() 
			+ "\nInputs::" + getInputs().toString() + "\nOutputs::" + getOutputs().toString();
		}
		 
		return s;
	}
	
}
