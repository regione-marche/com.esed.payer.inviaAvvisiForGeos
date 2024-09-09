package com.esed.payer.inviaAvvisiForGeos.components;

import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Logger;

import com.esed.payer.inviaAvvisiForGeos.config.InviaAvvisiForGeosResponse;
import com.esed.payer.inviaAvvisiForGeos.util.CipherHelper;
import com.seda.bap.components.core.spi.ClassPrinting;
import com.seda.commons.properties.PropertiesLoader;
import com.seda.data.dao.DAOHelper;
import com.seda.data.datasource.DataSourceFactoryImpl;

/** Avvio da riga comando della procedura.
 * Uso commons-cli-1.2 che l'ultima versione funzionante su Java6 */
@SuppressWarnings("deprecation")
public class Main {
  String fileConfig;
  String codiceUtente;

  public static void main(String... argv) {
    Options options = new Options();
    options.addOption("f", "config", true, "File di configurazione .properties");
    options.addOption("u", "codiceUtente", true, "Codice Utente (cutecute)");
    options.addOption("h", "help", false, "Istruzioni esplicative");
    CommandLineParser commandLineParser = new BasicParser();
    HelpFormatter formatter = new HelpFormatter();

    boolean printHelp = false;
    Main main = new Main();
    try {
      CommandLine line = commandLineParser.parse(options, argv);
      if (line.hasOption("h"))
        printHelp = true;
      if (line.hasOption("f")) {
        main.fileConfig = line.getOptionValue("f");
      } else {
        printHelp = true;
      }
      if (line.hasOption("u")) {
        main.codiceUtente = line.getOptionValue("u");
      } else {
        printHelp = true;
      }
    } catch (ParseException ex) {
      ex.printStackTrace();
      printHelp = true;
    }

    if (printHelp) {
      formatter.printHelp("InviaAvvisiForGEOS -u \"XXX\" -f \"path_to_file.properties\"\n"
          + "Estrazione dal DB di Avvisi-Rata, assemblaggio in file512, e upload ftp a GEOS. "
          + "Specificare il file di configurazione .properties e il codice utente,"
          + "come da documentazione.", options);
      System.exit(-1);
    }
    main.run();
  }

  void run() {

    Logger logger = Logger.getLogger(InviaAvvisiForGeosCore.class);

    System.out.println("Avviato da riga comando");
    System.setProperty("db2.jcc.charsetDecoderEncoder", "3");

    try {
      // configurazione connessione DB per dtasource, usa lo stesso file di configurazione BAP e 
      // condivide i parametri di crittografia password 
      Properties datasourceConfig = null;

      // costruzione del datasource, normalmente fornito da BAP
      DataSource datasource = null;
      String schema = null;
      try {
    	 System.out.println("inizio configurazioni");
        datasourceConfig = PropertiesLoader.load(fileConfig);

        String dbDriver = datasourceConfig.getProperty("dbDriver");
        String dbUrl = datasourceConfig.getProperty("dbUrl");
        String dbUser = datasourceConfig.getProperty("dbUser");
        // clearPassword= "SV!L09SE"
        String dbPassword = datasourceConfig.getProperty("dbPassword");
        String encryptionIV = datasourceConfig.getProperty("security.encryption.iv");
        String encryptionKey = datasourceConfig.getProperty("security.encryption.key");
        CipherHelper cipher = new CipherHelper(encryptionIV, encryptionKey);
        dbPassword = cipher.decryptData(dbPassword);

        Properties dsProperties = new Properties();
        dsProperties.put(DAOHelper.JDBC_DRIVER, dbDriver);
        dsProperties.put(DAOHelper.JDBC_URL, dbUrl);
        dsProperties.put(DAOHelper.JDBC_USER, dbUser);
        dsProperties.put(DAOHelper.JDBC_PASSWORD, dbPassword);
        dsProperties.put("autocommit", "false");

        DataSourceFactoryImpl dataSourceFactory = new DataSourceFactoryImpl();
        dataSourceFactory.setProperties(dsProperties);
        datasource = dataSourceFactory.getDataSource();
        datasource.getConnection().close(); // test connessione per verificare i parametri
        System.out.println("Ottenuto datasource DB: " + dbUrl);

        //schema = dbUser;
        schema=datasourceConfig.getProperty("schema");
      } catch (Exception e) {
    	  e.printStackTrace();
    	  System.out.println("Errore = " + e.getMessage());
        throw new Exception("Errore config datasource DB, File properties: " + fileConfig, e);
      }

      ClassPrinting classPrinting = null;
      String jobId = "";
      String[] params = new String[] {"CONFIGPATH      " + fileConfig,
          "CUTECUTE      " + codiceUtente};

      InviaAvvisiForGeosCore core = new InviaAvvisiForGeosCore();
      InviaAvvisiForGeosResponse res = core.run(params, datasource, schema, classPrinting, logger,
          jobId);
      if (!res.getCode().equals("00")) {
        throw new Exception("Comando eseguito con esito negativo: " + res.getMessage());
      }
      logger.info("Comando eseguito con esito positivo: " + res.getMessage());

    } catch (Exception ex) {
      ex.printStackTrace();
      logger.error(ex);
      System.exit(1);
    }
  }
}
