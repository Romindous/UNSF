package TrueAvarus.UNSF.gaylammas;

import java.awt.*;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;



public class unsf_zpm_shieldbooster extends BaseHullMod {
    // Fully opaque (0 is fully transparent, 255 is fully opaque)
    private static final String DEPENDENCY_HULLMOD_ID = "unsf_zpm_hm2"; // Replace with your specific hullmod ID

    private static final int ALPHA = 150;
    public static final float PIERCE_MULT = 0.0001f;
    public static final float SHIELD_BONUS = 0.6f;
    public static final float SHIELD_ARCBONUS = 0.5f;
    public static final float SHIELD_UNFOLDSPEED = 0.5f;



    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_BONUS);
        stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, PIERCE_MULT);
        stats.getShieldArcBonus().modifyMult(id, 1f + SHIELD_ARCBONUS);
        stats.getShieldUnfoldRateMult().modifyMult(id,1 * SHIELD_UNFOLDSPEED);

    }

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        // Check if the ship is alive
        if (!ship.isAlive()) return;

        // Set the shield color to white with the specified alpha value
        ship.getShield().setInnerColor(new Color(255, 255, 255, ALPHA));
    }
    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color good = Misc.getPositiveHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();

        tooltip.setBulletedListMode(" - ");
        tooltip.addPara("Increses durability of shields by %s", opad, good, Math.round(SHIELD_BONUS * 100f-100) + "%");
        tooltip.addPara("Changes color of shield to white", opad, good);
        tooltip.addPara("Increases shield arc to 360", opad, good);
        tooltip.addPara("Increases shield unfold speed", opad, good);
        tooltip.setBulletedListMode(null);
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
    }








