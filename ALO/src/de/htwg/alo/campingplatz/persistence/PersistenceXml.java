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
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Calendar;

public class PersistenceXml {
	public static void belegungenToXml(ArrayList<Stellplatz> stellplaetze, String outPath) {

		// XML-header
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<Campingplatz>");
		int stellplatzNr = 0;
		ArrayList<Stellplatz> arrayOfStellplatz = stellplaetze;
		int j = stellplaetze.size();
		for (int i = 0; i < j; i++) {
			Stellplatz stellplatz = arrayOfStellplatz.get(i);
			stellplatzNr++;

			Stellplatz.DateItem currItem = stellplatz.getBelegungen();
			while (currItem != null) {
				System.out.println("CurrItem fuer Xml: " + DateUtil.getInstance().formatDate(currItem.getDate()));
				Stellplatz.DateItem myFirstItem = currItem;
				sb.append("<Belegung>");
				sb.append("<Stellplatz>" + stellplatzNr + "</Stellplatz>");
				sb.append("<DatumVon>"
						+ DateUtil.getInstance().formatDate(currItem.getDate())
						+ "</DatumVon>");
				int dauer = 1;
				String name = currItem.getName();
				String zusatzInfos = currItem.getZusatzInfos();
				Calendar calCurrItem = Calendar.getInstance();
				calCurrItem.setTime(currItem.getDate());
				int tagCurrItem = calCurrItem.get(Calendar.DAY_OF_YEAR);
				System.out.println(tagCurrItem);
				Calendar calCurrItemNext = Calendar.getInstance();
	
				while ((currItem.next != null)
						&& (currItem.next.getName().equals(currItem.getName()))) {
					calCurrItemNext.setTime(currItem.next.getDate());
					int tagCurrItemNext = calCurrItemNext.get(Calendar.DAY_OF_YEAR);
					System.out.println(tagCurrItemNext);
					if(tagCurrItemNext-tagCurrItem == 1){
						dauer++;
						currItem = currItem.next;
						calCurrItem.setTime(currItem.getDate());
						tagCurrItem = calCurrItem.get(Calendar.DAY_OF_YEAR);
					}else{
						break;
					}
				}
				sb.append("<Dauer>" + dauer + "</Dauer>");
				sb.append("<Name>" + name + "</Name>");
				sb.append("<Zusatzinformationen>" + zusatzInfos + "</Zusatzinformationen>");
				sb.append("</Belegung>");
				
				currItem = currItem.next;    //V1.1 SoSe 2014 - Ersetzt Code unten: nur noch DatumVon in XML, vorher immer 2 Objekte pro Buchung
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
	public static void xmlToBelegung(Campingplatz cp, String inPath, String calendarDatum) {
		
		//logik
		readXmlForGUI(cp, inPath, calendarDatum); 
	
		
	}
	protected static void readXmlForGUI(Campingplatz cp, String path, String calendarDatum) {//V1.1 SoSe 2014
		try {

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("Belegung");
			System.out.println(nList.getLength());

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
									.getTextContent(), 
									eElement.getElementsByTagName("Zusatzinformationen").item(0).getTextContent(),
									calendarDatum);
					
				}
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}
}
