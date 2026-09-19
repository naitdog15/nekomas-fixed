package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.TooltipProvider;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

import java.util.function.Consumer;

public record ComboComponent(int multiplier) implements TooltipProvider {
	public static final Codec<ComboComponent> CODEC = Codec.INT.xmap(ComboComponent::new, ComboComponent::multiplier);
	public static final StreamCodec<ByteBuf, ComboComponent> PACKET_CODEC = ByteBufCodecs.VAR_INT.map(ComboComponent::new, ComboComponent::multiplier);

	@Override
	public void addToTooltip(Item.TooltipContext context, Consumer<Component> textConsumer, TooltipFlag type, DataComponentGetter components) {
		StringBuilder string = new StringBuilder();
		for (int i = 1; i <= 3;i++) string.append((i * multiplier)).append(i<3?"%, ":"%");
		string.append(" ... ");
		string.append((10 * multiplier)).append("%");
		textConsumer.accept(Component.translatable("component.nekomasfixed.combo", string.toString()).withStyle(ChatFormatting.GRAY));
		textConsumer.accept(Component.translatable("component.nekomasfixed.duel_wield").withStyle(ChatFormatting.GRAY));
	}
}