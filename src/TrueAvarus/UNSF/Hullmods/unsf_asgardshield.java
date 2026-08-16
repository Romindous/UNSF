package TrueAvarus.UNSF.Hullmods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import TrueAvarus.UNSF.Objects.HullMods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import org.magiclib.util.MagicIncompatibleHullmods;

public class unsf_asgardshield extends BaseHullMod {
	private static final String UNAPPLICABLE_REASON = "Dont cheat";
    public static final float SHIELD_SOFT_FLUX = 40f;
    public static final float SHIELD_ARC = 30f;

    public static final float SHIELD_UPKEEP = 25f;

    public static final float SMOD_SHIELD_ARC = SHIELD_ARC;
    public static final float SMOD_SHIELD_HURT = 5f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.HARDENED_SHIELDS);
        BLOCKED.add(HullMods.EXTENDED_SHIELDS);
        BLOCKED.add("ix_reactive_combat_shields");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, HullMods.UNSF_ASGARD_SHIELD);
        }

        final ShieldAPI shield = ship.getShield();
        if (shield == null) return;
        final MutableShipStatsAPI stats = ship.getMutableStats();
        if (!isSMod(ship)) return;
        stats.getShieldDamageTakenMult().modifyPercent(id, SMOD_SHIELD_HURT);
        if (shield.getType() != ShieldAPI.ShieldType.OMNI) {
            shield.setType(ShieldAPI.ShieldType.OMNI);
        }
    }
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getShieldSoftFluxConversion().modifyFlat(id, SHIELD_SOFT_FLUX * 0.01f);
        stats.getShieldUpkeepMult().modifyPercent(id, SHIELD_UPKEEP);
        stats.getShieldArcBonus().modifyFlat(id, SHIELD_ARC + (isSMod(stats) ? SMOD_SHIELD_ARC : 0));
	}

/*Rewires shield systems to absorb %s of damage taken as soft flux. Also extends the shield arc by %s degrees.
These modifications cause upkeep cost to increase by %s.*/
	public String getDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> (int) SHIELD_SOFT_FLUX + "%";
            case 1 -> (int) SHIELD_ARC + "";
            case 2 -> (int) SHIELD_UPKEEP + "%";
            default -> null;
        };
    }

/*Shield emitters become %s more damage resistant. Converts front emitters to omni, or extends omni emitters by a further %s degrees.*/
    public String getSModDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> (int) SMOD_SHIELD_HURT + "%";
            case 1 -> (int) SMOD_SHIELD_ARC + "";
            default -> null;
        };
    }

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
        return ship != null && ship.getVariant() != null && ship.getShield() != null
            && !ship.getVariant().hasHullMod(HullMods.UNSF_ZPM_SHIELDS);
    }

	@Override
	public String getUnapplicableReason(ShipAPI ship) {
		if (ship == null || ship.getVariant() == null) return "Unknown reason";
		if (ship.getShield() == null) return "Ship does not have a shield.";

		// Return reason if the specific incompatible hullmod is present
		if (ship.getVariant().hasHullMod(HullMods.UNSF_ZPM_SHIELDS)) {
			return UNAPPLICABLE_REASON;
		}
		// If no issues found
		return null;
	}
}
