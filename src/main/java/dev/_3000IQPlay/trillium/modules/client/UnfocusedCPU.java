package dev._3000IQPlay.trillium.modules.client;

import dev._3000IQPlay.trillium.modules.Module;

public class UnfocusedCPU extends Module {
	private static UnfocusedCPU instance;
    public UnfocusedCPU() {
        super("UnfocusedCPU", "Usefull thingy", Module.Category.CLIENT, true, false, false);
		instance = this;
    }
	
	public static UnfocusedCPU getInstance() {
        if (instance == null) {
            instance = new UnfocusedCPU();
        }
        return instance;
    }
}