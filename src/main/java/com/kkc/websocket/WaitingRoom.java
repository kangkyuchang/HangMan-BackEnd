package com.kkc.websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.kkc.Authentication;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/WaitingRoom")
public class WaitingRoom {

	private static List<Session> sessionList;
	private static List<String> participant;
	private static Session masterSession;
	
	static {
		sessionList = new LinkedList<Session>();
		participant = new LinkedList<String>();
		masterSession = null;
		System.out.println("waitingRoom");
	}
	
	@OnOpen
	public void echoOpen(Session session) {
		String[] params = new String[2];
		try {
			params = URLDecoder.decode(session.getQueryString(), "UTF-8").split("&");
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(params.length != 2)
			return;
		String name = params[0].split("=")[1];
		if(name.equals("null")) {
			try {
				session.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if(!GameRoom.game.isStart()) {
			if(name.equals("선생님")) {
				String password = params[1].split("=")[1];
				if(password.equals(Authentication.PASSWORD))
					masterSession = session;
			}
			if(!sessionList.contains(session)) {
				for(Session s : sessionList) {
					if(s.isOpen()) {
						try {
							s.getBasicRemote().sendText(getJSON(Arrays.asList(name)));
						} 
						catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
				sessionList.add(session);
				participant.add(name);
				try {
					if(session.isOpen())
						session.getBasicRemote().sendText(getJSON(participant));
				} 
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else {
			if(name.equals("선생님")) {
				String password = params[1].split("=")[1];
				if(password.equals(Authentication.PASSWORD)) {
					masterSession = session;
					sessionList.add(session);
					participant.add(name);
					return;
				}
			}
			try {
				if(session.isOpen())
					session.getBasicRemote().sendText(getStartJSON());
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) {
    	if(masterSession == null)
    		return;
    	if(session != masterSession) 
    		return;
    	if(msg.equals("시작")) {
    		GameRoom.game.setStart(true);
    		for(Session s : sessionList) {
    			if(s.isOpen()) {
					try {
						s.getBasicRemote().sendText(getStartJSON());
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
    		}
    	}
    	else if(msg.equals("재시작")) {
    		GameRoom.game.init();
    		GameRoom.game.setStart(true);
    		for(Session s : sessionList) {
    			if(s.isOpen()) {
					try {
						s.getBasicRemote().sendText(getStartJSON());
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
    		}
    	}
    }
    
    @OnClose
    public void onClose(Session session) {
    	if(sessionList.contains(session)) {
    		int index= sessionList.indexOf(session);
    		for(Session s : sessionList) {
    			if(s == session)
    				continue;
    			if(s.isOpen()) {
					try {
						s.getBasicRemote().sendText(getJSON(participant.get(index)));
					} 
					catch (IOException e) {
						e.printStackTrace();
					}
				}
    		}
    		sessionList.remove(index);
    		participant.remove(index);
    	}
    }
    
    private String getJSON(List<String> list) {
		StringBuffer sb = new StringBuffer("");
		sb.append("{ \"type\": \"join\", ");
		sb.append("\"player\": [");
		for(int i = 0; i < list.size(); i++) {
			String s = list.get(i);
			if(i != 0)
				sb.append(", ");
			sb.append("\"" + s + "\"");
		}
		sb.append("]}");
		return sb.toString();
	}
    
    private String getJSON(String name) {
		StringBuffer sb = new StringBuffer("");
		sb.append("{ \"type\": \"quit\", ");
		sb.append("\"player\": \"" + name + "\"");
		sb.append("}");
		return sb.toString();
	}
    
    private String getStartJSON() {
		StringBuffer sb = new StringBuffer("");
		sb.append("{ \"type\": \"start\" }");
		return sb.toString();
	}
    
//    public static void start() {
//    	StringBuffer sb = new StringBuffer("");
//		sb.append("[");
//		for(String s : list)
//			sb.append("\"" + s + "\", ");
//		sb.append("]");
//    	for(Session s : sessionList) {
//			if(s.isOpen()) {
//				try {
//					s.getBasicRemote().sendText(getJSON(Arrays.asList(name)));
//				} 
//				catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//	}
}
