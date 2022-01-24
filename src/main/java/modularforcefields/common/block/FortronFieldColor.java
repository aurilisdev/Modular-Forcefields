package modularforcefields.common.block;

import net.minecraft.world.level.material.MaterialColor;

public enum FortronFieldColor {
	ORANGE(MaterialColor.COLOR_ORANGE), MAGENTA(MaterialColor.COLOR_MAGENTA), LIGHT_BLUE(MaterialColor.COLOR_LIGHT_BLUE), YELLOW(MaterialColor.COLOR_YELLOW), LIGHT_GREEN(MaterialColor.COLOR_LIGHT_GREEN), PINK(MaterialColor.COLOR_PINK), GRAY(MaterialColor.COLOR_GRAY), LIGHT_GRAY(MaterialColor.COLOR_LIGHT_GRAY), CYAN(MaterialColor.COLOR_CYAN), PURPLE(MaterialColor.COLOR_PURPLE), BLUE(MaterialColor.COLOR_BLUE), BROWN(MaterialColor.COLOR_BROWN), GREEN(MaterialColor.COLOR_GREEN), RED(MaterialColor.COLOR_RED), BLACK(MaterialColor.COLOR_BLACK);

	private MaterialColor color;

	FortronFieldColor(MaterialColor color) {
		this.color = color;
	}

	public MaterialColor getColor() {
		return color;
	}
}