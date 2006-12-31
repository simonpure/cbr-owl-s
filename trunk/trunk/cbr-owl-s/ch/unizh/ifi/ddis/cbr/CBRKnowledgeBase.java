package ch.unizh.ifi.ddis.cbr;

import impl.jena.OWLKnowledgeBaseImpl;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mindswap.exceptions.LockNotSupportedException;
import org.mindswap.exceptions.UnboundVariableException;
import org.mindswap.owl.OWLClass;
import org.mindswap.owl.OWLDataProperty;
import org.mindswap.owl.OWLDataType;
import org.mindswap.owl.OWLDataValue;
import org.mindswap.owl.OWLDataValueList;
import org.mindswap.owl.OWLEntity;
import org.mindswap.owl.OWLFactory;
import org.mindswap.owl.OWLIndividual;
import org.mindswap.owl.OWLIndividualList;
import org.mindswap.owl.OWLKnowledgeBase;
import org.mindswap.owl.OWLObjectProperty;
import org.mindswap.owl.OWLOntology;
import org.mindswap.owl.OWLProperty;
import org.mindswap.owl.OWLReader;
import org.mindswap.owl.OWLType;
import org.mindswap.owl.OWLValue;
import org.mindswap.owl.OWLWriter;
import org.mindswap.owl.list.ListVocabulary;
import org.mindswap.owl.list.RDFList;
import org.mindswap.owls.OWLSOntology;
import org.mindswap.owls.generic.expression.Expression;
import org.mindswap.owls.generic.list.OWLSObjList;
import org.mindswap.owls.grounding.Grounding;
import org.mindswap.owls.grounding.JavaAtomicGrounding;
import org.mindswap.owls.grounding.MessageMap;
import org.mindswap.owls.grounding.UPnPAtomicGrounding;
import org.mindswap.owls.grounding.WSDLAtomicGrounding;
import org.mindswap.owls.grounding.WSDLOperationRef;
import org.mindswap.owls.process.AnyOrder;
import org.mindswap.owls.process.AtomicProcess;
import org.mindswap.owls.process.Choice;
import org.mindswap.owls.process.CompositeProcess;
import org.mindswap.owls.process.Condition;
import org.mindswap.owls.process.ControlConstruct;
import org.mindswap.owls.process.ControlConstructBag;
import org.mindswap.owls.process.ControlConstructList;
import org.mindswap.owls.process.ForEach;
import org.mindswap.owls.process.IfThenElse;
import org.mindswap.owls.process.Input;
import org.mindswap.owls.process.InputBinding;
import org.mindswap.owls.process.Local;
import org.mindswap.owls.process.Output;
import org.mindswap.owls.process.OutputBinding;
import org.mindswap.owls.process.Perform;
import org.mindswap.owls.process.Process;
import org.mindswap.owls.process.Produce;
import org.mindswap.owls.process.RepeatUntil;
import org.mindswap.owls.process.RepeatWhile;
import org.mindswap.owls.process.Result;
import org.mindswap.owls.process.Sequence;
import org.mindswap.owls.process.Split;
import org.mindswap.owls.process.SplitJoin;
import org.mindswap.owls.process.ValueData;
import org.mindswap.owls.process.ValueOf;
import org.mindswap.owls.profile.Profile;
import org.mindswap.owls.profile.ServiceParameter;
import org.mindswap.owls.service.Service;
import org.mindswap.query.ABoxQuery;
import org.mindswap.query.ValueMap;
import org.mindswap.swrl.AtomList;
import org.mindswap.utils.QNameProvider;

public class CBRKnowledgeBase implements OWLKnowledgeBase {
	private OWLKnowledgeBase kb;
	
	public CBRKnowledgeBase () {
		kb = OWLFactory.createKB();
		
	}

	public Set getOntologies() {
		// TODO Auto-generated method stub
		return kb.getOntologies();
	}

	public Set getOntologies(boolean all) {
		// TODO Auto-generated method stub
		return kb.getOntologies(all);
	}

	public OWLOntology getOntology(URI uri) {
		// TODO Auto-generated method stub
		return kb.getOntology(uri);
	}

