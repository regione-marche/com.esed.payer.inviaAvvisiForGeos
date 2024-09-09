/**
 * EstrattoContoSOAPBindingImpl.java
 *
 * This file was auto-generated from WSDL
 * by the IBM Web services WSDL2Java emitter.
 * cf311012.09 v41410192429
 */

package com.esed.payer.inviaAvvisiForGeos.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class InviaAvvisiForGeosUtil {
	
	 
	 public static String formatCalendarData(Calendar data)
	{
		if (data != null)
		{
			Calendar cal = Calendar.getInstance(Locale.ITALIAN);
			cal.setTime(data.getTime());
		
			return formatNumToString(2, String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" +
				formatNumToString(2, String.valueOf((cal.get(Calendar.MONTH) + 1))) + "/" +
				String.valueOf(cal.get(Calendar.YEAR));
		}
		else
			return "";
	}
	 
	 /**
	  * dateToSearch GG/MM/AAAA
	  * @param aMovimenti
	  * @param dateToSearch
	  * @return
	  */
	 
	 
	public static String formatCalendarData(Calendar data, String sFormato)
	{
		if (data != null)
		{
			Calendar cal = Calendar.getInstance(Locale.ITALIAN);
			cal.setTime(data.getTime());
		
			SimpleDateFormat formatDateTime = new SimpleDateFormat(sFormato);
			return formatDateTime.format(cal.getTime());
		}
		else
			return "";
	}
	private static String formatNumToString(int iLenght, String sNumToFormat)
	{
		String formattedString = sNumToFormat;

		while(formattedString.length() < iLenght) 
		{
			formattedString = "0" + formattedString;
		}
		return formattedString;
	}
	
	public static String formatDecimalNumber(BigDecimal bdValue)
	{
		if (bdValue != null)
		{
			DecimalFormat dcFormat = getDecimalFormat();
			return dcFormat.format(bdValue);
		}
		else
			return "0,00";
	}
	private static DecimalFormat getDecimalFormat()
	{
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(); 
		symbols.setDecimalSeparator(',');
		symbols.setGroupingSeparator('.');

		DecimalFormat dcFormat = new DecimalFormat("#0.00", symbols);
		return dcFormat;
	}
	/**
	 * verifico che la il numero della rta sia relativo al documento totale ovvero stringa vuota o "00"
	 * @param sNumRata
	 * @return
	 */
	public static boolean getRataDocumento(String sNumRata)
	{
		// verifico se la rata è relativo al documento
		if (sNumRata!=null && (sNumRata.trim().length()==0 || sNumRata.equals("00")))
			return true;
		else
			return false;
		
	}
	
	/**
	 * Splitta con l'elemento "|"
	 * @param iNumStrings: numero di elementi attesi nello split
	 * @param sToSPlit: stringa da separare
	 * @return l'array con le stringhe splittate
	 */
	public static String[] getSplit_NString(int iNumStrings, String sToSplit)
	{
		String[] sSplit = null;
		if (sToSplit != null)
		{
			String[] sSplitTemp = sToSplit.split("\\|");
			
			if (sSplitTemp.length != iNumStrings)
			{
				sSplit = new String[iNumStrings];
				for (int k=0; k<iNumStrings; k++)
					sSplit[k] = "";
			}
			else
				sSplit = sSplitTemp;	
		}
		else
		{
			sSplit = new String[iNumStrings];
			for (int k=0; k<iNumStrings; k++)
				sSplit[k] = "";
		}
			
		return sSplit;
	}
	
	public static String getMovimentiKey(String sNumRata, Calendar cDataPagamento)
	{
		return sNumRata +"|"+ formatCalendarData(cDataPagamento);
	}
	public static String[] getMovimentiNumRataDataFromKey(String sKey)
	{
		return getSplit_NString(2, sKey);
	}

	public static String rataScaduta(String sDDMMAAAA)
	{
		try
		{
			Calendar today = Calendar.getInstance();			
			Calendar calRata = convertDate(sDDMMAAAA);
			if (today.after(calRata))
				return "Y";
			else
				return "N";
		}
		catch (Exception ex){}
		
		return "N";
	}
	
	/**
	 * DD/MM/AAAA converte in AAAAMMDD
	 * @param sDDMMAAAA
	 * @return
	 */
	private static Calendar convertDate(String sDDMMAAAA)
	{
		Calendar cal = Calendar.getInstance();
		try
		{
		    SimpleDateFormat formatterIT = new SimpleDateFormat("dd/MM/yyyy");
		    java.util.Date utilDate = formatterIT.parse(sDDMMAAAA);
		    cal.setTime(utilDate);			   
		}
		catch (Exception ex)
		{
			cal.set(1, Calendar.JANUARY, 1);  
			
		}
		
		return cal;
	}    
}
