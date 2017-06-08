package es.sidelab.webchat;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
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
	ExecutorService executor2 = Executors.newSingleThreadExecutor();
	
	Exchanger<String> mensaje = new Exchanger<String>();
	
	ArrayList<String> arrayMensajes = new ArrayList<>();

	CountDownLatch barrera1 = new CountDownLatch(1);
	CountDownLatch barrera2 = new CountDownLatch(1);
	
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
			barrera1.countDown();
			barrera2.await();
			chat.sendMessage(user, message);
						
			assertTrue(arrayMensajes.get(0) == arrayMensajes.get(1) && arrayMensajes.get(0)==message);
				
				return null;
			};
			executor1.submit(tarea1);
			
			Callable<String> tarea2 = () -> {
				barrera1.await();
				TestUser user = new TestUser("César"){
					@Override
					public void newMessage(Chat chat, User user, String message) {
						arrayMensajes.add(message);
						System.out.println(message);
						
					}
				};
				chatManager.newUser(user);
				chat.addUser(user);
				barrera2.countDown();
				
				
					return null;
				};
				executor2.submit(tarea2);
			
		}
		
}

