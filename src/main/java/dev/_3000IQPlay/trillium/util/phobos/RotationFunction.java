package dev._3000IQPlay.trillium.util.phobos;

@FunctionalInterface
public interface RotationFunction
{
    float[] apply(double x, double y, double z, float yaw, float pitch);

}