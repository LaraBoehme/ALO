package de.htwg.alo.campingplatz.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.htwg.alo.campingplatz.controller.CheckAvailabilitySimple;
import de.htwg.alo.campingplatz.model.Campingplatz;
import de.htwg.alo.campingplatz.persistence.JavaToExcel;
import de.htwg.alo.campingplatz.util.DateUtil;

import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Toolkit;

public class MainFrame {

	int anzahlStellplaetze = 20; // Default-Einstellung
	Campingplatz cp = new Campingplatz(anzahlStellplaetze, new CheckAvailabilitySimple());
	
	private JFrame frmCampingplatzVerwaltung;
	private JLabel lblProtocol;
	private JTextField txtField_name;
	private JTextField txtField_name_del;
	
	private String chosenXml = "";
	private String ausgewaehlteXml = "";
	private File dataFolder;
	
	Calendar cal = new GregorianCalendar(); // new

	public static DefaultTableModel dtm; // V1.1 SOSE 2014 - Neu für Oberfläche

	private final String[] months = {
			// "Januar", "Februar", "M\u00E4rz", //SoSe 2014 - da Oberfläche //
			// WS 14/15
			// nur April - September
			"April", "Mai", "Juni", "Juli", "August", "September"
			// , "Oktober", "November", "Dezember" //SoSe 2014 - da Oberfläche
			// nur
			// April - September
	};

	private final String[] stellplaetze = { "1", "2", "3", "4", "5", "6", "7", // WS
																				// 14/15       //Default-Einstellung
			"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20" };
	
	String[] columnName = new String[184];  // um die Spalten der Belegungsplan-Anzeige zu füllen
	
	public static String myDate = "";
	
	/**
	 * Launch the application.
	 */
	
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					MainFrame window = new MainFrame();
					window.frmCampingplatzVerwaltung.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	
	public MainFrame() {
		initialize();
		if (new File(chosenXml).exists()){
			initializeOberflaeche();
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {

		// Label fuer Protokolle -- um anzuzeigen, ob Belegungsdatei gefunden
		// wurde oder nicht
		// Lara geändert damit man das Label sieht & alles richtig angezeigt
		// wird oder erstellt wird
		lblProtocol = new JLabel();
		lblProtocol.setBounds(10, 780, 250, 20);

		File jarFile = new File(MainFrame.class.getProtectionDomain().getCodeSource().getLocation().getPath());

		dataFolder = new File(jarFile.getAbsolutePath().replace(jarFile.getName(), "") + "data/");

		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}

		if (new File(dataFolder.getAbsolutePath() + "/Belegungen.xml").exists()) {
			chosenXml = dataFolder.getAbsolutePath() + "/Belegungen.xml";
			readXml(chosenXml);
			lblProtocol.setText("Belegungsdatei erfolgreich geladen...");

		} else {
			lblProtocol.setText("Keine Belegungsdatei gefunden...");
			chosenXml = dataFolder.getAbsolutePath() + "/Belegungen.xml";
		}

		// JFrame Campingplatz wird erstellt
		frmCampingplatzVerwaltung = new JFrame();
		frmCampingplatzVerwaltung
				.setIconImage(Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("camping.png")));
		frmCampingplatzVerwaltung.setTitle("Campingplatz - Verwaltung");
		frmCampingplatzVerwaltung.setResizable(true);// 1.1 SOSE 2014 - Changed
														// to true
		frmCampingplatzVerwaltung.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frmCampingplatzVerwaltung.getContentPane().setLayout(null); // liefert
																	// ContentPane
																	// des
																	// JFrame
																	// Campingplatz
		frmCampingplatzVerwaltung.getContentPane().add(lblProtocol); // lblProtocol
																		// wird
																		// dem
																		// ContentPane
																		// hinzugefuegt

		// ****************************************************************************************************************************
		frmCampingplatzVerwaltung.setLayout(new BorderLayout());
		frmCampingplatzVerwaltung.setPreferredSize(new Dimension(1615, 700));// 1.1
																				// SOSE
																				// 2014
																				// -
																				// from
																				// 1900,1200
																				// because
																				// of
																				// Mac
																				// resolution

		// TabbedPane fuer die unterschiedlichen Tabs wird erstellt und dem
		// ContentPane hinzugefuegt
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1280, 720);// 1.1 SOSE 2014 - Changed size of
												// Content Container, Changed
												// from (0, 0, 434, 272)
		tabbedPane.setSize(1280, 720);
		frmCampingplatzVerwaltung.getContentPane().add(tabbedPane);

		// Sebi: Kalender auf aktuelles Datum setzten - wird nicht benötigt für
		// belegungsplan
		if (cal.get(Calendar.MONTH) < 4) {
			cal.set(cal.get(Calendar.YEAR), 3, 1);

		}
		if (cal.get(Calendar.MONTH) > 9) {
			cal.set(cal.get(Calendar.YEAR) + 1, 3, 1);

		} else {

			cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		}

		// 1.Buchen
		JPanel panelBuchen = new JPanel();
		tabbedPane.addTab("Buchen", null, panelBuchen, null);
		panelBuchen.setLayout(null);

		// Wie heisst der neue Gast
		JLabel txtpnWieHeitDer = new JLabel();
		txtpnWieHeitDer.setBounds(10, 6, 190, 20);
		txtpnWieHeitDer.setText("Wie hei\u00DFt der neue Gast?");
		panelBuchen.add(txtpnWieHeitDer);

