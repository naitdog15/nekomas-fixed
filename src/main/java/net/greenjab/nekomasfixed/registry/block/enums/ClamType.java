package net.greenjab.nekomasfixed.registry.block.enums;
import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum ClamType implements StringRepresentable {
	REGULAR("regular"),
	BLUE("blue"),
	PINK("pink"),
	PURPLE("purple");

	private final String name;

	public static final Codec<ClamType> CODEC = StringRepresentable.fromEnum(ClamType::values);

	ClamType(final String name) {
		this.name = name;
	}

	@Override
	public String getSerializedName() {
		return this.name;
	}

}