package es.sidelab.webchat;

import java.util.ConcurrentModificationException;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class Mejora4A {
	static CountDownLatch latchUser = new CountDownLatch(3);
	static CountDownLatch actuacionRestoUsuarios = new CountDownLatch(1);
	static Lock cerrojo = new ReentrantLock();
	

	static ChatManager chatManager = new ChatManager(5);
	static Chat chat;
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
	
	ExecutorService executor2 = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService2 = new ExecutorCompletionService<String>(executor2);
	
	ExecutorService executor3 = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService3 = new ExecutorCompletionService<String>(executor3);
	
	ExecutorService executor4 = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService4 = new ExecutorCompletionService<String>(executor4);
	
	@Test
	public void testMejora4() throws Throwable {
		
		Callable <String> tarea = ()-> {
			crearChat("Jesus");
			Thread.sleep(1000);
			return "Jesus";
		};
		completionService.submit(tarea);
		
		Callable <String> tareaUser1 = ()-> {
			restoUsuarios("César");
			Thread.sleep(1000);
			return "Cesar";
		};
		completionService2.submit(tareaUser1);
		
		Callable <String> tareaUser2 = ()-> {
			restoUsuarios("Javi");
			Thread.sleep(1000);
			return "Javi";
		};
		completionService3.submit(tareaUser2);
		
		Callable <String> tareaUser3 = ()-> {
			restoUsuarios("Miguel");
			Thread.sleep(1000);
			return "Miguel";
		};
		completionService4.submit(tareaUser3);
		
		
		try{
			Future<String> f = completionService.take();
			System.out.println("Terminado " + f.get());
			
			Future<String> f2 = completionService2.take();
			System.out.println("Terminado " + f2.get());
			
			Future<String> f3 = completionService3.take();
			System.out.println("Terminado " + f3.get());
			
			Future<String> f4 = completionService4.take();
			System.out.println("Terminado " + f4.get());
		} catch (ExecutionException | ConcurrentModificationException e) {
			throw e.getCause();
		}
	}

	public void crearChat(String nombre) throws InterruptedException,TimeoutException {

		TestUser user = new TestUser(nombre);
		chatManager.newUser(user);
		chat = chatManager.newChat("Chat", 5, TimeUnit.SECONDS);
		chat.addUser(user);
		actuacionRestoUsuarios.countDown();
		latchUser.await();		
		
		chat.sendMessage(user, "Hola,¿Que tal estais?");		
	}
	
	public void restoUsuarios(String usuario) throws InterruptedException,TimeoutException {
		
		actuacionRestoUsuarios.await();
		cerrojo.lock();
		TestUser user = new TestUser(usuario);
		chatManager.newUser(user);
		chat.addUser(user);
		actuacionRestoUsuarios.countDown();
		latchUser.countDown();
		cerrojo.unlock();

	}
}

