package TrueAvarus.UNSF.Hullmods;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class unsf_asgardcpu extends BaseHullMod {
	public static final float BALISTIC_RANGE = 150f;
	public static final float PD_RANGE = 200f;
	public static final float BEAM_RANGE = 150f;

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getBallisticWeaponRangeBonus().modifyFlat(id, BALISTIC_RANGE);
		stats.getBeamWeaponRangeBonus().modifyFlat(id, BEAM_RANGE);
		stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);
	}


	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return !ship.getVariant().getHullMods().contains("dedicated_targeting_core") &&
				!ship.getVariant().getHullMods().contains(HullMods.DISTRIBUTED_FIRE_CONTROL) &&
				!ship.getVariant().getHullMods().contains("advancedcore");
	}


	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float pad = 3f;
		float opad = 10f;
		Color good = Misc.getPositiveHighlightColor();
		Color bad = Misc.getNegativeHighlightColor();

		tooltip.setBulletedListMode(" - ");
		tooltip.addPara("Increases the non-beam PD range by %s", opad, good, Math.round(PD_RANGE) + "");
		tooltip.addPara("Increases the balistic range by %s", opad, good, Math.round(BALISTIC_RANGE) + "");
		tooltip.addPara("Increases the beam range by %s", opad, good, Math.round(BEAM_RANGE) + "");

		tooltip.setBulletedListMode(null);
	}
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship.getVariant().getHullMods().contains("dedicated_targeting_core")) {
			return "Incompatible with Dedicated Targeting Core";
		}
		if (ship.getVariant().getHullMods().contains("advancedcore")) {
			return "Incompatible with Advanced Targeting Core";
		}
		if (ship.getVariant().getHullMods().contains(HullMods.DISTRIBUTED_FIRE_CONTROL)) {
			return "Incompatible with Distributed Fire Control";
		}
		return null;
	}
	
}
