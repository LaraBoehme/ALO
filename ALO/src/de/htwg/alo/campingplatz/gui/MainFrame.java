package de.htwg.alo.campingplatz.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
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
import java.awt.event.ActionEvent;

import javax.swing.JLabel;
import javax.swing.border.LineBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import java.awt.Toolkit;

public class MainFrame {

	int anzahlStellplaetze = 20;
	Campingplatz cp = new Campingplatz(anzahlStellplaetze,
			new CheckAvailabilitySimple());
	private JFrame frmCampingplatzVerwaltung;
	private JTextField txtField_name;
	private JTextField txtField_name_del;
	private String chosenXml = "";
	private File dataFolder;
	private JLabel lblProtocol;
	private JPanel btnPanel = new JPanel(new GridLayout(1, 2)); // V1.1 SOSE
																// 2014 - Neu
																// fuer
																// Oberflaeche
	public static DefaultTableModel dtm; // V1.1 SOSE 2014 - Neu für Oberfläche

	private final String[] months = {
			// "Januar", "Februar", "M\u00E4rz", //SoSe 2014 - da Oberfläche			// WS 14/15 
			// nur April - September
			"April", "Mai", "Juni", "Juli", "August", "September"
	// , "Oktober", "November", "Dezember" //SoSe 2014 - da Oberfläche nur
	// April - September
	};

