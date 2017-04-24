package sample.game;

import jdk.nashorn.api.scripting.JSObject;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;
import support.Coef;

/**
 * Created by andrey on 24.04.17.
 */
public class SnapShot {
    String method;
    String block;
    String target;
    Double hp;
    public @Nullable SnapShot(JSONObject json) throws Exception{
            method = json.get("method").toString();
            block=json.get("block").toString();
            target=json.get("target").toString();
            hp=(Double) json.get("hp");
            System.out.println(method);
            System.out.println(block);
            System.out.println(target);
            System.out.println(hp);
    }

}
