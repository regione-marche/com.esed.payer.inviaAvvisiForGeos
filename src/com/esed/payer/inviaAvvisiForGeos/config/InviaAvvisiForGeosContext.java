package com.esed.payer.inviaAvvisiForGeos.config;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.esed.payer.inviaAvvisiForGeos.util.CipherHelper;
import com.seda.bap.components.core.spi.ClassPrinting;
import com.seda.commons.properties.tree.PropertiesTree;
/**
 * Contiene parametri e properties di configurazione specifici di questo modulo.
 * Le properties di configurazione vengono interpretate e ritornate con appositi getter.
 *  
 * Ci sono properties con chiave composta es. codiceUtente.societa.ente, codiceUtente.idDominio.auxDigit.applicationCode
 * Implemento degli appositi getProperty
 * 
 *@author luciano.dercoli@gmail.com
 **/
public class InviaAvvisiForGeosContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2947950105036187706L;

	private Properties config;
	
	protected HashMap<String, List<String>> parameters = new HashMap<String, List<String>>();
	
	
	public InviaAvvisiForGeosContext() {
	}
	
	 
	public InviaAvvisiForGeosContext(PropertiesTree propertiesTree,
			DataSource dataSource, String schema, Logger logger,
			ClassPrinting printers, String idJob, Properties config) {
		super();
		this.config = config;
	}	
	
	public String getLogger() {
		return config.getProperty("log4j.path");
	}
	public Properties getProperties() {
		return config;
	}
	public void setConfig(Properties config) {
		this.config = config;
	}
	
	public String getInputDir() {
		return config.getProperty("directory.input");
	}
	public String getOutputDir() {
		return config.getProperty("directory.output");
	}
	public String getOutputFile() {
		return config.getProperty("file.output");
	}
	public String getProgressDir() {
		return config.getProperty("directory.progress");
	}
	public String getRejectedDir() {
		return config.getProperty("directory.rejected");
	}
	public String getBackupDir() {
		return config.getProperty("directory.backup");
	}
	public String getEncryptionIV() {
		return config.getProperty("security.encryption.iv");
	}
	public String getEncryptionKEY() {
		return config.getProperty("security.encryption.key");
	}
	/** Url completo (protocollo,host,porta,path) specifico per "cutecute".
	 * Nel file di config il %s viene sostituito dal cutecute */
	public String getFtpUrl(String codUtente) {
		String baseUrl = getFtpBaseUrl();
		return baseUrl;
		//return String.format(baseUrl, codUtente);
	}
	public String getFtpBaseUrl() {
		String baseUrl = config.getProperty("ftp.Geos.url");
		return baseUrl;
	}
	public String getFtpUser() {
		return config.getProperty("ftp.Geos.user");
	}
	
	public String getDirFtp(String codUtente) {
		String appo = config.getProperty("ftp.Geos.dir");
		return String.format(appo, codUtente);
		
	}
	public String getFtpPassword() {
		String password = config.getProperty("ftp.Geos.password");
		CipherHelper cipher = new CipherHelper(getEncryptionIV(), getEncryptionKEY());
		String decrypterPassword = cipher.decryptData(password);
		return decrypterPassword;
	}
	public String encryptPassword(String clearPass)
	{
		CipherHelper cipher = new CipherHelper(getEncryptionIV(), getEncryptionKEY());
		String cryptedPassword = cipher.cryptData(clearPass);
		return cryptedPassword;
	}

	
	/**
	 * In riferimento alla Stringa Barcode.<br/>
	 * dobbiamo mettere una parte di questa stringa (quella da 13 bite) su un file properties (quello del batch)
	 * L'IdDominio, corrisponde al codice fiscale presente sull'Ente che si trova sulla tabella PYENTTB.
	 * 
//	La chiave per andare a prendere il valore di questa stringa in base al "numero avviso pagoPa" di appartenenza,
//	dovrà essere:
//	                CUTECUTE.IDDOMINIO.AUX_DIGIT.APPLICATION_CODE = ciamammacomemdsaifjsdlfjld
//	 
//	Esempio:
//	                000P4.01234567890.0.01 = 01234567890123
*	AUX_DIGIT e Application code sono i primi 3 caratteri del Numero Avviso PagoPa che hai in canna (idbollettino)
*/
	public String getBarcodeParameter(String cutecute, String codFiscEnte, String auxDigit, String applicationCode) {
		//marini
		codFiscEnte=codFiscEnte.trim();
		String key = String.format("barcode.%s.%s.%s.%s", cutecute, codFiscEnte, auxDigit, applicationCode);
		return config.getProperty(key);
	}
	
	public int addParameter(String name, String value) {
		if(!this.parameters.containsKey(name)) {
			this.parameters.put(name, new LinkedList<String>());
		}
		this.parameters.get(name).add(value); //Aggiunge un valore alla lista delle ripetizioni
		return this.parameters.get(name).size();
	}
	
	public String getParameter(String name) {
		if(parameters.containsKey(name))
			return (String)parameters.get(name).get(0);
		else
			return "";
	}
	
	/** Parametri di lancio della procedura, passati dallo schedulatore */
	public void loadSchedeBap(String[] params) {
		for (int i=0;i < params.length; i++ ) {
			String[] p = params[i].split("\\s+");
			//if (p[0].equals("END") || p[0].equals("CONFIGPATH") || p[0].equals("CUTECUTE")|| p[0].equals("ENTE")) {
			if (p[0].equals("END")) {
				if (p[1].trim().equals("")) {
					addParameter(p[0].trim(), "");
				} else {
					addParameter(p[0].trim(), p[1].trim());
				}
			} else {
				addParameter(p[0].trim(), p[1].trim());//Nome parametro - valore(Aggiunge Lista di valori per schede con ripetizione)	
			}
		}
	}

	public String formatDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		return formatter.format(date);
	}
	
	public String getMailErroreBatch() {
		return config.getProperty("mail.errorebatch");
	}
	
	public String getWsMailSender() {
		return config.getProperty("url.mailsender");
	}
	
	/**
	 * Il template da utilizzare per il flusso.
	 * @return una stringa "STANDARD_" oppure "POSTE_" 
	 **/
	public String getTipoTemplate (String codiceUtente, String ente,String servizio, String tipoIban) {
		
		if("POSTE".equals(tipoIban)||"STANDARD".equals(tipoIban))
		{
			String templateKey = String.format("template.%s.%s.%s.%s", codiceUtente,ente,servizio.trim(),tipoIban); 
			String tipoTemplate = getProperties().getProperty(templateKey);
		
			if(tipoTemplate==null || tipoTemplate.length()==0) {
				throw new RuntimeException("Manca configurazione template: "+templateKey);
				
			}
				
			tipoTemplate = tipoTemplate.trim();
			
			if(!tipoTemplate.equals("STANDARD_") && !tipoTemplate.equals("POSTE_"))
				throw new RuntimeException("configurazione template errata: "+templateKey+"="+tipoTemplate);

			return tipoTemplate;
		}
		else
			throw new IllegalArgumentException("tipoIban="+tipoIban);
			

	}
	
	public String getCodiceAutorizzazione(String codiceUtente,String societa, String ente) {
		String key = String.format("autorizzazione.%s.%s.%s", codiceUtente,societa ,ente); 
		String codiceAut = getProperties().getProperty(key);
		//PAGONET - 368 - inizio
//		if(codiceAut==null || codiceAut.length()==0)
//			throw new RuntimeException("Manca configurazione Codice Autorizzazione: "+key);
		if(codiceAut==null || codiceAut.length()==0)
			System.out.println("Manca configurazione Codice Autorizzazione su file: "+key);
		//PAGONET - 368 - fine
		 if (codiceAut != null)
			 codiceAut = codiceAut.trim();
		return codiceAut;
	}
	
	public String getCbill(String codiceUtente,String societa, String dominio) {
			String key = String.format("cbill.%s.%s.%s", codiceUtente,societa ,dominio.trim()); 
			String codiceCbill = getProperties().getProperty(key);
			if(codiceCbill==null || codiceCbill.length() == 0)
				System.out.println("Manca configurazione Codice Autorizzazione su file: "+key);
			else {
				codiceCbill = codiceCbill.trim();
			}

		 return codiceCbill;
	}

	/** fornito come parametro BAP */
	public String getCodiceUtente() {
		return getParameter("CUTECUTE");
	}
	
	
}
