package com.esed.payer.inviaAvvisiForGeos.components;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.esed.payer.inviaAvvisiForGeos.config.InviaAvvisiForGeosContext;
import com.esed.payer.inviaAvvisiForGeos.config.InviaAvvisiForGeosResponse;
import com.esed.payer.inviaAvvisiForGeos.dao.InviaAvvisiForGeosDAO;
import com.esed.payer.inviaAvvisiForGeos.enums.EGeneratorePdf;
import com.esed.payer.inviaAvvisiForGeos.model.Tracciato512;
import com.esed.payer.inviaAvvisiForGeos.util.FTPHelper;
import com.esed.payer.inviaAvvisiForGeos.util.Generics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.esed.payer.inviaAvvisiForGeos.util.EMailSender;
import com.seda.bap.components.core.BapException;
import com.seda.bap.components.core.spi.ClassPrinting;
import com.seda.bap.components.core.spi.PrintCodes;
import com.seda.bap.components.util.FileUtils;
import com.seda.commons.properties.PropertiesLoader;
import com.seda.commons.properties.tree.PropertiesTree;
import com.seda.emailsender.webservices.dati.EMailSenderResponse;
import com.seda.payer.commons.inviaAvvisiForGeos.Debitore;
import com.seda.payer.commons.inviaAvvisiForGeos.Documento;
import com.seda.payer.commons.inviaAvvisiForGeos.File512;
import com.seda.payer.commons.inviaAvvisiForGeos.Flusso;
import com.seda.payer.commons.inviaAvvisiForGeos.FlussoMassivo;
import com.seda.payer.commons.inviaAvvisiForGeos.File512.GruppoAvvisi;
import com.seda.payer.core.bean.Configurazione;
import com.seda.payer.core.bean.DettaglioFlussoOttico;
import com.seda.payer.core.bean.TestataFlussoOttico;
import com.seda.payer.core.dao.ConfigurazioneDao;
import com.seda.payer.core.dao.DettaglioFlussoOtticoDao;
import com.seda.payer.core.dao.ElaborazioneFlussiDao;
import com.seda.payer.core.dao.TestataFlussoOtticoDao;
import com.seda.payer.commons.webservices.listener.PropKeys;
import com.seda.payer.pgec.webservice.commons.dati.ConfigPagamento;
import com.seda.payer.pgec.webservice.commons.dati.ConfigPagamentoRequest;
import com.seda.payer.pgec.webservice.commons.dati.ConfigPagamentoResponse;
import com.seda.payer.pgec.webservice.commons.source.CommonsSOAPBindingStub;
import com.seda.payer.pgec.webservice.commons.source.CommonsServiceLocator;

public class InviaAvvisiForGeosCore {
	private static Logger logger = Logger.getLogger(InviaAvvisiForGeosCore.class);
	private static String PRINT_REPORT = "REPORT";
	private static String PRINT_SYSOUT = "SYSOUT";
	
	// ini YLM PG22XX05
	private String listaErrori =""; 
	// fine YLM PG22XX05

	Calendar cal = Calendar.getInstance();
	private InviaAvvisiForGeosContext inviaAvvisiForGeosContext;

	DataSource datasource;
	private ClassPrinting classPrinting;
	String schema;
	String jobId;
//	int recordAnagraficheTotaliLette = 0;
//	int recordAnagraficheDaRivestire = 0;
//	int recordRivestizioniAnagrafiche = 0;
//	int flussiFisiciAnagraficaDaRivestireLetti = 0;
//	int flussiFisiciRivestizioniAnagraficaLetti = 0;
	int numAggiornati = 0;

	private File newZip;
	private File ftpForGeos;
	private File outputDirectory;
	private Connection connection;
	private static PropertiesTree configuration;

	InviaAvvisiForGeosDAO inviaAvvisiForGeosDAO = null;

	/** configurazione per ogni Ente del payer */
	ArrayList<Configurazione> confPayerList;

	FTPHelper ftp = new FTPHelper(logger);
	String lineSeparator = "============================================================================================";

	public InviaAvvisiForGeosCore() {
		super();
		welcome();
	}

	public InviaAvvisiForGeosResponse run(String[] params, DataSource datasource, String schema,
			ClassPrinting classPrinting, Logger logger, String jobId) throws BapException {
		InviaAvvisiForGeosResponse inviaAvvisiForGeosRespons = new InviaAvvisiForGeosResponse();
		inviaAvvisiForGeosRespons.setCode("00");
		inviaAvvisiForGeosRespons.setMessage("Elaborazione completata con successo");
		try {
			this.datasource = datasource;
			this.schema = schema;
			this.jobId = jobId;
			this.classPrinting = classPrinting;
			this.logger = logger;

			preProcess(params);
			processAvvisi(params);
			postProcess(classPrinting);
			// ini YLM PG22XX05
			if (this.listaErrori  != null && this.listaErrori != "") {
				printRow(PRINT_SYSOUT, "Elaborazione parziale");
				sendMailError(inviaAvvisiForGeosContext.getCodiceUtente() +": Errore Servizio AvvisiForGeos",
						this.listaErrori,
						inviaAvvisiForGeosContext.getCodiceUtente()) ;
		
			} else {

				printRow(PRINT_SYSOUT, "Elaborazione completata con successo ");
			}
			// fine YLM PG22XX05

			printRow(PRINT_SYSOUT, lineSeparator);
			
		} catch (Exception e) {
			sendMailError(inviaAvvisiForGeosContext.getCodiceUtente() +": Errore Servizio AvvisiForGeos", e.getMessage(),inviaAvvisiForGeosContext.getCodiceUtente()) ;
				
			// System.out.println(e);
			e.printStackTrace();
			printRow(PRINT_SYSOUT, "Elaborazione completata con errori " + e);
			printRow(PRINT_SYSOUT, lineSeparator);
			inviaAvvisiForGeosRespons.setCode("30"); // TODO da verificare se mantenere 30 come per altri processi
														// oppure impostare 12
			inviaAvvisiForGeosRespons.setMessage("Operazione terminata con errori ");
		}

		return inviaAvvisiForGeosRespons;
	}

