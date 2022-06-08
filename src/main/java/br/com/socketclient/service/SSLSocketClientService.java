package br.com.socketclient.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
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
			log.error(e);
		}
		
		return response;
	}
	
}
