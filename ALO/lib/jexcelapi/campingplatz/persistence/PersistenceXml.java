package de.htwg.alo.campingplatz.persistence;

import de.htwg.alo.campingplatz.gui.Visitors;
import de.htwg.alo.campingplatz.model.Campingplatz;
import de.htwg.alo.campingplatz.model.Stellplatz;
import de.htwg.alo.campingplatz.model.Stellplatz.DateItem;
import de.htwg.alo.campingplatz.util.DateUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PersistenceXml {
	public static void belegungenToXml(Stellplatz[] stellplaetze, String outPath) {

		// XML-header
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<Campingplatz>");
		int stellplatzNr = 0;
		Stellplatz[] arrayOfStellplatz = stellplaetze;
		int j = stellplaetze.length;
		for (int i = 0; i < j; i++) {
			Stellplatz stellplatz = arrayOfStellplatz[i];
			stellplatzNr++;

			Stellplatz.DateItem currItem = stellplatz.getBelegungen();
			while (currItem != null) {
				Stellplatz.DateItem myFirstItem = currItem;
				sb.append("<Belegung>");
				sb.append("<Stellplatz>" + stellplatzNr + "</Stellplatz>");
				sb.append("<DatumVon>"
						+ DateUtil.getInstance().formatDate(currItem.getDate())
						+ "</DatumVon>");
				int dauer = 1;
				String name = currItem.getName();
				while ((currItem.next != null)
						&& (currItem.next.getName().equals(currItem.getName()))) {
					dauer++;
					currItem = currItem.next;
				}
				sb.append("<Dauer>" + dauer + "</Dauer>");
				sb.append("<Name>" + name + "</Name>");
				sb.append("</Belegung>");
				
				currItem = currItem.next;//V1.1 SoSe 2014 - Ersetzt Code unten: nur noch DatumVon in XML, vorher immer 2 Objekte pro Buchung
				//if (currItem == myFirstItem) {
				//	currItem = currItem.next;
				//}
			}
		}
		sb.append("</Campingplatz>");

		File file = new File(outPath);
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(sb.toString());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//1.1 SOSE14 Neu
	public static void xmlToBelegung(Campingplatz cp, String inPath) {
		
		//logik
		readXmlForGUI(cp, inPath); 
	
		
	}
	protected static void readXmlForGUI(Campingplatz cp, String path) {//V1.1 SoSe 2014
		try {

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Belegung");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					
					cp.belegeStellplatzAufGUI(
							Integer.parseInt(eElement
									.getElementsByTagName("Stellplatz").item(0)
									.getTextContent()) - 1,
							DateUtil.getInstance().formatString(
									eElement.getElementsByTagName("DatumVon")
											.item(0).getTextContent()), Integer
									.parseInt(eElement
											.getElementsByTagName("Dauer")
											.item(0).getTextContent()),
							eElement.getElementsByTagName("Name").item(0)
									.getTextContent());
					
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