	private final String[] stellplaetze = { "1", "2", "3", "4", "5", "6", "7",			// WS 14/15
			"8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18",
			"19", "20" };

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		EventQueue.invokeLater(new Runnable() {

			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
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
		resetOberflaeche(); /* WS14/15 - löscht alle Einträge nach Neustart */

	}

	/**
	 * Initialize the contents of the frame.
	 */

	private void initialize() {

//		 Label fuer Protokolle -- um anzuzeigen, ob Belegungsdatei gefunden wurde oder nicht
		lblProtocol = new JLabel("");
		lblProtocol.setVerticalAlignment(SwingConstants.TOP);

		File jarFile = new File(MainFrame.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath());
		dataFolder = new File(jarFile.getAbsolutePath().replace(
				jarFile.getName(), "")
				+ "/data/");
		if (!dataFolder.exists()) {
			dataFolder.mkdirs();
		}

		if (new File(dataFolder.getAbsolutePath() + "\\Belegungen.xml")
				.exists()) {
			readXml(dataFolder.getAbsolutePath() + "\\Belegungen.xml");
			lblProtocol.setText("Belegungsdatei erfolgreich geladen...");

		} else {
			lblProtocol.setText("Keine Belegungsdatei gefunden...");
		}
		
//		JFrame Campingplatz wird erstellt
		frmCampingplatzVerwaltung = new JFrame();
		frmCampingplatzVerwaltung.setIconImage(Toolkit.getDefaultToolkit()
				.getImage(this.getClass().getResource("camping.png")));
		frmCampingplatzVerwaltung.setTitle("Campingplatz - Verwaltung");
		frmCampingplatzVerwaltung.setResizable(true);// 1.1 SOSE 2014 - Changed
														// to true
		frmCampingplatzVerwaltung
				.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		frmCampingplatzVerwaltung.getContentPane().setLayout(null); //liefert ContentPane des JFrame Campingplatz
		frmCampingplatzVerwaltung.getContentPane().add(lblProtocol); //lblProtocol wird dem ContentPane hinzugefuegt

		// ****************************************************************************************************************************
		frmCampingplatzVerwaltung.setLayout(new BorderLayout());
		frmCampingplatzVerwaltung.setPreferredSize(new Dimension(1280, 720));// 1.1
																				// SOSE
																				// 2014
																				// -
																				// from
																				// 1900,1200
																				// because
																				// of
																				// Mac
																				// resolution

//		TabbedPane fuer die unterschiedlichen Tabs wird erstellt und dem ContentPane hinzugefuegt
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 0, 1280, 720);// 1.1 SOSE 2014 - Changed size of
												// Content Container, Changed
												// from (0, 0, 434, 272)
		tabbedPane.setSize(1280, 720);
		frmCampingplatzVerwaltung.getContentPane().add(tabbedPane);
		
//		1.Buchen
		JPanel panelBuchen = new JPanel();
//		Tab Buchen wird erstellt mit dem Panel Buchen und dem TabbedPane hinzugefuegt
		tabbedPane.addTab("Buchen", null, panelBuchen, null);
		panelBuchen.setLayout(null);

//		Wie heisst der neue Gast
		JLabel txtpnWieHeitDer = new JLabel();
		txtpnWieHeitDer.setBounds(10, 6, 198, 20);
		txtpnWieHeitDer.setText("Wie hei\u00DFt der neue Gast?");
		panelBuchen.add(txtpnWieHeitDer);

		txtField_name = new JTextField();
		txtField_name.setHorizontalAlignment(SwingConstants.LEFT);
		txtField_name.setBounds(236, 6, 183, 30);
		panelBuchen.add(txtField_name);
		
//		Wann wird der Gast anreissen
		JLabel txtpnWannWirdDer = new JLabel();
		txtpnWannWirdDer.setText("Wann wird der Gast anreisen?");
		txtpnWannWirdDer.setBounds(10, 49, 198, 20);
		panelBuchen.add(txtpnWannWirdDer);
		
		// Tag des Monats
		final JComboBox comboBox_tag = new JComboBox();
		comboBox_tag.setModel(new DefaultComboBoxModel(new String[] { "1", "2",
				"3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
				"24", "25", "26", "27", "28", "29", "30", "31" }));
		comboBox_tag.setBounds(236, 49, 70, 25);
		panelBuchen.add(comboBox_tag);

		comboBox_tag.getModel();

		// Monat
		final JComboBox comboBox_monat = new JComboBox();
		comboBox_monat.setModel(new DefaultComboBoxModel(months));
		comboBox_monat.setBounds(300, 49, 120, 25);
		panelBuchen.add(comboBox_monat);

		// Jahr
		final JComboBox comboBox_jahr = new JComboBox();
		comboBox_jahr.setModel(new DefaultComboBoxModel(new String[] { "2014",
				"2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022",
				"2023", "2024", "2025" }));
		comboBox_jahr.setBounds(415, 49, 100, 25);
		panelBuchen.add(comboBox_jahr);
		
//		Wie lange bleibt der Gast
		JLabel txtpnWieLangeBleibt = new JLabel();
		txtpnWieLangeBleibt.setText("Wie lange bleibt der Gast?");
		txtpnWieLangeBleibt.setBounds(10, 91, 198, 20);
		panelBuchen.add(txtpnWieLangeBleibt);
		
		// Dauer des Aufenthalts
			final JComboBox comboBox_dauer = new JComboBox();
			comboBox_dauer.setModel(new DefaultComboBoxModel(new String[] { "1",
						"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
						"14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
						"24", "25", "26", "27", "28", "29", "30", "31" }));
			comboBox_dauer.setBounds(236, 91, 70, 25);
			panelBuchen.add(comboBox_dauer);

			JLabel lblTage = new JLabel("Tag(e)");
			lblTage.setBounds(315, 94, 45, 16);
			panelBuchen.add(lblTage);
			
//			Auf welchem Stellplaty soll gebucht werden
			JLabel lblWelcherStellplatz = new JLabel(); /* WiSe14/15 */
			lblWelcherStellplatz.setVerticalAlignment(SwingConstants.TOP);
			lblWelcherStellplatz.setBounds(10, 140, 280, 32);
			lblWelcherStellplatz
					.setText("<html>Auf welchem Stellplatz soll<br> gebucht werden?");
			panelBuchen.add(lblWelcherStellplatz);

			final JComboBox comboBox_StellPlatz = new JComboBox();
			comboBox_StellPlatz.setModel(new DefaultComboBoxModel(stellplaetze));
			comboBox_StellPlatz.setBounds(236, 135, 70, 25);
			panelBuchen.add(comboBox_StellPlatz);

			JLabel lblStellPlatzNr = new JLabel("Stellplatz Nr.");
			lblStellPlatzNr.setBounds(315, 140, 85, 15);
			panelBuchen.add(lblStellPlatzNr); /* WiSe14/15 */

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

//		Button Buchen
		JButton btnBuchen = new JButton("Buchen");
		btnBuchen.setBounds(5, 252, 87, 28);
		panelBuchen.add(btnBuchen);

	
//		2.Loeschen
		JPanel panelLoeschen = new JPanel();
		panelLoeschen.setToolTipText("");
		tabbedPane.addTab("L\u00F6schen", null, panelLoeschen, null);
		panelLoeschen.setLayout(null);

//		Welcher Gast soll geloescht werden
		JLabel lblwelcherGastSoll = new JLabel();
		lblwelcherGastSoll.setVerticalAlignment(SwingConstants.TOP);
		lblwelcherGastSoll.setBounds(10, 9, 169, 32);
		lblwelcherGastSoll
				.setText("<html>Welcher Gast soll gel\u00F6scht werden?");
		panelLoeschen.add(lblwelcherGastSoll);
		
		txtField_name_del = new JTextField();
		txtField_name_del.setBounds(240, 6, 179, 20);
		panelLoeschen.add(txtField_name_del);
		txtField_name_del.setColumns(10);
		
//		Auf welchem Stellplaty befindet sich der Gast
		JLabel lblAufWelchemStellplatz = new JLabel(
				"<html>Auf welchem Stellplatz befindet sich der Gast?");
		lblAufWelchemStellplatz.setVerticalAlignment(SwingConstants.TOP);
		lblAufWelchemStellplatz.setBounds(10, 52, 192, 32);
		panelLoeschen.add(lblAufWelchemStellplatz);
		
		JLabel lblStellplatzNr = new JLabel("Stellplatz Nr:");
		lblStellplatzNr.setBounds(240, 52, 85, 15);
		panelLoeschen.add(lblStellplatzNr);
		
		final JComboBox comboBox_sp = new JComboBox();
		comboBox_sp.setModel(new DefaultComboBoxModel(stellplaetze));
		comboBox_sp.setBounds(362, 49, 70, 25);
		panelLoeschen.add(comboBox_sp);
		
//		Ab welchem Tag soll geloescht werden
		JLabel lblabWelchemTag = new JLabel();
		lblabWelchemTag.setVerticalAlignment(SwingConstants.TOP);
		lblabWelchemTag.setBounds(10, 93, 183, 31);
		lblabWelchemTag
				.setText("<html>Ab welchem Tag soll gel\u00F6scht werden?");
		panelLoeschen.add(lblabWelchemTag);

		final JComboBox comboBox_tag_del = new JComboBox();
		comboBox_tag_del.setModel(new DefaultComboBoxModel(new String[] { "1",
				"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
				"24", "25", "26", "27", "28", "29", "30", "31" }));
		comboBox_tag_del.setBounds(240, 90, 70, 25);
		panelLoeschen.add(comboBox_tag_del);

		final JComboBox comboBox_monat_del = new JComboBox();
		comboBox_monat_del.setModel(new DefaultComboBoxModel(months));
		comboBox_monat_del.setBounds(305, 90, 120, 25);
		panelLoeschen.add(comboBox_monat_del);

		final JComboBox comboBox_jahr_del = new JComboBox();
		comboBox_jahr_del.setModel(new DefaultComboBoxModel(new String[] {
				"2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021",
				"2022", "2023", "2024", "2025" }));
		comboBox_jahr_del.setBounds(420, 90, 100, 25);
		panelLoeschen.add(comboBox_jahr_del);
		
//		Fuer wie viele Tage soll geloescht werden
		JLabel lblfrWieViele = new JLabel();
		lblfrWieViele.setVerticalAlignment(SwingConstants.TOP);
		lblfrWieViele.setBounds(10, 135, 192, 32);
		lblfrWieViele
				.setText("<html>F\u00FCr wie viele Tage soll gel\u00F6scht werden?");
		panelLoeschen.add(lblfrWieViele);
		
		final JComboBox comboBox_anzahl = new JComboBox();
		comboBox_anzahl.setModel(new DefaultComboBoxModel(new String[] { "1",
				"2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15", "16", "17", "18", "19", "20", "21", "22", "23",
				"24", "25", "26", "27", "28", "29", "30", "31" }));
		comboBox_anzahl.setBounds(240, 132, 70, 25);
		panelLoeschen.add(comboBox_anzahl);

		JLabel label_3 = new JLabel("Tag(e)");
		label_3.setBounds(315, 135, 50, 16);
		panelLoeschen.add(label_3);
		
//		Button Loeschen
		JButton buttonDel = new JButton("L\u00F6schen");
		buttonDel.setBounds(5, 190, 89, 25);
		panelLoeschen.add(buttonDel);

		
//		3.Excel
		JPanel panelExcel = new JPanel();
		tabbedPane.addTab("Excel", null, panelExcel, null);
		panelExcel.setLayout(null);

//		Fuer welchen Monat soll ein Belegungsplan gedruckt werden
		JLabel txtpnFrWelchenMonat = new JLabel(
				"<html>F\u00FCr welchen Monat soll ein Belegungsplan gedruckt werden?</html>");
		txtpnFrWelchenMonat.setBounds(10, 11, 205, 34);
		panelExcel.add(txtpnFrWelchenMonat);
		
		final JComboBox comboBoxMonth = new JComboBox();
		comboBoxMonth.setToolTipText("");
		comboBoxMonth.setModel(new DefaultComboBoxModel(months));
		comboBoxMonth.setBounds(300, 11, 120, 25);
		panelExcel.add(comboBoxMonth);

//		Fuer welches Jahr soll ein Belegungsplan gedruckt werden
		JLabel txtpnFrWelchesJahr = new JLabel();
		txtpnFrWelchesJahr
				.setText("<html>F\u00FCr welches Jahr soll ein Belegungsplan gedruckt werden?</html>");
		txtpnFrWelchesJahr.setBounds(10, 56, 205, 34);
		panelExcel.add(txtpnFrWelchesJahr);
		
		final JComboBox comboBoxYear = new JComboBox();
		comboBoxYear.setModel(new DefaultComboBoxModel());
		for (int n = 1984; n <= 2025; n++) {

			comboBoxYear.addItem(n);
		}
		comboBoxYear.setSelectedIndex(30);
		comboBoxYear.setBounds(300, 56, 100, 25);
		panelExcel.add(comboBoxYear);

//		Wo soll der Belegunsplan gespeichert werden
		JLabel txtpnWoSollDie = new JLabel();
		txtpnWoSollDie
				.setText("<html>Wo soll der Belegungsplan gespeichert werden?</html>");
		txtpnWoSollDie.setBounds(10, 101, 205, 34);
		panelExcel.add(txtpnWoSollDie);

		final JLabel textPane_speicherOrtBel = new JLabel();
		textPane_speicherOrtBel.setBounds(141, 146, 278, 14);
		panelExcel.add(textPane_speicherOrtBel);
		textPane_speicherOrtBel.setBorder(LineBorder.createBlackLineBorder());
		JButton btnNewButton_waehleSpeicherortBel = new JButton(
				"Ausw\u00E4hlen");
		btnNewButton_waehleSpeicherortBel.setBounds(300, 100, 110, 25);
		panelExcel.add(btnNewButton_waehleSpeicherortBel);

		final JLabel lblSpeicherort = new JLabel("Speicherort:");
		lblSpeicherort.setBounds(10, 146, 89, 14);
		panelExcel.add(lblSpeicherort);

		// Button Erstellen    Belegungsplan erstellen
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

					new JavaToExcel().exportToExcel(monat, jahr, cp,
							textPane_speicherOrtBel.getText());
					JOptionPane.showMessageDialog(null,
							"Belegungsplan erfolgreich unter "
									+ textPane_speicherOrtBel.getText()
									+ " abgespeichert");
				} else {
					JOptionPane.showMessageDialog(null,
							"W\u00E4hlen Sie zuerst einen Speicherort aus!");
				}
			}
		});
		btnErstellen.setBounds(10, 192, 87, 28);
		panelExcel.add(btnErstellen);

		
