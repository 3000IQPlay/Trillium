package dev._3000IQPlay.trillium.event.events;

import dev._3000IQPlay.trillium.event.EventStage;
import dev._3000IQPlay.trillium.util.phobos.SafeRunnable;
import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

import java.util.ArrayDeque;
import java.util.Deque;

public class PacketEvent extends EventStage {
    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public <T extends Packet<?>> T getPacket() {
        return (T) this.packet;
    }

    @Cancelable
    public static class Send
            extends PacketEvent {
        public Send(Packet<?> packet) {
            super(packet);
        }
    }

    @Cancelable
    public static class Receive extends PacketEvent {
        private final Deque<Runnable> postEvents = new ArrayDeque<>();

        public Receive(Packet<?> packet) {
            super(packet);
        }

        public void addPostEvent(SafeRunnable runnable) {
            postEvents.add(runnable);
        }


        public Deque<Runnable> getPostEvents() {
            return postEvents;
        }
    }

    @Cancelable
    public static class SendPost extends PacketEvent {
        public SendPost(Packet<?> packet) {
            super(packet);
        }
    }

    @Cancelable
    public static class ReceivePost extends PacketEvent {
        public ReceivePost(Packet<?> packet) {
            super(packet);
        }
    }
}