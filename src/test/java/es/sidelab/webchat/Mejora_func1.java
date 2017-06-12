package es.sidelab.webchat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Test;


import es.codeurjc.webchat.ChatManager;

public class Mejora_func1 {
	
	ExecutorService executor = Executors.newFixedThreadPool(3);
	ChatManager chatManager = new ChatManager(1);
	
	@Test
	public void nuevoChat() throws Exception{
		
			for(int i = 0; i < 3; i++){
				int id = i;
				Future<?> f = executor.submit(() -> chatManager.newChat("Chat" + id, 5, TimeUnit.MICROSECONDS));
				f.get();
			}
	}
}
