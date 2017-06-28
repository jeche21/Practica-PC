package es.sidelab.webchat;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class Mejora1 {
	
	ChatManager chatManager = new ChatManager(50);
	
	ExecutorService executor = Executors.newFixedThreadPool(4);
	CompletionService<String> completionService = new ExecutorCompletionService<>(executor);
	
	@Test
	public void newUserInChat() throws Throwable {
		
		for(int i = 0; i < 4; i++){
			int id = i;
			Callable<String> tarea = () -> {
				mejoras("User" + id);
				return null;
			};
			completionService.submit(tarea);
		}
		
		for(int i = 0; i < 4; i++){
			try{
				Future<String> f = completionService.take();
				f.get();
			}
			catch (InterruptedException | ConcurrentModificationException e){
				e.getCause();
			}
		}
		
	}
	
	public void mejoras (String stringhilo) throws InterruptedException, TimeoutException {
		TestUser user = new TestUser(stringhilo);
		chatManager.newUser(user);
		for (int j = 0; j<5; j++ ){
			Chat chat = chatManager.newChat("Chat"+j, 5, TimeUnit.SECONDS);
			chat.addUser(user);
			Collection<User> usuarios = chat.getUsers();//mostramos los usuarios que hay en el chat por pantalla.
			   for(User user1: usuarios){
				   System.out.println(user1.getName() + " in " + chat.getName());
			   }
		}	
	}	
}

