/*
 * created on 2006/12/20 by simonl
 * 
 * Implementation of a custom ExecutionMonitor to record any OWL-S services executed by the OWL-S API
 *  
 */
package ch.unizh.ifi.ddis.cbr.record;

import impl.owls.process.OutputImpl;
import impl.owls.process.ParameterImpl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.log4j.Logger;
import org.mindswap.exceptions.ExecutionException;
import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLValue;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputBinding;
import org.mindswap.owls.process.InputBindingList;
import org.mindswap.owls.process.InputList;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputList;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.execution.DefaultProcessMonitor;
import org.mindswap.owls.process.execution.ProcessExecutionEngine;
import org.mindswap.owls.process.execution.ProcessMonitor;
import org.mindswap.owls.service.Service;
import org.mindswap.owls.vocabulary.OWLS;
import org.mindswap.query.ValueMap;
import org.mindswap.swrl.Variable;

import impl.owls.process.InputImpl;

public class ExecutionMonitor extends DefaultProcessMonitor implements ProcessMonitor {
	// Logger
	private Logger logger = Logger.getLogger(this.getClass().getName());
	// ExecutionTrail
	private ExecutionTrail trail;
	
	/*
	 * Constructor for ExecutionMonitor
	 * @param ExecutionTrail object which records the execution
	 * @return a new instance of object ExecutionTrail 
	 */
	public ExecutionMonitor(ExecutionTrail trail) {
		this.trail = trail;
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessMonitor#executionStarted()
	 */
	public void executionStarted() {
		trail.setFinished(false);
		//logger.info("Execution started.\n\n");
	}

	/*
	 *  (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessMonitor#executionFinished()
	 */
	public void executionFinished() {
		trail.setFinished(true);
		//logger.info("Execution finished.\n\n");
				
	}

	/*
	 *  (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessMonitor#executionStarted(org.mindswap.owls.process.Process, org.mindswap.query.ValueMap)
	 */
	public void executionStarted(Process process, ValueMap inputs) {		
		//logger.info("started: " + process.debugString() + "\n\n----\n\n" + inputs.debugString() + "\n\n");
		trail.processStarted(process, inputs);
	}

	/*
	 *  (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessMonitor#executionFinished(org.mindswap.owls.process.Process, org.mindswap.query.ValueMap, org.mindswap.query.ValueMap)
	 */
	public void executionFinished(Process process, ValueMap inputs, ValueMap outputs) {
	//	if(process.canCastTo(AtomicProcess.class)) {
//			trail.addProcess(process);
//			trail.addInput(inputs);
//			trail.addOutputs(outputs);
			trail.processesFinished(process, inputs, outputs);
	//	}
	}

	/*
	 *  (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessMonitor#executionFailed(org.mindswap.exceptions.ExecutionException)
	 */
	public void executionFailed(ExecutionException e) {
		trail.setFailed(false, e);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.mindswap.owls.process.execution.ProcessMonitor#intermediateResultsReceived(org.mindswap.query.ValueMap)
	 */
	public void intermediateResultsReceived(ValueMap values) {
		// TODO Auto-generated method stub
		// don't think we need this
		
	}

/*
 * We don't need these methods and let the DefaultProcessor deal with it
 * 
	public void setMonitorFilter(int processType) {
		// TODO Auto-generated method stub
		
		
	}

	public int getMonitorFilter() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void executionInterrupted(Process process, AtomicProcess lastProcess) {
		// TODO Auto-generated method stub
		
	}

	public void executionContinued(Process process) {
		// TODO Auto-generated method stub
		
	}
*/
	
	
}