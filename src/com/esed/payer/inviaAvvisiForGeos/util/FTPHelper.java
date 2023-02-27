package com.esed.payer.inviaAvvisiForGeos.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.log4j.Logger;

import com.esed.payer.inviaAvvisiForGeos.config.InviaAvvisiForGeosContext;

/**
 * Incapsulo le funzioni FTP di upload file512 e download PDF.
 * Le cartelle e altri parametri FTP sono conosciuti e presenti nel {@link InviaAvvisiForGeosContext}.
 * Non espongo esternamente i dettagli implementativi della libreria "apache FTP client".
 * Oggetto stateless: ogni chiamata crea il client, esegue la funzione e conclude.
 **/
public class FTPHelper {
	
	public boolean Debug = false;
	
	private Logger log;
	
	public FTPHelper(Logger log){
		this.log = log;
	}
	public FTPHelper(Logger log, boolean debug){
		this.Debug = debug;
		this.log = log;
	}
	public FTPClient getFTPClientFromUrl(String ftpUrl, String ftpUser, String ftpPassword) {
		
		FTPClient ftpClient = new FTPClient();

//		URL url = new URL(ftpUrl);
//		String host = url.getHost();
//		String path = url.getPath();
//		if (Debug) log.debug("FTPClient> ftpUrl='" + ftpUrl + "', ftpUser='" + ftpUser );
//		
//		try {
//			ftpClient.connect(ftpUrl);
//			ftpClient.changeWorkingDirectory(path);
//			ftpClient.login(ftpUser, ftpPassword);
			if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
			
//		} catch (Exception e) {
//			if (Debug) log.debug("FTPClient #1", e);

			URL url;
			
			try {
				url = new URL(ftpUrl);

				String host = url.getHost();
				int port = url.getPort();
				String path = url.getPath();
			
				ftpClient.connect(host, port == -1 ? 21 : port);
				ftpClient.login(ftpUser, ftpPassword);
				if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
				
		    	ftpClient.changeWorkingDirectory("/" +  ftpUser + path);
		    	if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
			      
			} catch (Exception ex) {
				System.out.println("errore FTP" + ex);
				if (Debug) log.debug("FTPClient #2", ex);
			}
//		}

		return ftpClient;
	}
	
	public boolean downloadFile(InviaAvvisiForGeosContext ctx,  String remoteFilePath, String localFilePath) {
		
		boolean success = false;
		
		String ftpUrl = ctx.getFtpBaseUrl();
		if (ftpUrl.length() > 0) {
			String ftpUser = ctx.getFtpUser();
			String ftpPassword = ctx.getFtpPassword();
			try {	 
			    FTPClient ftpClient = getFTPClientFromUrl(ftpUrl, ftpUser, ftpPassword);
				success = downloadFile(ftpClient, remoteFilePath, localFilePath);
			    if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
		    	
		    	ftpClient.logout();
		    	ftpClient.disconnect();
		 
		    } catch (Exception e) {
		    	log.error("FTPHelper.downloadFile " + remoteFilePath +  " " + localFilePath, e);
		    }
		}
		
		return success;
	}

	
	private boolean downloadFile(FTPClient ftpClient, String remoteFilePath, String localFilePath) throws IOException {
		
		File downloadFile = new File(localFilePath);
		OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(downloadFile));
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			boolean success = ftpClient.retrieveFile(remoteFilePath, outputStream);
			//if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
			return success;
		} catch (IOException ex) {
			throw ex;
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}
		
	public boolean deleteFile(InviaAvvisiForGeosContext ctx,  String remoteFilePath) {
		
		boolean success = false;
		
		String ftpUrl = ctx.getFtpBaseUrl();
		if (ftpUrl.length() > 0) {
			String ftpUser = ctx.getFtpUser();
			String ftpPassword = ctx.getFtpPassword();
			try {	 
			    FTPClient ftpClient = getFTPClientFromUrl(ftpUrl, ftpUser, ftpPassword);
				success = ftpClient.deleteFile(remoteFilePath);
				if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
		    	
		    	ftpClient.logout();
		    	ftpClient.disconnect();
		 
		    } catch (Exception e) {
		    	log.error("FTPHelper.deleteFile " + remoteFilePath, e);
		    }
		}
		
		return success;
	}
	
	
	public boolean uploadFile(InviaAvvisiForGeosContext ctx, String localFilePath, String remoteFilePath) {
		
		boolean success = false;
		
		String ftpUrl = ctx.getFtpUrl(ctx.getCodiceUtente());
		if (ftpUrl.length() > 0) {
			String ftpUser = ctx.getFtpUser();
			String ftpPassword = ctx.getFtpPassword();
			System.out.println("ftpUser=" + ftpUser);
			System.out.println("ftpPassword=" + ftpPassword);
			System.out.println("ftpUrl=" + ftpUrl);
			try {	 
			    FTPClient ftpClient = getFTPClientFromUrl(ftpUrl, ftpUser, ftpPassword);
			    System.out.println("localFilePath= " + localFilePath);
			    System.out.println("remoteFilePath= " + remoteFilePath);
				success = uploadFile(ftpClient, localFilePath, remoteFilePath);
			    if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
		    	
		    	ftpClient.logout();
		    	ftpClient.disconnect();
		 
		    } catch (Exception e) {
		    	e.printStackTrace();
		    	log.error("FTPHelper.uploadFile " + localFilePath +  " " + remoteFilePath, e);
		    }
		}
		
		return success;
	}



	
	
	private boolean uploadFile(FTPClient ftpClient, String localFilePath, String remoteFilePath) throws IOException {
		
		System.out.print("uploadFile localFilePath = " + localFilePath);
		File uploadFile = new File(localFilePath);
		InputStream inputStream = new BufferedInputStream(new FileInputStream(uploadFile));
		try {
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			boolean success = ftpClient.storeFile(remoteFilePath, inputStream);
			System.out.print("ftpClient.getReplyCode() = " + ftpClient.getReplyCode());
			
			if (Debug) log.debug(ftpClient.getReplyString().replace("\r\n", ""));
			return success;
		} catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new IOException();
		}
	
		finally {
			if (inputStream != null) {
				inputStream.close();
			}
		}
	}

	public ArrayList<String> listFolderFiles(InviaAvvisiForGeosContext ctx, String remoteFolder) {
		
		ArrayList<String> retval = new ArrayList<String>();
		
		try {	 
			String ftpUrl = ctx.getFtpBaseUrl();
			System.out.println("ftpurl" + ftpUrl);
			if (ftpUrl.length() > 0) {
				String ftpUser = ctx.getFtpUser();
				String ftpPassword = ctx.getFtpPassword();
				FTPClient ftpClient = getFTPClientFromUrl(ftpUrl, ftpUser, ftpPassword);
				System.out.println("dopo ftpclient");
				String targetFolder = ftpClient.printWorkingDirectory() + "/" + remoteFolder;
			    System.out.println("targetFolder:" + targetFolder);
				FTPFile[] files = ftpClient.listFiles(targetFolder);
				if (files != null && files.length > 0) {
				  
					for (FTPFile file : files) {
						if (!file.isDirectory()) {							
							retval.add(remoteFolder + "/" + file.getName());
						}
					}
				}
		 
		    	ftpClient.logout();
		    	ftpClient.disconnect();
			}
	    } catch (Exception e) {
	    	log.error("FTPHelper.listFolderFiles", e);
	    }
		
		return retval;
	}
}