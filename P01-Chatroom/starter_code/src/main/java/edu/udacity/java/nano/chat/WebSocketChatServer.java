package edu.udacity.java.nano.chat;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.EncodeException;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * WebSocket Server
 *
 * @see ServerEndpoint WebSocket Client
 * @see Session   WebSocket Session
 */

@Component
@ServerEndpoint("/chat")
public class WebSocketChatServer {

    /**
     * All chat sessions.
     */
    private static Map<String, Session> onlineSessions = new ConcurrentHashMap<>();

    private static void sendMessageToAll(String msg) throws IOException, EncodeException {
        //TODO: add send message method.
    	System.out.println("sendMessageToAll : " + msg);
    	//broadcast the message
        for (String sessionId : onlineSessions.keySet()) {
              Session session = onlineSessions.get(sessionId);
              session.getBasicRemote().sendText(msg);
        }

    }
    /**
     * Open connection, 1) add session, 2) add user.
     */
    @OnOpen
    public void onOpen(Session session) {
        //TODO: add on open connection.
    	System.out.println(String.format("%s joined the chat room.", session.getId()));
        onlineSessions.put(session.getId(), session);
    	JSONObject numberOnlines = new JSONObject();
    	numberOnlines.put("onlineCount", onlineSessions.size());
    	numberOnlines.put("type", "NOTSPEAK");
    	try {
			onMessage(session, numberOnlines.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Send message, 1) get username and session, 2) send message to all.
     * @throws EncodeException 
     * @throws IOException 
     */
    @OnMessage
    public void onMessage(Session session, String jsonStr) throws IOException, EncodeException {
        //TODO: add send message.
    	JSONObject jsonObject = new JSONObject(jsonStr);
    	if(Strings.isBlank(jsonObject.optString("type"))) {
        	jsonObject.put("type", "SPEAK");
        	int onlineCount = onlineSessions.size();
        	jsonObject.put("onlineCount", onlineCount);
    	}
    	
    	sendMessageToAll(jsonObject.toString());
    }

    /**
     * Close connection, 1) remove session, 2) update user.
     * @throws EncodeException 
     * @throws IOException 
     */
    @OnClose
    public void onClose(Session session) throws IOException, EncodeException {
        //TODO: add close connection.    	
    	onlineSessions.remove(session.getId());
    	JSONObject numberOnlines = new JSONObject();
    	numberOnlines.put("onlineCount", onlineSessions.size());
    	numberOnlines.put("type", "NOTSPEAK");
    	
    	try {
			onMessage(session, numberOnlines.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EncodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /**
     * Print exception.
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

}
