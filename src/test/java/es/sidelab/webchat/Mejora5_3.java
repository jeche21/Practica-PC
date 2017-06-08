package es.sidelab.webchat;


import static org.junit.Assert.assertTrue;	
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

	import org.junit.Test;

	import es.codeurjc.webchat.Chat;
	import es.codeurjc.webchat.ChatManager;

	public class Mejora5_3 {
		//alta de un usuario en un chat
		ExecutorService executor1 = Executors.newSingleThreadExecutor();
		CompletionService<String> cs1 = new ExecutorCompletionService<String>(executor1);
		
		ExecutorService executor2 = Executors.newSingleThreadExecutor();
		CompletionService<String> cs2 = new ExecutorCompletionService<String>(executor2);
		
		CountDownLatch latch1 = new CountDownLatch(1);
		Exchanger<String> estoy_dentro = new Exchanger<String>();
		
		Chat chat = null;
		ChatManager chatManager = new ChatManager(5);
		final String[] chatName = new String[1];
		
		@Test
		public void newChat() throws Throwable {
			Callable<String> tarea1 = () -> {
				TestUser user = new TestUser("Cesar");
				chat = chatManager.newChat("Chat",5, TimeUnit.MILLISECONDS);
				chatManager.newUser(user);
				chat.addUser(user);
				latch1.countDown();
				
				String dentro = estoy_dentro.exchange(null);
				assertTrue(chat.getUser(dentro)!=null);
				return null;
			};
			cs1.submit(tarea1);
			
			Callable<String> tarea2 = () -> {
				latch1.await();
				TestUser user = new TestUser("Jesus");
				chatManager.newUser(user);
				chat.addUser(user);
				estoy_dentro.exchange(user.getName());
				
				return null;
			};
			cs2.submit(tarea2);
		}
		
}

