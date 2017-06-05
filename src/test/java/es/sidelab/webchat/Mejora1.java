package es.sidelab.webchat;


import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class Mejora1 {
	
	ChatManager chats =  new ChatManager(50);//chat manager con un tope de 50 chats.
	
	ExecutorService executor1 = Executors.newSingleThreadExecutor();
	CompletionService<String> cs1 = new ExecutorCompletionService<>(executor1);
	ExecutorService executor2 = Executors.newSingleThreadExecutor();
	CompletionService<String> cs2 = new ExecutorCompletionService<>(executor2);
	ExecutorService executor3 = Executors.newSingleThreadExecutor();
	CompletionService<String> cs3 = new ExecutorCompletionService<>(executor3);
	ExecutorService executor4 = Executors.newSingleThreadExecutor();
	CompletionService<String> cs4 = new ExecutorCompletionService<>(executor4);
	
	@Test
	public void newUserInChat() throws ConcurrentModificationException, Throwable{
		Callable<String> tarea1 = ()-> {
			TestUser user = new TestUser("User0");
			chats.newUser(user);
			for(int i=0;i<5;i++){
				Chat chat = chats.newChat("Chat "+i, 5,TimeUnit.SECONDS);
				chat.addUser(user);
			}
			return null;
		};
		cs1.submit(tarea1);
		Callable<String> tarea2 = ()-> {
			TestUser user = new TestUser("User1");
			chats.newUser(user);
			for(int i=0;i<5;i++){
				Chat chat = chats.newChat("Chat "+i, 5,TimeUnit.SECONDS);
				chat.addUser(user);
			}
			return null;
		};
		cs2.submit(tarea2);
		Callable<String> tarea3 = ()-> {
			TestUser user = new TestUser("User2");
			chats.newUser(user);
			for(int i=0;i<5;i++){
				Chat chat = chats.newChat("Chat "+i, 5,TimeUnit.SECONDS);
				chat.addUser(user);
			}
			return null;
		};
		cs3.submit(tarea3);
		Callable<String> tarea4 = ()-> {
			TestUser user = new TestUser("User3");
			chats.newUser(user);
			for(int i=0;i<5;i++){
				Chat chat = chats.newChat("Chat "+i, 5,TimeUnit.SECONDS);
				chat.addUser(user);
			}
			return null;
		};
		cs4.submit(tarea4);
		
		
		try {
			Future<String> f1= cs1.take();
			try {
				System.out.println("Terminado "+f1.get());
			} catch (ConcurrentModificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Future<String> f2 = cs2.take();
			try {
				System.out.println("Terminado "+f2.get());
			} catch (ConcurrentModificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Future<String> f3 = cs3.take();
			try {
				System.out.println("Terminado "+f3.get());
			} catch (ConcurrentModificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			Future<String> f4 = cs4.take();
			try {
				System.out.println("Terminado "+f4.get());
			} catch (ConcurrentModificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ConcurrentModificationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	



}
