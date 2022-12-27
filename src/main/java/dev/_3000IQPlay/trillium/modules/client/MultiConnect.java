package dev._3000IQPlay.trillium.modules.client;


import dev._3000IQPlay.trillium.modules.Module;


import java.util.ArrayList;
import java.util.List;

public class MultiConnect extends Module {

    public MultiConnect() {
        super("MultiConnect", "MultiConnect", Category.CLIENT, true, false, false);
        this.setInstance();
    }
    private static MultiConnect INSTANCE = new MultiConnect();

    public static MultiConnect getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MultiConnect();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }


    public List<Integer> serverData = new ArrayList<>();
}
