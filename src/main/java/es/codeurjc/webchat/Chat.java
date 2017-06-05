package es.codeurjc.webchat;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;


public class Chat {

	private String name;
	//******private Map<String, User> users = new HashMap<>();
	private ConcurrentHashMap<String, User> users = new ConcurrentHashMap<>();
	private ChatManager chatManager;

	public Chat(ChatManager chatManager, String name) {
		this.chatManager = chatManager;
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addUser(User user) {
		//*********users.put(user.getName(), user);
		users.putIfAbsent(user.getName(), user);
		for(User u : users.values()){
			if (u != user) {
				//mejora3
				//u.newUserInChat(this, user);
				/*esto lo hacemos para comunicar de manera paralela al los demas usuarios evitando asi hacerlo de forma secuencial
				*y que algunos usuarios con buena conexion queden bloqueados por culpa de uno que tiene mala conexion*/
				chatManager.getExecutor(u).execute(()->{u.newUserInChat(this, user);});
			}
		}
	}

	public void removeUser(User user) {
		//El remove al ser mapa concurrente devuelve booleano si se ha borrado correctamente el usuario del chat.
		if(users.remove(user.getName(),user)){
			//Entramos en el for y vamos avisando en paralelo a los demas miembros del grupo de que se ha ido un usuario.
			for(User u : users.values()){
				//mejora3
				//u.userExitedFromChat(this, user);
				chatManager.getExecutor(u).execute(()->{u.userExitedFromChat(this, user);});
			}
		}
	}

	public Collection<User> getUsers() {
		return Collections.unmodifiableCollection(users.values());
	}

	public User getUser(String name) {
		return users.get(name);
	}

	public void sendMessage(User user, String message) {
		for(User u : users.values()){
			//mejora3
			//u.newMessage(this, user, message);
			chatManager.getExecutor(u).execute(()->{u.newMessage(this, user, message);});
		}
	}

	public void close() {
		this.chatManager.closeChat(this);
	}
}