		txtField_name = new JTextField();
		txtField_name.setHorizontalAlignment(SwingConstants.LEFT);
		txtField_name.setBounds(236, 6, 183, 30);
		panelBuchen.add(txtField_name);

		// Wann wird der Gast anreissen
		JLabel txtpnWannWirdDer = new JLabel();
		txtpnWannWirdDer.setText("Wann wird der Gast anreisen?");
		txtpnWannWirdDer.setBounds(10, 49, 198, 20);
		panelBuchen.add(txtpnWannWirdDer);

		// Tag des Monats
		final JComboBox comboBox_tag = new JComboBox();
		comboBox_tag.setModel(new DefaultComboBoxModel());
		comboBox_tag.setBounds(236, 49, 70, 25);
		for (int n = cal.getActualMinimum(Calendar.DAY_OF_MONTH); n <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); n++) {
			comboBox_tag.addItem(n);
		}
		// Sebi aktueller tag des Monats sofern innerhalb von April-September
		comboBox_tag.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH) - 1);
		panelBuchen.add(comboBox_tag);

		// Monat
		final JComboBox comboBox_monat = new JComboBox();
		comboBox_monat.setModel(new DefaultComboBoxModel(months));
		comboBox_monat.setBounds(300, 49, 120, 25);
		// Sebi aktueller Monat, da Array nur aus 6 Monaten besteht, und April =
		// 3 daher -3
		comboBox_monat.setSelectedIndex(cal.get(Calendar.MONTH) - 3);
		panelBuchen.add(comboBox_monat);
		comboBox_monat.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
					comboBox_tag.removeAllItems();
				cal.set(Calendar.MONTH, comboBox_monat.getSelectedIndex()+3);
				for (int n = cal.getActualMinimum(Calendar.DAY_OF_MONTH); n <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); n++) {
					comboBox_tag.addItem(n);
				}
			}
		});

		// Jahr
		final JComboBox comboBox_jahr = new JComboBox();
		comboBox_jahr.setModel(new DefaultComboBoxModel());
		comboBox_jahr.setBounds(415, 49, 100, 25);
		for (int n = cal.get(Calendar.YEAR); n <= cal.get(Calendar.YEAR) + 10; n++) {
			comboBox_jahr.addItem(n);
		}
		// Sebi aktuelles Jahr
		comboBox_jahr.setSelectedIndex(0);
		panelBuchen.add(comboBox_jahr);

		// Wie lange bleibt der Gast
		JLabel txtpnWieLangeBleibt = new JLabel();
		txtpnWieLangeBleibt.setText("Wie lange bleibt der Gast?");
		txtpnWieLangeBleibt.setBounds(10, 91, 198, 20);
		panelBuchen.add(txtpnWieLangeBleibt);

		// Dauer des Aufenthalts
		JTextField dauerBuchen = new JTextField();
		dauerBuchen.setBounds(236, 91, 68, 30);
		dauerBuchen.setHorizontalAlignment(JTextField.CENTER);
		panelBuchen.add(dauerBuchen);

		JLabel lblTage = new JLabel("Tag(e)");
		lblTage.setBounds(310, 98, 40, 16);
		panelBuchen.add(lblTage);

		// Auf welchem Stellplatz soll gebucht werden
		JLabel lblWelcherStellplatz = new JLabel(); /* WiSe14/15 */
		lblWelcherStellplatz.setVerticalAlignment(SwingConstants.TOP);
		lblWelcherStellplatz.setBounds(10, 140, 280, 32);
		lblWelcherStellplatz.setText("<html>Auf welchem Stellplatz soll<br> gebucht werden?");
		panelBuchen.add(lblWelcherStellplatz);
		
		JLabel lblStellPlatzNr = new JLabel("Stellplatz Nr. :");
		lblStellPlatzNr.setBounds(240, 140, 90, 15);
		panelBuchen.add(lblStellPlatzNr); /* WiSe14/15 */

		final JComboBox comboBox_StellPlatz = new JComboBox();
		comboBox_StellPlatz.setModel(new DefaultComboBoxModel(stellplaetze));
		comboBox_StellPlatz.setBounds(343, 135, 75, 25);
		panelBuchen.add(comboBox_StellPlatz);
		
		// Zusatzinformationen
		JLabel zusatzInfos = new JLabel("Zusatzinformationen:");
		zusatzInfos.setBounds(10, 190, 280, 30);
		panelBuchen.add(zusatzInfos);
		
		JTextField zusatzInfosEingabe = new JTextField();
		zusatzInfosEingabe.setBounds(236, 185, 183, 30);
		panelBuchen.add(zusatzInfosEingabe);

		// JLabel lblZulssigeberbuchungen = new JLabel( // WS14/15
		// auskommentiert, da nicht benötigt
		// "zul\u00E4ssige \u00DCberbuchungen");
		// lblZulssigeberbuchungen.setBounds(10, 200, 177, 14);
		// panel.add(lblZulssigeberbuchungen);

		// Strafkosten
		final JComboBox comboBox_over = new JComboBox();
		// comboBox_over.setModel(new DefaultComboBoxModel(new String[] { "0",
		// // WS14/15 auskommentiert, da nicht benötigt
		// "1", "2", "3", "4", "5", "6", "7", ">7" }));
		// comboBox_over.setBounds(236, 200, 70, 25);
		// panel.add(comboBox_over);

		// Button Buchen
		JButton btnBuchen = new JButton("Buchen");
		btnBuchen.setBounds(5, 240, 87, 28);
		panelBuchen.add(btnBuchen);

		// 2.Loeschen
		JPanel panelLoeschen = new JPanel();
		panelLoeschen.setToolTipText("");
		tabbedPane.addTab("L\u00F6schen", null, panelLoeschen, null);
		panelLoeschen.setLayout(null);

		// Welcher Gast soll geloescht werden
		JLabel lblwelcherGastSoll = new JLabel();
		lblwelcherGastSoll.setVerticalAlignment(SwingConstants.TOP);
		lblwelcherGastSoll.setBounds(10, 9, 169, 32);
		lblwelcherGastSoll.setText("<html>Welcher Gast soll gel\u00F6scht werden?");
		panelLoeschen.add(lblwelcherGastSoll);

		txtField_name_del = new JTextField();
		txtField_name_del.setBounds(240, 6, 179, 30);
		panelLoeschen.add(txtField_name_del);
		txtField_name_del.setColumns(10);

		// Auf welchem Stellplaty befindet sich der Gast
		JLabel lblAufWelchemStellplatz = new JLabel("<html>Auf welchem Stellplatz befindet sich der Gast?");
		lblAufWelchemStellplatz.setVerticalAlignment(SwingConstants.TOP);
		lblAufWelchemStellplatz.setBounds(10, 50, 192, 32);
		panelLoeschen.add(lblAufWelchemStellplatz);

		JLabel lblStellplatzNr = new JLabel("Stellplatz Nr. :");
		lblStellplatzNr.setBounds(240, 55, 90, 15);
		panelLoeschen.add(lblStellplatzNr);

		final JComboBox comboBox_sp = new JComboBox();
		comboBox_sp.setModel(new DefaultComboBoxModel(stellplaetze));
		comboBox_sp.setBounds(348, 49, 75, 25);
		panelLoeschen.add(comboBox_sp);

		// Ab welchem Tag soll geloescht werden
		JLabel lblabWelchemTag = new JLabel();
		lblabWelchemTag.setVerticalAlignment(SwingConstants.TOP);
		lblabWelchemTag.setBounds(10, 93, 183, 31);
		lblabWelchemTag.setText("<html>Ab welchem Tag soll gel\u00F6scht werden?");
		panelLoeschen.add(lblabWelchemTag);

		final JComboBox comboBox_tag_del = new JComboBox();
		comboBox_tag_del.setModel(new DefaultComboBoxModel());
		comboBox_tag_del.setBounds(240, 90, 70, 25);
		for (int n = cal.getActualMinimum(Calendar.DAY_OF_MONTH); n <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); n++) {
			comboBox_tag_del.addItem(n);
		}
		// Sebi aktueller tag des Monats sofern innerhalb von April-September
		comboBox_tag_del.setSelectedIndex(cal.get(Calendar.DAY_OF_MONTH) - 1);
		panelLoeschen.add(comboBox_tag_del);

		final JComboBox comboBox_monat_del = new JComboBox();
		comboBox_monat_del.setModel(new DefaultComboBoxModel(months));
		comboBox_monat_del.setBounds(305, 90, 120, 25);
		// Sebi aktueller Monat sofern innerhalb von April-September
		comboBox_monat_del.setSelectedIndex(cal.get(Calendar.MONTH) - 3);
		panelLoeschen.add(comboBox_monat_del);
		comboBox_monat_del.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				cal.set(Calendar.MONTH, comboBox_monat_del.getSelectedIndex()+3);
				comboBox_tag_del.removeAllItems();
				for (int n = cal.getActualMinimum(Calendar.DAY_OF_MONTH); n <= cal.getActualMaximum(Calendar.DAY_OF_MONTH); n++) {
					comboBox_tag_del.addItem(n);
				}
			}
		});

		final JComboBox comboBox_jahr_del = new JComboBox();
		comboBox_jahr_del.setModel(new DefaultComboBoxModel());
		comboBox_jahr_del.setBounds(420, 90, 100, 25);
		for (int n = cal.get(Calendar.YEAR); n <= cal.get(Calendar.YEAR) + 10; n++) {
			comboBox_jahr_del.addItem(n);
		}
		// Sebi aktuelles jahr
		comboBox_jahr_del.setSelectedIndex(0);
		panelLoeschen.add(comboBox_jahr_del);

		// Fuer wie viele Tage soll geloescht werden
		JLabel lblfrWieViele = new JLabel();
		lblfrWieViele.setVerticalAlignment(SwingConstants.TOP);
		lblfrWieViele.setBounds(10, 135, 192, 32);
		lblfrWieViele.setText("<html>F\u00FCr wie viele Tage soll gel\u00F6scht werden?");
		panelLoeschen.add(lblfrWieViele);
		
		JTextField dauerLoeschen = new JTextField();
		dauerLoeschen.setBounds(240, 132, 70, 25);
		dauerLoeschen.setHorizontalAlignment(JTextField.CENTER);
		panelLoeschen.add(dauerLoeschen);

		JLabel label_3 = new JLabel("Tag(e)");
		label_3.setBounds(315, 135, 50, 16);
		panelLoeschen.add(label_3);

		// Button Loeschen
		JButton buttonDel = new JButton("L\u00F6schen");
		buttonDel.setBounds(5, 200, 89, 25);
		panelLoeschen.add(buttonDel);

		// 3.Excel
		JPanel panelExcel = new JPanel();
		tabbedPane.addTab("Excel", null, panelExcel, null);
		panelExcel.setLayout(null);

		// Fuer welchen Monat soll ein Belegungsplan gedruckt werden
		JLabel txtpnFrWelchenMonat = new JLabel(
				"<html>F\u00FCr welchen Monat soll ein Belegungsplan gedruckt werden?</html>");
		txtpnFrWelchenMonat.setBounds(10, 11, 205, 34);
		panelExcel.add(txtpnFrWelchenMonat);

		final JComboBox comboBoxMonth = new JComboBox();
		comboBoxMonth.setToolTipText("");
		comboBoxMonth.setModel(new DefaultComboBoxModel(months));
		comboBoxMonth.setBounds(280, 11, 120, 25);
		// Sebi aktueller Monat sofern innerhalb von April-September
		comboBoxMonth.setSelectedIndex(cal.get(Calendar.MONTH) - 3);
		panelExcel.add(comboBoxMonth);

		// Fuer welches Jahr soll ein Belegungsplan gedruckt werden
		JLabel txtpnFrWelchesJahr = new JLabel();
		txtpnFrWelchesJahr.setText("<html>F\u00FCr welches Jahr soll ein Belegungsplan gedruckt werden?</html>");
		txtpnFrWelchesJahr.setBounds(10, 56, 205, 34);
		panelExcel.add(txtpnFrWelchesJahr);

		final JComboBox comboBoxYear = new JComboBox();
		comboBoxYear.setModel(new DefaultComboBoxModel());
		for (int n = cal.get(Calendar.YEAR) - 2; n <= cal.get(Calendar.YEAR) + 10; n++) {
			comboBoxYear.addItem(n);
		}
		// comboBoxYear.setSelectedIndex(30);
		comboBoxYear.setBounds(300, 56, 100, 25);
		// Sebi aktuelles jahr
		comboBoxYear.setSelectedIndex(cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) - 2));
		panelExcel.add(comboBoxYear);

		// Wo soll der Belegunsplan gespeichert werden
		JLabel txtpnWoSollDie = new JLabel();
		txtpnWoSollDie.setText("<html>Wo soll der Belegungsplan gespeichert werden?</html>");
		txtpnWoSollDie.setBounds(10, 101, 205, 34);
		panelExcel.add(txtpnWoSollDie);
		
		JButton btnNewButton_waehleSpeicherortBel = new JButton("Ausw\u00E4hlen");
		btnNewButton_waehleSpeicherortBel.setBounds(290, 100, 110, 25);
		panelExcel.add(btnNewButton_waehleSpeicherortBel);
				
		final JLabel lblSpeicherort = new JLabel("Speicherort:");
		lblSpeicherort.setBounds(10, 146, 89, 14);
		panelExcel.add(lblSpeicherort);

		final JLabel textPane_speicherOrtBel = new JLabel();
		textPane_speicherOrtBel.setBounds(141, 146, 278, 14);
		panelExcel.add(textPane_speicherOrtBel);
		textPane_speicherOrtBel.setBorder(LineBorder.createBlackLineBorder());
		
		// Speicherort waehlen
		btnNewButton_waehleSpeicherortBel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				JFileChooser chooser = new JFileChooser();
				chooser.setCurrentDirectory(new java.io.File(""));
				chooser.setDialogTitle("Speicherort ausw\u00E4hlen");
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				//
				// "Alle Datein"-Option deaktivieren
				//
				chooser.setAcceptAllFileFilterUsed(false);
				//
				int rueckgabewert = chooser.showOpenDialog(null);
				if (rueckgabewert == JFileChooser.APPROVE_OPTION) {
					textPane_speicherOrtBel.setText(chooser.getSelectedFile().getAbsolutePath());
				}

			}

		});

		// Button Erstellen Belegungsplan erstellen
		JButton btnErstellen = new JButton("Erstellen");
		btnErstellen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				if (!textPane_speicherOrtBel.getText().equalsIgnoreCase("")) {

					int monat = comboBoxMonth.getSelectedIndex() + 4;// SoSe
																		// 2014
																		// - +4
																		// da ab
																		// April
					int jahr = (Integer) comboBoxYear.getSelectedItem();

					new JavaToExcel().xmlExportToExcel(monat, jahr, cp, textPane_speicherOrtBel.getText(), chosenXml);
					JOptionPane.showMessageDialog(null,
							"Belegungsplan erfolgreich unter " + textPane_speicherOrtBel.getText() + " abgespeichert");
				} else {
					JOptionPane.showMessageDialog(null, "W\u00E4hlen Sie zuerst einen Speicherort aus!");
				}
			}
		});
		btnErstellen.setBounds(10, 192, 87, 28);
		panelExcel.add(btnErstellen);

		// 4.Belegungsplan
		JPanel btnPanel = new JPanel(); // V1.1 SOSE014 - Neu
		btnPanel.setBounds(tabbedPane.getBounds());
		JPanel panelBelegungsplan = new JPanel();
		panelBelegungsplan.setPreferredSize(tabbedPane.getSize());// SoSe 2014 -
																	// new
		frmCampingplatzVerwaltung.add(new JScrollPane(tabbedPane));
		frmCampingplatzVerwaltung.pack();
		tabbedPane.addTab("Belegungsplan", null, panelBelegungsplan, null);

		// Sebi: Jahr auswählen für Tabelle
		JLabel txtpnjahr = new JLabel("<html>Belegungsplan anzeigen für Jahr: </html>");
		txtpnjahr.setBounds(10, 11, 205, 34);
		panelBelegungsplan.add(txtpnjahr);

		// Sebi: combobox einfügen für auswahl des jahres
		final JComboBox<Integer> comboBoxYear2 = new JComboBox<Integer>();

		for (int n = cal.get(Calendar.YEAR) - 2; n <= cal.get(Calendar.YEAR) + 10; n++) {
			comboBoxYear2.addItem(n);
		}

		// Sebi: wird nur einmal beim start aufgerufen, zeigt aktuelles jahr an
		comboBoxYear2.setSelectedIndex(cal.get(Calendar.YEAR) - (cal.get(Calendar.YEAR) - 2));
		panelBelegungsplan.add(comboBoxYear2);

		// Sebi: Start der tabelle festlegen
		GregorianCalendar first = new GregorianCalendar((int) comboBoxYear2.getSelectedItem(), // change
																								// "2014"
				GregorianCalendar.APRIL, 1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.YYYY");

		String[] rowName = new String[40];
		
		columnName[0] = "Stellplatz";
		// Sebi: Tag +1 für Tabelle im Tool
		for (int i = 1; i < columnName.length; i++) {
			columnName[i] = sdf.format(first.getTime());
			first.add(Calendar.DAY_OF_MONTH, 1);
		}

		dtm = new DefaultTableModel(columnName, rowName.length);

		int subtrahend = 0;
		for (int i = 0; i < rowName.length; i++) {
			if(i==0){
				dtm.setValueAt((i+1), i, 0);	
			}else{
				if(i%2==0){
					dtm.setValueAt(i-subtrahend, i, 0);
					subtrahend++;
				}else{
					dtm.setValueAt(" ", i, 0);
				}
			}
		}

		JTable table = new JTable(dtm);
		table.setEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollVert = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		table.setVisible(true);// SoSe 2014 - added
		table.setAutoResizeMode(table.AUTO_RESIZE_OFF);

		// um erste Spalte mit Stellplatz zu fixieren beim horizontalem Scrollen
		JTable fixed = new JTable();
		fixed.setAutoCreateColumnsFromModel(false);
		fixed.setModel(table.getModel());
		fixed.setSelectionModel(table.getSelectionModel());
		fixed.setEnabled(false);
		TableColumnModel columnModel = table.getColumnModel();
		TableColumn coulumn = columnModel.getColumn(0);
		columnModel.removeColumn(coulumn);
		fixed.getColumnModel().addColumn(coulumn);
		fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
		fixed.getColumnModel().getColumn(0).setResizable(false);
		fixed.setGridColor(Color.LIGHT_GRAY);
		scrollVert.setRowHeaderView(fixed);
		scrollVert.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());

		scrollVert.setPreferredSize(tabbedPane.getSize());

		table.setGridColor(Color.LIGHT_GRAY);
		panelBelegungsplan.add(scrollVert, BorderLayout.SOUTH);
		panelBelegungsplan.add(btnPanel, BorderLayout.NORTH);

		Toolkit tk = Toolkit.getDefaultToolkit();
		frmCampingplatzVerwaltung.setSize((int) tk.getScreenSize().getWidth() - 11,
				(((int) tk.getScreenSize().getHeight()) - 100));
		frmCampingplatzVerwaltung.setDefaultCloseOperation(3);
		frmCampingplatzVerwaltung.setVisible(true);

		// Sebi: Itemlistener checkbox year

		comboBoxYear2.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				
