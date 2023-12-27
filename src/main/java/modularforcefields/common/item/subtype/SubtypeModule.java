package modularforcefields.common.item.subtype;

import electrodynamics.api.ISubtype;

public enum SubtypeModule implements ISubtype {
	manipulationscale,
	manipulationtranslate,
	shapecube,
	shapehemisphere,
	shapepyramid,
	shapesphere,
	upgradeantifriendly,
	upgradeantihostile,
	upgradeantipersonnel,
	upgradeantispawn,
	upgradeblockaccess,
	upgradeblockalter,
	upgradecapacity,
	upgradecollection,
	upgradecolorchange,
	upgradeconfiscate,
	upgradedisintegration,
	upgradeinterior,
	upgradeshock,
	upgradespeed,
	upgradesponge,
	upgradestabilize,
	upgradestrength;

	@Override
	public String tag() {
		return "module" + name();
	}

	@Override
	public String forgeTag() {
		return "modules/" + name();
	}

	@Override
	public boolean isItem() {
		return true;
	}
}