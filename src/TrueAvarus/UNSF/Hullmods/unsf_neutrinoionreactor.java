package TrueAvarus.UNSF.Hullmods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.Constants.HullMods.UNSF_REACTOR;

public class unsf_neutrinoionreactor extends BaseLogisticsHullMod {

	private static final float MAINTENANCE = 15f;
    private static final float FLUX_CAPACITY = 10f;
    private static final float FLUX_DISIP = 5f;
    private static final float VENT_RATE = 15f;

    private static final float REPAIR_RATE = 50f;
    private static final float SHIP_SPACE = 75f;
    private static final float EMP_HURT = 25f;

    private static final float SMOD_ARMOR = 10f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.SAFETYOVERRIDES);
        BLOCKED.add(HullMods.UNSTABLE_INJECTOR);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_REACTOR);
        }
    }

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getSuppliesPerMonth().modifyPercent(id, -MAINTENANCE);
		stats.getFuelUseMod().modifyPercent(id, -MAINTENANCE);
		stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY);
		stats.getFluxDissipation().modifyPercent(id, FLUX_DISIP);
        stats.getVentRateMult().modifyPercent(id, VENT_RATE);

        stats.getCargoMod().modifyPercent(id, -SHIP_SPACE);
        stats.getRepairRatePercentPerDay().modifyPercent(id, -REPAIR_RATE);
        stats.getCombatEngineRepairTimeMult().modifyPercent(id, REPAIR_RATE);
        stats.getCombatWeaponRepairTimeMult().modifyPercent(id, REPAIR_RATE);
        stats.getEmpDamageTakenMult().modifyPercent(id, EMP_HURT);

        if (isSMod(stats)) {
            stats.getArmorBonus().modifyPercent(id, -SMOD_ARMOR);
        }
	}

/*Lowers supply demands and fuel consumption by %s, thanks to the reactor's energy management systems.
Augments the ship’s flux capacity by %s and enhances dissipation by %s. Interfacing of the reactor-flux modules increases vent speed by %s, but causes the ship to take %s more EMP damage.
Equipment needed for the reactor both takes up %s of cargo space and results in %s slower ship repairs (in and out of combat).*/
	public String getDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return switch (index) {
            case 0 -> (int) MAINTENANCE + "%";
            case 1 -> (int) FLUX_CAPACITY + "%";
            case 2 -> (int) FLUX_DISIP + "%";
            case 3 -> (int) VENT_RATE + "%";
            case 4 -> (int) EMP_HURT + "%";
            case 5 -> (int) SHIP_SPACE + "%";
            case 6 -> (int) REPAIR_RATE + "%";
            default -> null;
        };
    }

/*Reduces armor by %s to accomodate for reactor integration.*/
    public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return index == 0 ? (int) SMOD_ARMOR + "%" : null;
    }

	
}







