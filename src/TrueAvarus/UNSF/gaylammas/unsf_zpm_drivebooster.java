package TrueAvarus.UNSF.gaylammas;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class unsf_zpm_drivebooster extends BaseLogisticsHullMod {
    private static final int BURN_LEVEL_BONUS = 3;
    private static final float MAXSPEED_BONUS = 1.4f;
    private static final String DEPENDENCY_HULLMOD_ID = "unsf_zpm_hm2"; // Replace with your specific hullmod ID

//	private static final int STRENGTH_PENALTY = 50;
//	private static final int PROFILE_PENALTY = 50;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
//		stats.getSensorProfile().modifyPercent(id, PROFILE_PENALTY);
//		stats.getSensorStrength().modifyMult(id, 1f - STRENGTH_PENALTY * 0.01f);
        stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS );
        stats.getMaxSpeed().modifyMult(id,1 * MAXSPEED_BONUS);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        // This hullmod cannot be applied if the dependency hullmod is not present
        // Check if the ship has the specific dependency hullmod
        // If the ship has the required hullmod, this hullmod can be applied
        return ship != null && ship.getVariant() != null
            && ship.getVariant().hasHullMod(DEPENDENCY_HULLMOD_ID);
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return "Unknown reason";

        // Return reason if the specific dependency hullmod is not present
        if (!ship.getVariant().hasHullMod(DEPENDENCY_HULLMOD_ID)) {
            return "You need ZPM for this module";
        }

        // If no issues are found, return null (which means the hullmod is applicable)
        return null;
    }


    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color good = Misc.getPositiveHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();

        tooltip.setBulletedListMode(" - ");
        tooltip.addPara("Adds additional bonus of %s to burn speed", opad, good, BURN_LEVEL_BONUS + "");
        tooltip.addPara("Increases ship maximum speed in battle by %s", opad, good, Math.round(MAXSPEED_BONUS * 100f-100f) + "%");
        tooltip.setBulletedListMode(null);
    }
}
