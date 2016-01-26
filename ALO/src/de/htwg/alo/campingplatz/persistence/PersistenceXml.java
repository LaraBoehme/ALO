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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

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
						&& (currItem.next.getName().equals(currItem.getName()) && currItem.next.getZusatzInfos().equals(currItem.getZusatzInfos()))) {
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
	
	public static ArrayList<String> readXmlForExcel(String path, Campingplatz cp, int jahr, int monat, int stellplatz, int auswahl){
		
		ArrayList<String> namen = new ArrayList<String>();
		ArrayList<String> zusatzInfos = new ArrayList<String>();
		ArrayList<String> belegungen = new ArrayList<String>();
		
		GregorianCalendar calGewollt = new GregorianCalendar();
		calGewollt.set(jahr, monat -1, 01);
		DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		
		namen.add("" + (stellplatz));
		System.out.println("Zu namen hinzugefügt: " + namen.get(0));
		zusatzInfos.add("");
		System.out.println("Zu zusatzInfos hinzugefügt: " + zusatzInfos.get(0));
		
		try {

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Belegung");
			
				for (int temp = 0; temp < nList.getLength(); temp++) {     //läuft über alle Belegungen in xml
					System.out.println("Belegungen hat diese Länge: " + belegungen.size());
					
					Node nNode = nList.item(temp);

					if (nNode.getNodeType() == Node.ELEMENT_NODE) {
						Element eElement = (Element) nNode;
						
						if(Integer.parseInt(eElement.getElementsByTagName("Stellplatz").item(0).getTextContent()) == stellplatz){
							System.out.println("ja Stellplatz " + (stellplatz));
							
							String jahrBelegungString = eElement.getElementsByTagName("DatumVon").item(0).getTextContent().substring(6);
							int jahrBelegung = Integer.parseInt(jahrBelegungString);
							
							if( jahrBelegung == jahr){
								System.out.println("Ja Jahre stimmern überein");
								
								String monatBelegungString = eElement.getElementsByTagName("DatumVon").item(0).getTextContent().substring(3,5);
								int monatBelegung = Integer.parseInt(monatBelegungString);
								System.out.println("monatBelegung: "+ monatBelegung);
								
								String tagBelegungString = eElement.getElementsByTagName("DatumVon").item(0).getTextContent().substring(0,2);
								int tagBelegung = Integer.parseInt(tagBelegungString);
								
								System.out.println("tagBelegung: "+ tagBelegung);
							
								GregorianCalendar calBelegung = new GregorianCalendar();
								calBelegung.set(jahrBelegung, monatBelegung - 1, tagBelegung);
								System.out.println("Datum Belegung: " + calBelegung.getTime());
								
								if(calBelegung.before(calGewollt)){
									String dauerGewolltString = eElement.getElementsByTagName("Dauer").item(0).getTextContent();
									int dauerGewollt = Integer.parseInt(dauerGewolltString);
									System.out.println("dauerGewollt " + dauerGewollt);
									for(int i = 0; i < dauerGewollt; i++){
										int neuerMonatBelegung = calBelegung.get(Calendar.MONTH) + 1;
										if(neuerMonatBelegung == monat){
											belegungen.add(df.format(calBelegung.getTime()));
											belegungen.add(String.valueOf(dauerGewollt-i));
											belegungen.add(eElement.getElementsByTagName("Name").item(0).getTextContent());
											belegungen.add(eElement.getElementsByTagName("Zusatzinformationen").item(0).getTextContent());
											System.out.println("belegungen hat diese Länge: " + belegungen.size());
											break;
										}else{
											calBelegung.add(Calendar.DATE, 1);
										}
									}
								}
								
								if(monatBelegung == monat){
									System.out.println("Ja Monate stimmen überein");
									belegungen.add(eElement.getElementsByTagName("DatumVon").item(0).getTextContent());
									belegungen.add(eElement.getElementsByTagName("Dauer").item(0).getTextContent());
									belegungen.add(eElement.getElementsByTagName("Name").item(0).getTextContent());
									belegungen.add(eElement.getElementsByTagName("Zusatzinformationen").item(0).getTextContent());
									System.out.println("belegungen hat diese Länge: " + belegungen.size());
								}
							}
							
						}
					}
				}
				
					GregorianCalendar gc = new GregorianCalendar();
					gc.set(jahr, monat - 1, 01);			//gregorianCalendar wird wieder auf Anfang gesetzt
					int currentMonth = gc.get(GregorianCalendar.MONTH);
					
					int datumZaehler = 0;
					int dauerZaehler = 1;
					int namenZaehler = 2;
					int infosZaehler = 3;
					int anzahlBelegungen = belegungen.size();
					
					if(belegungen.size() != 0){
						System.out.println("Belegungen size ist größer null, nämlich " + belegungen.size());
						while(gc.get(Calendar.MONTH) == currentMonth){
							System.out.println("Datum: " + gc.getTime());
							
							if(anzahlBelegungen > 0){
								if(belegungen.get(datumZaehler).equalsIgnoreCase(df.format(gc.getTime()))){
									datumZaehler = datumZaehler + 4;
									for(int dauer = 0; dauer < Integer.parseInt(belegungen.get(dauerZaehler)); dauer++){
										namen.add(belegungen.get(namenZaehler));
										if(dauer == 0){
											zusatzInfos.add(belegungen.get(infosZaehler));
										}else{
											zusatzInfos.add("");
										}
										gc.add(Calendar.DATE, 1);
									}
									dauerZaehler = dauerZaehler + 4;
									namenZaehler = namenZaehler + 4;
									infosZaehler = infosZaehler + 4;
									anzahlBelegungen = anzahlBelegungen -4;
								}else{
									namen.add("");
									zusatzInfos.add("");
									gc.add(Calendar.DATE, 1);
								}
							}else{
								namen.add("");
								zusatzInfos.add("");
								gc.add(Calendar.DATE, 1);
							}	
					}
					
				}else{
					for(int j = (gc.getActualMinimum(Calendar.DAY_OF_MONTH)); j <= (gc.getActualMaximum(Calendar.DAY_OF_MONTH)); j++){
						namen.add("");
						zusatzInfos.add("");
					}
				}
				
			

		} catch (Exception e1) {
			// e1.printStackTrace();
		}
		
		if(auswahl == 1){
			return namen;	
		}else{
			return zusatzInfos;
		}
		
		
	}
}
