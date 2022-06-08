package br.com.socketclient.config;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Configuration
public class SSLContextClientConfig {
	
	public static final String KEYSTORE = "/clientkeystore";
	public static final String STOREPASS = "123456";
	public static final String SERVERCERT = "/certserver.cer";
	public static final String SERVERCERTNEW = "/certservernew.cer";
	
	@Bean
	@Qualifier("sslContextSimpleClient")
	public SSLContext sslContextConfigTrust() {
		
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(new FileInputStream(getClass().getResource(KEYSTORE).getPath()), null);	
			ks.setEntry("certserver", new KeyStore.TrustedCertificateEntry(generateCertificate(SERVERCERT)), null);
			ks.setEntry("certservernew", new KeyStore.TrustedCertificateEntry(generateCertificate(SERVERCERTNEW)), null);
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, STOREPASS.toCharArray());

		    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		    tmf.init(ks);
		    			
			SSLContext contextoSSL = SSLContext.getInstance("TLS");
			contextoSSL.init(null, tmf.getTrustManagers(), null);		
			
			return contextoSSL;
		} catch (Exception e) {
			log.error(e);
			return null;
		}
	}
	
	
	public Certificate generateCertificate(String fileName) {
		try (InputStream caInput = new BufferedInputStream(
				new FileInputStream(getClass().getResource(fileName).getPath()))){
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate ca = cf.generateCertificate(caInput);
			X509Certificate x509 = (X509Certificate) ca;
			log.info("SubjectDN: {} - Algoritmo: {} - Tipo: {}", 
					x509.getSubjectDN(), x509.getSigAlgName(), x509.getType());
			return ca;
		} catch(Exception e) {
			log.error(e);
		}
		return null;
	}
	

}
