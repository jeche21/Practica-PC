package es.codeurjc.webchat;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.tomcat.util.collections.SynchronizedQueue;

public class ChatManager {

	//private Map<String, Chat> chats = new HashMap<>();
	//private Map<String, User> users = new HashMap<>();
	//mejora1
	private ConcurrentHashMap<String, Chat> chats = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	/*nos creamos esta estructura para poder pedir a la clase user un determinado executor de un usuario en concreto para poder mandar mensajes en paralelo evitando que
	un usuario con mala conexion haga que los demas tambien vayan lentos. De esta manera si tienes buena conexion no habra problema.*/
	private ConcurrentHashMap<User,ExecutorService> tareas= new ConcurrentHashMap<>();
	Semaphore semaforo = new Semaphore(1);
	Executor numChats = Executors.newSingleThreadExecutor();
	Executor numUser = Executors.newSingleThreadExecutor();
	Executor numMedUsuer =  Executors.newSingleThreadExecutor();
	Executor chatMaxUser = Executors.newSingleThreadExecutor();
	Executor chatMinUser = Executors.newSingleThreadExecutor();
	

	private int maxChats;
	//Mejora_func1
	private BlockingQueue<String> colaChats;//hacemos una blocking queue de 50 chat como maximo.
	

	public ChatManager(int maxChats) {
		this.maxChats = maxChats;
		this.colaChats = new ArrayBlockingQueue<>(maxChats);
	}
	
	public void  estadisticas(){
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while(true){
			Runnable numeroChats = () ->{
				System.out.println("El numero de chats es: " + chats.size());
			};
			numChats.execute(numeroChats);
			Runnable numeroUsuarios = () -> {
				int usuariosTotalesEnChats = this.numeroUsuariosTotales();
				System.out.println("El numero de usuarios activos son: " + usuariosTotalesEnChats);
			};
			numUser.execute(numeroUsuarios);
			Runnable mediaUser = () -> {
				float mediaUsuariosChats = this.numeroUsuariosTotales() / (chats.size());
				System.out.println("La media de usuarios por chat es de: " + mediaUsuariosChats);
			};
			numMedUsuer.execute(mediaUser);
			Runnable maximoUserChats = () -> {
				int max = 0;
				Chat chatMayor = null;
				for(Chat chat : this.getChats()){
					if(chat.getUsers().size() >= max){
						max = chat.getUsers().size();
						chatMayor = chat;
					}
				}
				System.out.println("El chat que mas usuarios tiene es: " + chatMayor.getName());
			};
			chatMaxUser.execute(maximoUserChats);
			Runnable minimoUserChats = () -> {
				int min = 0;
				Chat chatMenor = null;
				for(Chat chat : this.getChats()){
					if(chat.getUsers().size() <= min){
						min = chat.getUsers().size();
						chatMenor = chat;
					}
				}
				System.out.println("El chat que menos usuarios tiene es: " + chatMenor.getName());
			};
			chatMinUser.execute(minimoUserChats);
			
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void newUser(User user) {
			
		if(users.containsKey(user.getName())){
			throw new IllegalArgumentException("There is already a user with name \'"
					+ user.getName() + "\'");
		} else {
			//users.put(user.getName(), user);
			//users.putIfAbsent(user.getName(), user);
			//si se cumple la condicion del if quiere decir que no hay ningun usuario con ese nombre entonces insertamos en el hash map de las tareas el usuario y creamos un executor para que pueda realizar tareas en paralelo cuando le manden.
			//mejora3 y mejora1
			if(users.putIfAbsent(user.getName(), user) == null){
				tareas.putIfAbsent(user, Executors.newSingleThreadExecutor());
			}		
		}
	}

	public Chat newChat(String name, long timeout, TimeUnit unit) throws InterruptedException,
			TimeoutException {
		//esto tenemos que cambiarlo y bloquear hasta que haya hueco para meter un chat
		if (chats.size() == maxChats) {
			//tenemos que recoger el resultado de offer o automaticamente lo imprime por pantalla el solo??
				boolean conseguido = colaChats.offer(name, timeout, unit);//metemos un chat mas en la cola.
				if(!conseguido){
					throw new TimeoutException("There is no enought capacity to create a new chat");
			}
			
		}

		if(chats.containsKey(name)){
			//si existe no crea otro sino que te lo devuelve el que ya existe
			return chats.get(name);
		} else {
			colaChats.offer(name);
			Chat newChat = new Chat(this, name);
			//chats.put(name, newChat);
			if((chats.putIfAbsent(name, newChat)) == null){
				for(User user : users.values()){
					//user.newChat(newChat);
					//avisamos de manera paralela a todos los usuarios de que se ha creado un nuevo chat
					this.getExecutor(user).execute(()->{user.newChat(newChat);});			
				}
			}
			
			return newChat;
		}
	}

	public void closeChat(Chat chat) {
		Chat removedChat = chats.remove(chat.getName());
		
		if (colaChats.size()>0){
			colaChats.remove(chat.getName());
		}
		if (removedChat == null) {
			throw new IllegalArgumentException("Trying to remove an unknown chat with name \'"
					+ chat.getName() + "\'");
		}

		for(User user : users.values()){
			user.chatClosed(removedChat);
		}
	}

	public Collection<Chat> getChats() {
		return Collections.unmodifiableCollection(chats.values());
	}

	public Chat getChat(String chatName) {
		return chats.get(chatName);
	}

	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	public User getUser(String userName) {
		return users.get(userName);
	}
	//NO ENTIENDO MUY BIEN QUE SE DEVUELVA EL NOMBRE
	//devolvemos el executor de el usuario para que podamos asignarle una tarea a realizar
	//mejora3
	public ExecutorService getExecutor(User name){
		return tareas.get(name);
	}

	public void close() {}
	
	public int numeroUsuariosTotales(){
		int contadorUser = 0;
		for(Chat chat :this.getChats()){
			contadorUser = contadorUser + chat.getUsers().size();
		}
		return contadorUser;
	}
}
