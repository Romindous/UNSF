package TrueAvarus.UNSF.Hullmods;

import java.awt.*;
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

import static TrueAvarus.UNSF.Constants.HullMods.UNSF_ASGARD_HYPERDRIVE;
import static TrueAvarus.UNSF.Constants.HullMods.UNSF_ZPM_DRIVE;

public class unsf_asgardhyperdrive extends BaseLogisticsHullMod {

    private static final String UNAPPLICABLE_REASON = "Dont cheat";

	private static final int BURN_LEVEL_BONUS = 1;
    private static final float CORONA_EFFECT = 50f;

    public static final float FUEL_USE = 15f;

    private static final float SMOD_CORONA_EFFECT = 25f;

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
        ship.getEngineController().getFlameColorShifter()
            .setBase(new Color(60, 200, 240, 255));
    }
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyPercent(id,
            -(CORONA_EFFECT + (isSMod(stats) ? SMOD_CORONA_EFFECT : 0)));
        if (hullSize.ordinal() < HullSize.CRUISER.ordinal()) {
            stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
        } else {
            stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS + 1);
            stats.getFuelUseMod().modifyPercent(id, FUEL_USE);
        }
	}

/*Increases maximum burn level by %s for frigates & destroyers, with an additional +1 for cruisers & capitals.
Restructure of the drive bubble reduces the impact of solar storms and other hazards by %s.
Hyperdrive efficiency scales poorly with size, increasing cruiser & capital fuel use by %s.*/
	public String getDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> BURN_LEVEL_BONUS + "";
            case 1 -> (int) CORONA_EFFECT + "%";
            case 2 -> (int) FUEL_USE + "%";
            default -> null;
        };
    }

/*Further reduces solar and hyperspace hazardous impacts by %s.*/
    public String getSModDescriptionParam(int index, HullSize hullSize) {
        return index == 0 ? (int) SMOD_CORONA_EFFECT + "%" : null;
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


