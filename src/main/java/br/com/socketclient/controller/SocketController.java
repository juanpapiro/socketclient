package br.com.socketclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.socketclient.service.SSLSocketClientService;
import br.com.socketclient.service.SocketClientService;

@RestController
@CrossOrigin(origins = "*")
public class SocketController {
	
	@Autowired
	private SocketClientService socketClientService;
	
	@Autowired
	private SSLSocketClientService SSLSocketClientService;
	
	@PostMapping("/message/send")
	public ResponseEntity<String> sendMessage(@RequestParam String message) {
		return ResponseEntity.ok(socketClientService.sendMessage(message));
	}
	
	@PostMapping("/message/sendssl")
	public ResponseEntity<String> sendMessageSSL(@RequestParam String message) {
		return ResponseEntity.ok(SSLSocketClientService.sendMessage(message));
	}

}
