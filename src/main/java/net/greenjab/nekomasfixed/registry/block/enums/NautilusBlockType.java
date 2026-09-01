package net.greenjab.nekomasfixed.registry.block.enums;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum NautilusBlockType implements StringRepresentable {
	REGULAR("regular"),
	ZOMBIE("zombie"),
	CORAL("coral");

	private final String name;

	public static final Codec<NautilusBlockType> CODEC = StringRepresentable.fromEnum(NautilusBlockType::values);

	NautilusBlockType(final String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

}