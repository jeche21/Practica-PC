package es.sidelab.webchat;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class Mejora4B {
	String [] mensajesOrdenados = {"Hola", "¿Que tal?", "Bien", "Me alegro", "Adios"};
	List <String> mensajes = new ArrayList<String>();
	Exchanger<Boolean> intercambiador= new Exchanger<Boolean>();
	

	CountDownLatch finChat = new CountDownLatch(1);
	CountDownLatch nuevoUsuario = new CountDownLatch(1);
	static ChatManager chatManager = new ChatManager(5);
	static Chat chat;
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService = new ExecutorCompletionService<String>(executor);
	
	ExecutorService executor2 = Executors.newSingleThreadExecutor();
	CompletionService<String> completionService2 = new ExecutorCompletionService<String>(executor2);
	
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
		
		try{
			Future<String> f = completionService.take();
			System.out.println("Terminado " + f.get());
			
			Future<String> f2 = completionService2.take();
			System.out.println("Terminado " + f2.get());
		} catch (ExecutionException | ConcurrentModificationException e) {
			throw e.getCause();
		}
	}

	public void crearChat(String nombre) throws InterruptedException,TimeoutException {
		TestUser user = new TestUser(nombre);
		chatManager.newUser(user);
		chat = chatManager.newChat("Chat", 5, TimeUnit.SECONDS);
		chat.addUser(user);	
		finChat.countDown();
		nuevoUsuario.await();
		for(int i = 0; i < 5; i++){
			chat.sendMessage(user, mensajesOrdenados[i]);
		}
		boolean iguales = intercambiador.exchange(null);
		assertTrue(iguales);
	}
	
	public void restoUsuarios(String usuario) throws InterruptedException,TimeoutException {
		finChat.await();
		TestUser user = new TestUser(usuario){
			@Override
			public void newMessage(Chat chat, User user, String message) {
				mensajes.add(message);
			}
		};
		chatManager.newUser(user);
		chat.addUser(user);
		nuevoUsuario.countDown();
		
		Thread.sleep(500);
		for(String mensaje : mensajes){
			System.out.println(mensaje);
		}
		boolean iguales = true;
		for(int i = 0; i < 5 && iguales; i++){
			iguales =(mensajes.get(i) == mensajesOrdenados[i]);
		}
		
		intercambiador.exchange(iguales);
	}
}
