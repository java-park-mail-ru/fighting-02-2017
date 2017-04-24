package sample.game;

import jdk.nashorn.api.scripting.JSObject;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

/**
 * Created by andrey on 24.04.17.
 */
public class SnapShot {
    enum Method{arm,leg,head}
    enum Block{head,body}
    enum Target{head,body}
    Integer hp;
    Method method;
    Block block;
    Target target;
    public @Nullable SnapShot(JSONObject json) throws Exception{
            method = Method.valueOf(json.get("method").toString());
            block=Block.valueOf(json.get("block").toString());
            target=Target.valueOf(json.get("target").toString());
            hp=(Integer)json.get("hp");
            System.out.println(method);
            System.out.println(block);
            System.out.println(target);
            System.out.println(hp);
    }

}
