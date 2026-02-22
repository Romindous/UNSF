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
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.dunno.HullMods.UNSF_ASGARD_SHIELD;
import static TrueAvarus.UNSF.dunno.HullMods.UNSF_ZPM_SHIELDS;

public class unsf_asgardshield extends BaseHullMod {
	private static final String UNAPPLICABLE_REASON = "Dont cheat";
    public static final float SHIELD_HURT = 15f;
    public static final float SHIELD_PIERCE = 50f;
	public static final float SHIELD_UPKEEP = 50f;
    public static final float SHIELD_UNFOLD = 50f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.HARDENED_SHIELDS);
        BLOCKED.add("ix_reactive_combat_shields");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_ASGARD_SHIELD);
        }
    }
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldDamageTakenMult().modifyPercent(id, -SHIELD_HURT);
		stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyPercent(id, -SHIELD_PIERCE);
        stats.getShieldUpkeepMult().modifyPercent(id, SHIELD_UPKEEP);
        stats.getShieldUnfoldRateMult().modifyPercent(id, -SHIELD_UNFOLD);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> (int) SHIELD_HURT + "%";
            case 1 -> (int) SHIELD_PIERCE + "%";
            case 2 -> (int) SHIELD_UPKEEP + "%";
            case 3 -> (int) SHIELD_UNFOLD + "%";
            default -> null;
        };
    }

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
        return ship != null && ship.getVariant() != null && ship.getShield() != null
            && !ship.getVariant().hasHullMod(UNSF_ZPM_SHIELDS);
    }

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship == null || ship.getVariant() == null) return "Unknown reason";
		if (ship.getShield() == null) return "Ship does not have a shield.";

		// Return reason if the specific incompatible hullmod is present
		if (ship.getVariant().hasHullMod(UNSF_ZPM_SHIELDS)) {
			return UNAPPLICABLE_REASON;
		}
		// If no issues found
		return null;
	}


}
