package sample.websocket;

import org.json.JSONObject;

/**
 * Created by andrey on 27.04.17.
 */
public class Message {
    public enum messageType {step}

    messageType type;
    private JSONObject content;

    public messageType getType() {
        return type;
    }

    public JSONObject getContent() {
        return content;
    }

    public Message(String textmessage) {
        content = new JSONObject(textmessage);
        try {
            type = messageType.valueOf(content.get("type").toString());
        } catch (IllegalArgumentException e) {
            type = null;
        }
        content.remove("type");
    }
}