//		4.Belegungsplan
		btnPanel.setSize(1000, 1000);// SOSE 14 - Oberflaeche
		btnPanel.setBounds(tabbedPane.getBounds());
		JPanel panelBelegungsplan = new JPanel();
		panelBelegungsplan.setPreferredSize(tabbedPane.getSize());// SoSe 2014 - new Dimension(800, 600));
		panelBelegungsplan.setSize(1600, 920);
		panelBelegungsplan.setBounds(0, 0, 1600, 920);

		frmCampingplatzVerwaltung.add(new JScrollPane(tabbedPane));
		frmCampingplatzVerwaltung.pack();
		tabbedPane.addTab("Belegungsplan", null, panelBelegungsplan, null);

		String[][] tableContent = new String[21][184];// SoSe 2014 - auf 184
														// days geaendert, da
														// September 30 Tage

		GregorianCalendar first = new GregorianCalendar(2014,
				GregorianCalendar.APRIL, 1);
		GregorianCalendar last = new GregorianCalendar(2014,
				GregorianCalendar.SEPTEMBER, 30);
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		String[] columnName = { "Datum", "Platz1", "Platz2", "Platz3",
				"Platz4", "Platz5", "Platz6", "Platz7", "Platz8", "Platz9",
				"Platz10", "Platz11", "Platz12", "Platz13", "Platz14",
				"Platz15", "Platz16", "Platz17", "Platz18", "Platz19",
				"Platz20" };

		dtm = new DefaultTableModel(tableContent, columnName.length);
		dtm.setColumnIdentifiers(columnName);
		dtm.setRowCount(tableContent[0].length);

		for (int i = 0; i < tableContent[0].length; i++) {
			dtm.setValueAt(sdf.format(first.getTime()), i, 0);
			first.add(Calendar.DAY_OF_MONTH, 1);
		}

		first.set(2014, 3, 1);

		JTable table = new JTable(dtm);
		table.setEnabled(false);
		table.getTableHeader().setReorderingAllowed(false);

		JScrollPane scrollVert = new JScrollPane(table,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		table.setVisible(true);// SoSe 2014 - added
		table.setAutoResizeMode(table.AUTO_RESIZE_ALL_COLUMNS);
		TableColumnModel tcm = table.getColumnModel();
		tcm.getColumn(0).setPreferredWidth(110);
		// SoSe 2014 - for schleife die für alle Colums reziable ausschaltet
		for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setResizable(false);
		}
		scrollVert.setPreferredSize(tabbedPane.getSize());

		table.setGridColor(Color.LIGHT_GRAY);
		panelBelegungsplan.add(scrollVert, BorderLayout.SOUTH);
		panelBelegungsplan.add(btnPanel, BorderLayout.NORTH);

		Toolkit tk = Toolkit.getDefaultToolkit();
		frmCampingplatzVerwaltung.setSize((int) tk.getScreenSize().getWidth(),
				(((int) tk.getScreenSize().getHeight()) - 100));
		frmCampingplatzVerwaltung.setDefaultCloseOperation(3);
		frmCampingplatzVerwaltung.setVisible(true);

		// SOSE14 - Oberflaeche

		
