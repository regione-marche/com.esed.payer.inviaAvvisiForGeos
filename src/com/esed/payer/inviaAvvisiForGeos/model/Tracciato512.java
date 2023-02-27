package com.esed.payer.inviaAvvisiForGeos.model;

import java.util.ArrayList;
import java.util.List;

import com.esed.payer.inviaAvvisiForGeos.util.FlatFileHelper;
import com.esed.payer.inviaAvvisiForGeos.util.FlatFileHelper.FlatFileField;
import com.esed.payer.inviaAvvisiForGeos.util.FlatFileHelper.FlatFileFieldType;
import com.seda.payer.commons.inviaAvvisiForGeos.AvvisoRata;
import com.seda.payer.commons.inviaAvvisiForGeos.Debitore;
import com.seda.payer.commons.inviaAvvisiForGeos.Documento;
import com.seda.payer.commons.inviaAvvisiForGeos.File512;
import com.seda.payer.commons.inviaAvvisiForGeos.Flusso;
import com.seda.payer.commons.inviaAvvisiForGeos.Tributo;

/**
 * Tracciato record 512. Inizialmente in base alle specifiche v0.8.0.26 [2017-03-20]... tuttavia
 * successivamente ci sono state modifiche/aggiustamneti.
 * Serializzazione in un file flusso di una lista di {@link AvvisoRata}.
 * 
 **/
