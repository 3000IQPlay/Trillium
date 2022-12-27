package dev._3000IQPlay.trillium.event.events;


import dev._3000IQPlay.trillium.event.EventStage;

public class ChorusEvent extends EventStage
{
    private final double chorusX;
    private final double chorusY;
    private final double chorusZ;

    public ChorusEvent(final double x,  final double y,  final double z) {
        this.chorusX = x;
        this.chorusY = y;
        this.chorusZ = z;
    }

    public double getChorusX() {
        return this.chorusX;
    }

    public double getChorusY() {
        return this.chorusY;
    }

    public double getChorusZ() {
        return this.chorusZ;
    }
}