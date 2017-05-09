package sample.game;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * Created by andrey on 08.05.17.
 */
public class Players {
    private SnapClient earlySnap;
    private ArrayList<String> logins;

    Players(@NotNull ArrayList<String> logins) {
        this.logins = logins;
    }

    public ArrayList<String> getLogins() {
        return logins;
    }

    public void setSnap(@Nullable SnapClient snap) {
        this.earlySnap = snap;
    }


    public @Nullable SnapClient getSnap() {
        return earlySnap;
    }
}
