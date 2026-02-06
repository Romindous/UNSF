package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class unsf_asgardhyperdrive extends BaseLogisticsHullMod {

	private static final String INCOMPATIBLE_HULLMOD_ID = "unsf_zpm_drivebooster"; // Replace with your specific hullmod ID
	private static final String UNAPPLICABLE_REASON = "Dont cheat";
	private static int BURN_LEVEL_BONUS = 3;
	
//	private static final int STRENGTH_PENALTY = 50;
//	private static final int PROFILE_PENALTY = 50;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
//		stats.getSensorProfile().modifyPercent(id, PROFILE_PENALTY);
//		stats.getSensorStrength().modifyMult(id, 1f - STRENGTH_PENALTY * 0.01f);
		stats.getMaxBurnLevel().modifyFlat(id,BURN_LEVEL_BONUS);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + BURN_LEVEL_BONUS;
//		if (index == 1) return "" + STRENGTH_PENALTY + "%";
//		if (index == 2) return "" + PROFILE_PENALTY + "%";
		return null;
	}
	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		if (ship == null || ship.getVariant() == null) return false;
		if (ship.getShield() == null) return false;
		// Check if the ship has the specific incompatible hullmod
		if (ship.getVariant().hasHullMod(INCOMPATIBLE_HULLMOD_ID)) {
			return false; // This hullmod cannot be applied if the specific hullmod is present
		}
		// If all checks pass, the hullmod can be applied
		return true;
	}

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship == null || ship.getVariant() == null) return "Unknown reason";
		if (ship.getShield() == null) return "Ship does not have a shield.";

		// Return reason if the specific incompatible hullmod is present
		if (ship.getVariant().hasHullMod(INCOMPATIBLE_HULLMOD_ID)) {
			return UNAPPLICABLE_REASON;
		}
		// If no issues found
		return null;
	}







}


