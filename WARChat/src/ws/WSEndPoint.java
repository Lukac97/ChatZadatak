package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;

@Singleton
@ServerEndpoint("/ws/{username}")
@LocalBean
public class WSEndPoint {
	static List<Session> sessions = new ArrayList<Session>();
	static List<String> loggedUsers = new ArrayList<String>();
	
	@OnOpen
	public void onOpen(Session session, @PathParam("username") final String username) {
		if(!sessions.contains(session)) {
			sessions.add(session);
			loggedUsers.add(username);
			String loglist = "";
			for(String user: loggedUsers) {
				loglist = loglist+","+user;
			}
			try{
				for(Session s: sessions) {
			
				s.getBasicRemote().sendText(loglist);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(session.getId());
		}
	}
	
	@OnMessage
	public void echoTextMessage(String msg) {
		try {
			for(Session s: sessions) {
				System.out.println("WSEndPoint: " + msg);
				s.getBasicRemote().sendText(msg);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@OnClose
	public void close(Session session,@PathParam("username") final String username) {
		sessions.remove(session);
		loggedUsers.remove(username);
	}
	
	@OnError
	public void error(Session session, @PathParam("username") final String username, Throwable t) {
		sessions.remove(session);
		loggedUsers.remove(username);
		t.printStackTrace();
	}
}
