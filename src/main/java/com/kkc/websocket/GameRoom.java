package com.kkc.websocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.kkc.HangMan;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/GameRoom")
public class GameRoom {

	public static HangMan game;
	private static HashMap<Session, String> sessionMap;
	static {
		game = new HangMan();
		sessionMap = new HashMap<Session, String>();
		System.out.println("gameRoom");
	}
	
	@OnOpen
	public void echoOpen(Session session) {
		String[] params = new String[1];
		try {
			params = URLDecoder.decode(session.getQueryString(), "UTF-8").split("&");
		} 
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if(params.length != 1) {
			try {
				session.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if(!game.isStart()) {
			try {
				session.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		if(!sessionMap.containsKey(session)) {
			sessionMap.put(session, params[0].split("=")[1]);
			try {
				if(session.isOpen())
					session.getBasicRemote().sendText(getJSON("", "", false));
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    @OnMessage
    public void echoTextMessage(Session session, String msg, boolean last) { 
    	if(!game.isStart())
    		return;
    	if(game.getStep() >= game.getMaxStep()) {
    		return;
    	}
    	if(msg.equals(""))
    		return;
    	System.out.println(msg);
    	boolean isCorrect = false;
    	if(msg.length() == 1) {
    		char c = msg.charAt(0);
    		if(c >= 12593 && c <= 12643) {
    			if(!game.isUsedAlphabet(msg)) {
    				if(!game.isCorrectAlphabet(msg)) {
        				game.setLife(game.getLife() - 1);
        			}
    			}
    		}
    		else {
    			isCorrect = game.isCorrectAnswer(msg);
    		}
    	}
    	else {
    		isCorrect = game.isCorrectAnswer(msg);
    	}
    	String text = getJSON(msg, sessionMap.get(session), isCorrect);
    	for(Session s : sessionMap.keySet()) {
    		try {
    			if(s.isOpen())
    				s.getBasicRemote().sendText(text);
    		} 
    		catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	
    	if(isCorrect) {
    		game.nextStep();
    		game.putWinner(sessionMap.get(session));
    		schedule();
    	}
    	else if(game.getLife() <= 0) {
    		game.nextStep();
    		schedule();
    	}
    }
    
    @OnClose
    public void onClose(Session session) {
    	if(sessionMap.containsKey(session)) {
    		sessionMap.remove(session);
    	}
    }
    
    private String getJSON(String msg, String sender, boolean isCorrect) {
		StringBuffer sb = new StringBuffer("");
		sb.append("{");
		sb.append("\"type\": \"game\", ");
		sb.append("\"sender\": \"" + sender + "\", ");
		sb.append("\"content\": \"" + msg + "\", ");
		sb.append("\"question\": \"" + game.getQuestion() + "\", ");
//		sb.append("\"isStart\": \"" + game.isStart() + "\", ");
		sb.append("\"maxStep\": \"" + game.getMaxStep() + "\", ");
		sb.append("\"nowStep\": \"" + game.getStep() + "\", ");
		sb.append("\"life\": \""+ game.getLife() + "\", ");
		sb.append("\"isCorrect\": \""+ isCorrect + "\", ");
		sb.append("\"correctAlphabet\": [");
		int count = 0;
		for(String[] str : game.getCorrectAlphabet()) {
			for(String s : str) {
				sb.append("\"" + s + "\"");
				if(game.getCorrectAlphabet().size() * 3 > ++count) 
					sb.append(", ");
				else
					sb.append("], ");
			}
		}
		sb.append("\"usedAlphabet\": [");
		for(int i = 0; i < game.getUsedAlphabet().size(); i++) {
			String s = game.getUsedAlphabet().get(i);
			sb.append("\"" + s + "\"");
			if(i < game.getUsedAlphabet().size() - 1) 
				sb.append(", ");
		}
		sb.append("]}");
		return sb.toString();
	}
    
    private String getEndGameJSON() {
		StringBuffer sb = new StringBuffer("");
		sb.append("{");
		sb.append("\"type\": \"end\", ");
		sb.append("\"ranking\": [");
		ArrayList<String> ranking = game.getRanking();
		for(int i = 0; i < ranking.size(); i++) {
			String s = ranking.get(i);
			sb.append("\"" + s + "\"");
			if(i == ranking.size() - 1) 
				sb.append("]");
			else
				sb.append(", ");
		}
		sb.append("}");
		return sb.toString();
	}
    
    private void schedule() {
    	ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
		scheduler.schedule(() -> {
			String text = !game.isStart() && game.getStep() >= game.getMaxStep() ? getEndGameJSON() : getJSON("", "", false);
			for(Session s : sessionMap.keySet()) {
				if(s.isOpen()) {
					try {
						s.getBasicRemote().sendText(text);
		    		} 
		    		catch (IOException e) {
		    			e.printStackTrace();
		    		}
				}
	    	}
		}, 5, TimeUnit.SECONDS);
    }
}