//				Lara Inhalt von Tabelle löschen außer Kopfzeile und -spalte
				if (new File(chosenXml).exists()){
					resetOberflaeche();
				}
				
				// first richtig setzten als selektierte jahreszahl
				GregorianCalendar first = new GregorianCalendar((int) e.getItem(), // change
																					// "2014"
						GregorianCalendar.APRIL, 1);
				// Tabelle befüllen
				for (int i = 1; i < columnName.length; i++) {
					columnName[i] = sdf.format(first.getTime());
					first.add(Calendar.DAY_OF_MONTH, 1);
				}
				dtm.setColumnIdentifiers(columnName);
				
				// um erste Spalte mit Stellplatz zu fixieren beim horizontalem Scrollen
				JTable fixed = new JTable();
				fixed.setAutoCreateColumnsFromModel(false);
				fixed.setModel(table.getModel());
				fixed.setSelectionModel(table.getSelectionModel());
				fixed.setEnabled(false);
				TableColumnModel columnModel = table.getColumnModel();
				TableColumn coulumn = columnModel.getColumn(0);
				columnModel.removeColumn(coulumn);
				fixed.getColumnModel().addColumn(coulumn);
				fixed.setPreferredScrollableViewportSize(fixed.getPreferredSize());
				fixed.getColumnModel().getColumn(0).setResizable(false);
				fixed.setGridColor(Color.LIGHT_GRAY);
				scrollVert.setRowHeaderView(fixed);
				scrollVert.setCorner(JScrollPane.UPPER_LEFT_CORNER, fixed.getTableHeader());
				
				//Lara Belegungsplan richtig anzeigen für spezielles Jahr
				if (new File(chosenXml).exists()){
					initializeOberflaeche();
				}

			}
		});

		// SOSE14 - Oberflaeche

		// 5.Einstellungen
		JPanel panelEinstellungen = new JPanel();
		tabbedPane.addTab("Einstellungen", null, panelEinstellungen, null);
		panelEinstellungen.setLayout(null);

		// Lara hinzugefuegt Anzahl Stellplaetze EingabeFeld
		// Anzahl Stellplätze
		JLabel anzahlSp = new JLabel("Anzahl Stellpl\u00E4tze:");
		anzahlSp.setBounds(10, 10, 120, 20);
		panelEinstellungen.add(anzahlSp);

		JTextField eingabeSp = new JTextField();
		eingabeSp.setBounds(170, 10, 50, 20);
		eingabeSp.setToolTipText("Zahl eingeben");
		panelEinstellungen.add(eingabeSp);
		
		JButton ausfuehrenSp = new JButton("\u00dcbernehmen");
		ausfuehrenSp.setBounds(260, 10, 110, 25);
		panelEinstellungen.add(ausfuehrenSp);
		
		// Lara ActionListener für Anzahl Stellplätze verändern
				ausfuehrenSp.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						
						if (eingabeSp.getText().equalsIgnoreCase("")) {
							JOptionPane.showMessageDialog(null, "Sie müssen zuerst die Anzahl der Stellplätze eingeben!");
							return;
						}
							int anzahlStellplaetze = Integer.parseInt(eingabeSp.getText());
							cp.setAnzahlStellplaetze(anzahlStellplaetze);
							cp.aendereAnzahlStellplaetze(anzahlStellplaetze, chosenXml);
							
							String[] neueStellplaetze = new String[anzahlStellplaetze];
							for (int i = 0; i < cp.getAnzahlStellplaetze(); i++) {
								neueStellplaetze[i] = String.valueOf(i + 1);
							}
						
							String[] neueStellplaetzeFuerBelegungsplan = new String[anzahlStellplaetze*2];
							dtm.setRowCount(neueStellplaetzeFuerBelegungsplan.length);
							int subtrahend = 0;
							for (int i = 0; i < neueStellplaetzeFuerBelegungsplan.length; i++) {
								if(i==0){
									dtm.setValueAt((i+1), i, 0);	
								}else{
									if(i%2==0){
										dtm.setValueAt(i-subtrahend, i, 0);
										subtrahend++;
									}else{
										dtm.setValueAt(" ", i, 0);
									}
								}
							}
							comboBox_StellPlatz.setModel(new DefaultComboBoxModel(neueStellplaetze));
							comboBox_sp.setModel(new DefaultComboBoxModel(neueStellplaetze));
							
							if (new File(chosenXml).exists()){
								initializeOberflaeche();
							}
							JOptionPane.showMessageDialog(null,
									"<html>Die Anzahl der Stellplätze wurde auf " + eingabeSp
											.getText() + " geändert.");
						}

				});

		// Belegungsdatei einlesen
		JLabel belegdateiHochladen = new JLabel("Belegungsdatei einlesen:");
		belegdateiHochladen.setBounds(10, 45, 155, 20);
		panelEinstellungen.add(belegdateiHochladen);

		// Button Auswaehlen
		JButton btnAuswaehlen = new JButton("Ausw\u00E4hlen");
		btnAuswaehlen.setBounds(170, 45, 102, 25);
		panelEinstellungen.add(btnAuswaehlen);

		// Belegungsdatei
		final JLabel txtpnBelegungsdatei = new JLabel();
		txtpnBelegungsdatei.setText("Belegungsdatei:");
		txtpnBelegungsdatei.setBounds(10, 80, 102, 20);
		panelEinstellungen.add(txtpnBelegungsdatei);

		final JLabel textPane_dateiName = new JLabel();
		textPane_dateiName.setBounds(170, 80, 147, 20);
		panelEinstellungen.add(textPane_dateiName);
		textPane_dateiName.setBorder(LineBorder.createBlackLineBorder());

		// Button "EINLESEN"
		JButton btnEinlesen = new JButton("Einlesen");
		btnEinlesen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textPane_dateiName.getText().equalsIgnoreCase("")) {
					chosenXml = ausgewaehlteXml;
					resetOberflaeche();
					cp.newStellplaetze(cp.getAnzahlStellplaetze());
					readXml(chosenXml);
					initializeOberflaeche();
					JOptionPane.showMessageDialog(null, "Belegungsdatei erfolgreich eingelesen!");
				} else {
					JOptionPane.showMessageDialog(null, "Bitte w\u00E4hlen Sie zuerst eine Datei aus!");
				}

			}
		});
		btnEinlesen.setBounds(10, 111, 102, 25);
		panelEinstellungen.add(btnEinlesen);

		// Button "EINLESEN und ueberschreiben"
		JButton btnEinlesenUber = new JButton("Einlesen und \u00fcberschreiben");
		btnEinlesenUber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textPane_dateiName.getText().equalsIgnoreCase("")) {
					chosenXml = ausgewaehlteXml;
					resetOberflaeche();
					cp.newStellplaetze(cp.getAnzahlStellplaetze());
					readXml(chosenXml);
					initializeOberflaeche();
					
					if (new File(dataFolder.getAbsolutePath() + "/Belegungen.xml").exists()){
						File zuLoeschen = new File(dataFolder.getAbsolutePath() + "/Belegungen.xml");
						zuLoeschen.delete();
					}
					cp.belegungToXml(dataFolder.getAbsolutePath() + "/Belegungen.xml");
					File alteDatei = new File(chosenXml);
					alteDatei.delete();
					chosenXml = dataFolder.getAbsolutePath() + "/Belegungen.xml";
			
					JOptionPane.showMessageDialog(null, "Belegungsdatei erfolgreich eingelesen und alte Belegungsdatei \u00fcberschrieben!");
				} else {
					JOptionPane.showMessageDialog(null, "Bitte w\u00E4hlen Sie zuerst eine Datei aus!");
				}

			}
		});

		btnEinlesenUber.setBounds(140, 111, 202, 25);
		panelEinstellungen.add(btnEinlesenUber);

		// Klasse, um bei der Auswahl des Belegungsplans nur XML-Dateien
		// anzuzeigen
		class MyFilter extends FileFilter {
			private String endung;

			public MyFilter(String endung) {
				this.endung = endung;
			}

			@Override
			public boolean accept(File f) {
				if (f == null)
					return false;

				// Ordner anzeigen
				if (f.isDirectory())
					return true;

				// true, wenn File gewuenschte Endung besitzt
				return f.getName().toLowerCase().endsWith(endung);
			}

			@Override
			public String getDescription() {
				return endung + " only";
			}
		}

		// button 'AUSWAEHLEN'
		btnAuswaehlen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jc = new JFileChooser("data");
				jc.setFileFilter(new MyFilter(".xml"));
				int rueckgabewert = jc.showOpenDialog(null);
				if (rueckgabewert == JFileChooser.APPROVE_OPTION) {
					ausgewaehlteXml = jc.getSelectedFile().getAbsolutePath();
					System.out.println("Datei "+jc.getSelectedFile().getAbsolutePath() +" zum einlesen ausgewählt");
					textPane_dateiName.setText(jc.getSelectedFile().getName());
				}
			}
		});

		// -----------------------------------------------

		// 6.Auslastung //WS14/15
		final JPanel panelAuslastung = new JPanel();
		tabbedPane.addTab("Auslastung", null, panelAuslastung, null);
		panelAuslastung.setLayout(null);

		String lblText = "<html>";
		for (String month : months) {
			Set<String> belegungen = cp.getAllBelegungen(month);

			lblText += "Auslastung Monat " + month + ": " + belegungen.size() + "<br>";
		}
		lblText += "</html>";

		JLabel lblAuslastung = new JLabel(); /* WiSe14/15 */
		lblAuslastung.setVerticalAlignment(SwingConstants.TOP);
		lblAuslastung.setBounds(10, 10, 280, 192);
		lblAuslastung.setText(lblText);
		panelAuslastung.add(lblAuslastung); // WS14/15

		// ------------------------------------------------

		// Ereignisverarbeitung

		// button 'BUCHEN'
		btnBuchen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int limit = comboBox_over.getSelectedIndex();

				if (txtField_name.getText().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null, "Sie müssen einen Namen angeben!");
					return;
				}
				if (dauerBuchen.getText().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null, "Sie müssen noch angeben, wie lange der Gast bleibt!");
					return;
				}
				
				if (comboBox_tag.getSelectedIndex() <= 8) {
					myDate = "0" + (comboBox_tag.getSelectedIndex() + 1) + ".";
				} else {
					myDate = "" + (comboBox_tag.getSelectedIndex() + 1) + ".";
				}
				if (comboBox_monat.getSelectedIndex() <= 8) {
					myDate = myDate + "0" + (comboBox_monat.getSelectedIndex() + 4) + ".";// SoSe
																							// 2014
																							// -
																							// Monat
																							// +4,
																							// da
																							// ab
																							// April
				} else {
					myDate = myDate + (comboBox_monat.getSelectedIndex() + 4)// SoSe
																				// 2014
																				// -
																				// Monat
																				// +4,
																				// da
																				// ab
																				// April
							+ ".";
				}
				myDate = myDate + comboBox_jahr.getSelectedItem();

				boolean checker = cp.checkAvailability(
						DateUtil.getInstance() // WS 14/15
								.formatString(myDate),
						(Integer.parseInt(dauerBuchen.getText())), limit, txtField_name.getText(),
						comboBox_StellPlatz.getSelectedIndex(), zusatzInfosEingabe.getText());

				if (checker == false) { // wenn Stellplatz belegt ist

					JOptionPane.showMessageDialog(null, "<html> Platz " + (comboBox_StellPlatz.getSelectedIndex() + 1)
							+ " ist belegt." + "<br>Freie Tage werden trotzdem gebucht.");

					JOptionPane.showMessageDialog(null, "<html>Überbuchungen überprüfen und von Hand eintragen.");

					cp.checkAvailability(DateUtil.getInstance().formatString(myDate),
							(Integer.parseInt(dauerBuchen.getText())), limit, txtField_name.getText(),
							comboBox_StellPlatz.getSelectedIndex(),zusatzInfosEingabe.getText());

					cp.belegungToXml(chosenXml);
					initializeOberflaeche(); // WS 14/15

				} else { // checker = true (Platz ist frei)

					cp.checkAvailability(DateUtil.getInstance().formatString(myDate),
							(Integer.parseInt(dauerBuchen.getText())), limit, txtField_name.getText(), comboBox_StellPlatz
									.getSelectedIndex(), zusatzInfosEingabe.getText()); /* WiSe14/15 */

					JOptionPane.showMessageDialog(null,
							"<html>Buchung erfolgreich!<br>Um die Änderungen anzuzeigen, bitte Belegungsplan erneut erstellen!");
					cp.belegungToXml(chosenXml);

					initializeOberflaeche();
				}

				String lblText = "<html>"; // WS 14/15
				for (String month : months) {
					Set<String> belegungen = cp.getAllBelegungen(month);

					lblText += "Auslastung Monat " + month + ": " + belegungen.size() + "<br>";
				}
				lblText += "</html>";

				JLabel lblAuslastung = new JLabel(); /* WiSe14/15 */
				lblAuslastung.setVerticalAlignment(SwingConstants.TOP);
				lblAuslastung.setBounds(10, 10, 280, 192);
				lblAuslastung.setText(lblText);
				panelAuslastung.removeAll();
				panelAuslastung.add(lblAuslastung);
				panelAuslastung.updateUI();

				return; // WS 14/15
			}
		});

		// button 'LOESCHEN'
		buttonDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				
				if (txtField_name_del.getText().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null, "Geben Sie den Namen des zu löschenden Gastes ein!");
					return;
				}
				
				if (dauerLoeschen.getText().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null, "Sie müssen noch eingeben, für wie viele Tage der Gast gelöscht werden soll. ");
					return;
				}
				
				int dauer = Integer.parseInt(dauerLoeschen.getText());
				int platz = comboBox_sp.getSelectedIndex() + 1;

				String myDate = "";

				if (comboBox_tag_del.getSelectedIndex() <= 8) {
					myDate = "0" + (comboBox_tag_del.getSelectedIndex() + 1) + ".";
				} else {
					myDate = "" + (comboBox_tag_del.getSelectedIndex() + 1) + ".";
				}
				if (comboBox_monat_del.getSelectedIndex() <= 8) {
					myDate = myDate + "0" + (comboBox_monat_del.getSelectedIndex() + 4) + ".";// SoSe
																								// 2014
																								// -
																								// Monat
																								// +4,
																								// da
																								// ab
																								// April
				} else {
					myDate = myDate + (comboBox_monat_del.getSelectedIndex() + 4) + ".";// SoSe
																						// 2014
																						// -
																						// Monat
																						// +4,
																						// da
																						// ab
																						// April
				}
				myDate = myDate + comboBox_jahr_del.getSelectedItem();

				Date aktuell = DateUtil.getInstance().formatString(myDate);
				int checkRemove = 0;
				boolean treffer = false;

				for (int i = 0; i < dauer; i++) {

					if (cp.removeDatum(platz, txtField_name_del.getText(), aktuell) == 1) {
						treffer = true;
						checkRemove = checkRemove + 1;
					} else {
						checkRemove = checkRemove - 1;
					}

					aktuell = new Date(aktuell.getTime() + (24 * 60 * 60 * 1000));
				}

				if (treffer) {
					if (checkRemove == dauer) {
						JOptionPane.showMessageDialog(null,
								"<html>Der Gast " + txtField_name_del
										.getText() + " wurde ab dem Datum " + myDate + " vom Stellplatz " + platz
								+ " für die angegebene Dauer von " + dauer
								+ " Tag(en) gelöscht!<br>Um die Änderungen anzuzeigen, bitte Belegungsplan erneut erstellen!");
						cp.belegungToXml(chosenXml);
						resetOberflaeche();
						initializeOberflaeche();
						
					}
					if (checkRemove < dauer) {
						JOptionPane.showMessageDialog(null,
								"<html>Der Gast " + txtField_name_del
										.getText() + " wurde ab dem Datum " + myDate + " vom Stellplatz " + platz
								+ " entfernt, jedoch nicht für die angegebene Dauer von " + dauer
								+ " Tag(en). Bitte überprüfen Sie den Belegungsplan!<br>Um die Änderungen anzuzeigen, bitte Belegungsplan erneut erstellen!");
						cp.belegungToXml(chosenXml);
						resetOberflaeche();
						initializeOberflaeche();

					}
				} else {
					JOptionPane.showMessageDialog(null,
							"Es wurde kein passender Eintrag gefunden! Bitte überprüfen Sie Ihre Angaben!");
				}

			}

		});
	}

	private void initializeOberflaeche() {
		cp.xmlToBelegung(chosenXml, columnName[1]);
	}

	private void resetOberflaeche() {    //				Lara Inhalt von Tabelle löschen außer Kopfzeile und -spalte
		for(int i = 0; i < cp.getAnzahlStellplaetze()*2; i++){
			for(int j = 1; j < columnName.length;j++){
				if(dtm.getValueAt(i, j) != null){
					System.out.println("null");
					dtm.setValueAt(null, i, j);
				}
			}	
		}
	}

	protected void readXml(String path) {
		try {
			// Belegungsdatei wird geladen. Siehe Schnittstellenbeschreibung

			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);

			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Belegung");

			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {

					Element eElement = (Element) nNode;

					cp.belegeStellplatz(
							Integer.parseInt(eElement.getElementsByTagName("Stellplatz").item(0).getTextContent()) - 1,
							DateUtil.getInstance()
									.formatString(eElement.getElementsByTagName("DatumVon").item(0).getTextContent()),
							Integer.parseInt(eElement.getElementsByTagName("Dauer").item(0).getTextContent()),
							eElement.getElementsByTagName("Name").item(0).getTextContent(), eElement.getElementsByTagName("Zusatzinformationen").item(0).getTextContent());
				}
			}

		} catch (Exception e1) {
			// e1.printStackTrace();
		}

	}
	


}