package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;

public class unsf_NeutrinoIonreactor extends BaseLogisticsHullMod {
	public static float MAINTENANCE_MULT = 0.6f;
	public static float REPAIR_RATE_BONUS = 60f;
	public static float CR_RECOVERY_BONUS = 60f;
	public static float FLUX_BONUS = 35f;
	public static float FLUX_DISIP = 25f;
	public static float MAXSPEED_BONUS = 1.2f;

	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		stats.getSuppliesPerMonth().modifyMult(id, MAINTENANCE_MULT);
		stats.getFuelUseMod().modifyMult(id, MAINTENANCE_MULT);
		stats.getFluxCapacity().modifyPercent(id, FLUX_BONUS);
		stats.getFluxDissipation().modifyPercent(id, FLUX_DISIP);
		stats.getBaseCRRecoveryRatePercentPerDay().modifyPercent(id, CR_RECOVERY_BONUS);
		stats.getRepairRatePercentPerDay().modifyPercent(id, REPAIR_RATE_BONUS);
		stats.getMaxSpeed().modifyMult(id,1 * MAXSPEED_BONUS);
	}

	
	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
		if (index == 0) return "" + Math.round((MAXSPEED_BONUS) * 100f) + "%";
		if (index == 1) return "" + Math.round((1f - MAINTENANCE_MULT) * 100f) + "%";
		if (index == 2) return "" + Math.round(FLUX_BONUS) + "%";
		if (index == 3) return "" + Math.round(FLUX_DISIP) + "%";
		if (index == 4) return "" + Math.round(CR_RECOVERY_BONUS) + "%";
		return null;
	}

	
}







