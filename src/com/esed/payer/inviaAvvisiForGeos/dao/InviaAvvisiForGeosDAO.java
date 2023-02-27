package com.esed.payer.inviaAvvisiForGeos.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import com.esed.payer.inviaAvvisiForGeos.config.InviaAvvisiForGeosContext;
import com.seda.data.helper.Helper;
import com.seda.data.helper.HelperException;
import com.seda.payer.commons.inviaAvvisiForGeos.AvvisoRata;
import com.seda.payer.commons.inviaAvvisiForGeos.Debitore;
import com.seda.payer.commons.inviaAvvisiForGeos.Documento;
import com.seda.payer.commons.inviaAvvisiForGeos.Flusso;
import com.seda.payer.commons.inviaAvvisiForGeos.Tributo;

/**
 * Query al DB tramite stored procedure... ritorna oggetti del model. PG170100
 *
 * @author luciano.dercoli@gmail.com
 */
public class InviaAvvisiForGeosDAO {

	InviaAvvisiForGeosContext context;
	Connection connection;
	String schema;

	protected CallableStatement stat = null;

	public InviaAvvisiForGeosDAO(InviaAvvisiForGeosContext cont, Connection connection, String schema) {
		this.context = cont;
		this.connection = connection;
		this.schema = schema;
	}

	/**
	 * Lista di avvisi, strutturata ad albero come da query DB:
	 * "flusso/debitore/documento/avvisiRate"
	 * 
	 * <p> Aggiunto oggetto "tributo", che viene quindi "moltiplicato in join". Tuttavia
	 * il tributo è relativo al documento... quindi elimino i duplicati.
	 */
  public synchronized ArrayList<Flusso> listAnagrafiche512(String codiceUtente) throws Exception {
    ArrayList<Flusso> listaFlussi = new ArrayList<Flusso>();
    try {
      if (stat == null) {
        stat = Helper.prepareCall(connection, schema, "PY512SP_AVVI");
      }
      stat.setString(1, codiceUtente);
      stat.setString(2, "");
      stat.setString(3, "");
      stat.setString(4, "");
      stat.setString(5, "");
      stat.registerOutParameter(6, Types.INTEGER); // CODICE ERR
      stat.registerOutParameter(7, Types.VARCHAR); // MESS ERR
      stat.execute();

      ResultSet resultSet = stat.getResultSet();

      if (resultSet != null && resultSet.next()) {
        // primo avviso con tutte le colonne Debitore/Docum/Avviso

        Flusso curFlusso = extractFlusso(resultSet);
        Debitore curDeb = extractDebitore(resultSet);
        Documento curDoc = extractDoc(resultSet);
        AvvisoRata curAvv = extractAvv(resultSet);
        Tributo currTrib = extractTrib(resultSet);

        curDoc.putTributo(currTrib);
        curDoc.addAvviso(curAvv);
        curDeb.addDocumento(curDoc);
        curFlusso.addDebitore(curDeb);
        listaFlussi.add(curFlusso);

        // scansione dei flussi da DB in un unico Resultset... ci sono tutte le colonne
        while (resultSet.next()) {
          // riga del resultset...
          currTrib = extractTrib(resultSet);
          if (!(sameFlusso(resultSet, curFlusso) && sameDebitore(resultSet, curDeb)
              && sameDoc(resultSet, curDoc) && sameAvv(resultSet, curAvv))) {
            curAvv = extractAvv(resultSet);

            if (!(sameFlusso(resultSet, curFlusso) && sameDebitore(resultSet, curDeb) && sameDoc(
                resultSet, curDoc))) {
              // nuovo documento...
              curDoc = extractDoc(resultSet);
              if (!(sameFlusso(resultSet, curFlusso) && sameDebitore(resultSet, curDeb))) {
                // nuovo debitore
                curDeb = extractDebitore(resultSet);

                if (!sameFlusso(resultSet, curFlusso)) {
                  // nuovo flusso
                  curFlusso = extractFlusso(resultSet);
                  listaFlussi.add(curFlusso);
                }
                curFlusso.addDebitore(curDeb);
              }
              curDeb.addDocumento(curDoc);
            }
            curDoc.addAvviso(curAvv);
          }
          curDoc.putTributo(currTrib);
        }
      }
      resultSet.close();

    } catch (SQLException e) {
      e.printStackTrace();	
      throw new Exception(e);
    } catch (IllegalArgumentException e) {
      throw new Exception(e);
    } catch (HelperException e) {
      throw new Exception(e);
    } finally {
    }

    return listaFlussi;
  }

