package attack_trees;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.IDN;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.Vector;

import org.w3c.dom.*;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 
import org.xml.sax.helpers.DefaultHandler;

import factoring.Expression;
import factoring.FactoredExpression;

public class HandMadeIPTVTree{

	public static void main (String argv []){
		
	    try {
	    	HandMadeIPTVTree o = new HandMadeIPTVTree();
	        o.buildExpression("IPTV_Attack_Tree.xml");

	        }catch (SAXParseException err) {
	        System.out.println ("** Parsing error" + ", line " 
	             + err.getLineNumber () + ", uri " + err.getSystemId ());
	        System.out.println(" " + err.getMessage ());

	        }catch (SAXException e) {
	        Exception x = e.getException ();
	        ((x == null) ? e : x).printStackTrace ();

	        }catch (Throwable t) {
	        t.printStackTrace ();
	        }
	        //System.exit (0);

	    }//end of main




	  public void buildExpression(String fileName) throws ParserConfigurationException, SAXException, IOException{
	      /*StreamSource stream = new StreamSource(getClass().getClassLoader().getResourceAsStream("adtree.xsd"));
	      Schema schema=SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI).newSchema(stream);
	      Validator validator = schema.newValidator();
	      factory.setSchema(schema);
	      factory.setNamespaceAware(true);
	      factory.setValidating(true);
	      builder.setErrorHandler(new DefaultHandler());*/
	      DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
	      DocumentBuilder builder = factory.newDocumentBuilder();
	      FileInputStream fileStream = new FileInputStream(new File(fileName));
	      Document doc=builder.parse(fileStream);
	      doc.getDocumentElement().normalize();
	      Node treeNode=doc.getElementsByTagName("adtree").item(0);
	      NodeList list = treeNode.getChildNodes();
	      System.out.println("There are "+list.getLength()+" elements NODE");
	      Vector<HashMap<String,HashMap<String,String>>> values=null;
	      Map<String, Integer> dictionary = new TreeMap<String, Integer>();
	      Map<Integer, String> dualDictionary = new TreeMap<Integer, String>();
	      FactoredExpression expression = null;
	      for(int i=0; i<list.getLength();i++ ){
	        Node n=list.item(i);
	        if(n.getNodeType() == Node.ELEMENT_NODE){
	          Element e=(Element)n;
	          if(e.getNodeName().equals("node")){
	        	 expression = buildExpression(e, dictionary, dualDictionary);
	            //values=importTree(e,list.getLength()-1);
	          }
	          else if(e.getNodeName().equals("domain")){
	            //we just ignore the domains
	          }
	          //mainWindow.createAttrDomainMenu();
	        }
	      }
	      System.out.println("The formed expression has "+expression.totalNumberOfLiterals()+" literals");
	      System.out.println("The formed expression has size = "+expression.size());
	      System.out.println("The number of actions is "+dictionary.size());
	      
	      System.out.println("De-factorizing expression");
	      Expression e = expression.normalize();
	      System.out.println("The normalized expression has "+e.totalNumberOfLiterals()+" literals");
	      System.out.println("The normalized expression has size = "+e.size());

	      System.out.println("Factorizing expression");
	      FactoredExpression f = e.factorize();
	      System.out.println("The factorized expression has "+f.totalNumberOfLiterals()+" literals");
	      System.out.println("The factorized expression has size = "+e.size());

	      fileStream.close();
	}
	
	private static boolean isConjunction(Element e){
	    if (e.getAttribute("refinement").equals("conjunctive")){
	      return true;
	    }
	    else if (e.getAttribute("refinement").equals("disjunctive")){
	      return false;
	    }
	    else throw new RuntimeException(); 
	}

    private static FactoredExpression buildExpression(Element root, Map<String, Integer> dictionary, Map<Integer, String> dualDictionary) {
    	//we first need to know whether this is a conjunction or not
    	boolean conjunction = isConjunction(root);
    	NodeList list =	root.getChildNodes();
    	FactoredExpression result;
    	if (noMoreChildNodes(list)){
    		//this a leaf node
		    String label = getChildTag(root,"label").getTextContent();
		    int identifier = 0;  
		    if (dictionary.containsKey(label)) identifier = dictionary.get(label);
		    else {
		    	Random r = new Random();
		    	identifier = r.nextInt();
		    	while (dualDictionary.containsKey(identifier)) identifier = r.nextInt();
		    	dictionary.put(label, identifier);
		    	dualDictionary.put(identifier, label);
		    }
		    return new FactoredExpression(new Expression(identifier));
    	}
    	if (conjunction) result	= new FactoredExpression(Expression.ONE_EXPRESSION);
    	else result	= new FactoredExpression(Expression.ZERO_EXPRESSION);
    	for(int i=0; i<list.getLength();i++ ){
    		Node n=list.item(i);
    		if(n.getNodeType() == Node.ELEMENT_NODE){
    			Element e=(Element)n;
    			if(e.getNodeName().equals("node")){
    				FactoredExpression tmp = buildExpression(e, dictionary, dualDictionary);
    				if (conjunction) result = result.multiplyBy(tmp);
    				else result = result.add(tmp);
    				//values=importTree(e,list.getLength()-1);
    			}
    			else if (e.getNodeName().equals("label")){
    				System.out.println(e.getTextContent());   				
    			}
    		}
    	}
    	return result;
	}

    private static boolean noMoreChildNodes(NodeList list) {
    	for (int s = 0; s < list.getLength(); s++){
    		Node n=list.item(s);
    		if(n.getNodeType() == Node.ELEMENT_NODE){
    			Element e=(Element)n;
    			if(e.getNodeName().equals("node")) return false;
    		}
    	}
    	return true;
	}




	private static Element getChildTag(Element e,String tag)
    {
      NodeList list= e.getChildNodes();
      for (int i = 0; i < list.getLength(); i++) {
  		  if (list.item(i).getNodeType() == Node.ELEMENT_NODE) {
          Element eChild = (Element)list.item(i);
          if (eChild.getNodeName()==tag){
            return eChild;
          }
        }
      }
      return null;
    }

}