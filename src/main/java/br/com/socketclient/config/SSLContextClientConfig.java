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

@Configuration
public class SSLContextClientConfig {
	
	
	@Bean
	@Qualifier("sslContextSimpleClient")
	public SSLContext sslContextConfigTrust() {
		
		try {
			KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
			ks.load(new FileInputStream(getClass().getResource("/canclientkeystore").getPath()), "Cielo_2022".toCharArray());	
			ks.setEntry("certserver", new KeyStore.TrustedCertificateEntry(generateCertificate()), null);
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
			kmf.init(ks, "Cielo_2022".toCharArray());

		    TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
		    tmf.init(ks);
		    			
			SSLContext contextoSSL = SSLContext.getInstance("TLS");
			contextoSSL.init(null, tmf.getTrustManagers(), null);		
			
			return contextoSSL;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
//	@Bean
//	@Qualifier("sslContextNeedClientAuth")
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
		    			
			SSLContext contextoSSL = SSLContext.getInstance("TLS");
//			contextoSSL.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());		
			contextoSSL.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);		
			
			return contextoSSL;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Certificate generateCertificate() {
		Certificate ca = null;
		try (InputStream caInput = new BufferedInputStream(
				new FileInputStream(getClass().getResource("/certserver.cer").getPath()))){
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			ca = cf.generateCertificate(caInput);
			System.out.println("client=" + ((X509Certificate) ca).getSubjectDN());				
		} catch(Exception e) {
			e.printStackTrace();
		}
		return ca;
	}
	

}
