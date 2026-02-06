package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class unsf_asgardshield extends BaseHullMod {
	private static final String INCOMPATIBLE_HULLMOD_ID = "unsf_zpm_shieldbooster"; // Replace with your specific hullmod ID
	private static final String UNAPPLICABLE_REASON = "Dont cheat";
	public static float PIERCE_MULT = 0.5f;
	public static float SHIELD_BONUS = 35f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().modifyMult(id, 1f - SHIELD_BONUS * 0.01f);
		stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, PIERCE_MULT);		
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) SHIELD_BONUS + "%";
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
