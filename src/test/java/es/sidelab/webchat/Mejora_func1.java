package es.sidelab.webchat;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import es.codeurjc.webchat.ChatManager;

public class Mejora_func1 {
	
	@Test
	public void nuevoChat() throws Throwable {
		
		ChatManager chatManager = new ChatManager(1);
		
		chatManager.newChat("Chat1", 5, TimeUnit.SECONDS);
		chatManager.newChat("Chat2", 5, TimeUnit.SECONDS);
		chatManager.newChat("Chat3", 5, TimeUnit.SECONDS);
		
	}
	
}
