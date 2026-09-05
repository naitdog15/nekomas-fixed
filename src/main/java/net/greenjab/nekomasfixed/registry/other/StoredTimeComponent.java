package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

// presence of the "stored_time" key is what means a clock has a recorded time, not this value -
// 0 is a valid reading, so callers must check StackData.contains, not just read the int
public record StoredTimeComponent(int time) {
    public static final Codec<StoredTimeComponent> CODEC =
            Codec.INT.xmap(StoredTimeComponent::new, StoredTimeComponent::time);

    public String formatted() {
        int hour = time / 1000;
        int min = ((time % 1000) * 60) / 1000;
        return (hour < 10 ? "0" : "") + hour + ":" + (min < 10 ? "0" : "") + min;
    }

    public Component tooltipLine() {
        return Component.translatable("component.nekomasfixed.storedtime", this.formatted())
                .withStyle(ChatFormatting.GRAY);
    }
}
