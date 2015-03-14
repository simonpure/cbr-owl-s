# Overview #

This is an implementation of a CBR system for OWL-S. It is based on Mindswap's OWL-S API (http://www.mindswap.org/2004/owl-s/api/) and uses similarity measures of the Simpack (http://www.ifi.unizh.ch/ddis/simpack.html) package.

# Details #

A custom implementation of ExecutionMonitor is used to record executed services which is then saved as a new OWL-S ontology. These recorded services serve as basis for the CBR system.

The CBR system implements various matching algorithms to compare a new OWL-S ontology to past cases:
  * Semantic matching of Inputs, Outputs, Processes using Pellet reasoner
  * Syntactic matches using Cosine similarity, Extended Jaccard Measure, Jensen Shannon Measure (may use any of the string similarities provided by Simpack or an individual implementation)
  * Graph similarity of the Service