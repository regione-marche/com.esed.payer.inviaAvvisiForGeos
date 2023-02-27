package com.esed.payer.inviaAvvisiForGeos.model;

import java.io.Serializable;
import java.sql.Date;

import com.seda.data.dao.ModelAttributes;

public class AvvisiForGeos extends ModelAttributes implements Serializable {
	
	private static final long serialVersionUID = 5858280198910356171L;
	
	String codiceSocieta;
	String codiceUtente;
	String codiceAnagraficaEnte;
	String codiceFiscale;
	String cognome;
	String nome;
	String numeroCellulare;
	String indirizzoMail;
	String indirizzoMailPec;
	String flagAttivazione;
	String codiceAttivazione;
	String flagPrimoAccesso;
	String flagProduzioneWelcomeKit;
	String flagProduzioneFileCsi;
	String flagInfoDaBorsellino;
	String indirizzo;
	String cap;
	String comune;
	String provincia;
	Date dataCaricamento;
	Date dataAggiornamento;
	String operatoreAgggiornamento;
	
	public String getCodiceSocieta() {
		return codiceSocieta;
	}
	public void setCodiceSocieta(String codiceSocieta) {
		this.codiceSocieta = codiceSocieta;
	}
	public String getCodiceUtente() {
		return codiceUtente;
	}
	public void setCodiceUtente(String codiceUtente) {
		this.codiceUtente = codiceUtente;
	}
	public String getCodiceAnagraficaEnte() {
		return codiceAnagraficaEnte;
	}
	public void setCodiceAnagraficaEnte(String codiceAnagraficaEnte) {
		this.codiceAnagraficaEnte = codiceAnagraficaEnte;
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) {
		this.cognome = cognome;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getNumeroCellulare() {
		return numeroCellulare;
	}
	public void setNumeroCellulare(String numeroCellulare) {
		this.numeroCellulare = numeroCellulare;
	}
	public String getIndirizzoMail() {
		return indirizzoMail;
	}
	public void setIndirizzoMail(String indirizzoMail) {
		this.indirizzoMail = indirizzoMail;
	}
	public String getIndirizzoMailPec() {
		return indirizzoMailPec;
	}
	public void setIndirizzoMailPec(String indirizzoMailPec) {
		this.indirizzoMailPec = indirizzoMailPec;
	}
	public String getFlagAttivazione() {
		return flagAttivazione;
	}
	public void setFlagAttivazione(String flagAttivazione) {
		this.flagAttivazione = flagAttivazione;
	}
	public String getCodiceAttivazione() {
		return codiceAttivazione;
	}
	public void setCodiceAttivazione(String codiceAttivazione) {
		this.codiceAttivazione = codiceAttivazione;
	}
	public String getFlagPrimoAccesso() {
		return flagPrimoAccesso;
	}
	public void setFlagPrimoAccesso(String flagPrimoAccesso) {
		this.flagPrimoAccesso = flagPrimoAccesso;
	}
	public String getFlagProduzioneWelcomeKit() {
		return flagProduzioneWelcomeKit;
	}
	public void setFlagProduzioneWelcomeKit(String flagProduzioneWelcomeKit) {
		this.flagProduzioneWelcomeKit = flagProduzioneWelcomeKit;
	}
	public String getFlagProduzioneFileCsi() {
		return flagProduzioneFileCsi;
	}
	public void setFlagProduzioneFileCsi(String flagProduzioneFileCsi) {
		this.flagProduzioneFileCsi = flagProduzioneFileCsi;
	}
	public String getFlagInfoDaBorsellino() {
		return flagInfoDaBorsellino;
	}
	public void setFlagInfoDaBorsellino(String flagInfoDaBorsellino) {
		this.flagInfoDaBorsellino = flagInfoDaBorsellino;
	}
	public String getIndirizzo() {
		return indirizzo;
	}
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	public String getCap() {
		return cap;
	}
	public void setCap(String cap) {
		this.cap = cap;
	}
	public String getComune() {
		return comune;
	}
	public void setComune(String comune) {
		this.comune = comune;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public Date getDataCaricamento() {
		return dataCaricamento;
	}
	public void setDataCaricamento(Date dataCaricamento) {
		this.dataCaricamento = dataCaricamento;
	}
	public Date getDataAggiornamento() {
		return dataAggiornamento;
	}
	public void setDataAggiornamento(Date dataAggiornamento) {
		this.dataAggiornamento = dataAggiornamento;
	}
	public String getOperatoreAgggiornamento() {
		return operatoreAgggiornamento;
	}
	public void setOperatoreAgggiornamento(String operatoreAgggiornamento) {
		this.operatoreAgggiornamento = operatoreAgggiornamento;
	}
	
	public AvvisiForGeos() {
		super();
	}
	@Override
	public String toString() {
		return "AnagraficaEstrattoConto [cap=" + cap
				+ ", codiceAnagraficaEnte=" + codiceAnagraficaEnte
				+ ", codiceAttivazione=" + codiceAttivazione
				+ ", codiceFiscale=" + codiceFiscale + ", codiceSocieta="
				+ codiceSocieta + ", codiceUtente=" + codiceUtente
				+ ", cognome=" + cognome + ", comune=" + comune
				+ ", dataAggiornamento=" + dataAggiornamento
				+ ", dataCaricamento=" + dataCaricamento + ", flagAttivazione="
				+ flagAttivazione + ", flagInfoDaBorsellino="
				+ flagInfoDaBorsellino + ", flagPrimoAccesso="
				+ flagPrimoAccesso + ", flagProduzioneFileCsi="
				+ flagProduzioneFileCsi + ", flagProduzioneWelcomeKit="
				+ flagProduzioneWelcomeKit + ", indirizzo=" + indirizzo
				+ ", indirizzoMail=" + indirizzoMail + ", indirizzoMailPec="
				+ indirizzoMailPec + ", nome=" + nome + ", numeroCellulare="
				+ numeroCellulare + ", operatoreAgggiornamento="
				+ operatoreAgggiornamento + ", provincia=" + provincia + "]";
	}
}
