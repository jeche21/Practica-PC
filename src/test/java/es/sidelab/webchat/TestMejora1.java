package es.sidelab.webchat;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;
import es.codeurjc.webchat.User;

public class TestMejora1 {
	
	//ChatManager con tope de 50 chats
	ChatManager chatManager = new ChatManager(50);
	
	Executor usuario1 = Executors.newSingleThreadExecutor();
	Executor usuario2 = Executors.newSingleThreadExecutor();
	Executor usuario3 = Executors.newSingleThreadExecutor();
	Executor usuario4 = Executors.newSingleThreadExecutor();
	
	@Test
	public void newUserInChat() throws Throwable{
		Runnable tarea1 = () ->{
			try {
				implementarMejora("User1");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		usuario1.execute(tarea1);
		
		Runnable tarea2 = () ->{
			try {
				implementarMejora("User2");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		usuario1.execute(tarea2);
		
		Runnable tarea3 = () ->{
			try {
				implementarMejora("User3");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		usuario1.execute(tarea3);
		
		Runnable tarea4 = () ->{
			try {
				implementarMejora("User4");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		usuario1.execute(tarea4);
	}
	
	public void implementarMejora(String usuario) throws InterruptedException, TimeoutException{
		
		//Creo un TestUser
		TestUser user = new TestUser(usuario);
		
		//Registro el TestUser en el chatManager
		chatManager.newUser(user);
		
		for(int i = 0; i < 5; i++){
			int idChat = i;
			//Creo un chat con el nombre chat+i
			Chat chat = chatManager.newChat("Chat"+idChat, 5, TimeUnit.SECONDS);
			//Registro en ese chat al TestUser
			chat.addUser(user);
			//Muestro por pantalla todos los usuarios de ese chat
			Collection<User> usuarios = chat.getUsers();
			for(User usuarioChat: usuarios){
				String nombreUsuario = usuarioChat.getName();
				System.out.println(nombreUsuario + " en Chat"+idChat);
			}
		}
	}
}