//		5.Einstellungen
		JPanel panelEinstellungen = new JPanel();
		tabbedPane.addTab("Einstellungen", null, panelEinstellungen, null);
		panelEinstellungen.setLayout(null);
		
//		Button Auswaehlen
		JButton btnAuswaehlen = new JButton("Ausw\u00E4hlen");
		btnAuswaehlen.setBounds(10, 10, 102, 25);
		panelEinstellungen.add(btnAuswaehlen);
		
		// Speicherort waehlen
		btnNewButton_waehleSpeicherortBel
				.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {

						JFileChooser chooser = new JFileChooser();
						chooser.setCurrentDirectory(new java.io.File("."));
						chooser.setDialogTitle("Speicherort ausw\u00E4hlen");
						chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
						//
						// "Alle Datein"-Option deaktivieren
						//
						chooser.setAcceptAllFileFilterUsed(false);
						//
						int rueckgabewert = chooser.showOpenDialog(null);
						if (rueckgabewert == JFileChooser.APPROVE_OPTION) {
							textPane_speicherOrtBel.setText(chooser
									.getSelectedFile().getAbsolutePath());
						}

					}

				});

//		Belegungsdatei
		final JLabel txtpnBelegungsdatei = new JLabel();
		txtpnBelegungsdatei.setText("Belegungsdatei:");
		txtpnBelegungsdatei.setBounds(10, 45, 102, 20);
		panelEinstellungen.add(txtpnBelegungsdatei);	

		final JLabel textPane_dateiName = new JLabel();
		textPane_dateiName.setBounds(135, 45, 147, 20);
		panelEinstellungen.add(textPane_dateiName);
		textPane_dateiName.setBorder(LineBorder.createBlackLineBorder());

		// Button "EINLESEN"
		JButton btnEinlesen = new JButton("Einlesen");
		btnEinlesen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textPane_dateiName.getText().equalsIgnoreCase("")) {

					readXml(chosenXml);
					cp.belegungToXml(dataFolder.getAbsolutePath()
							+ "/Belegungen.xml");
					initializeOberflaeche();
					JOptionPane.showMessageDialog(null,
							"Belegungsdatei erfolgreich eingelesen!");
				} else {
					JOptionPane.showMessageDialog(null,
							"keine Datei ausgew\u00E4hlt");
				}

			}
		});
		btnEinlesen.setBounds(10, 76, 102, 25);
		panelEinstellungen.add(btnEinlesen);

		// Button "EINLESEN und ueberschreiben"
		JButton btnEinlesenUber = new JButton("Einlesen und \u00fcberschreiben");
		btnEinlesenUber.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!textPane_dateiName.getText().equalsIgnoreCase("")) {

					cp.resetStellplaetze();
					cp.newStellplaetze(anzahlStellplaetze);
					readXml(chosenXml);
					cp.belegungToXml(dataFolder.getAbsolutePath()
							+ "/Belegungen.xml");
					resetOberflaeche();
					initializeOberflaeche();
					JOptionPane.showMessageDialog(null,
							"Belegungsdatei erfolgreich eingelesen!");
				} else {
					JOptionPane.showMessageDialog(null,
							"keine Datei ausgew\u00E4hlt");
				}

			}
		});

		btnEinlesenUber.setBounds(140, 76, 202, 25);
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
					chosenXml = jc.getSelectedFile().getAbsolutePath();
					textPane_dateiName.setText(jc.getSelectedFile().getName());
				}
				initializeOberflaeche();
			}
		});

		// -----------------------------------------------
		
