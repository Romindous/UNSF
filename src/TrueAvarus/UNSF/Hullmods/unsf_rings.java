package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class unsf_rings extends BaseHullMod {

	public static final float GROUND_SUPPORT_BONUS = 300f;
	public static final float MEDEVAC_MODUS = -40f;
	@Override
	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, GROUND_SUPPORT_BONUS);
		stats.getDynamic().getMod(Stats.PLANETARY_OPERATIONS_CASUALTIES_MULT).modifyPercent(id, MEDEVAC_MODUS);
	}



	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
		if (index == 0) return "" + (int) GROUND_SUPPORT_BONUS;
		if (index == 1) return "" + (int) MEDEVAC_MODUS * -1 ;
		return null;
	}
}




