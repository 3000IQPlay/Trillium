package dev._3000IQPlay.trillium.util.phobos;

import net.minecraft.util.math.Vec3i;

public interface GeoCache
{
    void cache();

    int getRadius(double radius);

    Vec3i get(int index);

    Vec3i[] array();

}