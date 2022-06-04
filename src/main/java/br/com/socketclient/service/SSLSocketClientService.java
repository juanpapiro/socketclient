package br.com.socketclient.service;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SSLSocketClientService {
	
	@Autowired
	@Qualifier("sslContextSimpleClient")
	private SSLContext sslContext;

	public String sendMessage(String message) {
		
		String response = "";
		
		try(SSLSocket sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket("localhost", 9001)) {
			
			sslSocket.setEnabledCipherSuites(sslSocket.getSupportedCipherSuites());
			sslSocket.setEnabledProtocols(sslSocket.getEnabledProtocols());
			
//			sslSocket.startHandshake();
			
//			Optional.ofNullable(sslSocket.getSession())
//					.orElseThrow(() -> new RuntimeException("Falha ao abrir sessão hendshake"));
			
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
	
}
