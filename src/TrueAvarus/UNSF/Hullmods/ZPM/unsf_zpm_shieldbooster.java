package TrueAvarus.UNSF.Hullmods.ZPM;

import java.awt.*;
import TrueAvarus.UNSF.Constants.Format;
import TrueAvarus.UNSF.Constants.HullMods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;


public class unsf_zpm_shieldbooster extends BaseHullMod {
    // Fully opaque (0 is fully transparent, 255 is fully opaque)
    private static final int ALPHA = 150;
    private static final Color SHIELD_COLOR = new Color(255, 255, 255, ALPHA);
    private static final float SHIELD_PIERCE = 0.0001f;
    private static final float SHIELD_HURT = 15f;
    private static final float SHIELD_UPKEEP = 50f;
    private static final float SHIELD_UNFOLD = 100f;

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getDynamic().getStat(Stats.SHIELD_PIERCED_MULT).modifyMult(id, SHIELD_PIERCE);
        stats.getShieldDamageTakenMult().modifyPercent(id, -SHIELD_HURT);
        stats.getShieldUpkeepMult().modifyPercent(id, -SHIELD_UPKEEP);
        stats.getShieldUnfoldRateMult().modifyPercent(id, SHIELD_UNFOLD);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (ship.getShield() == null) return;
        // Set the shield color to white with the specified alpha value
        ship.getShield().setInnerColor(SHIELD_COLOR);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.setBulletedListMode("• ");
        tooltip.addPara("Increses shield damage resistance by %s.", Format.OPAD, Format.GOOD, (int) SHIELD_HURT + "%");
        tooltip.addPara("Increases shield unfold speed by %s and lowers upkeep by %s.", Format.OPAD, Format.GOOD, (int) SHIELD_UNFOLD + "%", (int) SHIELD_UPKEEP + "%");
        tooltip.addPara("The shield can no longer be pierced by emp arcs.", Format.OPAD, Format.GOOD);
        tooltip.addPara("Changes color of shield to white.", Format.OPAD, Format.GOOD);
        tooltip.setBulletedListMode(null);
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        // This hullmod cannot be applied if the dependency hullmod is not present
        // Check if the ship has the specific dependency hullmod
        // If the ship has the required hullmod, this hullmod can be applied
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
}








