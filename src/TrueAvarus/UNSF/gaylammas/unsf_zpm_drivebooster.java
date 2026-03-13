package TrueAvarus.UNSF.gaylammas;

import TrueAvarus.UNSF.dunno.Format;
import TrueAvarus.UNSF.dunno.HullMods;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class unsf_zpm_drivebooster extends BaseLogisticsHullMod {
    private static final int BURN_LEVEL = 2;
    private static final float MOVEMENT = 20f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL);
        stats.getMaxSpeed().modifyPercent(id, MOVEMENT);
        stats.getZeroFluxSpeedBoost().modifyPercent(id, MOVEMENT);
        stats.getAcceleration().modifyPercent(id, MOVEMENT);
        stats.getDeceleration().modifyPercent(id, MOVEMENT);
        stats.getMaxTurnRate().modifyPercent(id, MOVEMENT);
        stats.getTurnAcceleration().modifyPercent(id, MOVEMENT);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return ship != null && ship.getVariant() != null
            && ship.getVariant().hasHullMod(HullMods.UNSF_ZPM);
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return "Unknown reason";

        // Return reason if the specific dependency hullmod is not present
        if (!ship.getVariant().hasHullMod(HullMods.UNSF_ZPM)) {
            return "You need a ZPM for this module";
        }

        // If no issues are found, return null (which means the hullmod is applicable)
        return null;
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.setBulletedListMode("• ");
        tooltip.addPara("Adds additional bonus of %s to burn speed.", Format.OPAD, Format.GOOD, BURN_LEVEL + "");
        tooltip.addPara("Increases ship movement stats in battle by %s.", Format.OPAD, Format.GOOD, (int) MOVEMENT + "%");
        tooltip.setBulletedListMode(null);
    }
}
