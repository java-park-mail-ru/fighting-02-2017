package sample.websocket;

import org.json.JSONObject;

/**
 * Created by andrey on 27.04.17.
 */
public class Message {
    public enum Type {step}

    Type type;
    private JSONObject content;

    public Type getType() {
        return type;
    }

    public JSONObject getContent() {
        return content;
    }

    public Message(String textmessage) {
        content = new JSONObject(textmessage);
        try {
            type = Type.valueOf(content.get("type").toString());
        } catch (IllegalArgumentException e) {
            type = null;
        }
        content.remove("type");
    }
}
