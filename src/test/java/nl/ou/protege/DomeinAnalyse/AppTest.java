package nl.ou.protege.DomeinAnalyse;

import java.io.InputStream;
import java.util.ArrayList;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.util.FileManager;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

    /**
     * Rigourous Test :-)
     */
    public void testJena()
    {
    	org.apache.log4j.BasicConfigurator.configure();
    	// create an empty Model
    	Model model = ModelFactory.createDefaultModel();
    	
    	// use the FileManager to find the input file
    	InputStream in = FileManager.get().open( "pizza.n3" );
    	if (in == null) {
    	    throw new IllegalArgumentException(
    	                                 "File: " + "pizza.n3" + " not found");
    	}

    	// read the RDF/XML file
    	model.read(in, null,"N3");
    	model.write(System.out, "N3");
    	for (String key: model.getNsPrefixMap().keySet()) {
    		System.out.println(key);
    	}
    }

    /**
     * Gets the rdf nodes from a List represented by a collection resource
     * @param r resource
     * @return list of rdf nodes
     */
    protected ArrayList<RDFNode> getListedNodes(Resource r) {
    	ArrayList<RDFNode> nodes = new ArrayList<RDFNode>();
    	Property first = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#first");
    	Property rest = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#rest");
    	Resource nil = ResourceFactory.createResource("http://www.w3.org/1999/02/22-rdf-syntax-ns#nil");
    	if (r.isAnon()) {
    		StmtIterator properties = r.listProperties(first);
    		if (properties.hasNext()) {
    			nodes.add(properties.next().getObject()); // add the first node to the list
    		}
    		properties = r.listProperties(rest); // look for the tail
    		if (properties.hasNext()) {
    			RDFNode node = properties.next().getObject();
    			if (node.isResource()) {
    				Resource resource = node.asResource();
    				if (! nil.equals(resource)) {
    					nodes.addAll(getListedNodes(resource)); // recurse to get the rest
    				}
    			}
    		}
    	} else if (! nil.equals(r)) {
    		nodes.add(r); // only add the resource if it isn't equal to nil
    	}
    	return nodes;
    }

    /**
     * Rigourous Test :-)
     */
    public void testStatements()
    {
    	org.apache.log4j.BasicConfigurator.configure();
    	// create an empty Model
    	Model model = ModelFactory.createDefaultModel();
    	
    	// use the FileManager to find the input file
    	InputStream in = FileManager.get().open( "pizza.n3" );
    	if (in == null) {
    	    throw new IllegalArgumentException(
    	                                 "File: " + "pizza.n3" + " not found");
    	}

    	model.read(in, null, "TURTLE");
    	Resource lenstype = ResourceFactory.createResource("http://www.w3.org/2004/09/fresnel#Lens");
    	Property rdftype = ResourceFactory.createProperty("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    	System.out.println(rdftype.getNameSpace());
    	System.out.println(rdftype.getLocalName());
    	
    	// read the RDF/XML file
    	for (ResIterator i = model.listResourcesWithProperty(rdftype, lenstype); i.hasNext(); ) {
    		Resource lens = i.next();
    		System.out.println(lens.toString() + ": ");

    		for (StmtIterator j = lens.listProperties(); j.hasNext(); ) {
    			
        	    Statement lensstmt = j.nextStatement();  // get next statement
        		Property  property = lensstmt.getPredicate();
        		
        		if (!lenstype.equals(property)) {
	        		for (NodeIterator n = model.listObjectsOfProperty(lens, property); n.hasNext();) {
		        		RDFNode node = n.next();
	
			    	    System.out.println("\t" + property.toString());
			    	    if (node.isResource()) {
			    	    	for (RDFNode subnode: getListedNodes(node.asResource())) {
			    	    		System.out.println("\t\t" + subnode.toString());
			    	    	}
			    	    } else {
			    	        // object is a literal
			    	        System.out.println("\t\t\"" + node.toString() + "\"");
			    	    }
	        		}
        		}
        	}
    	} 
    	model.write(System.out, "TURTLE");
    	for (String key: model.getNsPrefixMap().keySet()) {
    		System.out.println(key);
    	}
    }
}
