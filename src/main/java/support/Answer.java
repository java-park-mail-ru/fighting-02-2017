package support;

import objects.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by andrey on 06.04.17.
 */
public class Answer {
    private String result;

    public Answer() {
    }

    public String getResult() {
        return result;
    }

    public void withObject(String status, User user) {
        final JSONObject answer = new JSONObject();
        answer.put("status", status);
        answer.put("user", user.getJson());
        result = answer.toString();
    }

    public void onlyStatus(String status) {
        final JSONObject answer = new JSONObject();
        answer.put("status", status);
        result = answer.toString();
    }

    public void forLeaders(String status, JSONArray leaders) {
        final JSONObject answer = new JSONObject();
        answer.put("leaders", leaders);
        answer.put("status", status);
        result = answer.toString();
    }


}
