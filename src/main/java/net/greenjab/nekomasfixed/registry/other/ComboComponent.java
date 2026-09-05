package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.List;

// 1.20.1 has no default-component mechanism, so the sickle bakes its own starting value
// (ModItemSettings#sickleDefaultCombo); this record only holds a stack's override
public record ComboComponent(int multiplier) {
    public static final Codec<ComboComponent> CODEC =
            Codec.INT.xmap(ComboComponent::new, ComboComponent::multiplier);

    // ramp shown is steps 1-3 then step 10, where the combo caps
    public List<Component> tooltipLines() {
        StringBuilder ramp = new StringBuilder();
        for (int step = 1; step <= 3; step++) {
            ramp.append(step * this.multiplier).append(step < 3 ? "%, " : "%");
        }
        ramp.append(" ... ").append(10 * this.multiplier).append("%");
        return List.of(
                Component.translatable("component.nekomasfixed.combo", ramp.toString()).withStyle(ChatFormatting.GRAY),
                Component.translatable("component.nekomasfixed.duel_wield").withStyle(ChatFormatting.GRAY)
        );
    }
}
