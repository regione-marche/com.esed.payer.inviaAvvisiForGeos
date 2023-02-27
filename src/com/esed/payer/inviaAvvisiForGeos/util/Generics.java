package com.esed.payer.inviaAvvisiForGeos.util;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;

public class Generics {
	
	/**
	 * Splitta con l'elemento ";"
	 * @param iNumStrings: numero di elementi attesi nello split
	 * @param sToSPlit: stringa da separare
	 * @return l'array con le stringhe splittate
	 */
	public static String[] getSplit_NString(int iNumStrings, String sToSplit)
	{
		String[] sSplit = null;
		if (sToSplit != null)
		{
			String[] sSplitTemp = sToSplit.split(";",-1);
			
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
	
	public static String formatDecimalNumber(BigDecimal bdValue)
	{
		DecimalFormat dcFormat = getDecimalFormat();
		bdValue = bdValue.setScale(2, BigDecimal.ROUND_HALF_UP);
		
		return dcFormat.format(bdValue);
	}
	
	public static DecimalFormat getDecimalFormat()
	{
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(); 
		symbols.setDecimalSeparator(',');
		//symbols.setGroupingSeparator('.');

		DecimalFormat dcFormat = new DecimalFormat("#0.00", symbols);
		return dcFormat;
	}
	public static Calendar getMinDate()
	{
		Timestamp timestamp = Timestamp.valueOf("1000-01-01 00:00:00.000");
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(timestamp.getTime());
		
		return cal;
	}
}
