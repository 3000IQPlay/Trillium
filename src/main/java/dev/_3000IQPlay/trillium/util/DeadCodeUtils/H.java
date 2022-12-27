package dev._3000IQPlay.trillium.util.DeadCodeUtils;
import net.minecraft.entity.Entity;

import com.google.common.base.Predicate;

import javax.annotation.Nullable;

public class H implements Predicate {

    @Override
    public boolean apply(@Nullable Object input) {
        Entity entity = (Entity) input;
        return entity != null && entity.canBeCollidedWith();
    }
}