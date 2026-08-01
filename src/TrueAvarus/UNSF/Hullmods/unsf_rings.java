package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class unsf_rings extends BaseHullMod {

	public static final float GROUND_SUPPORT = 50f;
	public static final float RAID_CASUALTIES = 10f;
	public static final float SMOD_MAINTENANCE = 20f;

	@Override
	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getDynamic().getMod(Stats.FLEET_GROUND_SUPPORT).modifyFlat(id, impactOf(hullSize) * GROUND_SUPPORT);
		stats.getDynamic().getMod(Stats.PLANETARY_OPERATIONS_CASUALTIES_MULT).modifyPercent(id, -RAID_CASUALTIES);
		if (isSMod(stats)) {
			stats.getSuppliesPerMonth().modifyPercent(id, -SMOD_MAINTENANCE);
		}
	}

	@Override
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
		if (index == 0) return "" + (impactOf(hullSize) * (int) GROUND_SUPPORT);
		if (index == 1) return (int) RAID_CASUALTIES + "%";
		return null;
	}

	@Override
	public String getSModDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        return index == 0 ? (int) SMOD_MAINTENANCE + "%" : null;
    }

	private int impactOf(final ShipAPI.HullSize size) {
		return switch (size) {
			case DESTROYER -> 2;
			case CRUISER -> 3;
			case CAPITAL_SHIP -> 4;
			default -> 1;
		};
	}
}




