package br.com.socketclient.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.springframework.stereotype.Service;

@Service
public class SSLSocketClientService {

	public String sendMessage(String message) {
		
		String response = "";
		
		try(SSLSocket sslSocket = (SSLSocket) sslContextConfigJKS().getSocketFactory().createSocket("localhost", 9001)) {
			sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
			
			//EVNVIA MENSAGEM PARA SOCKET SERVER
			PrintWriter writer = new PrintWriter(sslSocket.getOutputStream(), true);
			writer.println(message);
			
			//RECEBE MENSAGEM DE RESPOSTA DO SOCKET SERVER
			InputStreamReader isReader = new InputStreamReader(sslSocket.getInputStream());
			BufferedReader bfReader = new BufferedReader(isReader);
			response = bfReader.readLine();
			
		} catch (IOException e) {
			System.out.println(e);
		}
		
		return response;
	}
	
	
	public SSLContext sslContextConfigJKS() {
		
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(new FileInputStream(getClass().getResource("/clientkeystore").getPath()), "123456".toCharArray());
			ks.setCertificateEntry("certserver", generateCertificate());	
//			ks.setEntry("certserver", new KeyStore.TrustedCertificateEntry(generateCertificate()), null);
			
//			KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
//			trustStore.load(new FileInputStream(getClass().getResource("/clientkeystore").getPath()), "123456".toCharArray());

			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "123456".toCharArray());

//			TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		    tmf.init(ks);
		    
		    TrustManager[] trustManager = tmf.getTrustManagers();
			
			SSLContext contextoSSL = SSLContext.getInstance("TLS");
//			contextoSSL.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());		
			contextoSSL.init(kmf.getKeyManagers(), trustManager, null);		
			
			return contextoSSL;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Certificate generateCertificate() {
		Certificate ca = null;
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			InputStream caInput = new BufferedInputStream(new FileInputStream(getClass().getResource("/certserver.cer").getPath()));
			try {
			    ca = cf.generateCertificate(caInput);
			    System.out.println("client=" + ((X509Certificate) ca).getSubjectDN());
			} finally {
			    caInput.close();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ca;
	}
	
}
