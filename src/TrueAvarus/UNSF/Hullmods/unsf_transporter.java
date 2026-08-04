package TrueAvarus.UNSF.Hullmods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.DynamicStatsAPI;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.Constants.HullMods.UNSF_ASGARD_TRANS;

public class unsf_transporter extends BaseHullMod {

	public static final float CREW_SAVED = 90f;
    public static final float FIGHTER_REARM = 20f;

    public static final float FLUX_CAPACITY = 10f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.RECOVERY_SHUTTLES);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_ASGARD_TRANS);
        }
    }

	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        final DynamicStatsAPI dynamic = stats.getDynamic();
        dynamic.getStat(Stats.FIGHTER_CREW_LOSS_MULT).modifyPercent(id, -CREW_SAVED);
        dynamic.getStat(Stats.FIGHTER_REARM_TIME_MULT).modifyPercent(id, -FIGHTER_REARM);
        stats.getFighterRefitTimeMult().modifyPercent(id, -FIGHTER_REARM);
        if (!isSMod(stats)) stats.getFluxCapacity().modifyPercent(id, -FLUX_CAPACITY);
	}

/*Reduces the casualties suffered by fighter pilots by %s. Also lowers fighter refit time by %s, but uses up %s of the ship's flux capacity.*/
	public String getDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return (int) CREW_SAVED + "%";
        if (index == 1) return (int) FIGHTER_REARM + "%";
        if (index == 2) return (int) FLUX_CAPACITY + "%";
        return null;
    }
	
	public boolean isApplicableToShip(ShipAPI ship) {
        return ship != null && !ship.getVariant().hasHullMod(HullMods.AUTOMATED)
            && ship.getMutableStats().getNumFighterBays().getModifiedValue() > 0;
    }
	
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship != null && ship.getVariant().hasHullMod(HullMods.AUTOMATED)) {
			return "Can not be installed on automated ships";
		}
		return "Ship does not have fighter bays";
	}
}




