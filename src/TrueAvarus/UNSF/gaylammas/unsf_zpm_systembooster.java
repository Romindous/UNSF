package TrueAvarus.UNSF.gaylammas;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class unsf_zpm_systembooster extends BaseHullMod {
    private static final String DEPENDENCY_HULLMOD_ID = "unsf_zpm_hm2"; // Replace with your specific hullmod ID

    public static final float SUPPLY_CONSUM_REDUCTION = 0.8f;
    public static final float FLUX_CAPACITY = 1.5f;
    public static final float FLUX_DISSIPATION = 1.2f;


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getSuppliesPerMonth().modifyMult(id, 1- SUPPLY_CONSUM_REDUCTION);
        stats.getFluxCapacity().modifyMult(id,1 * FLUX_CAPACITY);
        stats.getFluxDissipation().modifyMult(id,1 * FLUX_DISSIPATION);
        stats.getMaxCombatReadiness().modifyMult(id,1 * 1.5f);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color good = Misc.getPositiveHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();

        tooltip.setBulletedListMode(" - ");
        tooltip.addPara("Decreses supply consumption by %s", opad, good, Math.round(SUPPLY_CONSUM_REDUCTION * 100f) + "%");
        tooltip.addPara("Increases flux capacity by %s", opad, good, Math.round(FLUX_CAPACITY * 100f) + "%");
        tooltip.addPara("Increases flux dissipation by %s", opad, good, Math.round(FLUX_DISSIPATION * 100f) + "%");
        tooltip.setBulletedListMode(null);
    }
    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null || ship.getVariant() == null) return false;

        // Check if the ship has the specific dependency hullmod
        if (!ship.getVariant().hasHullMod(DEPENDENCY_HULLMOD_ID)) {
            return false; // This hullmod cannot be applied if the dependency hullmod is not present
        }

        // If the ship has the required hullmod, this hullmod can be applied
        return true;
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


}
