package attack_trees;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException; 
import org.xml.sax.helpers.DefaultHandler;

import factoring.Expression;
import factoring.FactoredExpression;
import factoring.Factorizations;
import factoring.Mapping;

public class ComputerMadeIPTVTree{

	public static void main (String argv []){
		
	    try {
	    	//System.setOut(new PrintStream(new File("out.txt")));
	    	//System.setErr(new PrintStream(new File("error.txt")));
	    	ComputerMadeIPTVTree o = new ComputerMadeIPTVTree();
	        o.buildExpression("IPTV_Attack_Tree.xml");
	        //o.buildExpression("IPTV.xml");
	        //o.buildExpression("branch1.xml");
	        //o.buildExpression("branch2.xml");
	        //o.buildExpression("IPTV_scenario.xml");
	        
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
	      //System.out.println("The expression is");
	      //printFactoredExpression(expression, dictionary, dualDictionary);
	      
	      System.out.println("De-factorizing expression");
	      Expression e = expression.normalize();
	      System.out.println("The normalized expression has "+e.totalNumberOfLiterals()+" literals");
	      System.out.println("The normalized expression has size = "+e.size());
	      //System.out.println("The expression is");
	      //printFactoredExpression(e, dictionary, dualDictionary);

	      System.out.println("Factorizing expression");
	      FactoredExpression f = e.factorize();
	      System.out.println("The factorized expression has "+f.totalNumberOfLiterals()+" literals");
	      System.out.println("The factorized expression has size = "+f.size());
	      /*Factorizations factors = new Factorizations(e);
	      int optimal = Integer.MAX_VALUE;
	      FactoredExpression best = null; 
	      while (factors.hasNext()){
	    	  FactoredExpression next = factors.next();
			  if (next.totalNumberOfLiterals() < optimal){
					best = next;
					optimal = next.totalNumberOfLiterals();
					System.out.println("Best found factorized expression has "+best.totalNumberOfLiterals()+" literals");
					System.out.println("Best found factorized expression has size = "+best.size());
			  }
	      }*/

	      System.out.println("The factorized expression is");
	      printFactoredExpression(f, dictionary, dualDictionary);

	      System.out.println("Printing tree");
	      
	      fileStream.close();


	      //printTree(f, dictionary, dualDictionary, "generated-"+fileName);
	}
	
	private void printFactoredExpression(FactoredExpression f,
			Map<String, Integer> dictionary,
			final Map<Integer, String> dualDictionary) {
		Expression.mapping = new Mapping() {
			
			@Override
			protected String getSpecificValue(int key) {
				return dualDictionary.get(key);
			}
		};
		System.out.println(f.toString());
	}

	private void printFactoredExpression(Expression f,
			Map<String, Integer> dictionary,
			final Map<Integer, String> dualDictionary) {
		Expression.mapping = new Mapping() {
			
			@Override
			protected String getSpecificValue(int key) {
				return dualDictionary.get(key);
			}
		};
		System.out.println(f.toString());
	}




	private void printTree(FactoredExpression f,
			Map<String, Integer> dictionary, Map<Integer, String> dualDictionary, String filename) {
	    FileOutputStream out = new FileOutputStream(new File(filename));
	    try{
	      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
	      DocumentBuilder builder = factory.newDocumentBuilder();
	      //builder.setEntityResolver(new EntityManager());
	      Document doc = builder.newDocument();
	      doc.setXmlStandalone(true);
	      Element rootElement = doc.createElement("adtree");
			  doc.appendChild(rootElement);

	      Element node = transform(tree.getRoot(true),doc);
	      rootElement.appendChild(node);
	      //exporting domains
	      prettyPrint(doc,out);
	      out.close();
	    }
	    catch (ParserConfigurationException pce) {
	      pce.printStackTrace();
		  }
	    catch (SAXException e) {
	      e.printStackTrace();
		  }
	    catch (IOException e) {
	      e.printStackTrace();
	    }

	}

	private Element transform(FactoredExpression node, Document doc)
	{
		Element result = doc.createElement("node");
		Element label = doc.createElement("label");
		label.insertBefore(doc.createTextNode("no label"), label.getLastChild());
		result.appendChild(label);
		if (node.isASum()){
			result.setAttribute("refinement","disjunctive");
		}
		else{
			result.setAttribute("refinement","conjunctive");
		}
		List<ADTreeNode> children= tree.getChildrenList(node,true);
		ADTNode term=node.getTerm();
		//adding values from domains
		if(Options.main_saveDomains){
			for (Integer i:domains.keySet()){
				ValuationDomain<?> vd=domains.get(i);
				String domainId=vd.getDomain().getClass().getSimpleName()+new Integer(i.intValue()+1).toString();
				if(term.isEditable(vd.getDomain())){//modifable values
					if(vd.getValue(node)!=null){
						result.appendChild(createParameter(doc,domainId,"basic",vd.getValue(node).toString()));
					}
				}
				else{
					if(Options.main_saveDerivedValues){
						if ((term.getType() == ADTNode.Type.CP || term.getType() == ADTNode.Type.CO)
								&& ((ADTNode)term.getChildren().elementAt(0)).getChildren().size()==0) {
							String value = vd.getTermValue((ADTNode) term.getChildren().elementAt(0)).toString();
							result.appendChild(createParameter(doc,domainId,"default",value));
						}
						else {
							result.appendChild(createParameter(doc,domainId,"derived",vd.getTermValue(node.getTerm()).toString()));
						}
					}
				}
				if ((term.getType() == ADTNode.Type.CP || term.getType() == ADTNode.Type.CO)
						&& ((ADTNode)term.getChildren().elementAt(0)).getChildren().size()==0
						&& Options.main_saveDerivedValues && vd.isShowAllLabels()) {
					result.appendChild(createParameter(doc,domainId,"derived",vd.getTermValue(node.getTerm()).toString()));
				}
			}
		}
		Iterator<ADTreeNode> iterator=children.iterator();
		while (iterator.hasNext()) {
			result.appendChild(transform(iterator.next(),doc));
		}
		return result;
	}


	public static final void prettyPrint(Document xml, FileOutputStream out) {
		try{
			Transformer tf = TransformerFactory.newInstance().newTransformer();
			tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tf.setOutputProperty(OutputKeys.INDENT, "yes");
			tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			tf.transform(new DOMSource(xml), new StreamResult(out));
		}
		catch (TransformerException tfe) {
			tfe.printStackTrace();
		}
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