package TrueAvarus.UNSF.Hullmods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.dunno.HullMods.UNSF_ASGARD_HYPERDRIVE;
import static TrueAvarus.UNSF.dunno.HullMods.UNSF_ZPM_DRIVE;

public class unsf_asgardhyperdrive extends BaseLogisticsHullMod {

    private static final String UNAPPLICABLE_REASON = "Dont cheat";

	private static final int BURN_LEVEL_BONUS = 1;
    private static final float CORONA_EFFECT = 50f;

    private static final float SENSOR_PROFILE = 25f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.AUGMENTEDENGINES);
        BLOCKED.add("Pro_augmentedengines");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_ASGARD_HYPERDRIVE);
        }
    }
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyPercent(id, -CORONA_EFFECT);
        if (hullSize.ordinal() < HullSize.CRUISER.ordinal()) {
            stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
        } else {
            stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS + 1);
            stats.getSensorProfile().modifyPercent(id, SENSOR_PROFILE);
        }
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> "" + BURN_LEVEL_BONUS;
            case 1 -> CORONA_EFFECT + "%";
            case 2 -> SENSOR_PROFILE + "%";
            default -> null;
        };
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship != null && ship.getVariant() != null && ship.getShield() != null
            && !ship.getVariant().hasHullMod(UNSF_ZPM_DRIVE);
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return "Unknown reason";
        if (ship.getShield() == null) return "Ship does not have a shield.";

        // Return reason if the specific incompatible hullmod is present
        if (ship.getVariant().hasHullMod(UNSF_ZPM_DRIVE)) {
            return UNAPPLICABLE_REASON;
        }
        // If no issues found
        return null;
    }







}