public class Tracciato512 {
	/** Record 0 */
	static class Rec0 {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"0");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField DATA_FORNITURA= new FlatFileField("DATA_FORNITURA", FlatFileFieldType.String, 10, 8, true);
		static FlatFileField DESCRIZIONE_ENTE= new FlatFileField("DESCRIZIONE_ENTE", FlatFileFieldType.String, 18, 40, true);
		static FlatFileField INDIRIZZO_ENTE= new FlatFileField("INDIRIZZO_ENTE", FlatFileFieldType.String, 58, 30, true);
		static FlatFileField CAP_ENTE= new FlatFileField("CAP_ENTE", FlatFileFieldType.String, 88, 5, true);
		static FlatFileField COMUNE_ENTE= new FlatFileField("COMUNE_ENTE", FlatFileFieldType.String, 93, 30, true);
		static FlatFileField PROVINCIA_ENTE= new FlatFileField("PROVINCIA_ENTE", FlatFileFieldType.String, 123,2, true);
		static FlatFileField CODICE_FISCALE_ENTE= new FlatFileField("CODICE_FISCALE_ENTE", FlatFileFieldType.String, 125, 16, true);
		static FlatFileField CODICE_UTENTE= new FlatFileField("CODICE_UTENTE", FlatFileFieldType.String, 141, 5, true);
		static FlatFileField IDENTIFICATIVO_CARICO= new FlatFileField("IDENTIFICATIVO_CARICO", FlatFileFieldType.String, 146, 21, true);
		static FlatFileField _IMPORTO_MINIMO= new FlatFileField("IMPORTO_MINIMO", FlatFileFieldType.Numeric, 245, 9, true);
		static FlatFileField _IMPORTO_MASSIMO= new FlatFileField("IMPORTO_MASSIMO", FlatFileFieldType.Numeric, 254, 9, true);
		static FlatFileField TIPO_FLUSSO= new FlatFileField("TIPO_FLUSSO", FlatFileFieldType.Numeric, 266, 2, true);
		static FlatFileField _ASSENZA_BOLLETTINI= new FlatFileField("ASSENZA_BOLLETTINI", FlatFileFieldType.Numeric, 326, 1, true);
		static FlatFileField _FLAG_ENTE= new FlatFileField("FLAG_ENTE", FlatFileFieldType.Numeric, 327, 1, true);
		static FlatFileField _CODICE_ENTE_IMPOSITORE= new FlatFileField("CODICE_ENTE_IMPOSITORE", FlatFileFieldType.Numeric, 328, 6, true);
		static FlatFileField PROGRESSIVO_FLUSSO= new FlatFileField("PROGRESSIVO_FLUSSO", FlatFileFieldType.String, 482, 9, true);
		static FlatFileField VERSIONE_FLUSSO= new FlatFileField("VERSIONE_FLUSSO", FlatFileFieldType.Constant, 491, 8, true,"00800004");
		static FlatFileField PROCESSO= new FlatFileField("PROCESSO", FlatFileFieldType.String, 499, 4, true);
		static FlatFileField CBILL= new FlatFileField("CBILL", FlatFileFieldType.String, 503, 10, true);
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(DATA_FORNITURA);
			fileHelper.addField(DESCRIZIONE_ENTE);
			fileHelper.addField(INDIRIZZO_ENTE);
			fileHelper.addField(CAP_ENTE);
			fileHelper.addField(COMUNE_ENTE);
			fileHelper.addField(PROVINCIA_ENTE);
			fileHelper.addField(CODICE_FISCALE_ENTE);
			fileHelper.addField(CODICE_UTENTE);
			fileHelper.addField(IDENTIFICATIVO_CARICO);
			fileHelper.addField(_IMPORTO_MINIMO);
			fileHelper.addField(_IMPORTO_MASSIMO);
			fileHelper.addField(TIPO_FLUSSO);
			fileHelper.addField(_ASSENZA_BOLLETTINI);
			fileHelper.addField(_FLAG_ENTE);
			fileHelper.addField(_CODICE_ENTE_IMPOSITORE);
			fileHelper.addField(PROGRESSIVO_FLUSSO);
			fileHelper.addField(VERSIONE_FLUSSO);
			fileHelper.addField(PROCESSO);
			fileHelper.addField(CBILL);
			addEmptyFields(fileHelper);
		}

		public synchronized static String format(File512 f) {
//			IMPOSTA_SERVIZIO.setValue();
//			DATA_FORNITURA.setValue(f.data);

			// "ENTE AGGRAGANTE" in EPTS.
			// Esiste SOLO per alcuni utenti... e andrebbero valorizzati i seguenti 
			// campi a livello di flusso.
//			CODICE_ENTE.setValue(f.ente);
			DESCRIZIONE_ENTE.setValue(f.descrizioneEnte);
//			INDIRIZZO_ENTE.setValue(f.);
//			CAP_ENTE.setValue(f.);
//			COMUNE_ENTE.setValue(f.);
//			PROVINCIA_ENTE.setValue(f.);
//			CODICE_FISCALE_ENTE.setValue(f.);
			
			CODICE_UTENTE.setValue(f.cutecute);
//			IDENTIFICATIVO_CARICO.setValue(f.);
			TIPO_FLUSSO.setValue(01);	// TODO: verificare tipo flusso
//			ASSENZA_BOLLETTINI.setValue(0);

			PROGRESSIVO_FLUSSO.setValue(f.idFlusso);
			PROCESSO.setValue("AVVI");
			CBILL.setValue(f.cBill);
			
			
			return fileHelper.toString();
		}

	}
	
	/** Record A */
	static class RecA {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"A");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField ID_CONTRIBUENTE = new FlatFileField("ID_CONTRIBUENTE", FlatFileFieldType.String, 10, 10, true);
		static FlatFileField DENOMINAZIONE_1 = new FlatFileField("DENOMINAZIONE_1", FlatFileFieldType.String, 20, 50, true);
		static FlatFileField INDIRIZZO_1= new FlatFileField("INDIRIZZO_1", FlatFileFieldType.String, 70, 50, true);
		static FlatFileField CAP_1= new FlatFileField("CAP_1", FlatFileFieldType.String, 120, 5, true);
		static FlatFileField COMUNE_1= new FlatFileField("COMUNE_1", FlatFileFieldType.String, 125, 30, true);
		static FlatFileField PROVINCIA_1= new FlatFileField("PROVINCIA_1", FlatFileFieldType.String, 155, 2, true);
		static FlatFileField CODFISC_1 = new FlatFileField("CODFISC_1", FlatFileFieldType.String, 157, 16, true);
		static FlatFileField CODFISC_2 = new FlatFileField("CODFISC_2", FlatFileFieldType.String, 310, 16, true);

		static FlatFileField _MINIMO_ANNUO = new FlatFileField("MINIMO_ANNUO", FlatFileFieldType.Numeric, 439, 8, true);
		static FlatFileField _NUMERO_CONC_DOM = new FlatFileField("NUMERO_CONC_DOM", FlatFileFieldType.Numeric, 456, 4, true);
		static FlatFileField _NUMERO_CONC_NDOM = new FlatFileField("NUMERO_CONC_NDOM", FlatFileFieldType.Numeric, 460, 4, true);
		static FlatFileField _NUOVE_CONC_DOM = new FlatFileField("NUOVE_CONC_DOM", FlatFileFieldType.Numeric, 472, 4, true);
		static FlatFileField _NUOVE_CONC_NDOM = new FlatFileField("NUOVE_CONC_NDOM", FlatFileFieldType.Numeric, 476, 4, true);
		static FlatFileField _PORTATA = new FlatFileField("PORTATA", FlatFileFieldType.Numeric, 480, 8, true);
		static FlatFileField _FATTORE_MOLTIPLIC = new FlatFileField("FATTORE_MOLTIPLIC", FlatFileFieldType.Numeric, 502, 4, true);
		static FlatFileField _NUM_GG_SCADENZA_1RATA_NOTIFICA= new FlatFileField("NUM_GG_SCADENZA_1RATA_NOTIFICA", FlatFileFieldType.Numeric, 510, 3, true);
		

		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(ID_CONTRIBUENTE);
			fileHelper.addField(DENOMINAZIONE_1);
			fileHelper.addField(INDIRIZZO_1);
			fileHelper.addField(CAP_1);
			fileHelper.addField(COMUNE_1);
			fileHelper.addField(PROVINCIA_1);
			fileHelper.addField(CODFISC_1);
			fileHelper.addField(CODFISC_2);

			fileHelper.addField(_MINIMO_ANNUO);
			fileHelper.addField(_NUMERO_CONC_DOM);
			fileHelper.addField(_NUMERO_CONC_NDOM);
			fileHelper.addField(_NUOVE_CONC_DOM);
			fileHelper.addField(_NUOVE_CONC_NDOM);
			fileHelper.addField(_PORTATA);
			fileHelper.addField(_FATTORE_MOLTIPLIC);
			fileHelper.addField(_NUM_GG_SCADENZA_1RATA_NOTIFICA);

			addEmptyFields(fileHelper);
		}

		public synchronized static String format(Debitore deb) {
			CODICE_ENTE.setValue(deb.listaDocumenti.get(0).codiceEnte);
			IMPOSTA_SERVIZIO.setValue("  ");
//			ID_CONTRIBUENTE.setValue("??");

			
			DENOMINAZIONE_1.setValue(deb.nomeCognRagSoc);
			INDIRIZZO_1.setValue(deb.indirizzo);
			CAP_1.setValue(deb.cap);
			COMUNE_1.setValue(deb.comune);
			PROVINCIA_1.setValue(deb.provincia);
			CODFISC_1.setValue(deb.codiceFiscale);
			CODFISC_2.setValue(deb.codiceFiscaleAlternativo);
			
			return fileHelper.toString();
		}
		public synchronized static String serializeXml(Debitore deb) {
			format(deb);
			String s = "<RecA>";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecA>";
			return s;
		}
	}
	
	/** Record H */
	static class RecH {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"H");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField ID_CONTRIBUENTE = new FlatFileField("ID_CONTRIBUENTE", FlatFileFieldType.String, 10, 10, true);
		static FlatFileField DENOMINAZIONE_1 = new FlatFileField("DENOMINAZIONE_1", FlatFileFieldType.String, 20, 120, true);
		static FlatFileField DENOMINAZIONE_2= new FlatFileField("DENOMINAZIONE_2", FlatFileFieldType.String, 210, 120, true);
		
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
//			fileHelper.addField(CODICE_ENTE);
//			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(ID_CONTRIBUENTE);
			fileHelper.addField(DENOMINAZIONE_1);
			fileHelper.addField(DENOMINAZIONE_2);
			addEmptyFields(fileHelper);
		}

		public synchronized static String format(Debitore deb) {
			CODICE_ENTE.setValue(deb.listaDocumenti.get(0).codiceEnte);
			IMPOSTA_SERVIZIO.setValue("  ");
			ID_CONTRIBUENTE.setValue("  ");
			DENOMINAZIONE_1.setValue(deb.mail);
			DENOMINAZIONE_2.setValue(deb.pec);
			return fileHelper.toString();
		}
		
		public synchronized static String serializeXml(Debitore deb) {
			format(deb);
			String s = "<RecH>";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecH>";
			return s;
		}
	}
	
	/** Record E */
	static class RecE {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"E");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField ID_CONTRIBUENTE = new FlatFileField("ID_CONTRIBUENTE", FlatFileFieldType.String, 10, 10, true);
		/** ridefinisce EMISSIONE*/
		static FlatFileField ANNO_DOCUMENTO = new FlatFileField("ANNO_DOCUMENTO", FlatFileFieldType.String, 20, 6, true);
		static FlatFileField IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO= new FlatFileField("IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO", 
				FlatFileFieldType.String, 53, 11, true);
		/** ridefinisce campi 10...12.c*/
		static FlatFileField NUMERO_DOCUMENTO = new FlatFileField("NUMERO_DOCUMENTO", FlatFileFieldType.String, 64, 20, true);
		static FlatFileField TIPOLOGIA_SERVIZIO = new FlatFileField("TIPOLOGIA_SERVIZIO", FlatFileFieldType.String, 84, 5, true);
		static FlatFileField FATTURAZIONE_ELETTRONICA = new FlatFileField("FATTURAZIONE_ELETTRONICA", FlatFileFieldType.String, 89, 1, true);
		static FlatFileField NUMERO_AVVISO_PAGOPA_DOCUMENTO = new FlatFileField("NUMERO_AVVISO_PAGOPA_DOCUMENTO", FlatFileFieldType.String, 110,20 , true);
		static FlatFileField IUV_DOCUMENTO = new FlatFileField("IUV_DOCUMENTO", FlatFileFieldType.String, 130, 20, true);
	
		static FlatFileField _QUANTITA_1 = new FlatFileField("QUANTITA_1", FlatFileFieldType.Numeric, 152, 4, true);
		static FlatFileField _IVA_1 = new FlatFileField("IVA_1", FlatFileFieldType.Numeric, 176, 5, true);
	
		/** ridefinisce DESCRIZIONE_VOCE_2*/
		static FlatFileField DESCRIZIONE_ENTE = new FlatFileField("DESCRIZIONE_ENTE", FlatFileFieldType.String,193 ,42 , true);
		static FlatFileField _QUANTITA_2 = new FlatFileField("QUANTITA_2", FlatFileFieldType.Numeric, 235, 4, true);
		static FlatFileField _IVA_2 = new FlatFileField("IVA_2", FlatFileFieldType.Numeric, 259, 5, true);
		
		/** ridefinisce DESCRIZIONE_VOCE_3*/
		static FlatFileField DESCRIZIONE_SERVIZIO = new FlatFileField("DESCRIZIONE_SERVIZIO", FlatFileFieldType.String,276 ,42 , true);
		static FlatFileField _QUANTITA_3 = new FlatFileField("QUANTITA_3", FlatFileFieldType.Numeric, 318, 4, true);

		/** ridefinisce vari campi*/
		static FlatFileField IBAN = new FlatFileField("IBAN", FlatFileFieldType.String,332 ,35 , true);
		static FlatFileField QRCODE = new FlatFileField("QRCODE", FlatFileFieldType.String, 367, 60, true);
		static FlatFileField BARCODE = new FlatFileField("BARCODE", FlatFileFieldType.String, 427, 60, true);
		static FlatFileField CAUSALE = new FlatFileField("CAUSALE", FlatFileFieldType.String, 487, 50, true);
			
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(ID_CONTRIBUENTE);
			fileHelper.addField(ANNO_DOCUMENTO );

			fileHelper.addField(IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO );
			fileHelper.addField(NUMERO_DOCUMENTO );
			fileHelper.addField(TIPOLOGIA_SERVIZIO );
			fileHelper.addField(FATTURAZIONE_ELETTRONICA );
			fileHelper.addField(NUMERO_AVVISO_PAGOPA_DOCUMENTO );
			
			fileHelper.addField(IUV_DOCUMENTO );
			fileHelper.addField(_QUANTITA_1 );
			fileHelper.addField(_IVA_1 );
			fileHelper.addField(DESCRIZIONE_ENTE );
			fileHelper.addField(_QUANTITA_2 );

			fileHelper.addField(_IVA_2 );
			fileHelper.addField(DESCRIZIONE_SERVIZIO );
			fileHelper.addField(_QUANTITA_3 );
			fileHelper.addField(IBAN );
			fileHelper.addField(QRCODE );
			
			fileHelper.addField(BARCODE );
			fileHelper.addField(CAUSALE );
			addEmptyFields(fileHelper);
		}

		public synchronized static String format(Documento doc) {
			CODICE_ENTE.setValue(doc.codiceEnte);
			//20220406 - inizio
			//IMPOSTA_SERVIZIO.setValue("  ");
			IMPOSTA_SERVIZIO.setValue(doc.codiceImpostaServizio);
			//20220406 - fine
			ID_CONTRIBUENTE.setValue("");
			ANNO_DOCUMENTO.setValue(doc.anno);
			NUMERO_DOCUMENTO.setValue(doc.numero);

			// scarto zeri iniziali
			System.out.println("doc.codiceEnte = " + doc.codiceEnte);
			System.out.println("doc.importoTotal = " + doc.importoTotale);
			System.out.println("doc.numAvvisoPagoPal = " + doc.numAvvisoPagoPa);
			System.out.println("NUMERO_DOCUMENTO = " + NUMERO_DOCUMENTO);
			
			IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO.setValue(
					doc.importoTotale.substring(doc.importoTotale.length()-IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO.getLength()));
			TIPOLOGIA_SERVIZIO.setValue(doc.tipologiaServizio);
			FATTURAZIONE_ELETTRONICA.setValue(doc.fatturazioneElettronica?"Y":"N");
			NUMERO_AVVISO_PAGOPA_DOCUMENTO.setValue(doc.numAvvisoPagoPa);
			IUV_DOCUMENTO.setValue(doc.codiceIUV);
			
			DESCRIZIONE_ENTE.setValue(doc.descrizioneEnte);
			DESCRIZIONE_SERVIZIO.setValue(doc.descrizioneServizio);
			IBAN.setValue(doc.iban);
			QRCODE.setValue(doc.codiceQRcode);
			BARCODE.setValue(doc.codiceBarcode);
			
			CAUSALE.setValue(doc.causale);
			return fileHelper.toString();
		}
		
		public synchronized static String serializeXml(Documento doc) {
			format(doc);
			String s = "<RecE>\n";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecE>\n";
			return s;
		}
	}
	
	/** Record F */
	static class RecF {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"F");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField ID_CONTRIBUENTE = new FlatFileField("ID_CONTRIBUENTE", FlatFileFieldType.String, 10, 10, true);
		/** ridefinisce EMISSIONE*/
		static FlatFileField ANNO_DOCUMENTO = new FlatFileField("ANNO_DOCUMENTO", FlatFileFieldType.String, 20, 6, true);
		static FlatFileField IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO= new FlatFileField("IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO", 
				FlatFileFieldType.String, 53, 11, true);
		/** ridefinisce campi 10...12.c*/
