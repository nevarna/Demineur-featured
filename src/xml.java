//AUTEUR  : Navarna 
import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;
import java.util.*;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class xml {

	public xml(){}
	
	public  void ajouterElement (Joueur j){

		try {	
         File inputFile = new File("ranking.xml");
         DocumentBuilderFactory dbFactory 
            = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(inputFile);
       //  doc.getDocumentElement().normalize();
      		Node root = doc.getFirstChild();
         Element ajout = doc.createElement("player");
         Attr attr = doc.createAttribute("username");
         attr.setValue(j.getPseudo());
         ajout.setAttributeNode(attr);
         Attr attr2 = doc.createAttribute("password");
         attr2.setValue(j.getMdp());
         ajout.setAttributeNode(attr2);
         root.appendChild(ajout);
         Element ajoutSuite = doc.createElement("points");
         ajoutSuite.appendChild(doc.createTextNode("\n			"+j.getPoint()+"\n"));
         ajout.appendChild(ajoutSuite);
         
         TransformerFactory transformerFactory = 
         TransformerFactory.newInstance();
         Transformer transformer = transformerFactory.newTransformer();
         DOMSource source = new DOMSource(doc);
         System.out.println("-----------Modified File-----------");
         StreamResult consoleResult = new StreamResult(System.out);
         transformer.transform(source, consoleResult);
      } catch (Exception e) {
         e.printStackTrace();
      }

	}

	public List<Joueur> lireElement () {
		List <Joueur> joueur = Collections.synchronizedList(new ArrayList<Joueur>()); 
      try {	
         File inputFile = new File("ranking.xml");
         DocumentBuilderFactory dbFactory 
            = DocumentBuilderFactory.newInstance();
         DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
         Document doc = dBuilder.parse(inputFile);
         doc.getDocumentElement().normalize();
         NodeList nList = doc.getElementsByTagName("player");
         for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
               Element eElement = (Element) nNode;
               String pseudo = eElement.getAttribute("username");
               String mdp = eElement.getAttribute("password") ;
               String point =  eElement.getElementsByTagName("points").item(0).getTextContent();
               String [] p = point.split("\n");
               String [] p2 = p[1].split("	");
              	int intpoint = Integer.parseInt(p2[3]); 
               Joueur j = new Joueur(pseudo,mdp,intpoint);
              	joueur.add(j);
            	}
         	}
      	} 
      	catch (Exception e) {
         e.printStackTrace();
      	}
      	return joueur; 
      }
	
}
