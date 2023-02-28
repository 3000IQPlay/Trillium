package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.util.phobos.SafeRunnable;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.ArrayDeque;
import java.util.Deque;

public class EventPostMotion extends Event {
    public EventPostMotion() {
    }

    private final Deque<Runnable> postEvents = new ArrayDeque<>();


    public void addPostEvent(SafeRunnable runnable) {
        postEvents.add(runnable);
    }


    public Deque<Runnable> getPostEvents() {
        return postEvents;
    }
}