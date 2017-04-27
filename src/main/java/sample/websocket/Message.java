package sample.websocket;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.json.JSONObject;
import sample.game.SnapClient;

/**
 * Created by andrey on 27.04.17.
 */
public class Message {
    private String type;
    private JSONObject content;
    public String getType(){return type;}
    public JSONObject getContent(){return content;}
    public Message(String textmessage){
        content = new JSONObject(textmessage);
        type=content.get("type").toString();
        content.remove("type");
    }
}
