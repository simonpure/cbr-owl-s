package ch.unizh.ifi.ddis.cbr.record;

import impl.owls.process.ParameterImpl;

import java.util.Collection;
import java.util.Iterator;

import org.mindswap.query.ValueMap;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.vocabulary.OWLS;

/** Helper Class
 * 
 * @author simonl
 *
 */
public class ExecutionRecord {
	private Process process;
	private ValueMap input;
	private ValueMap output;
	private boolean used = false;
	
	public ExecutionRecord() {
		
	}
	
	public ExecutionRecord(Process process, ValueMap input, ValueMap output) {
		this.process = process;
		addInput(input);
		addOutput(output);
	}

	public ExecutionRecord(Process process, ValueMap input) {
		this.process = process;
		addInput(input);
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public void setUsed(boolean used){
		this.used = used;
	}
	
	public boolean isUsed() {
		return used;
	}
	
	public void setInput(ValueMap input) {
		addInput(input);
	}

	public void setOutput(ValueMap output) {
		addOutput(output);
	}

	public Process getProcess() {
		return process;
	}
	
	public ValueMap getInput() {
		return input;
	}
	public ValueMap getOutput() {
		return output;
	}
	
	public boolean isAtomic() {
		return process.canCastTo(AtomicProcess.class);
	}
	
	public boolean isComposite() {
		return process.canCastTo(CompositeProcess.class);
	}
	
	
	public String toString() {
		String s = "Processe: " + process.toString() + "\nInput: "
		+ input.toString() + "\nOutput: " + output.toString() + "\n";

		return s;
	}
	/* add ValueMaps of type Input only
	 * 
	 */
	private void addInput(ValueMap inputs) {
		ValueMap v = new ValueMap();

		Collection i = inputs.getVariables();
		Iterator it = i.iterator();

		while (it.hasNext()) {
			ParameterImpl p = (ParameterImpl) it.next();
			if (p.isType(OWLS.Process.Input)) {
				v.setValue(p, inputs.getValue(p));
			}
		}
		this.input = v;
	}

	/* add ValueMaps of type Output only
	 * 
	 */
	private void addOutput(ValueMap outputs) {
		// if(!inputs.contains(outputs)) {
		ValueMap v = new ValueMap();

		Collection i = outputs.getVariables();
		Iterator it = i.iterator();

		while (it.hasNext()) {
			ParameterImpl p = (ParameterImpl) it.next();
			if (p.isType(OWLS.Process.Output)) {
				v.setValue(p, outputs.getValue(p));
				// logger.info("output::" + outputs.getValue(p));
			}
		}
		this.output = v;
	}

}
