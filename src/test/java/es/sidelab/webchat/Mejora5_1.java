package es.sidelab.webchat;

import static org.junit.Assert.assertTrue;

import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class Mejora5_1 {
	ExecutorService executor = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
	
	@Test
	public void newChat() throws Throwable {
		
		Callable <String> tarea = ()-> {
			
			ChatManager chatManager = new ChatManager(5);
			
			final String[] chatName = new String[1];

			chatManager.newUser(new TestUser("user") {
				@Override
				public void newChat(Chat chat) {
					chatName[0] = chat.getName();
				}
			});
			
			chatManager.newChat("Chat", 5, TimeUnit.SECONDS);
			Thread.sleep(1000);//ponemos este sleep para que de tiempo a que el chat se cree
			System.out.println("Se ha creado el chat llamado "+chatName[0]);
			
			assertTrue("No se ha creado el nuevo chat"+ chatName[0], Objects.equals(chatName[0], "Chat"));
			
			return null;
		};
		
		completionService.submit(tarea);
		
		try{
			Future<String> f = completionService.take();
			System.out.println("Terminado " + f.get());
		} catch (ExecutionException e) {
			throw e.getCause();
		}
	}
	
}
