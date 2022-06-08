package br.com.socketclient.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SocketClientService {
	
	public String sendMessage(String message) {
		
		try(Socket socketClient = new Socket("localhost", 9000)) {

			long time = System.nanoTime();

			OutputStreamWriter osw = new OutputStreamWriter(socketClient.getOutputStream());
			BufferedWriter bw = new BufferedWriter(osw);
			PrintWriter writer = new PrintWriter(bw);
			writer.println(message);
			writer.flush();
			
			
			InputStreamReader reader = new InputStreamReader(socketClient.getInputStream());
			BufferedReader br = new BufferedReader(reader);
			String resp = br.readLine();
			
			socketClient.close();
			log.info("Tempo processamento: {} nanossegundos", System.nanoTime() - time);
			return resp;
			
		} catch(Exception e) {
			log.error(e);
			return "Falha de comunicação com servidor";
		}
		
	}
	

}
