package es.sidelab.webchat;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import es.codeurjc.webchat.Chat;
import es.codeurjc.webchat.ChatManager;

public class Mejora_func_1_2 {
	
	CountDownLatch esperar = new CountDownLatch(1);
	ChatManager chatManager = new ChatManager(1);
	
	ExecutorService executor = Executors.newSingleThreadExecutor();
	ExecutorService executor2 = Executors.newSingleThreadExecutor();
	
	@Test
	public void newChatTimeIn() throws InterruptedException, ExecutionException{
		Future<?> f = executor.submit(() -> {
			Chat chat;
				try {
					chat = chatManager.newChat("Chat1" , 5, TimeUnit.SECONDS);
					esperar.countDown();
					Thread.sleep(2000);
					chatManager.closeChat(chat);
				} catch (InterruptedException | TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
		f.get();
		
		Future<?> f2 = executor.submit(() -> {
				try {
					esperar.await();
					Chat chat2 = chatManager.newChat("Chat2" , 5, TimeUnit.SECONDS);
					assertTrue(chatManager.getChat(chat2.getName()) == chat2);
				} catch (InterruptedException | TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		});
		f2.get();
	}
}