	private void postProcess(ClassPrinting classPrinting) {

		printRow(PRINT_SYSOUT, " ");
//		printRow(PRINT_SYSOUT, "Flusso Anagrafiche Rivestite creato  : " + nomeFlussoAnagraficheRivestite);
//		printRow(PRINT_SYSOUT, "       tot. record                   : " + recFlussoAnagraficheRivestite);
//		if (classPrinting!=null)
//			try {
//				classPrinting.print(PRINT_REPORT, "Flusso Anagrafiche Rivestite creato  : " + nomeFlussoAnagraficheRivestite);
//				classPrinting.print(PRINT_REPORT, "       tot. record                   : " + recFlussoAnagraficheRivestite);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//	
	}

	private void welcome() {
		StringBuffer w = new StringBuffer("");
		w.append("" + "Invio Flussi Avvisi 512 per GEOS " + "\n");
		w.append(System.getProperties().get("java.specification.vendor") + " ");
		w.append(System.getProperties().get("java.version") + "\n");
		w.append("(C) Copyright 2015 di SEDA spa - Gruppo KGS" + "\n");
		w.append("\n");
		System.out.println(w.toString());
		w = null;

		System.out.println(lineSeparator);
		System.out.println("Avvio " + "Invio Flussi 512 per GEOS " + "");
		System.out.println(lineSeparator);
	}

	public void preProcess(String[] params) throws Exception {
		inviaAvvisiForGeosContext = new InviaAvvisiForGeosContext();
		inviaAvvisiForGeosContext.loadSchedeBap(params);
		Properties config = null;

		String fileConf = inviaAvvisiForGeosContext.getParameter("CONFIGPATH");

		try {
			config = PropertiesLoader.load(fileConf);
		} catch (FileNotFoundException e) {
			printRow(PRINT_SYSOUT, "File properties di configurazione " + fileConf + " non trovato");
			throw new Exception();
		} catch (IOException e) {
			printRow(PRINT_SYSOUT, "Errore file di configurazione " + fileConf + " " + e);
			throw new Exception("File properties di configurazione " + fileConf + " non trovato");
		}
		inviaAvvisiForGeosContext.setConfig(config);

//	if (logger==null) {
		PropertyConfigurator.configure(inviaAvvisiForGeosContext.getLogger());
		logger = Logger.getLogger(InviaAvvisiForGeosCore.class);
		// cx.setLogger(logger);
//	}

		if (inviaAvvisiForGeosContext.getFtpBaseUrl() == null) {
			printRow(PRINT_SYSOUT, "Url ftp per acquisizione anagrafica non configurata");
			throw new Exception("Url ftp per acquisizione anagrafica non configurata");
		}

		if (inviaAvvisiForGeosContext.getFtpUser() == null) {
			printRow(PRINT_SYSOUT, "Username ftp per acquisizione anagrafica non configurato");
			throw new Exception("Username ftp per acquisizione anagrafica non configurato");
		}

		if (inviaAvvisiForGeosContext.getFtpPassword() == null) {
			printRow(PRINT_SYSOUT, "Password ftp per acquisizione anagrafica non configurata");
			throw new Exception("Password ftp per acquisizione anagrafica non configurata");
		}

		printRow(PRINT_SYSOUT, "Configurazione esterna caricata da " + fileConf);
		connection = this.datasource.getConnection();
		connection.setAutoCommit(true);

		ConfigurazioneDao daoObj = new ConfigurazioneDao(connection, schema);

		// Per ogni configuration, (filtro solo i Payer)
		confPayerList = new ArrayList<Configurazione>(daoObj.doList());
		for (int i = 0; i < confPayerList.size();) {
			Configurazione conf = confPayerList.get(i);

			// configurazione non interesante?... la elimino
			if (!conf.getFlagWebServiceOttico().equals("P"))
				confPayerList.remove(i);
			else
				i++;
		}
	}

	private File[] getInputFiles(String inputDirFile) throws Exception {
		File[] inputFiles;
		File inputDirectory = new File(inputDirFile);
		inputFiles = inputDirectory.listFiles();
		if (inputFiles == null) {
			printRow(PRINT_SYSOUT, "Cartella anagrafica input non trovata");
			throw new Exception("Cartella anagrafica input non trovata");
		}
		return inputFiles;
	}

	public boolean cancellaFile(File fileorigine) {
		boolean ritorno = false;
		try {
			if (fileorigine.exists()) {
				if (!fileorigine.delete()) {
					printRow(PRINT_SYSOUT, "Il file " + fileorigine + " non puo essere eliminato");
				} else {
					ritorno = true;
				}
			}
		} catch (Exception e) {
			printRow(PRINT_SYSOUT, "Errore durante l'operazione di cancellazione del file " + fileorigine + " " + e);
		}
		return ritorno;
	}

	private boolean spostaInAnagraficaOutputDir(File f) throws Exception {
		boolean spostato = false;
		File fileDest = new File(outputDirectory.getAbsoluteFile() + File.separator + f.getName());
		spostato = spostaFile(f, fileDest);
		return spostato;
	}

	public boolean spostaFile(File pathOrigine, File pathDestinazione) {
		boolean ret = false;
		try {
			if (pathDestinazione.exists()) {
				if (!pathDestinazione.delete()) {
					printRow(PRINT_SYSOUT, "Il file " + pathDestinazione + " non puo essere eliminato");
				}
			}
			if (pathOrigine.exists()) {
				pathOrigine.renameTo(pathDestinazione);
				ret = true;
			}

		} catch (Exception e) {
			printRow(PRINT_SYSOUT, "Errore durante l'operazione di spostamento del file " + pathOrigine + " in "
					+ pathDestinazione + " " + e);
		}
		return ret;
	}

