package es.sidelab.webchat;



import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class Mejora1 {
	
	ChatManager chatManager = new ChatManager(50);
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	ExecutorService executor2 = Executors.newSingleThreadExecutor();
	ExecutorService executor3 = Executors.newSingleThreadExecutor();
	ExecutorService executor4 = Executors.newSingleThreadExecutor();
	
	@Test
	public void newUserInChat() throws Throwable {
		
		Runnable tarea = ()-> {
			try {
				mejoras("user0");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		};
		executor.execute(tarea);
		
		Runnable tareaUser1 = ()-> {
			
			try {
				mejoras("user1");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		executor2.execute(tareaUser1);
		
		Runnable tareaUser2 = ()-> {
			
			try {
				mejoras("user2");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		executor3.execute(tareaUser2);
		
		Runnable tareaUser3 = ()-> {
			
			try {
				mejoras("user3");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		executor4.execute(tareaUser3);
		
	}
	
	public void mejoras (String stringhilo) throws InterruptedException, TimeoutException {
		TestUser user = new TestUser(stringhilo);
		chatManager.newUser(user);
		for (int j = 0; j<5; j++ ){
			Chat chat = chatManager.newChat("Chat"+j, 5, TimeUnit.SECONDS);
			chat.addUser(user);
			
		}
		
		
	}	
}
