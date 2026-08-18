package TrueAvarus.UNSF.Hullmods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.Objects.HullMods.UNSF_TRINIUM;

public class unsf_asgardcpu extends BaseHullMod {

    public static final float HYBRID_RANGE = 30f;
    public static final float PD_RANGE = 250f;
    public static final float BEAM_RANGE = 400f;

    public static final float SMOD_WEAPON_RANGE = 40;
    public static final float SMOD_PROJ_SPEED = 15;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.INTEGRATED_TARGETING_UNIT);
        BLOCKED.add(HullMods.DISTRIBUTED_FIRE_CONTROL);
        BLOCKED.add(HullMods.ADVANCED_TARGETING_CORE);
        BLOCKED.add(HullMods.DEDICATED_TARGETING_CORE);
        BLOCKED.add("vice_unified_targeting_core");
        BLOCKED.add("DEX_targetcomp");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_TRINIUM);
        }
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyPercent(id, HYBRID_RANGE);
        stats.getEnergyWeaponRangeBonus().modifyPercent(id, HYBRID_RANGE);
        stats.getBeamWeaponRangeBonus().modifyFlat(id, BEAM_RANGE);
        stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);

        if (isSMod(stats)) {
            stats.getWeaponRangeMultPastThreshold().modifyPercent(id, SMOD_WEAPON_RANGE);
            stats.getProjectileSpeedMult().modifyPercent(id, SMOD_PROJ_SPEED);
        }
    }

/*Increases the non-beam weapon range by %s, non-beam PD range by %s, and beam range by %s.
Can not work in conjunction with %s, %s, and other range related hullmods.*/
    public String getDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> (int) HYBRID_RANGE + "%";
            case 1 -> (int) PD_RANGE + "";
            case 2 -> (int) BEAM_RANGE + "";
            case 3 -> "DTC";
            case 4 -> "ITU";
            default -> null;
        };
    }

/*Increases projectile speed by %s, as well as their distance traveled, past weapon range, by %s.*/
    public String getSModDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> (int) SMOD_WEAPON_RANGE + "%";
            case 1 -> (int) SMOD_PROJ_SPEED + "%";
            default -> null;
        };
    }
}