	static boolean debug = true;
	Date dataOraElaborazione;
	
	
	
	// Inizio PAGONET-368 LM
	
	
	// istanzio il pgec
	public CommonsSOAPBindingStub getCommonsSOAPBindingStub(String dbSchemaCodSocieta,String commonsWsUrl) throws Exception {
		// we initialize commons serviceLocator
		CommonsServiceLocator serviceLocator = new CommonsServiceLocator();
		serviceLocator.setCommonsPortEndpointAddress(commonsWsUrl);

		// we initialize commons stub
		CommonsSOAPBindingStub binding = (CommonsSOAPBindingStub)serviceLocator.getCommonsPort(); //new URL(commonsWsUrl));
		
		binding.clearHeaders();
		binding.setHeader("","dbSchemaCodSocieta",dbSchemaCodSocieta);	
		
		return binding;
	}
	
	
	
	public com.seda.payer.pgec.webservice.commons.dati.ConfigPagamento recuperaListaFunzioniEnte
	(CommonsSOAPBindingStub commonsWs, 
			String codiceSocieta, 
			String codiceUtente, 
			String chiaveEnte, 
			String canalePagamento, 
			String codTipologiaServizio) throws Exception {
		com.seda.payer.pgec.webservice.commons.dati.ConfigPagamento configPagamento = null;
    	ConfigPagamentoRequest configPagamentoRequest = new ConfigPagamentoRequest(codiceSocieta, codiceUtente, chiaveEnte, canalePagamento);
    	ConfigPagamentoResponse configPagamentoResponse = commonsWs.recuperaListaFunzioniEnte(configPagamentoRequest);
    	//In caso di errore, configPagamentoResponse.getRetCode() vale "01" altrimenti "00"
    	if (configPagamentoResponse != null && configPagamentoResponse.getRetCode()!=null) {
    		if (!configPagamentoResponse.getRetCode().equals("00")) {
    			//error(" - errore in recuperaListaFunzioniEnte: " + configPagamentoResponse.getRetMessage());
        		throw new Exception(" - errore in recuperaListaFunzioniEnte: " + configPagamentoResponse.getRetMessage());
        	} else {
        		if (configPagamentoResponse.getListConfigPagamento() != null){
        			//TODO. Si potrebbe nel caso della sendRT prendere il primo configPagamento a prescindere da codTipologiaServizio ....
        			//      e nel caso non fare andare in errore se codTipologiaServizio non e' presente
    	    		for(int i = 0; i < configPagamentoResponse.getListConfigPagamento().length; i++){
    					if (configPagamentoResponse.getListConfigPagamento(i).getCodTipologiaServizio().equals(codTipologiaServizio)) {
    						configPagamento = configPagamentoResponse.getListConfigPagamento(i);
    					}
    				}
    	    	}
        	}
    	}
    	return configPagamento;
    }
	
	// Fine PAGONET-368 LM
	
