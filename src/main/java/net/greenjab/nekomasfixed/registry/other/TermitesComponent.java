package net.greenjab.nekomasfixed.registry.other;

import com.mojang.serialization.Codec;
import net.greenjab.nekomasfixed.registry.block.entity.TermitehiveBlockEntity;

import java.util.List;

/**
 * REWRITTEN: inherits the same {@code TypedEntityData}/1.21+ dependency
 * TermitehiveBlockEntity.TermiteData carries at {@code TermitehiveBlockEntity.java:265-272}
 * through its {@code CODEC}. Read/written through {@code StackData} under key {@code "termites"},
 * never a 1.21+ data component. Every {@code PACKET_CODEC} on this and the other three record
 * components is deleted, not ported: 1.20.1 syncs the whole stack tag to the client for
 * free.
 */
public record TermitesComponent(List<TermitehiveBlockEntity.TermiteData> termites) {
    public static final Codec<TermitesComponent> CODEC = TermitehiveBlockEntity.TermiteData.LIST_CODEC
            .xmap(TermitesComponent::new, TermitesComponent::termites);
    public static final TermitesComponent DEFAULT = new TermitesComponent(List.of());
}