	private boolean sameAvv(ResultSet resultSet, AvvisoRata avv) throws SQLException {
		boolean same = avv.progressivoBollettino == resultSet.getInt("AR_PROGRESSIVO_BOLLETTINO")
			&& avv.numeroAvviso.equals(resultSet.getString("AR_NUMERO_AVVISO"));
		return same;
	}

	private boolean sameDoc(ResultSet resultSet, Documento doc) throws SQLException {
		boolean same = doc.anno == resultSet.getInt("DOC_ANNO")
				&& doc.numero.equals( resultSet.getString("DOC_NUMERO"))
				&& doc.codiceImpostaServizio.equals( resultSet.getString("IMP_SERV"));
		return same;
	}

	private boolean sameDebitore(ResultSet resultSet, Debitore deb) throws SQLException {
		boolean same = deb.codiceFiscale.equals(resultSet.getString("DEB_CODICE_FISCALE"));
		return same;
	}

	private boolean sameFlusso(ResultSet resultSet, Flusso flu) throws SQLException {
		boolean same = flu.idFlusso.equals(resultSet.getString("FLU_ID_FLUSSO"));

		return same;
	}

	private Flusso extractFlusso(ResultSet resultSet) throws SQLException {
		Flusso flu = new Flusso();
		flu.cutecute = resultSet.getString("FLU_CUTECUTE");
		flu.idFlusso = resultSet.getString("FLU_ID_FLUSSO");
		flu.societa =  resultSet.getString("FLU_SOCIETA");
		flu.ccp =  resultSet.getString("FLU_CCP");
		//flu.ccpIntest =  resultSet.getString("FLU_CCP_INTEST");
		String intestazioneCCP = resultSet.getString("FLU_CCP_INTEST");
		if(intestazioneCCP!=null && intestazioneCCP.contains("/")){
			intestazioneCCP = intestazioneCCP.split("/")[0].trim();
		}
		flu.ccpIntest = intestazioneCCP; 
		flu.type =  resultSet.getString("FLU_TYPE");
		flu.impServ = resultSet.getString("IMP_SERV");
		flu.progressivoBollettino = resultSet.getInt("AR_PROGRESSIVO_BOLLETTINO");
		return flu;
	}

	private Debitore extractDebitore(ResultSet resultSet) throws SQLException {
		Debitore deb = new Debitore();
		deb.codiceFiscale = resultSet.getString("DEB_CODICE_FISCALE");
		deb.nomeCognRagSoc = resultSet.getString("DEB_NOME_COGN_RAG_SOC");
		deb.indirizzo = resultSet.getString("DEB_INDIRIZZO");
		deb.comune = resultSet.getString("DEB_COMUNE");
		deb.provincia = resultSet.getString("DEB_PROVINCIA");
		deb.cap = resultSet.getString("DEB_CAP");
		deb.mail = resultSet.getString("DEB_MAIL");
		deb.pec = resultSet.getString("DEB_PEC");
		deb.codiceFiscaleAlternativo = resultSet.getString("DEB_CODICE_FISCALE_ALTERNATIVO");
		deb.idDominio = resultSet.getString("DOC_CFISCALE_ENTE");

		return deb;
	}