	private boolean pControllaPresenzaEnte(CommonsSOAPBindingStub commonsWs, String codiceSocieta, 
			String codiceUtente, String chiaveEnte, String canalePagamento,String codTipologiaServizio) {
		
		com.seda.payer.pgec.webservice.commons.dati.ConfigPagamento configPagamento = null;
		
		try {
		  configPagamento = 
					this.recuperaListaFunzioniEnte(commonsWs, 
							codiceSocieta, codiceUtente, chiaveEnte, "PSP", 
							"paVerifyPaymentNotice");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return configPagamento == null;
	}
	
	
	public boolean controllaPresenzaEnte(CommonsSOAPBindingStub commonsWs, String codiceSocieta, 
			String codiceUtente, String chiaveEnte, String canalePagamento,String codTipologiaServizio) {
		
		return pControllaPresenzaEnte(commonsWs,codiceSocieta, 
				 codiceUtente,chiaveEnte,canalePagamento,codTipologiaServizio);
	}
	
	
	/**
	 * Function per ontrollare il configpagamento centralizzata, usabile in tutto i codice senza dover riscrivere sempre il controllo
	 */
	Function<ConfigPagamento,Boolean> checkConfigPagamento = configPagamento -> configPagamento == null || 
			configPagamento.getAutorizzazioneStampaAvvisoPagoPa().trim().equals("");
	
	
	/**
	 * @param commonsWs oggetto che instazia il PGEC tramite la funzione {@code getCommonsSOAPBindingStub}
	 * @param f Oggetto Flusso
	 * @param doc Oggetto Documento
	 * @param tipo Tipo di chiamata, "WEB" o "PSP"
	 * @param servizio
	 * @return {@link ConfigPagamento}
	 */
	private ConfigPagamento pConfigPagamento(CommonsSOAPBindingStub commonsWs,
			Flusso f,Documento doc,String tipo,String servizio) {
		
		ConfigPagamento configPagamento = null;
		try {
			 configPagamento = recuperaListaFunzioniEnte(commonsWs, 
					f.societa, 
					f.cutecute, 
					doc.chiaveEnte, 
					tipo, 
					servizio);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return configPagamento;
	}
	
	/**
	 * @param commonsWs
	 * @param f
	 * @param doc
	 * @param tipo
	 * @param servizio
	 * @return Un oggetto {@link ConfigPagamento} ma di tipo {@link Optional} in modo tale da evitare
	 * confronti ridondanti con il valore "null"
	 */
	public ConfigPagamento optionalConfigPagamento(CommonsSOAPBindingStub commonsWs,
			Flusso f,Documento doc,String tipo,String servizio) {
		
		return pConfigPagamento(commonsWs, f, doc, tipo, servizio);
	}
	
	

	public void processAvvisi(String[] params) throws Exception {
		dataOraElaborazione = new Date();

		printRow(PRINT_SYSOUT, lineSeparator);
		printRow(PRINT_SYSOUT, "Process " + "Generazione flussi 512 per GEOS " + "");
		printRow(PRINT_SYSOUT, lineSeparator);
		// ini YLM PG22XX05
		Boolean erroreFlusso = false; 
		// fine YLM PG22XX05
		try {
			inviaAvvisiForGeosDAO = new InviaAvvisiForGeosDAO(inviaAvvisiForGeosContext, connection, schema);
			/** cutecute */
			String codiceUtente = inviaAvvisiForGeosContext.getCodiceUtente();
			System.out.println("PY512SP_AVVI inizio");
			ArrayList<Flusso> listaFlussi = inviaAvvisiForGeosDAO.listAnagrafiche512(codiceUtente);
			System.out.println("PY512SP_AVVI fine");

			// lista avvisi suddivisi per i vari files e le 10 categorie
			TreeMap<String, File512> files = new TreeMap<String, File512>();
			ArrayList<String> listaFlussiConErrori = new ArrayList<String>();
			// PAGONET-368
			String commonsWsUrl = inviaAvvisiForGeosContext.getProperties().getProperty("webServices.url.commons");
			CommonsSOAPBindingStub commonsWs = null;
			ConfigPagamento configPagamento = null;
			////String codiceCbill;
			////String codiceAut;
			// PAGONET-368

			for (Flusso f : listaFlussi) {
				for (Debitore deb : f.listaDebitori) {
					for (Documento doc : deb.listaDocumenti) {
						String ente = doc.codiceEnte;
						System.out.println("ente = " + ente);
						String idFlusso = doc.debitore.flusso.idFlusso;
						System.out.println("idFlusso = " + idFlusso);
						String servizio = doc.tipologiaServizio;

						// tipo template
//marini
//          boolean ibanPostale = doc.iban.contains("07606");
						boolean ibanPostale = false;
						if (doc.iban.length() > 9) {
							if (doc.iban.substring(5, 10).equals("07601")) {
								ibanPostale = true;
							}
						}
						String tipoIban = ibanPostale ? "POSTE" : "STANDARD";
						
						// ini YLM PG22XX05
						String tipoTemplate = "";

						try {
							
							if("POSTE".equals(tipoIban))
									tipoTemplate = "POSTE_";
							else
									tipoTemplate = "STANDARD_";

							// Asseganazione disabilitata PAGONET-368
							//tipoTemplate = inviaAvvisiForGeosContext.getTipoTemplate(codiceUtente, ente, servizio, tipoIban);
							
						} catch (Exception e) {
							erroreFlusso = true;
							listaFlussiConErrori.add(idFlusso);
							//lista errori flusso per email, in caso tutto il resto vada a buon fine
							this.listaErrori += System.lineSeparator() + " Il flusso con id " +  idFlusso + " presenta l'errore :" + e.getMessage();
							continue;
						} 
						// fine YLM PG22XX05

						//PAGONET-541 - inizio
						if(doc.flagMultiBeneficiario!=null && doc.flagMultiBeneficiario.equals("Y")) {
							System.out.println("template post flag flagMultiBeneficiario ");
							
							if(inviaAvvisiForGeosContext.getProperties().getProperty(String.format("archivioCarichiWs.%s.STAMPAPOSTEMB", codiceUtente))
									.equals("N")) {
								tipoTemplate = "STANDARD_";
							}else {
								if(tipoIban.equals("POSTE")) {
									tipoTemplate = "POSTE_";
								}else {
									tipoTemplate = "STANDARD_";
								}
							}

						}
						//PAGONET-541 - fine
						// raggruppamento
						File512.GruppoAvvisi gruppo;

						if (doc.fatturazioneElettronica)
							gruppo = GruppoAvvisi.FATTURAZIONE_ELETTRONICA;
						else if (doc.debitore.pec != null && doc.debitore.pec.trim().length() > 0)
							gruppo = GruppoAvvisi.PEC;
						else if (doc.debitore.mail != null && doc.debitore.mail.trim().length() > 0)
							gruppo = GruppoAvvisi.MAIL;
						else if (doc.debitore.provincia!=null && doc.debitore.provincia.equalsIgnoreCase("EE"))
							gruppo = GruppoAvvisi.DOC_EE;
						else
							gruppo = GruppoAvvisi.DOC_ITALIA;

						String key = tipoTemplate + "." + codiceUtente + "." + ente + "." + idFlusso + "." + gruppo;
						
						File512 file = files.get(key);

						String descrizioneEnte = doc.descrizioneEnte.split("/")[0].trim(); // LUCAP_04032020
						
						if (file == null) {
							file = new File512(tipoTemplate, gruppo, codiceUtente, ente, idFlusso,
									descrizioneEnte.equals("") ? doc.descrizioneEnte : descrizioneEnte); // LUCAP_04032020
							file.societa = f.societa;
							file.dataElaborazione = dataOraElaborazione;
							file.dataOra = inviaAvvisiForGeosContext.formatDate(dataOraElaborazione, "yyyyMMddHHmmss");
							//PAGONET - 368 - inizio
//							file.codiceAutorizzazione = inviaAvvisiForGeosContext.getCodiceAutorizzazione(codiceUtente,
//									f.societa, ente);
							String codiceAutorizzazione = inviaAvvisiForGeosContext.getCodiceAutorizzazione(codiceUtente,
									f.societa, ente);
							
							String cBill = inviaAvvisiForGeosContext.getCbill(codiceUtente, f.societa, deb.idDominio);
							
							if((codiceAutorizzazione==null || codiceAutorizzazione.length()==0) || (cBill == null || cBill.length()==0))  {
								commonsWs = getCommonsSOAPBindingStub(codiceUtente, commonsWsUrl);
								configPagamento = pConfigPagamento(commonsWs,f,doc,"WEB",servizio);
								if(configPagamento == null)
									configPagamento = pConfigPagamento(commonsWs,f,doc,"PSP",servizio);
								if(configPagamento!=null) {
									codiceAutorizzazione = configPagamento.getAutorizzazioneStampaAvvisoPagoPa();
									cBill = configPagamento.getCbillStampaAvvisoPagoPa();
								}
								
								else {
									throw new RuntimeException("Manca configurazione Codice Autorizzazione/Codice Cbill: " + key);
								}
							}
								
							
							if(codiceAutorizzazione==null || codiceAutorizzazione.trim().length()==0)  {
								throw new RuntimeException("Manca configurazione Codice Autorizzazione: " + key);
							} else {
								if(cBill == null || cBill.trim().length()==0) {
									throw new RuntimeException("Manca configurazione Codice cbill: " + key);
								}else {
									if(cBill.length() > 10) {
										throw new RuntimeException("Configurazione Codice Cbill maggiore di 10 caratteri: " + key);
									}
								file.codiceAutorizzazione = codiceAutorizzazione;
								file.cBill = cBill;
							  }
							}

							//PAGONET - 368 - fine
							
							//file.cBill = inviaAvvisiForGeosContext.getCbill(codiceUtente, f.societa, deb.idDominio);
							f.cbill = file.cBill; // SB 19042019
							f.codiceAutorizzazione = file.codiceAutorizzazione; // SB 19042019
							files.put(key, file);
						}

						file.listaDocumenti.add(doc);
					}
					// ini YLM PG22XX05
					if ( erroreFlusso ) {
						continue;
					}
					// fine YLM PG22XX05
				}	
			}
			
			// ini YLM PG22XX05
			if ( erroreFlusso ) {
				//rimuovo i flussi che hanno un errore al loro interno in modo che non vengano processati
				System.out.println("listaFlussiConErrori= " + listaFlussiConErrori);
				System.out.println("files= " + files.values());
				
				int index = 0;
				for ( File512 file : files.values()) {
					for ( String flussoConErrori : listaFlussiConErrori) {
						if (file.idFlusso == flussoConErrori) {
							files.values().remove(index); 
						}
					}
					index++;
				}
			}
			// fine YLM PG22XX05
		
			System.out.println("codiceUtente= " + codiceUtente);
			EGeneratorePdf generatorePdf = EGeneratorePdf.valueOf(
							inviaAvvisiForGeosContext.getProperties().getProperty(String.format("generatorePdf.%s", codiceUtente))
							);
			
			System.out.println("generatorePdf= " + generatorePdf);
			switch (generatorePdf) {
			case PagoPAPdf:
				System.out.println("Funzionalita PagoPAPdf");
				String uri = inviaAvvisiForGeosContext.getProperties().getProperty(String.format("%s.wsRest.PagoPAPdf", codiceUtente));
				String path = inviaAvvisiForGeosContext.getProperties().getProperty(String.format("%s.wsRest.PagoPAPdf.path", codiceUtente));
				
				FlussoMassivo flussoMassivo = new FlussoMassivo();
				flussoMassivo.flussoList = new ArrayList<File512>(files.values());
				flussoMassivo.path = path;
				
				ObjectMapper mapper = new ObjectMapper();
				System.out.println("uri = " + uri);
				System.out.println("path = " + path);
				
				int progr = 1;
				
				String fileNameFlussoPrecedente = null;
				for (File512 file : flussoMassivo.flussoList) {
					
					String chiaveFlusso = file.getFileName();
					if (fileNameFlussoPrecedente == null || !fileNameFlussoPrecedente.equals(chiaveFlusso)) {
						// il progressivo va da 1 a 10... all'interno dello stesso idFlusso,
						// in base al raggruppamento
						progr = 1;
						fileNameFlussoPrecedente = chiaveFlusso;
					}
					file.progressivo = progr;
					progr++;
				}

				System.out.println(mapper.writeValueAsString(flussoMassivo));

				
				Client client = ClientBuilder.newClient();

				Response response = client.target(uri)
						.request(MediaType.APPLICATION_JSON)
						.post(Entity.entity(flussoMassivo, MediaType.APPLICATION_JSON));
				
				if (response.getStatus() == Status.OK.getStatusCode()) {
					logger.info(String.format("Flusso massivo inviato al generatore Pdf con uuid: %s", response.readEntity(String.class)));
		
					for (File512 file : flussoMassivo.flussoList) {
						// generato file512... scrivo record TOT/DOT
						totTableInsert(file);
						
						int id = Integer.parseInt(file.idFlusso);
						// Quando ho trasmesso tutti i files di un flusso posso marcare il
						// corrispondente
						// flusso sul DB come "inviato in stampa"
						new ElaborazioneFlussiDao(connection, schema).doUpdateLogFlussi(id, null, null, 0, null, null, null, null, null, null, "S");
					}
					
				} else {
					throw new Exception(String.format("Errore chiamata servizio PagoPAPdfService - Status: %s, Body: %s",
						response.getStatus(), response.getEntity()));
				}
				// TODO: fare gli update su DB come GEOS
				break;
			case GEOS:
				System.out.println("Funzionalita GEOS");
				// salvataggio pendenze su files, ogni flusso ha un insieme di files (max 10)
				TreeMap<String, ArrayList<File>> listaFilesFlussi = new TreeMap<String, ArrayList<File>>();

				// TODO: verificare l ordinamento delle chiavi
				int progressivo = 1;
				SortedSet<String> sortedKeys = new TreeSet<String>(files.keySet());

				// quando cambio file riparto con il progressivo da 1
				String keyPrecedenteFlusso = null;
				for (String key : sortedKeys) {
					String keyFlusso = key.substring(0, key.lastIndexOf("."));
					if (keyPrecedenteFlusso == null || !keyPrecedenteFlusso.equals(keyFlusso)) {
						// il progressivo va da 1 a 10... all'interno dello stesso idFlusso,
						// in base al raggruppamento
						progressivo = 1;
						keyPrecedenteFlusso = keyFlusso;
					}

					File512 file = files.get(key);

					file.progressivo = progressivo++;
					ArrayList<String> fileContent = Tracciato512.serialize(file);
					File file512Path = getFile512Path(file);

					// usa default character encoding e newLine
					BufferedWriter writer = new BufferedWriter(
							new OutputStreamWriter(new FileOutputStream(file512Path, true)));
					for (String line : fileContent) {
						writer.write(line);
						writer.newLine();
					}
					writer.close();

					printRow(PRINT_SYSOUT, "Salvato file512:" + file512Path);
					logger.debug("Salvato file512:" + file512Path);

					ArrayList<File> listaFilesFlusso = listaFilesFlussi.get(file.idFlusso);
					if (listaFilesFlusso == null) {
						listaFilesFlusso = new ArrayList<File>();
						listaFilesFlussi.put(file.idFlusso, listaFilesFlusso);
					}
					listaFilesFlusso.add(file512Path);

					// generato file512... scrivo record TOT/DOT
					totTableInsert(file);
				}

				// marini
				URL url = new URL(inviaAvvisiForGeosContext.getFtpUrl(codiceUtente));
				String dirFTP = inviaAvvisiForGeosContext.getDirFtp(codiceUtente);
				for (String idFlusso : listaFilesFlussi.keySet()) {
					ArrayList<File> listaFilesFlusso = listaFilesFlussi.get(idFlusso);

					logger.debug("Inizio trasferimento FTP, idFlusso=" + idFlusso + ", n files=" + listaFilesFlusso.size());

					for (File fileToSend : listaFilesFlusso) {
						logger.debug("fileToSend.getCanonicalPath()=" + fileToSend.getCanonicalPath());
						boolean success = new FTPHelper(logger).uploadFile(inviaAvvisiForGeosContext,
								fileToSend.getCanonicalPath(), dirFTP + "/" + fileToSend.getName());
						if (success) {
							logger.debug("FTP upload success: " + fileToSend.getName());
						} else {
							logger.debug("ERRORE FTP upload: " + fileToSend.getName());
							throw new Exception("ERRORE FTP upload: " + fileToSend.getName());
						}
					}

					int id = Integer.parseInt(idFlusso);
					// Quando ho trasmesso tutti i files di un flusso posso marcare il
					// corrispondente
					// flusso sul DB come "inviato in stampa"
					new ElaborazioneFlussiDao(connection, schema).doUpdateLogFlussi(id, null, null, 0, null, null, null, null, null, null, "S");
				}
				// printRow(myPrintingKeyANA_SYSOUT, "Elaborazione completata con successo");
				break;
			default:
				break;
			}
		} catch (Exception e) {
			//sendMailError(inviaAvvisiForGeosContext.getCodiceUtente() +": Errore Servizio AvvisiForGeos", e.getMessage(),inviaAvvisiForGeosContext.getCodiceUtente()) ;
			
			 e.printStackTrace();
			// printRow(myPrintingKeyANA_SYSOUT, "Elaborazione completata con errori");
			throw new Exception(e);
		} finally {
//			connection.commit();
			connection.setAutoCommit(true);
			connection.close();
		}
	}

	/**
	 * Costanti per tipoDoc: FTE - Fatturazione Elettr. PEC - Mail PEC MAL - MAILDOI
	 * - Doc. per Italia DOI - Doc. per Italia DOE - Doc. per Estero
	 * 
	 * FEP - Fatturazione Elettr. - Poste PEP - Mail PEC - Poste MAP - MAIL - Poste
	 * DIP - Doc. per Italia - Poste DEP - Doc. per Estero - Poste
	 */
	private void totTableInsert(File512 file) throws Exception {

		// C e sempre almeno un documento e sono tutti relativi allo stesso ente.
		String codiceSocieta = file.societa;
		String codiceUtente = file.cutecute;
		String chiaveEnte = file.listaDocumenti.get(0).chiaveEnte;

		int numDettagli = file.listaDocumenti.size();

		TestataFlussoOttico totRec = new TestataFlussoOttico();
		totRec.setCodiceSocieta(codiceSocieta);
		totRec.setCodiceUtente(codiceUtente);
		totRec.setCodiceEnte(chiaveEnte);

//		totRec.setDataElaborazione(new java.sql.Date(file.dataElaborazione.getTime()));
		java.sql.Date dataCreazioneFlusso = new java.sql.Date(Generics.getMinDate().getTime().getTime());
		totRec.setDataCreazioneFlusso(dataCreazioneFlusso);
		String tipoDoc = null;
		switch (file.gruppoAvvisi) {
		case FATTURAZIONE_ELETTRONICA:
			if (file.tipoTemplate.equals("STANDARD_"))
				tipoDoc = "FTE";
			else if (file.tipoTemplate.equals("POSTE_"))
				tipoDoc = "FEP";
			break;
		case PEC:
			if (file.tipoTemplate.equals("STANDARD_"))
				tipoDoc = "PEC";
			else if (file.tipoTemplate.equals("POSTE_"))
				tipoDoc = "PEP";
			break;
		case MAIL:
			if (file.tipoTemplate.equals("STANDARD_"))
				tipoDoc = "MAL";
			else if (file.tipoTemplate.equals("POSTE_"))
				tipoDoc = "MAP";
			break;
		case DOC_ITALIA:
			if (file.tipoTemplate.equals("STANDARD_"))
				tipoDoc = "DOI";
			else if (file.tipoTemplate.equals("POSTE_"))
				tipoDoc = "DIP";
			break;
		case DOC_EE:
			if (file.tipoTemplate.equals("STANDARD_"))
				tipoDoc = "DOE";
			else if (file.tipoTemplate.equals("POSTE_"))
				tipoDoc = "DEP";
			break;
		}
		if (tipoDoc == null)
			throw new Exception("Situazione imprevista, tipoDoc=" + tipoDoc);

		totRec.setTipologiaDocumento(tipoDoc);
		totRec.setNumeroDettagli(numDettagli);
		totRec.setNumeroDettagliCaricati(numDettagli);
		totRec.setNumeroDettagliScartati(0);
		totRec.setNomeFileFisicoImg(" ");
		totRec.setNomeFileLog(" ");
		totRec.setNomeFileLogDettaglio(" ");
		totRec.setNomeFileDati(file.getFileName());
		totRec.setDataUltimoAgg(new Timestamp(file.dataElaborazione.getTime()));
		totRec.setOperatoreUltimoAgg("Batch");
//		totRec.setPaginaInizio(0);
//		totRec.setPaginaFine(0);

		TestataFlussoOtticoDao totDao = new TestataFlussoOtticoDao(connection, schema);
		totDao.doInsert(totRec);

		for (Documento doc : file.listaDocumenti) {
			// inserisco un dettaglio per ogni documento...
			logger.debug("Inserimento tabella DOT, numDocumento=" + doc.numero);
			try {

				String operatoreUltimoAgg = "Batch";

				// select per ottenere la chiave flusso
				TestataFlussoOttico totRecord = totDao.doDetail(codiceSocieta, codiceUtente, chiaveEnte,
						file.getFileName());

				String chiaveTestata = totRecord.getChiaveFlusso();

				logger.debug("TEST: INFORMAZIONI PRELIMINARI INSERIEMNT DOT: " + "CSOC = " + codiceSocieta + "\n"
						+ "CUTE = " + codiceUtente + "\n" + "CENT = " + chiaveEnte + "\n" + "CFLOW = " + chiaveTestata
						+ "\n");

				DettaglioFlussoOttico dett = new DettaglioFlussoOttico();

				dett.setChiaveFlussoOttico(chiaveTestata);
				dett.setCodiceSocieta(codiceSocieta);
				dett.setCodiceUtente(codiceUtente);
				dett.setCodiceEnte(chiaveEnte);
				dett.setTipologiaDocumento(tipoDoc);
				dett.setNumeroDocumento(doc.numero);
				dett.setChiaveQuietanza(0);
				dett.setCodiceBollettino(" ");
				dett.setNumeroQuietanza("0");
				dett.setChiaveRelata(" ");
				dett.setNomeFileFisicoImg(" ");
				dett.setDataUltimoAggiornamento(new Timestamp(file.dataElaborazione.getTime()));
				dett.setOperatoreUltimoAggiornamento("Batch");
				dett.setCodiceFiscaleDebitore(doc.debitore.codiceFiscale);
				dett.setCodiceImpostaServizio(doc.codiceImpostaServizio);	//PG22XX01_GG1

				logger.debug("TEST: INSERIMENTO TABELLA DOT: " + dett.toString());	
				DettaglioFlussoOtticoDao detDao = new DettaglioFlussoOtticoDao(connection, schema);
				Boolean retVal = detDao.doInsert(dett);

			} catch (Exception ex) {
				logger.error("dotTableInsert failed, generic error due to: ", ex);
				throw new Exception("dotTableInsert failed, generic error due to: ", ex);
			}
		}
		String msg = String.format("Inserito nel DB: flusso=%s, numeroDocumenti=%d", file.getFileName(),
				file.listaDocumenti.size());
		printRow(PRINT_SYSOUT, msg);
		logger.info(msg);
	}

	/**
	 * Ogni ente ha la sua catella in cui scrivo il file tmp e ci rimane come
	 * storico.
	 * 
	 * @throws Exception
	 */
	File getFile512Path(File512 file) throws Exception {
		String path = getDirectoryOutputEnte(file.ente, file.societa, file.cutecute);
		return new File(new File(path), file.getFileName());
	}

	String getDirectoryOutputEnte(String codiceEnte, String societa, String codiceUtente) throws Exception {
		String path = null;
		for (Configurazione conf : confPayerList) {
			if (conf.getCodiceEnte().equals(codiceEnte) && conf.getCodiceSocieta().equals(societa)
					&& conf.getCodiceUtente().equals(codiceUtente)) {
				path = conf.getDirectoryFlussiDatiOtticoInput();
			}
		}
		if (path != null)
			return path;
		else
      throw new Exception(String.format(
          "Manca configurazione getDirectoryFlussiDatiOtticoInput() per ente:%s, societa:%s, utente:%s",
          codiceEnte, societa, codiceUtente));
	}

	private void stampaRiepilogoAnagrafica() {
		String riga = " ";

		riga = lineSeparator;
		printRow(PRINT_REPORT, riga, PrintCodes.AFTER_LINE);

		riga = "Riepilogo Elaborazione:                         ";
		printRow(PRINT_REPORT, riga, PrintCodes.AFTER_LINE);
	}

	public void printRow(String printer, String row) {
		System.out.println(row);
		if (classPrinting != null)
			try {
				classPrinting.print(printer, row);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public void printRow(String printer, String row, PrintCodes printCodes) {
		System.out.println(row);
		if (classPrinting != null)
			try {
				classPrinting.print(printer, row, printCodes);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}
	
	public void sendMailError(String oggettoMail, String bodyEmail,String cutecute) { 
		   EMailSenderResponse emsRes = null;
			try {
				if(inviaAvvisiForGeosContext.getMailErroreBatch()!=null) {
					String endPoint="";
					String oggetto = "";
					printRow(PRINT_SYSOUT, "inviaAvvisiForGeosContext.getMailErroreBatch() = " + inviaAvvisiForGeosContext.getMailErroreBatch());
					
					endPoint = inviaAvvisiForGeosContext.getWsMailSender();
					
					
					EMailSender emailSender = new EMailSender(endPoint);
					
					
					emsRes = emailSender.sendEMail(inviaAvvisiForGeosContext.getMailErroreBatch(), "", "", oggettoMail, "<pre>" + bodyEmail.toString() + "</pre>", "", cutecute);
					
				}
				
			} catch (Exception e) {
				try {
					throw new Exception("errore nella creazione dell'email", e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
		
		}

//	
//	private void acquisisciFiles() throws Exception {
//		//Gestione files in FTP
//		String localFolder = inviaAvvisiForGeosContext.getInputDir();
//		boolean success = true;
//		printRow(PRINT_SYSOUT, "Copia flussi da rivestire dalla directory FTP di input alla directory di input");
//		//Sposto tutti i files dalla directory di input FTP alla directory di input locale
//		remoteFilesAnaDaRivestire = ftp.listFolderFiles(inviaAvvisiForGeosContext, "DARIVESTIRE");
//		
//		if (remoteFilesAnaDaRivestire.isEmpty()){
//			printRow(PRINT_SYSOUT, "Non ci sono flussi da rivestire nella directory FTP di input");
//		} else {
//			printRow(PRINT_SYSOUT, "Copia flussi di rivestizione dalla directory FTP di input alla directory di input");
//			//Sposto tutti i files dalla directory di input FTP alla directory di input locale
//			remoteFilesRivestizioniAna = ftp.listFolderFiles(inviaAvvisiForGeosContext, "RIVESTITE");
//			
//			printRow(PRINT_SYSOUT, lineSeparator);
//			printRow(PRINT_SYSOUT, "Elaborazione flussi da rivestire nella directory di input");
//			for(String remoteFileAnaDaRivestirePath : remoteFilesAnaDaRivestire) {			
//				printRow(PRINT_SYSOUT, lineSeparator);
//				printRow(PRINT_SYSOUT, "Acquisizione file " + remoteFileAnaDaRivestirePath);
//				File remoteFileAnaDaRivestire = new File(remoteFileAnaDaRivestirePath);
//				String localFileAnaDaRivestirePath = localFolder + "/" + remoteFileAnaDaRivestire.getName();
//				File localFileAnaDaRivestire = new File(localFileAnaDaRivestirePath);
//				printRow(PRINT_SYSOUT, "Spostamento file in acquisizione dalla directory di input alla directory di progress");
//				if (spostaInAnagraficaProgressDir(localFileAnaDaRivestire)){
//					//Elaboro tutte le anagrafiche nella directory di progress per il file ftp in esame
//					recordAnagraficheDaRivestire = 0;
//					File[] filesAnagraficaTxT = getInputFiles(inviaAvvisiForGeosContext.getProgressDir());
//					for (File flussoFisicoAnagrafica : filesAnagraficaTxT) {
//						if (flussoFisicoAnagrafica.getName().toUpperCase().startsWith("SCARTIDARIVESTIRE")) {
//							acquisisciAnagraficaDaRivestire(flussoFisicoAnagrafica);	//popolamento PYADRTB
//						}
//					}
//				}
//			}
//			
//			if (remoteFilesRivestizioniAna.isEmpty()){
//				printRow(PRINT_SYSOUT, "Non ci sono flussi di rivestizione nella directory FTP di input");
//			} else {
//				printRow(PRINT_SYSOUT, lineSeparator);
//				printRow(PRINT_SYSOUT, "Elaborazione flussi di rivestizione nella directory di input");
//				for(String remoteFileRivestizioniAnaPath : remoteFilesRivestizioniAna) {			
//					printRow(PRINT_SYSOUT, lineSeparator);
//					printRow(PRINT_SYSOUT, "Acquisizione file " + remoteFileRivestizioniAnaPath);
//					File remoteFileRivestizioniAna = new File(remoteFileRivestizioniAnaPath);
//					String localFileRivestizioniAnaPath = localFolder + "/" + remoteFileRivestizioniAna.getName();
//					File localFileRivestizioniAna = new File(localFileRivestizioniAnaPath);
//					printRow(PRINT_SYSOUT, "Spostamento file in acquisizione dalla directory di input alla directory di progress");
//					if (spostaInAnagraficaProgressDir(localFileRivestizioniAna)){
//						//Elaboro tutte le anagrafiche nella directory di progress per il file ftp in esame
//						recordRivestizioniAnagrafiche = 0;
//						File[] filesAnagraficaTxT = getInputFiles(inviaAvvisiForGeosContext.getProgressDir());
//						for (File flussoFisicoAnagrafica : filesAnagraficaTxT) {
//							if (!flussoFisicoAnagrafica.getName().toUpperCase().startsWith("SCARTIDARIVESTIRE")) {
//								acquisisciRivestizioneAnagrafica(flussoFisicoAnagrafica);	//popolamento PYRVATB}
//							}
//						}
//					}
//				}
//			}
//		}
//	}
//	
//	private void deleteFilesFtp() {
//		if (remoteFilesAnaDaRivestire != null) {
//			printRow(PRINT_SYSOUT, "Eliminazione flussi da rivestire dalla directory FTP di input");
//			for(String remoteFileAnaDaRivestirePath : remoteFilesAnaDaRivestire) {			
//				ftp.deleteFile(inviaAvvisiForGeosContext, remoteFileAnaDaRivestirePath, "DARIVESTIRE");	//cancellazione da FTP al termine dell'elaborazione
//			}
//			if (remoteFilesRivestizioniAna!=null) {
//				printRow(PRINT_SYSOUT, "Eliminazione flussi di rivestizione dalla directory FTP di input");
//				for(String remoteFileRivestizioniAnaPath : remoteFilesRivestizioniAna) {				
//					ftp.deleteFile(inviaAvvisiForGeosContext, remoteFileRivestizioniAnaPath, "RIVESTITE");	//cancellazione da FTP al termine dell'elaborazione
//				}
//			}
//		}
//	}
	
}