	public OWLOntology getBaseOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology createOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology createOntology(boolean load) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology createOntology(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology createOntology(URI uri, URI fileURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology createOntology(URI uri, URI fileURI, Object implementation) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology loadOntology(OWLOntology ontology) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology load(OWLOntology ontology) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology load(OWLOntology ontology, boolean withImports) {
		// TODO Auto-generated method stub
		return null;
	}

	public void unload(OWLOntology ontology) {
		// TODO Auto-generated method stub
		
	}

	public void unload(URI uri) {
		// TODO Auto-generated method stub
		
	}

	public OWLReader getReader() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setReader(OWLReader reader) {
		// TODO Auto-generated method stub
		
	}

	public OWLOntology read(String uri) throws URISyntaxException, FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology read(URI uri) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology read(Reader in, URI baseURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLOntology read(InputStream in, URI baseURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public QNameProvider getQNames() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean getAutoConsistency() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAutoConsistency(boolean auto) {
		// TODO Auto-generated method stub
		
	}

	public boolean getAutoTranslate() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setAutoTranslate(boolean auto) {
		// TODO Auto-generated method stub
		
	}

	public OWLKnowledgeBase getTranslationSource() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLSOntology createOWLSOntology() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLSOntology createOWLSOntology(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Service readService(String uri) throws URISyntaxException, FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Service readService(URI uri) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public Service readService(Reader in, URI baseURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public Service readService(InputStream in, URI baseURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public List readAllServices(String uri) throws URISyntaxException, FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public List readAllServices(URI uri) throws FileNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	public List readAllServices(Reader in, URI baseURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public List readAllServices(InputStream in, URI baseURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getImplementation() {
		// TODO Auto-generated method stub
		return null;
	}

	public void refresh() {
		// TODO Auto-generated method stub
		
	}

	public boolean isConsistent() {
		// TODO Auto-generated method stub
		return false;
	}

	public void prepare() {
		// TODO Auto-generated method stub
		
	}

	public void classify() {
		// TODO Auto-generated method stub
		
	}

	public boolean isClassified() {
		// TODO Auto-generated method stub
		return false;
	}

	public void setReasoner(String reasonerName) {
		// TODO Auto-generated method stub
		
	}

	public void setReasoner(Object reasoner) {
		// TODO Auto-generated method stub
		
	}

	public Object getReasoner() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClass createClass(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLObjectProperty createObjectProperty(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataProperty createDataProperty(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual createIndividual(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual createInstance(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual createInstance(OWLClass c, URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValue createDataValue(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValue createDataValue(String value, String language) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValue createDataValue(Object value, URI datatypeURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValue createDataValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLClass getClass(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual getIndividual(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividualList getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLProperty getProperty(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLObjectProperty getObjectProperty(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataProperty getDataProperty(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataType getDataType(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLType getType(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLEntity getEntity(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setProperty(OWLIndividual ind, OWLDataProperty prop, String value) {
		// TODO Auto-generated method stub
		
	}

	public void setProperty(OWLIndividual ind, OWLDataProperty prop, OWLDataValue value) {
		// TODO Auto-generated method stub
		
	}

	public void setProperty(OWLIndividual ind, OWLDataProperty prop, Object value) {
		// TODO Auto-generated method stub
		
	}

	public void addProperty(OWLIndividual ind, OWLProperty prop, OWLValue value) {
		// TODO Auto-generated method stub
		
	}

	public void addProperty(OWLIndividual ind, OWLDataProperty prop, OWLDataValue value) {
		// TODO Auto-generated method stub
		
	}

	public void addProperty(OWLIndividual ind, OWLDataProperty prop, Object value) {
		// TODO Auto-generated method stub
		
	}

	public void addProperty(OWLIndividual ind, OWLDataProperty prop, String value) {
		// TODO Auto-generated method stub
		
	}

	public void removeProperties(OWLIndividual ind, OWLProperty prop) {
		// TODO Auto-generated method stub
		
	}

	public void removeProperty(OWLIndividual theInd, OWLProperty theProp, OWLValue theValue) {
		// TODO Auto-generated method stub
		
	}

	public void addProperty(OWLIndividual ind, OWLObjectProperty prop, OWLIndividual value) {
		// TODO Auto-generated method stub
		
	}

	public void setProperty(OWLIndividual ind, OWLObjectProperty prop, OWLIndividual value) {
		// TODO Auto-generated method stub
		
	}

	public void addType(OWLIndividual ind, OWLClass c) {
		// TODO Auto-generated method stub
		
	}

	public void removeTypes(OWLIndividual ind) {
		// TODO Auto-generated method stub
		
	}

	public boolean isEnumerated(OWLClass c) {
		// TODO Auto-generated method stub
		return false;
	}

	public OWLIndividualList getEnumerations(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSubClassOf(OWLClass c1, OWLClass c2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubTypeOf(OWLDataType t1, OWLDataType t2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSubTypeOf(OWLType t1, OWLType t2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isEquivalent(OWLType t1, OWLType t2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDisjoint(OWLType t1, OWLType t2) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isDisjoint(OWLClass c1, OWLClass c2) {
		// TODO Auto-generated method stub
		return false;
	}

	public Set getSubClasses(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSubClasses(OWLClass c, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSuperClasses(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSuperClasses(OWLClass c, boolean direct) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEquivalentClasses(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSubProperties(OWLProperty p) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getSuperProperties(OWLProperty p) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getEquivalentProperties(OWLProperty p) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividualList getInstances(OWLClass c) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isType(OWLIndividual ind, OWLClass c) {
		// TODO Auto-generated method stub
		return false;
	}

	public OWLClass getType(OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public Set getTypes(OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual getProperty(OWLIndividual ind, OWLObjectProperty prop) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividualList getProperties(OWLIndividual ind, OWLObjectProperty prop) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map getProperties(OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValue getProperty(OWLIndividual ind, OWLDataProperty prop) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValue getProperty(OWLIndividual ind, OWLDataProperty prop, String lang) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLDataValueList getProperties(OWLIndividual ind, OWLDataProperty prop) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual getIncomingProperty(OWLObjectProperty prop, OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividualList getIncomingProperties(OWLObjectProperty prop, OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividualList getIncomingProperties(OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual getIncomingProperty(OWLDataProperty prop, OWLDataValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividualList getIncomingProperties(OWLDataProperty prop, OWLDataValue value) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasProperty(OWLIndividual ind, OWLProperty prop) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasProperty(OWLIndividual ind, OWLProperty prop, OWLValue value) {
		// TODO Auto-generated method stub
		return false;
	}

	public void apply(AtomList atoms) throws UnboundVariableException {
		// TODO Auto-generated method stub
		
	}

	public void applyGround(AtomList atoms) {
		// TODO Auto-generated method stub
		
	}

	public boolean isTrue(ABoxQuery query) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTrue(Condition condition) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTrue(Condition condition, ValueMap binding) {
		// TODO Auto-generated method stub
		return false;
	}

	public List query(ABoxQuery query) {
		// TODO Auto-generated method stub
		return null;
	}

	public List query(ABoxQuery query, ValueMap values) {
		// TODO Auto-generated method stub
		return null;
	}

	public List query(String query) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isLockSupported() {
		// TODO Auto-generated method stub
		return false;
	}

	public void lockForRead() throws LockNotSupportedException {
		// TODO Auto-generated method stub
		
	}

	public void lockForWrite() throws LockNotSupportedException {
		// TODO Auto-generated method stub
		
	}

	public void releaseLock() throws LockNotSupportedException {
		// TODO Auto-generated method stub
		
	}

	public List getServices() {
		// TODO Auto-generated method stub
		return null;
	}

	public Service getService(URI serviceURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getProfiles() {
		// TODO Auto-generated method stub
		return null;
	}

	public Profile getProfile(URI profileURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public List getProcesses() {
		// TODO Auto-generated method stub
		return null;
	}

	public List getProcesses(int type) {
		// TODO Auto-generated method stub
		return null;
	}

	public Process getProcess(URI processURI) {
		// TODO Auto-generated method stub
		return null;
	}

	public AnyOrder createAnyOrder() {
		// TODO Auto-generated method stub
		return null;
	}

	public AnyOrder createAnyOrder(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomicProcess createAtomicProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	public AtomicProcess createAtomicProcess(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Choice createChoice() {
		// TODO Auto-generated method stub
		return null;
	}

	public Choice createChoice(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public CompositeProcess createCompositeProcess() {
		// TODO Auto-generated method stub
		return null;
	}

	public CompositeProcess createCompositeProcess(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Condition createSWRLCondition() {
		// TODO Auto-generated method stub
		return null;
	}

	public Condition createSWRLCondition(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public ControlConstructList createControlConstructList(ControlConstruct item) {
		// TODO Auto-generated method stub
		return null;
	}

	public ControlConstructBag createControlConstructBag(ControlConstruct item) {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression createSWRLExpression() {
		// TODO Auto-generated method stub
		return null;
	}

	public Expression createSWRLExpression(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public ForEach createForEach() {
		// TODO Auto-generated method stub
		return null;
	}

	public ForEach createForEach(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Grounding createGrounding() {
		// TODO Auto-generated method stub
		return null;
	}

	public Grounding createGrounding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public IfThenElse createIfThenElse() {
		// TODO Auto-generated method stub
		return null;
	}

	public IfThenElse createIfThenElse(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Input createInput() {
		// TODO Auto-generated method stub
		return null;
	}

	public Input createInput(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public InputBinding createInputBinding() {
		// TODO Auto-generated method stub
		return null;
	}

	public InputBinding createInputBinding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Local createLocal() {
		// TODO Auto-generated method stub
		return null;
	}

	public Local createLocal(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageMap createWSDLInputMessageMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageMap createWSDLInputMessageMap(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageMap createWSDLOutputMessageMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageMap createWSDLOutputMessageMap(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageMap createUPnPMessageMap() {
		// TODO Auto-generated method stub
		return null;
	}

	public MessageMap createUPnPMessageMap(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Output createOutput() {
		// TODO Auto-generated method stub
		return null;
	}

	public Output createOutput(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public OutputBinding createOutputBinding() {
		// TODO Auto-generated method stub
		return null;
	}

	public OutputBinding createOutputBinding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Perform createPerform() {
		// TODO Auto-generated method stub
		return null;
	}

	public Perform createPerform(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Perform createPerform(OWLIndividual individual) {
		// TODO Auto-generated method stub
		return null;
	}

	public Produce createProduce() {
		// TODO Auto-generated method stub
		return null;
	}

	public Produce createProduce(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Profile createProfile() {
		// TODO Auto-generated method stub
		return null;
	}

	public Profile createProfile(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public RepeatUntil createRepeatUntil() {
		// TODO Auto-generated method stub
		return null;
	}

	public RepeatUntil createRepeatUntil(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public RepeatWhile createRepeatWhile() {
		// TODO Auto-generated method stub
		return null;
	}

	public RepeatWhile createRepeatWhile(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Result createResult() {
		// TODO Auto-generated method stub
		return null;
	}

	public Result createResult(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Sequence createSequence() {
		// TODO Auto-generated method stub
		return null;
	}

	public Sequence createSequence(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Service createService() {
		// TODO Auto-generated method stub
		return null;
	}

	public Service createService(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceParameter createServiceParameter() {
		// TODO Auto-generated method stub
		return null;
	}

	public ServiceParameter createServiceParameter(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Split createSplit() {
		// TODO Auto-generated method stub
		return null;
	}

	public Split createSplit(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public SplitJoin createSplitJoin() {
		// TODO Auto-generated method stub
		return null;
	}

	public SplitJoin createSplitJoin(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaAtomicGrounding createJavaAtomicGrounding() {
		// TODO Auto-generated method stub
		return null;
	}

	public JavaAtomicGrounding createJavaAtomicGrounding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public Grounding createJavaGrounding() {
		// TODO Auto-generated method stub
		return null;
	}

	public Grounding createJavaGrounding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public UPnPAtomicGrounding createUPnPAtomicGrounding() {
		// TODO Auto-generated method stub
		return null;
	}

	public UPnPAtomicGrounding createUPnPAtomicGrounding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueOf createValueOf() {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueOf createValueOf(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public WSDLAtomicGrounding createWSDLAtomicGrounding() {
		// TODO Auto-generated method stub
		return null;
	}

	public WSDLAtomicGrounding createWSDLAtomicGrounding(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public WSDLOperationRef createWSDLOperationRef() {
		// TODO Auto-generated method stub
		return null;
	}

	public WSDLOperationRef createWSDLOperationRef(URI uri) {
		// TODO Auto-generated method stub
		return null;
	}

	public ValueData createValueData(OWLValue dataValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLSObjList createList() {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLSObjList createList(OWLIndividual item) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLSObjList createList(OWLIndividualList items) {
		// TODO Auto-generated method stub
		return null;
	}

	public RDFList createList(ListVocabulary vocabulary) {
		// TODO Auto-generated method stub
		return null;
	}

	public RDFList createList(ListVocabulary vocabulary, OWLIndividual item) {
		// TODO Auto-generated method stub
		return null;
	}

	public RDFList createList(ListVocabulary vocabulary, OWLIndividualList items) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLIndividual parseLiteral(String literal) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isSameAs(OWLIndividual ind1, OWLIndividual ind2) {
		// TODO Auto-generated method stub
		return false;
	}

	public OWLIndividualList getSameIndividuals(OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isDifferentFrom(OWLIndividual ind1, OWLIndividual ind2) {
		// TODO Auto-generated method stub
		return false;
	}

	public OWLIndividualList getDifferentIndividuals(OWLIndividual ind) {
		// TODO Auto-generated method stub
		return null;
	}

	public OWLWriter getWriter() {
		// TODO Auto-generated method stub
		return null;
	}

	public void write(Writer writer) {
		// TODO Auto-generated method stub
		
	}

	public void write(Writer writer, URI baseURI) {
		// TODO Auto-generated method stub
		
	}

	public void write(OutputStream out) {
		// TODO Auto-generated method stub
		
	}

	public void write(OutputStream out, URI baseURI) {
		// TODO Auto-generated method stub
		
	}

}
