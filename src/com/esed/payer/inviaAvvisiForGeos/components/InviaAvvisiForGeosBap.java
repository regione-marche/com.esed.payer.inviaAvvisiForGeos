package com.esed.payer.inviaAvvisiForGeos.components;

import com.esed.payer.inviaAvvisiForGeos.config.InviaAvvisiForGeosResponse;
import com.seda.bap.components.core.BapException;
import com.seda.bap.components.core.spi.ClassRunnableHandler;

public class InviaAvvisiForGeosBap extends ClassRunnableHandler {
	public void run(String[] args) throws BapException {
		
		//Stampa parametri di input - inizio
		for (int i=0; i< args.length;i++) {
			System.out.println( "argomento[" + i +"] " + args[i] );   
		} 
		//In caso di esecuzione da BAP:
		String[] parameters = getParameters();
		for (int i=0; i< parameters.length;i++) {
			System.out.println( "param[" + i +"] [" + parameters[i] + "]" );   
		}
		//Stampa parametri di input - fine
		
		InviaAvvisiForGeosCore core= new InviaAvvisiForGeosCore();
		InviaAvvisiForGeosResponse res;
 		
		res =  core.run(this.getParameters(), this.getDataSource(), this.getSchema(),printer(),logger(),this.getJobId());
		
		this.setCode(res!=null?res.getCode():"");
		this.setMessage(res!=null?res.getMessage():"");
	}
}