	private Documento extractDoc(ResultSet resultSet) throws SQLException {
		Documento doc = new Documento();
		doc.codiceEnte = resultSet.getString("DOC_CODICE_ENTE");
		doc.chiaveEnte = resultSet.getString("DOC_CHIAVE_ENTE");
		//doc.descrizioneEnte = resultSet.getString("DOC_DESCRIZIONE_ENTE");
		String descEnte = resultSet.getString("DOC_DESCRIZIONE_ENTE");
		if(descEnte!=null && descEnte.contains("/"))
			descEnte = descEnte.split("/")[0].trim();
		doc.descrizioneEnte = descEnte;
		doc.anno = resultSet.getInt("DOC_ANNO");
		doc.numero = resultSet.getString("DOC_NUMERO");
		doc.tipologiaServizio = resultSet.getString("DOC_TIPOLOGIA_SERVIZIO");
		doc.descrizioneServizio = resultSet.getString("DOC_DESCRIZIONE_SERVIZIO");
		doc.importoTotale = resultSet.getString("DOC_IMPORTO_TOTALE");
		doc.fatturazioneElettronica = "S".equalsIgnoreCase(resultSet.getString("DOC_F_FATT_ELETTR"));
		doc.iban = resultSet.getString("DOC_IBAN");
		doc.numAvvisoPagoPa = resultSet.getString("DOC_NUM_AVVISO_PAGO_PA");
		doc.codiceIUV = resultSet.getString("DOC_CODICE_IUV");
		doc.codiceQRcode = resultSet.getString("DOC_CODICE_QRCODE");
		doc.causale = resultSet.getString("DOC_CAUSALE");
		doc.oggettoPagamento= resultSet.getString("OGG_PAGAMENTO");
		doc.codiceImpostaServizio=resultSet.getString("IMP_SERV");	//PG22XX01_GG1

		//E' a due cifre decimali? No,è già in centesimi? devo solo garantire un Minimo di 4 digit.
//marini
//		String importoCentesimi = leftZeroPad( doc.importoTotale,4,10);
		String importoCentesimi = leftZeroPad( doc.importoTotale,4,6);
			
		doc.codiceBarcode = String.format("(415)%s(8020)%s(3902)%s", getBarcodeParameter1(resultSet,doc.numAvvisoPagoPa.trim()),doc.numAvvisoPagoPa,importoCentesimi);
		return doc;
	}
	
	/** Aggiungo "0" iniziali*/
	private String leftZeroPad(String s, int minLen,int maxLen)
	{
		if(s.length()<minLen)
		{
			int zeri = minLen-s.length();
			return String.format("%0"+zeri+"d", 0)+s;
		}
		if(s.length()>maxLen)
		{
			return s.substring(s.length()-maxLen);
		}
		return s;
	}
	
	private String getBarcodeParameter1(ResultSet resultSet, String numAvviso) throws SQLException {
		String cutecute = resultSet.getString("FLU_CUTECUTE");
		String codFiscEnte=resultSet.getString("DOC_CFISCALE_ENTE");
		String auxDigit= numAvviso.substring(0, 1);
		String applicationCode = numAvviso.substring(1, 3);

		String barcodePar = context.getBarcodeParameter(cutecute, codFiscEnte, auxDigit, applicationCode);
		return barcodePar;
	}

	private AvvisoRata extractAvv(ResultSet resultSet) throws SQLException {
		AvvisoRata avv = new AvvisoRata();
		avv.dataScadenza = resultSet.getString("AR_DATA_SCADENZA").replace(".","");
		avv.importo = resultSet.getString("AR_IMPORTO");
		avv.progressivoBollettino = resultSet.getInt("AR_PROGRESSIVO_BOLLETTINO");
		avv.numeroAvviso = resultSet.getString("AR_NUMERO_AVVISO");
		avv.codiceIUV = resultSet.getString("AR_CODICE_IUV");
		avv.codiceQRcode = resultSet.getString("AR_CODICE_QRCODE");

//marini
//		String importoCentesimi = leftZeroPad( avv.importo,4,10);
		String importoCentesimi = leftZeroPad( avv.importo,4,6);
		avv.codiceBarcode = String.format("(415)%s(8020)%s(3902)%s", getBarcodeParameter1(resultSet,avv.numeroAvviso),avv.numeroAvviso,importoCentesimi);
		return avv;
	}

	private Tributo extractTrib(ResultSet resultSet) throws SQLException {
		Tributo trib = new Tributo();
		trib.anno = resultSet.getInt("TR_ANNO_TRIBUTO");
		trib.codiceTributo = resultSet.getString("TR_COD_TRIBUTO");
		trib.importo = resultSet.getString("TR_IMP_TRIBUTO");
		trib.note = resultSet.getString("TR_NOTE");

		return trib;
	}

}
