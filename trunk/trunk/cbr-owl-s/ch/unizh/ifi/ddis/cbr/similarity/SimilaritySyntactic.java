package ch.unizh.ifi.ddis.cbr.similarity;

import java.lang.reflect.Constructor;
import java.util.StringTokenizer;

import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.Process;

import ch.unizh.ifi.ddis.cbr.PorterStemmer;

import simpack.accessor.string.StringAccessor;
import simpack.api.ISequenceAccessor;
import simpack.api.impl.SimilarityMeasure;
import simpack.tokenizer.SplittedStringTokenizer;

public class SimilaritySyntactic extends SimilarityDefault {

	// similarity methods for syntactic measures
	// must extend simpack.api.impl.SimilarityMeasure 
	// must have a constructor that accepts String, String or ISequenceAccessor, ISequenceAccessor
	// see Simpack for details (http://www.ifi.unizh.ch/ddis/simpack.html)
	private String[] methods = {"simpack.measure.external.owlsmx.CosineSimilarity", "simpack.measure.external.owlsmx.ExtendedJaccardMeasure", 
			"simpack.measure.external.owlsmx.JensenShannonMeasure"};
	

	public double compareParameter(Parameter oldParameter, Parameter newParameter) {
		String oldParameterString = prepareString(oldParameter.getLabel());
		String newParameterString = prepareString(newParameter.getURI().toString());
		
    	double match = 0;
    	for(int k = 0; k<methods.length; k++) {
			//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldParameter, newParameter));
			match += compareStrings(methods[k], oldParameterString, newParameterString);
    	}
    	
		// normalize match and return
		return (match / methods.length);
	}
	
	public double compareInput(Input oldInput, Input newInput) {
		String oldInputString = prepareString(oldInput.getLabel());
		String newInputString = prepareString(newInput.getURI().toString());
		
    	double match = 0;
    	for(int k = 0; k<methods.length; k++) {
			//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldInput, newInput));
			match += compareStrings(methods[k], oldInputString, newInputString);
    	}
    	
		// normalize match and return
		return (match / methods.length);
	}

	public double compareOutput(Output oldOutput, Output newOutput) {
		String oldOutputString = prepareString(oldOutput.getLabel());
		String newOutputString = prepareString(newOutput.getURI().toString());
		
    	double match = 0;
    	for(int k = 0; k<methods.length; k++) {
			//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldOutput, newOutput));
			match += compareStrings(methods[k], oldOutputString, newOutputString);
    	}
    	
		// normalize match and return
		return (match / methods.length);
	}

	public double compareProcess(Process oldProcess, Process newProcess) {
		String oldProcessString = prepareString(oldProcess.getLabel());
		String newProcessString = prepareString(newProcess.getURI().toString());
		
    	double match = 0;
    	for(int k = 0; k<methods.length; k++) {
			//logger.info("method::" + methods[k] + " :: " + compareStrings(methods[k], oldProcess, newProcess));
			match += compareStrings(methods[k], oldProcessString, newProcessString);
    	}
    	
		// normalize match and return
		return (match / methods.length);
	}

	private double compareStrings(String method, String s1, String s2) {
		StringAccessor a1 = new StringAccessor(s1, new SplittedStringTokenizer(" "));
		StringAccessor a2 = new StringAccessor(s2, new SplittedStringTokenizer(" "));
		double similarity;
		try {
			Class clazz = Class.forName(method);		
			Constructor con;
			SimilarityMeasure similaritymeasure = null;
			try {
				con = clazz.getConstructor(new Class[]{ISequenceAccessor.class, ISequenceAccessor.class});
				similaritymeasure = (SimilarityMeasure) con.newInstance(a1, a2);

			} catch (NoSuchMethodException e) {
				try {
					con = clazz.getConstructor(new Class[]{String.class, String.class});
					similaritymeasure = (SimilarityMeasure) con.newInstance(s1, s2);
				} catch (NoSuchMethodException e1) {
					// hmmm.. shouldn't happen
					similarity = 0;
				}
			}
			similarity = similaritymeasure.getSimilarity();
		} catch (Exception e) {
			// ups...
			similarity = 0;
		}
		return similarity;
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

}