//		 6.Auslastung																		//WS14/15 			
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
		panelAuslastung.add(lblAuslastung);														//WS14/15 

		// ------------------------------------------------
		
		
//		Ereignisverarbeitung
		
		// button 'BUCHEN'		
		btnBuchen.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent arg0) {

				int limit = comboBox_over.getSelectedIndex();
				String myDate = "";

				if (txtField_name.getText().equalsIgnoreCase("")) {
					JOptionPane.showMessageDialog(null,
							"Sie müssen einen Namen angeben!");
					return;
				}

				if (comboBox_tag.getSelectedIndex() <= 8) {
					myDate = "0" + (comboBox_tag.getSelectedIndex() + 1) + ".";
				} else {
					myDate = "" + (comboBox_tag.getSelectedIndex() + 1) + ".";
				}
				if (comboBox_monat.getSelectedIndex() <= 8) {
					myDate = myDate + "0"
							+ (comboBox_monat.getSelectedIndex() + 4) + ".";// SoSe
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

				
				boolean checker = cp.checkAvailability(DateUtil.getInstance() 			// WS 14/15
																				
						.formatString(myDate),
						(comboBox_dauer.getSelectedIndex() + 1), limit,
						txtField_name.getText(),
						comboBox_StellPlatz.getSelectedIndex());

				if (checker == false) { // wenn Stellplatz belegt ist

					JOptionPane.showMessageDialog(null, "<html> Platz "
							+ (comboBox_StellPlatz.getSelectedIndex() + 1)
							+ " ist belegt."
							+ "<br>Freie Tage werden trotzdem gebucht.");

					JOptionPane
							.showMessageDialog(null,
									"<html>Überbuchungen überprüfen und von Hand eintragen.");

					cp.checkAvailability(
							DateUtil.getInstance().formatString(myDate),
							(comboBox_dauer.getSelectedIndex() + 1), limit,
							txtField_name.getText(),
							comboBox_StellPlatz.getSelectedIndex()); 				
							
					cp.belegungToXml(dataFolder.getAbsolutePath()
							+ "/Belegungen.xml");
					initializeOberflaeche();											//WS 14/15
					
				} else { // checker = true (Platz ist frei)

					cp.checkAvailability(
							DateUtil.getInstance().formatString(myDate),
							(comboBox_dauer.getSelectedIndex() + 1), limit,
							txtField_name.getText(),
							comboBox_StellPlatz.getSelectedIndex()); /* WiSe14/15 */

					JOptionPane
							.showMessageDialog(
									null,
									"<html>Buchung erfolgreich!<br>Um die Änderungen anzuziegen, bitte Belegungsplan erneut erstellen!");
					cp.belegungToXml(dataFolder.getAbsolutePath()
							+ "/Belegungen.xml");

					initializeOberflaeche();
				}						
				
				String lblText = "<html>";														//WS 14/15
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
				System.out.println(lblText);
				
				return;																			//WS 14/15
			}									
		});

		// button 'LOESCHEN'
		buttonDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int dauer = comboBox_anzahl.getSelectedIndex() + 1;
				int platz = comboBox_sp.getSelectedIndex() + 1;

				String myDate = "";

				if (txtField_name_del.getText().equalsIgnoreCase("")) {
					JOptionPane
							.showMessageDialog(null,
									"Geben Sie den Namen des zu löschenden Gastes ein!");
					return;
				}

				if (comboBox_tag_del.getSelectedIndex() <= 8) {
					myDate = "0" + (comboBox_tag_del.getSelectedIndex() + 1)
							+ ".";
				} else {
					myDate = "" + (comboBox_tag_del.getSelectedIndex() + 1)
							+ ".";
				}
				if (comboBox_monat_del.getSelectedIndex() <= 8) {
					myDate = myDate + "0"
							+ (comboBox_monat_del.getSelectedIndex() + 4) + ".";// SoSe
																				// 2014
																				// -
																				// Monat
																				// +4,
																				// da
																				// ab
																				// April
				} else {
					myDate = myDate
							+ (comboBox_monat_del.getSelectedIndex() + 4) + ".";// SoSe
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

					if (cp.removeDatum(platz, txtField_name_del.getText(),
							aktuell) == 1) {
						treffer = true;
						checkRemove = checkRemove + 1;
					} else {
						checkRemove = checkRemove - 1;
					}

					aktuell = new Date(aktuell.getTime()
							+ (24 * 60 * 60 * 1000));
				}

				if (treffer) {
					if (checkRemove == dauer) {
						JOptionPane.showMessageDialog(
								null,
								"<html>Der Gast "
										+ txtField_name_del.getText()
										+ " wurde ab dem Datum "
										+ myDate
										+ " vom Stellplatz "
										+ platz
										+ " für die angegebene Dauer von "
										+ dauer
										+ " Tag(en) gelöscht!<br>Um die Änderungen anzuziegen, bitte Belegungsplan erneut erstellen!");
						cp.belegungToXml(dataFolder.getAbsolutePath()
								+ "/Belegungen.xml");
						readXml(dataFolder.getAbsolutePath()
								+ "\\Belegungen.xml");
						removeFromOberflaeche(txtField_name_del.getText(),
								platz, myDate, dauer);

					}
					if (checkRemove < dauer) {
						JOptionPane.showMessageDialog(
								null,
								"<html>Der Gast "
										+ txtField_name_del.getText()
										+ " wurde ab dem Datum "
										+ myDate
										+ " vom Stellplatz "
										+ platz
										+ " entfernt, jedoch nicht für die angegebene Dauer von "
										+ dauer
										+ " Tag(en). Bitte überprüfen Sie den Belegungsplan!<br>Um die änderungen anzuziegen, bitte Belegungsplan erneut erstellen!");
						cp.belegungToXml(dataFolder.getAbsolutePath()
								+ "/Belegungen.xml");
						initializeOberflaeche();

					}
					initializeOberflaeche();
				} else {
					JOptionPane
							.showMessageDialog(null,
									"Es wurde kein passender Eintrag gefunden! Bitte überprüfen Sie Ihre Angaben!");
				}

			}

		});
		buttonDel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				cp.xmlToBelegung(dataFolder.getAbsolutePath()
						+ "/Belegungen.xml");
			}
		});
		initializeOberflaeche();
	}

	private void initializeOberflaeche() {
		cp.xmlToBelegung(dataFolder.getAbsolutePath() + "/Belegungen.xml");
	}

	private void resetOberflaeche() {
		for (int i = 1; i < 21; i++) {
			for (int j = 0; j < 183; j++) {
				dtm.setValueAt("", j, i);
			}
		}
	}

	private void removeFromOberflaeche(String text, int platz, String myDate,
			int dauer) {
		cp.removeFromOberflaeche(text, platz, myDate, dauer);

	}

	protected void readXml(String path) {
		try {
			// Belegungsdatei wird geladen. Siehe Schnittstellenbeschreibung

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

					cp.belegeStellplatz(
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
			//e1.printStackTrace();
		}

	}

}
