package modularforcefields.common.block;

import net.minecraft.world.level.material.MapColor;

public enum FortronFieldColor {
	ORANGE(MapColor.COLOR_ORANGE),
	MAGENTA(MapColor.COLOR_MAGENTA),
	LIGHT_BLUE(MapColor.COLOR_LIGHT_BLUE),
	YELLOW(MapColor.COLOR_YELLOW),
	LIGHT_GREEN(MapColor.COLOR_LIGHT_GREEN),
	PINK(MapColor.COLOR_PINK),
	GRAY(MapColor.COLOR_GRAY),
	LIGHT_GRAY(MapColor.COLOR_LIGHT_GRAY),
	CYAN(MapColor.COLOR_CYAN),
	PURPLE(MapColor.COLOR_PURPLE),
	BLUE(MapColor.COLOR_BLUE),
	BROWN(MapColor.COLOR_BROWN),
	GREEN(MapColor.COLOR_GREEN),
	RED(MapColor.COLOR_RED),
	BLACK(MapColor.COLOR_BLACK);

	private MapColor color;

	FortronFieldColor(MapColor color) {
		this.color = color;
	}

	public MapColor getColor() {
		return color;
	}
}