//		static FlatFileField NUMERO_DOCUMENTO = new FlatFileField("NUMERO_DOCUMENTO", FlatFileFieldType.String, 64, 20, true);
//		static FlatFileField TIPOLOGIA_SERVIZIO = new FlatFileField("TIPOLOGIA_SERVIZIO", FlatFileFieldType.String, 84, 5, true);
//		static FlatFileField FATTURAZIONE_ELETTRONICA = new FlatFileField("FATTURAZIONE_ELETTRONICA", FlatFileFieldType.String, 89, 1, true);

		static FlatFileField VOCE[] = new FlatFileField[5];
		static FlatFileField TIPO_VOCE[] = new FlatFileField[5];
		static FlatFileField ANNO_VOCE[] = new FlatFileField[5];
		static FlatFileField FAMIGLIA_VOCE[] = new FlatFileField[5];
		static FlatFileField DESCRIZIONE_VOCE[] = new FlatFileField[5];
		static FlatFileField QUANTITA[] = new FlatFileField[5];
		static FlatFileField IMPO_UNIT[] = new FlatFileField[5];
		static FlatFileField IMPO_VOCE[] = new FlatFileField[5];
		static FlatFileField IVA_VOCE[] = new FlatFileField[5];
		
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// ci sono 5 ripetizioni degli stessi campi ad indicare 5 tributi (codice, importo...)
			for(int i=0;i<5;i++) {
				int offset=i*83;
				VOCE[i] = new FlatFileField("VOCE_"+(i+1), FlatFileFieldType.String, 98+offset, 4, true);
				TIPO_VOCE[i] = new FlatFileField("TIPO_VOCE_"+(i+1), FlatFileFieldType.String, 102+offset, 1, true);
				ANNO_VOCE[i] = new FlatFileField("ANNO_VOCE_"+(i+1), FlatFileFieldType.String, 103+offset, 4, true);
				FAMIGLIA_VOCE[i] = new FlatFileField("FAMIGLIA_VOCE_"+(i+1), FlatFileFieldType.String, 107+offset, 3, true);
				DESCRIZIONE_VOCE[i] = new FlatFileField("DESCRIZIONE_VOCE_"+(i+1), FlatFileFieldType.String, 110+offset, 42, true);
				QUANTITA[i] = new FlatFileField("QUANTITA_"+(i+1), FlatFileFieldType.String, 152+offset, 4, true);
				IMPO_UNIT[i] = new FlatFileField("IMPO_UNIT_"+(i+1), FlatFileFieldType.String, 156+offset, 10, true);
				IMPO_VOCE[i] = new FlatFileField("IMPO_VOCE_"+(i+1), FlatFileFieldType.String, 166+offset, 10, true);
				IVA_VOCE[i] = new FlatFileField("IVA_VOCE_"+(i+1), FlatFileFieldType.String, 176+offset, 5, true);
			}
			
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(ID_CONTRIBUENTE);

			fileHelper.addField(ANNO_DOCUMENTO );
			fileHelper.addField(IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO );
//			fileHelper.addField(NUMERO_DOCUMENTO );
//			fileHelper.addField(TIPOLOGIA_SERVIZIO );
//			fileHelper.addField(FATTURAZIONE_ELETTRONICA );
			
			for(int i=0;i<5;i++) {
				fileHelper.addField(VOCE[i]);
				fileHelper.addField(TIPO_VOCE[i]);
				fileHelper.addField(ANNO_VOCE[i]);
				fileHelper.addField(FAMIGLIA_VOCE[i]);
				fileHelper.addField(DESCRIZIONE_VOCE[i]);
				fileHelper.addField(QUANTITA[i]);
				fileHelper.addField(IMPO_UNIT[i]);
				fileHelper.addField(IMPO_VOCE[i]);
				fileHelper.addField(IVA_VOCE[i]);
			}
			addEmptyFields(fileHelper);
		}

		public synchronized static String format(List<Tributo> tributo) {
			Documento doc = tributo.get(0).documento;
			
			// Nota: questi campi devono essere identici al tracciato rec_E
			CODICE_ENTE.setValue(doc.codiceEnte);
			IMPOSTA_SERVIZIO.setValue("  ");
			ID_CONTRIBUENTE.setValue("");
			
			ANNO_DOCUMENTO.setValue(doc.anno);
//			NUMERO_DOCUMENTO.setValue(doc.numero);
			// scarto zeri iniziali
			IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO.setValue(
					doc.importoTotale.substring(doc.importoTotale.length()-IMPORTO_BOLLETTINO_TOTALE_DOCUMENTO.getLength()));
//			TIPOLOGIA_SERVIZIO.setValue(doc.tipologiaServizio);
//			FATTURAZIONE_ELETTRONICA.setValue(doc.fatturazioneElettronica?"Y":"N");

      for (int i = 0; i < 5; i++) {
        Tributo tr = (i < tributo.size()) ? tributo.get(i) : null;

        VOCE[i].setValue(tr != null ? tr.codiceTributo : "");
        TIPO_VOCE[i].setValue(" ");
        ANNO_VOCE[i].setValue(tr != null ? tr.anno : "");
        FAMIGLIA_VOCE[i].setValue(" ");
        DESCRIZIONE_VOCE[i].setValue(tr != null ? tr.note : "");
        QUANTITA[i].setValue("0000");
        IMPO_UNIT[i].setValue("0000000000");
        IMPO_VOCE[i].setValue(tr != null ? tr.importo.substring(5) : "");
        IVA_VOCE[i].setValue("00000");
      }
			return fileHelper.toString();
		}
		
		public synchronized static String serializeXml(List<Tributo> tributo) {
			format(tributo);
			String s = "<RecF>\n";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecF>\n";
			return s;
		}
	}
	
	//inizio SB 18042019
	/** Record U*/
	static class RecU {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"U");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField ID_CONTRIBUENTE = new FlatFileField("ID_CONTRIBUENTE", FlatFileFieldType.String, 10, 10, true);
		static FlatFileField NUMERO_CCP = new FlatFileField("NUMERO_CCP", FlatFileFieldType.String, 20, 8, true);
		static FlatFileField DESCRIZIONE_CCP = new FlatFileField("DESCRIZIONE_CCP", FlatFileFieldType.String, 28, 40, true);
		static FlatFileField CODE_LINE_1 = new FlatFileField("CODE_LINE_1", FlatFileFieldType.String, 68, 20, true);
		static FlatFileField CODE_LINE_2 = new FlatFileField("CODE_LINE_2", FlatFileFieldType.String, 88, 12, true);
		static FlatFileField CODE_LINE_3 = new FlatFileField("CODE_LINE_3", FlatFileFieldType.String, 100, 9, true);
		static FlatFileField CODE_LINE_4 = new FlatFileField("CODE_LINE_4", FlatFileFieldType.String, 109, 4, true);
		static FlatFileField STRING_LETTORE= new FlatFileField("STRING_LETTORE", FlatFileFieldType.String, 113, 27, true);
		static FlatFileField DATA_SCADENZA = new FlatFileField("DATA_SCADENZA", FlatFileFieldType.String, 140, 8, true);
		static FlatFileField NUMERO_RATA = new FlatFileField("NUMERO_RATA", FlatFileFieldType.Numeric, 148, 2, true); 
		static FlatFileField TIPO_BOLLETTINO = new FlatFileField("TIPO_BOLLETTINO", FlatFileFieldType.Numeric, 150, 1, true); 
		static FlatFileField CAUSALE = new FlatFileField("CAUSALE", FlatFileFieldType.String, 151, 60, true);
		static FlatFileField NUMERO_AVVISO_PAGOPA = new FlatFileField("NUMERO_AVVISO_PAGOPA", FlatFileFieldType.String, 211, 20, true);
		static FlatFileField QRCODE = new FlatFileField("QRCODE", FlatFileFieldType.String, 231, 60, true);
		static FlatFileField BARCODE_GS1_128 = new FlatFileField("BARCODE_GS1_128", FlatFileFieldType.String, 291, 60, true);
		static FlatFileField CBILL= new FlatFileField("CBILL", FlatFileFieldType.String, 351, 10, true);
		static FlatFileField ENTE_CREDITORE = new FlatFileField("ENTE_CREDITORE", FlatFileFieldType.String, 361, 40, true);
		static FlatFileField IUV_RATA = new FlatFileField("IUV_RATA", FlatFileFieldType.String, 401, 20, true);
	    static FlatFileField NUMERO_CCP_12 = new FlatFileField("NUMERO_CCP_12", FlatFileFieldType.String, 421, 12, true);
		static FlatFileField CODE_LINE_3_12 = new FlatFileField("CODE_LINE_3_12", FlatFileFieldType.String, 433, 13, true);
		static FlatFileField AUTORIZZAZIONE_CCP = new FlatFileField("AUTORIZZAZIONE_CCP", FlatFileFieldType.String, 446, 50, true);
		static FlatFileField NUMERO_RATA_ESTESO = new FlatFileField("NUMERO_RATA_ESTESO", FlatFileFieldType.String, 496, 3, true);	
		
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(ID_CONTRIBUENTE);
			fileHelper.addField(NUMERO_CCP);
			fileHelper.addField(DESCRIZIONE_CCP);
			fileHelper.addField(CODE_LINE_1);
			fileHelper.addField(CODE_LINE_2);
			fileHelper.addField(CODE_LINE_3);
			fileHelper.addField(CODE_LINE_4);
			fileHelper.addField(STRING_LETTORE);
			fileHelper.addField(DATA_SCADENZA);
			fileHelper.addField(NUMERO_RATA);
			fileHelper.addField(TIPO_BOLLETTINO);
			fileHelper.addField(CAUSALE);
			fileHelper.addField(NUMERO_AVVISO_PAGOPA);
			fileHelper.addField(QRCODE);
			fileHelper.addField(BARCODE_GS1_128);
			fileHelper.addField(CBILL);
			fileHelper.addField(ENTE_CREDITORE);
			fileHelper.addField(IUV_RATA );
			fileHelper.addField(NUMERO_CCP_12);
			fileHelper.addField(CODE_LINE_3_12 );
			fileHelper.addField(AUTORIZZAZIONE_CCP);
			fileHelper.addField(NUMERO_RATA_ESTESO);
			addEmptyFields(fileHelper);
		}

		public synchronized static String format(AvvisoRata ar) {
			
			CODICE_ENTE.setValue(ar.documento.codiceEnte);
			//20220406 - inizio
			//IMPOSTA_SERVIZIO.setValue(ar.documento.debitore.flusso.impServ);   //"  "
			IMPOSTA_SERVIZIO.setValue(ar.documento.codiceImpostaServizio);
			//20220406 - fine
			ID_CONTRIBUENTE.setValue("");  
			String doc="";
			
			if(ar.documento.iban!=null && !ar.documento.iban.equals(""))
				doc = ar.documento.iban;
			else
				doc = ar.documento.debitore.flusso.ccp;
			
			String ccp="";
			int len=0;
			if(doc != null){
				len =  doc.length();			
				if (len > 12) {
					ccp=doc.substring(len-12);
					//NUMERO_CCP.setValue(doc.substring(len-12)); // prende gli ultimi12 bytes dell'IBAN
				} else {
					//NUMERO_CCP.setValue("");
					ccp=doc;
				}
			}
				
			if(len>8){
				NUMERO_CCP.setValue("");
			}else{
				NUMERO_CCP.setValue(ccp);
			}
				
			
			DESCRIZIONE_CCP.setValue(ar.documento.descrizioneEnte);
			
			// CODE LINE 1,2,3,4
			CODE_LINE_1.setValue(String.format(">%s<",ar.numeroAvviso));
			StringBuilder importoRata = new StringBuilder( ar.importo);
			importoRata.insert(importoRata.length()-2, "+");
			importoRata.append(">");
			CODE_LINE_2.setValue(importoRata.substring(importoRata.length()-CODE_LINE_2.getLength()));
			
			
			if(len>8){
				CODE_LINE_3.setValue("");
			}else{
				CODE_LINE_3.setValue(ccp);
			}
		
			//CODE_LINE_3.setValue(ar.documento.debitore.flusso.ccp);
			CODE_LINE_4.setValue(ar.documento.debitore.flusso.type+">");
			
			STRING_LETTORE.setValue(ar.documento.debitore.idDominio);
			DATA_SCADENZA.setValue(ar.dataScadenza);
			NUMERO_RATA.setValue(ar.progressivoBollettino);  //TODO: testare
			TIPO_BOLLETTINO.setValue("3");  //3 = PagoPA ar.documento.debitore.flusso.type
  			CAUSALE.setValue(ar.documento.oggettoPagamento);
			
			NUMERO_AVVISO_PAGOPA.setValue(ar.documento.numAvvisoPagoPa);
			QRCODE.setValue(ar.codiceQRcode);
			BARCODE_GS1_128.setValue(ar.codiceBarcode);
			CBILL.setValue(ar.documento.debitore.flusso.cbill);
			ENTE_CREDITORE.setValue(ar.documento.descrizioneServizio);  //TODO: controllare
			IUV_RATA.setValue(ar.codiceIUV);
			
			NUMERO_CCP_12.setValue(ccp);  //TODO:controllare    ar.documento.iban
			CODE_LINE_3_12.setValue(ccp);     //ar.documento.debitore.flusso.ccp
	
			
			
			AUTORIZZAZIONE_CCP.setValue(ar.documento.debitore.flusso.codiceAutorizzazione);   	//ar.documento.numAvvisoPagoPa+ar.codiceIUV

			if(ar.progressivoBollettino == 99){
				NUMERO_RATA_ESTESO.setValue("9"+"99");  
			}else{
				NUMERO_RATA_ESTESO.setValue("0"+String.format("%02d",ar.progressivoBollettino));  
			}
			
			
		
			return fileHelper.toString();
		}
		
		public synchronized static String serializeXml(AvvisoRata ar) {
			format(ar);
			String s = "<RecU>\n";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecU>\n";
			return s;
		}
	}
	//fine SB 18042019
	
	
	/** Record V */
	static class RecV {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"V");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField ID_CONTRIBUENTE = new FlatFileField("ID_CONTRIBUENTE", FlatFileFieldType.String, 10, 10, true);
		static FlatFileField CODE_LINE_1 = new FlatFileField("CODE_LINE_1", FlatFileFieldType.String, 68, 20, true);
		/** importo rata, formato 000012+34 */
		static FlatFileField CODE_LINE_2 = new FlatFileField("CODE_LINE_2", FlatFileFieldType.String, 88, 12, true);
		static FlatFileField CODE_LINE_4 = new FlatFileField("CODE_LINE_4", FlatFileFieldType.String, 109, 4, true);

		static FlatFileField DATA_SCADENZA = new FlatFileField("DATA_SCADENZA", FlatFileFieldType.String, 140, 8, true);
		/** ridefinisce NUMERO_RATA*/
		static FlatFileField PROGRESSIVO_BOLLETTINO = new FlatFileField("PROGRESSIVO_BOLLETTINO", FlatFileFieldType.String, 148, 2, true);
		
		static FlatFileField DESCRIZIONE_ESTESA_CCP = new FlatFileField("DESCRIZIONE_ESTESA_CCP", FlatFileFieldType.String, 151, 60, true);
		
		/** ridefinisce campi 16...26 (prima parte) */
		static FlatFileField QRCODE = new FlatFileField("QRCODE", FlatFileFieldType.String, 211, 100, true);
		/** ridefinisce campi 16...26 (seconda parte) */
		static FlatFileField BARCODE_GS1_128 = new FlatFileField("BARCODE_GS1_128", FlatFileFieldType.String, 311, 100, true);
		static FlatFileField NUMERO_CCP = new FlatFileField("NUMERO_CCP", FlatFileFieldType.String, 415, 12, true);
		static FlatFileField CODE_LINE_3 = new FlatFileField("CODE_LINE_3", FlatFileFieldType.String, 427, 13, true);
		
		/** ridefinisce la prima parte di AUTORIZZAZIONE CCP */
		static FlatFileField NUMERO_AVVISO_PAGOPA_RATA = new FlatFileField("NUMERO_AVVISO_PAGOPA_RATA", FlatFileFieldType.String, 440, 20, true);
		/** ridefinisce la seconda parte di AUTORIZZAZIONE CCP, lascia terza parte vuota */
		static FlatFileField IUV_RATA = new FlatFileField("IUV_RATA", FlatFileFieldType.String, 460, 20, true);

		static FlatFileField _NUMERO_RATA_ESTESO = new FlatFileField("NUMERO_RATA_ESTESO", FlatFileFieldType.Numeric, 490, 3, true);
		
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(ID_CONTRIBUENTE);

			fileHelper.addField(CODE_LINE_1);
			fileHelper.addField(CODE_LINE_2);
			fileHelper.addField(CODE_LINE_4);
			fileHelper.addField(DATA_SCADENZA);
			fileHelper.addField(PROGRESSIVO_BOLLETTINO );
			fileHelper.addField(DESCRIZIONE_ESTESA_CCP);
			fileHelper.addField(QRCODE );
			fileHelper.addField(BARCODE_GS1_128 );
			fileHelper.addField(NUMERO_CCP);
			fileHelper.addField(CODE_LINE_3);
			fileHelper.addField(NUMERO_AVVISO_PAGOPA_RATA );
			fileHelper.addField(IUV_RATA );

			fileHelper.addField(_NUMERO_RATA_ESTESO);
			addEmptyFields(fileHelper);
		}

		public synchronized static String format(AvvisoRata ar) {
			CODICE_ENTE.setValue(ar.documento.codiceEnte);
			IMPOSTA_SERVIZIO.setValue("  ");
			ID_CONTRIBUENTE.setValue("");
//			NUMERO_CCP.setValue(ar.documento.debitore.flusso.ccp);qui!!!!!!!!!!!!!!!!
			String doc = ar.documento.iban.trim();
			int len =  doc.length();
			if (len > 12) {
				NUMERO_CCP.setValue(doc.substring(len-12)); // prende gli ultimi12 bytes dell'IBAN
			} else {
				NUMERO_CCP.setValue("");
			}
			DESCRIZIONE_ESTESA_CCP.setValue(ar.documento.debitore.flusso.ccpIntest);
			
			// CODE LINE 1,2,3,4
			CODE_LINE_1.setValue(String.format(">%s<",ar.numeroAvviso));
			StringBuilder importoRata = new StringBuilder( ar.importo);
			importoRata.insert(importoRata.length()-2, "+");
			importoRata.append(">");
			CODE_LINE_2.setValue(importoRata.substring(importoRata.length()-CODE_LINE_2.getLength()));
			CODE_LINE_3.setValue(ar.documento.debitore.flusso.ccp+"<");
			CODE_LINE_4.setValue(ar.documento.debitore.flusso.type+">");
			
			DATA_SCADENZA.setValue(ar.dataScadenza);
			PROGRESSIVO_BOLLETTINO.setValue(ar.progressivoBollettino);
			QRCODE.setValue(ar.codiceQRcode);
			BARCODE_GS1_128.setValue(ar.codiceBarcode);
			NUMERO_AVVISO_PAGOPA_RATA.setValue(ar.numeroAvviso);
			IUV_RATA.setValue(ar.codiceIUV);
			
			return fileHelper.toString();
		}
		
		public synchronized static String serializeXml(AvvisoRata ar) {
			format(ar);
			String s = "<RecV>\n";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecV>\n";
			return s;
		}
	}

	
	/** Record Z */
	static class RecZ {
		static FlatFileField TIPO_RECORD = new FlatFileField("TIPO_RECORD", FlatFileFieldType.Constant, 1, 1, true,"Z");
		static FlatFileField CODICE_ENTE = new FlatFileField("CODICE_ENTE", FlatFileFieldType.String, 2, 6, true);
		static FlatFileField IMPOSTA_SERVIZIO= new FlatFileField("IMPOSTA_SERVIZIO", FlatFileFieldType.String, 8, 2, true);
		static FlatFileField DESCRIZIONE_ENTE= new FlatFileField("DESCRIZIONE_ENTE", FlatFileFieldType.String, 10, 50, true);

		static FlatFileField NUMERO_RECORD = new FlatFileField("NUMERO_RECORD", FlatFileFieldType.Numeric, 110, 9, true);
		static FlatFileField IMPORTO = new FlatFileField("IMPORTO", FlatFileFieldType.Numeric, 119, 15, true);
		static FlatFileField DATA_ELABORAZIONE = new FlatFileField("DATA_ELABORAZIONE", FlatFileFieldType.String, 134, 8, true);
		
		static FlatFileField AUTORIZZAZIONE_CCP = new FlatFileField("AUTORIZZAZIONE_CCP", FlatFileFieldType.String, 144, 50, true);
		static FlatFileField PROGRAMMA = new FlatFileField("PROGRAMMA", FlatFileFieldType.String, 194, 8, true);
		
		static FlatFileField INDIRIZZO_ENTE = new FlatFileField("INDIRIZZO_ENTE", FlatFileFieldType.String, 325, 60, true);
		static FlatFileField TELEFONO_ENTE = new FlatFileField("TELEFONO_ENTE", FlatFileFieldType.Numeric, 385, 15, true);
		static FlatFileField FAX_ENTE = new FlatFileField("FAX_ENTE", FlatFileFieldType.Numeric, 400, 15, true);
		static FlatFileField MAIL_ENTE= new FlatFileField("MAIL_ENTE", FlatFileFieldType.String, 415, 45, true);
		static FlatFileHelper fileHelper = new FlatFileHelper();
		
		static {
			// NB: sono ordinati
			fileHelper.addField(TIPO_RECORD);
			fileHelper.addField(CODICE_ENTE);
			fileHelper.addField(IMPOSTA_SERVIZIO);
			fileHelper.addField(DESCRIZIONE_ENTE);
			fileHelper.addField(NUMERO_RECORD);

			fileHelper.addField(IMPORTO);
			fileHelper.addField(DATA_ELABORAZIONE);
			fileHelper.addField(AUTORIZZAZIONE_CCP);
			fileHelper.addField(PROGRAMMA);
			fileHelper.addField(INDIRIZZO_ENTE);

			fileHelper.addField(TELEFONO_ENTE);
			fileHelper.addField(FAX_ENTE);
			fileHelper.addField(MAIL_ENTE);

			addEmptyFields(fileHelper);
		}

		public synchronized static String format(File512 f) {
			PROGRAMMA.setValue("PY514SPA");
//			CODICE_ENTE.setValue();
			DESCRIZIONE_ENTE.setValue((f.descrizioneEnte!=null && f.descrizioneEnte.trim().length()>40)?f.descrizioneEnte.trim().substring(40):"");	//SPAG - 87 
			NUMERO_RECORD.setValue(f.numRecord+1);	// ci considero anche il presente record di chiusura
			DATA_ELABORAZIONE.setValue(f.dataOra.substring(0, 8));	// solo la data yyyyMMdd!
			AUTORIZZAZIONE_CCP.setValue(f.codiceAutorizzazione);
			return fileHelper.toString();
		}
		
		public synchronized static String serializeXml(File512 f) {
			format(f);
			String s = "<RecZ>";
			s+=Tracciato512.serializeXml(fileHelper);
			s += "</RecZ>";
			return s;
		}
	}
	/** Le posizioni non utilizzate del record di 540 caratteri, sono tutte a blank con campi "vuoti"
	 * @param fileHelper con almeno un campo. */
	static void addEmptyFields(FlatFileHelper fileHelper)	{
		int recLen = 540;
		// i campi sono già  ordinati
		ArrayList<FlatFileField> fields = fileHelper.getFields();
		
		// prima posizione
		int firstStartAt = fields.get(0).getStart();
		if(firstStartAt>1)
			fields.add(0, new FlatFileField("empty", FlatFileFieldType.Constant, 1, firstStartAt-1, true));

		// ultima posizione
		FlatFileField lastField = fields.get(fields.size()-1);
		int lastEndAt = lastField.getStart()+lastField.getLength()-1;
		if(lastEndAt<recLen)
			fields.add( new FlatFileField("empty", FlatFileFieldType.Constant, lastEndAt+1, recLen-lastEndAt, true));

		// posizioni intermdie
		for(int i=1;i<fields.size();i++)
		{
			FlatFileField currField = fields.get(i);
			FlatFileField precField = fields.get(i-1);
			int precEndsAt = precField.getStart()+precField.getLength() -1;
			int spazi = currField.getStart()-(precEndsAt+1);
			if(spazi>0)
				fields.add(i, new FlatFileField("empty", FlatFileFieldType.Constant, precEndsAt+1, spazi, true));
		}
	}
	
	/** output di debug, "xml grossolano" leggibile da eclipse*/
  public static String serializeXml(FlatFileHelper fileHelper) {
    String s = "";
    for (FlatFileField field : fileHelper.getFields()) {
      if (field.getName().equals("empty"))
        continue;
      String value = fieldToString(field);
      s += String.format("<%s value=\"%s\" from=\"%d\" to=\"%d\" len=\"%d\"/>\n", field.getName(),
          value, field.getStart(), field.getStart() + field.getLength() - 1, field.getLength());
    }
    return s;
  }
	
	/** Conversione di un field in stringa... da spostare in {@link FlatFileHelper} in modo da avere
	 *  una unica funzione utilizzabile per la corversione in record a lunghezza fissa e XML */
  static String fieldToString(FlatFileField field) {

    StringBuilder retval = new StringBuilder();

    FlatFileFieldType type = field.getType();
    int length = field.getLength();
    Object value = String.format("%s", field.getValue()).replaceAll("null", "");

    String result = value.toString().trim();

    if (type == FlatFileFieldType.Constant || type == FlatFileFieldType.String) {
      while (result.length() < length) {
        result += " ";
      }
    }

    if (type == FlatFileFieldType.Numeric) {
      while (result.length() < length) {
        result = "0" + result;
      }
    }
    retval.append(result.substring(0, length));

    return retval.toString();
  }


	
	/** La lista di documeti si intende ordinata... quindi posso confrontare con la riga precedente 
	 * per vedere i punti di rottura/raggruppamento.
	 * @param listaAR ci deve essere almento un {@link AvvisoRata} */
	public static ArrayList<String> serialize(File512 curFile) {
		
    if (curFile.listaDocumenti == null || curFile.listaDocumenti.size() == 0)
      throw new IllegalArgumentException("lista documenti=" + curFile.listaDocumenti);

    Flusso curFlusso = null;
    Debitore curDeb = null;

    for (Documento curDoc : curFile.listaDocumenti) {
    	if (curFlusso == null) {
        // prima riga
        curDeb = curDoc.debitore;
        curFlusso = curDeb.flusso;
        
        curFile.addRecordTxt(Rec0.format(curFile));
        // primo debitore
        curFile.addRecordTxt(RecA.format(curDeb));
        curFile.addRecordTxt(RecH.format(curDeb));
// Inizio modifiche - 29/11/2017 - Modifiche chieste da Quaresima/Marcucci
//                                 Ogni documento deve avere il rec A + H (sempre!
//    	} else if (curDoc.debitore != curDeb) {
//    		// nuovo debitore
    	} else {
    		curDeb = curDoc.debitore;
    		
    		if (curDeb.flusso != curFlusso) {
    			throw new RuntimeException("Ci sono pendenze provenienti da flussi diversi, idFlusso:"
    					+ curFlusso.idFlusso + ", " + curDeb.flusso.idFlusso);
    		}
    		curFile.addRecordTxt(RecA.format(curDeb));
    		curFile.addRecordTxt(RecH.format(curDeb));
    	}
// Fine   modifiche - 29/11/2017 - Modifiche chieste da Quaresima/Marcucci
      // nuovo documento
      curFile.addRecordTxt(RecE.format(curDoc));
      ArrayList<Tributo> listaTribRecordF = new ArrayList<Tributo>();
      for (int indiceTributo = 0; indiceTributo < curDoc.listaTributi.size(); indiceTributo++) {
        listaTribRecordF.add(curDoc.listaTributi.get(indiceTributo));
        if (listaTribRecordF.size() == 5) {
          // nuovi tributi, blocco da 5 sullo stesso record
          curFile.addRecordTxt(RecF.format(listaTribRecordF));
          listaTribRecordF.clear();
        }
      }
      if (listaTribRecordF.size() > 0) {
        // residuo 1..4 tributi
        curFile.addRecordTxt(RecF.format(listaTribRecordF));
      }
      
      String dataScadenza="";
      int progressivoBollettino=0;
      if(curDoc.listaAvvisi.size()==1){
    	  for (AvvisoRata curAvv : curDoc.listaAvvisi) {
    	        // nuova rata
    		  	progressivoBollettino=curAvv.progressivoBollettino;
    		  	dataScadenza=curAvv.dataScadenza;
    		    curAvv.progressivoBollettino=99;
    		    curAvv.dataScadenza="00000000";
    	    	curFile.addRecordTxt(RecU.format(curAvv)); //SB 18042019
    	      }
    	  for (AvvisoRata curAvv : curDoc.listaAvvisi) {
    	        // nuova rata
    		  	curAvv.dataScadenza=dataScadenza;
    		  	curAvv.progressivoBollettino= progressivoBollettino;
    	    	curFile.addRecordTxt(RecU.format(curAvv)); //SB 18042019
    	        //curFile.addRecordTxt(RecV.format(curAvv));
    	      }
      }else{
    	  //inizio LP PG22XX05 - Bug rata unica assente
    	  AvvisoRata unica = new AvvisoRata();
    	  unica.codiceBarcode = curDoc.codiceBarcode;
    	  unica.codiceIUV = curDoc.codiceIUV;
    	  unica.codiceQRcode = curDoc.codiceQRcode;
    	  unica.dataScadenza = "00000000";
    	  unica.documento = curDoc;
    	  unica.importo = curDoc.importoTotale;
    	  unica.numeroAvviso = curDoc.numAvvisoPagoPa;
    	  unica.progressivoBollettino = 99;
    	  curFile.addRecordTxt(RecU.format(unica));
		  //fine LP PG22XX05 - Bug rata unica assente
    	  for (AvvisoRata curAvv : curDoc.listaAvvisi) {
    	        // nuova rata
    	    	curFile.addRecordTxt(RecU.format(curAvv)); //SB 18042019
    	        //curFile.addRecordTxt(RecV.format(curAvv));
    	      }
      }
      
      
      
    }
    curFile.addRecordTxt(RecZ.format(curFile));
    return curFile.getFileContent();
  }

	public synchronized static String serializeXmlFlusso(Flusso f) {
		String s = "";
		s += String.format("<idFlusso value=\"%s\" />",f.idFlusso);
		s += "";
		return s;
	}

  static String newLine = "\n";
  
	/**
	 * Formattazione in una "specie di xml" grossolano al solo scopo di debug dei flussi ritornati dal DB
	 */
	public static String serializeXml(ArrayList<Flusso> listaFlussi) {
		String fileContent = "<flussi>";

    for (Flusso f : listaFlussi) {
      fileContent += "<flusso>" + newLine;
      fileContent += serializeXmlFlusso(f) + newLine;
      fileContent += String.format("<debitori len=\"%d\">", f.listaDebitori.size()) + newLine;
      for (Debitore deb : f.listaDebitori) {
        fileContent += "<debitore>" + newLine;

        fileContent += RecA.serializeXml(deb) + newLine;
        fileContent += RecH.serializeXml(deb) + newLine;

        fileContent += String.format("<documenti len=\"%d\">", deb.listaDocumenti.size()) + newLine;
        for (Documento doc : deb.listaDocumenti) {
          fileContent += "<documento>" + newLine;
          fileContent += RecE.serializeXml(doc) + newLine;

          fileContent += String.format("<tributi len=\"%d\">", doc.listaTributi.size()) + newLine;
          List<Tributo> lista = new ArrayList<Tributo>(doc.listaTributi);
          while (lista.size() > 0) {
            fileContent += "<tributo_x5>" + newLine;
            int len = Math.min(5, lista.size());
            fileContent += RecF.serializeXml(lista.subList(0, len));
            lista = lista.subList(len, lista.size());
            fileContent += "</tributo_x5>" + newLine;
          }
          fileContent += "</tributi>" + newLine;
          
          fileContent += String.format("<avvisi len=\"%d\">", doc.listaAvvisi.size()) + newLine;
          for (AvvisoRata ar : doc.listaAvvisi) {
        	fileContent += "<avviso>" + newLine;
            fileContent += RecU.serializeXml(ar);
            fileContent += "</avviso>" + newLine;
//            fileContent += "<avviso>" + newLine;
//            fileContent += RecV.serializeXml(ar);
//            fileContent += "</avviso>" + newLine;
          }
          fileContent += "</avvisi>" + newLine;
          fileContent += "</documento>" + newLine;
        }
        fileContent += "</documenti>" + newLine;
        fileContent += "</debitore>" + newLine;
      }
      fileContent += "</debitori>" + newLine;
//			fileContent += RecZ.serializeXml(f)+newLine;
      fileContent += "</flusso>" + newLine;
    }

		return fileContent+"</flussi>";
  }
	
}
