package es.sidelab.webchat;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class Mejora5_5 {
	//envio de un mensaje
	ExecutorService executor1 = Executors.newSingleThreadExecutor();
	
	ArrayList<String> arrayMensajes = new ArrayList<>();
	
	String message = "Hola,¿que tal?";
	Chat chat = null;
	ChatManager chatManager = new ChatManager(5);
	
	@Test
	public void newChat() throws Throwable {
		Callable<String> tarea1 = () -> {
			TestUser user = new TestUser("Jesus"){
				@Override
				public void newMessage(Chat chat, User user, String message) {
					arrayMensajes.add(message);
					System.out.println(message);
				}
			};
			chat = chatManager.newChat("Chat",5, TimeUnit.MILLISECONDS);
			chatManager.newUser(user);
			chat.addUser(user);
			chat.sendMessage(user, message);
			
			assertTrue(message == arrayMensajes.get(0));
				
				return null;
			};
			executor1.submit(tarea1);
			
		}
		
}

