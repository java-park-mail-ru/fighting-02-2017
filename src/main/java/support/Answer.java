package support;

import objects.Mutual;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by andrey on 06.04.17.
 */
public final class Answer {
    private Answer(){}
    public static String withObject(String status, Mutual user) {
        final JSONObject answer = new JSONObject();
        answer.put("status", status);
        answer.put("user", user.getJson());
        return answer.toString();
    }

    public static String onlyStatus(String status) {
        final JSONObject answer = new JSONObject();
        answer.put("status", status);
        return answer.toString();
    }

    public static String forLeaders(String status, JSONArray leaders) {
        final JSONObject answer = new JSONObject();
        answer.put("leaders", leaders);
        answer.put("status", status);
        return answer.toString();
    }

    public static JSONObject messageClient(String answer) {
        final JSONObject result = new JSONObject();
        result.put("message", answer);
        return result;
    }

    public static JSONObject messageClient(Long id, ArrayList<String> logins) {
        final JSONObject result = new JSONObject();
        result.put("key", id);
        result.put("first", logins.get(0));
        result.put("second", logins.get(1));
        return result;
    }

    public static JSONObject messageClient(Long id, String first, String second) {
        final JSONObject result = new JSONObject();
        result.put("key", id);
        result.put("first", first);
        result.put("second", second);
        return result;
    }


}
