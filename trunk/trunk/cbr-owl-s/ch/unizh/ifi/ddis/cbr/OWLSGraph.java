package ch.unizh.ifi.ddis.cbr;

import java.util.Iterator;
import java.util.Set;

import org.mindswap.owls.process.AnyOrder;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.ParameterList;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Parameter;
import org.mindswap.owls.process.RepeatUntil;
import org.mindswap.owls.process.RepeatWhile;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.Split;
import org.mindswap.owls.process.SplitJoin;
import org.mindswap.owls.service.Service;

import com.sun.org.apache.xalan.internal.xsltc.runtime.Hashtable;

import simpack.api.IGraphNode;
import simpack.api.impl.AbstractGraphAccessor;
import simpack.exception.InvalidElementException;
import simpack.util.graph.GraphNode;

public class OWLSGraph extends AbstractGraphAccessor {
	// root node
	private GraphNode serviceNode;
	// counter used to create unique labels
    private Hashtable counter = new Hashtable();
	
	public OWLSGraph(Service service) {
		   serviceNode = addNewNode("Service");
		   // get the process associated with this service
		   Process process = service.getProcess();
		   // start building the graph
		   setEdge(serviceNode, createProcess(process, serviceNode));
	}
	
	public GraphNode getRoot() {
		return serviceNode;
	}
	
	
	  private GraphNode createProcess(Process process, GraphNode previousNode) {
		   GraphNode node = addNewNode(process.getType().toString());
		   addParameters(process, node);
		   
		   //System.out.println("process node::"+node);
		   
		   setEdge(previousNode, node);
		   
	       if (process instanceof CompositeProcess)
	           setEdge(node, createControlConstruct(((CompositeProcess) process).getComposedOf(), node));
	       else if (process instanceof AtomicProcess) {
	           //node.addSuccessor(node);
	       }
	       return node;
	   }
	  
	  
	   private GraphNode createControlConstruct(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   
		   //System.out.println("cc node::"+node);

		   setEdge(previousNode, node);
	       // create graph entitities
	       if (cc instanceof Sequence)
	    	   setEdge(node, createSequence(cc, node));
	       else if (cc instanceof IfThenElse)
	           setEdge(node, createIF(cc, node));
	       else if (cc instanceof Split)
	           setEdge(node, createSplit(cc, node));
	       else if (cc instanceof SplitJoin)
	           setEdge(node, createSplitJoin(cc, node));
	       else if (cc instanceof AnyOrder)
	           setEdge(node, createAnyOrder(cc, node));
	       else if (cc instanceof Choice)
	           setEdge(node, createChoice(cc, node));
	       else if (cc instanceof RepeatWhile)
	           setEdge(node, createRepeatWhile(cc, node));
	       else if (cc instanceof RepeatUntil)
	           setEdge(node, createRepeatUntil(cc, node));
	       else if (cc instanceof Perform)
	           setEdge(node, createPerform(cc, node));
	       else {
	    	   // not implemented - just add the node
	           //throw new NotImplementedException("This ControlConstruct " + cc.getConstructName() + " is not yet implemented!");
	       }

	       return node;
	   }
	   
	   
	   private GraphNode createPerform(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);
		   setEdge(node, createProcess(((Perform) cc).getProcess(), node));
	       return node;
	   }

	   private GraphNode createSequence(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);

	       Iterator iter = cc.getConstructs().iterator();
	       while (iter.hasNext()) {
	           ControlConstruct tempCC = (ControlConstruct) iter.next();
	           setEdge(node, createControlConstruct(tempCC, node));
	       }

	       return node;
	   }

	   private GraphNode createIF(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);
		   
	       IfThenElse ifThenElse = (IfThenElse) cc;

	       ControlConstruct thenCC = ifThenElse.getThen();
	       setEdge(node, createControlConstruct(thenCC, node));

	       ControlConstruct elseCC = ifThenElse.getElse();
	       if (elseCC != null) {
	           setEdge(node, createControlConstruct(elseCC, node));
	       }
	       return node;
	   }

	   private GraphNode createSplit(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);

	       Iterator iter = cc.getConstructs().iterator();
	       while (iter.hasNext()) {
	           ControlConstruct tempCC = (ControlConstruct) iter.next();
	           setEdge(node, createControlConstruct(tempCC, node));
	       }

	       return node;
	   }

	   private GraphNode createSplitJoin(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);

	       Iterator iter = cc.getConstructs().iterator();
	       while (iter.hasNext()) {
	           ControlConstruct tempCC = (ControlConstruct) iter.next();
	           setEdge(node, createControlConstruct(tempCC, node));
	       }

	       return node;
	   }

	   private GraphNode createAnyOrder(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);

	       Iterator iter = cc.getConstructs().iterator();
	       while (iter.hasNext()) {
	           ControlConstruct tempCC = (ControlConstruct) iter.next();
	           setEdge(node, createControlConstruct(tempCC, node));
	       }

	       return node;
	   }

	   private GraphNode createChoice(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);

	       Iterator iter = cc.getConstructs().iterator();
	       while (iter.hasNext()) {
	           ControlConstruct tempCC = (ControlConstruct) iter.next();
	           setEdge(node, createControlConstruct(tempCC, node));
	       }

	       return node;
	   }

	   private GraphNode createRepeatWhile(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);
		   
	       RepeatWhile repeatWhile = (RepeatWhile) cc;
	 
	       setEdge(node, createControlConstruct(repeatWhile.getComponent(), node));

	       return node;
	   }

	   private GraphNode createRepeatUntil(ControlConstruct cc, GraphNode previousNode) {
		   GraphNode node = addNewNode(cc.getConstructName());
		   node.addPredecessor(previousNode);
		   
	       RepeatUntil repeatUntil = (RepeatUntil) cc;

	       setEdge(node, createControlConstruct(repeatUntil.getComponent(), node));

	       return node;
	   }
	   
	   public void addParameters(Process process, GraphNode processNode) {
		   ParameterList pl = process.getParameters();
		   Iterator i = pl.iterator();
		   Parameter param;
		   GraphNode node;
		   while(i.hasNext()) {
			   param = (Parameter) i.next();
			   if(param.canCastTo(Input.class)) {
				   node = addNewNode(param.getType().toString());
				   setEdge(node, processNode);
			   } else if (param.canCastTo(Output.class)) {
				   node = addNewNode(param.getType().toString());
				   setEdge(processNode, node);
			   }
		   }
	   }
	   
	   public GraphNode addNewNode(String label) {
		   if(counter.containsKey(label)) {
			   Integer i = (Integer) counter.get(label);
			   i++;
			   counter.put(label, i);
			   label += i.toString();
		   } else {
			   counter.put(label, new Integer(0));
		   }
		   
		   GraphNode node = new GraphNode(label);
		   addNode(node);
		   nodeSet.add(node);
		   //System.out.println("adding new node::" + node + " size:" + nodeSet.size());
		   return node;
	   }
	   
	public double getShortestPath(IGraphNode nodeA, IGraphNode nodeB) throws InvalidElementException {
		// TODO Auto-generated method stub
		return 0;
	}

	public double getMaximumDirectedPathLength() {
		// TODO Auto-generated method stub
		return 0;
	}

	public Set<IGraphNode> getPredecessors(IGraphNode node, boolean direct) throws InvalidElementException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<IGraphNode> getSuccessors(IGraphNode node, boolean direct) throws InvalidElementException {
		// TODO Auto-generated method stub
		return null;
	}

	public IGraphNode getMostRecentCommonAncestor(IGraphNode nodeA, IGraphNode nodeB) throws InvalidElementException {
		// TODO Auto-generated method stub
		return null;
	}

}
