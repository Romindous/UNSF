package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class TempestCargoHolds extends BaseLogisticsHullMod {
    public static final float SHIP_STATS_MULTIPLIER = 0.10f;
    public static final float CARGO_MODIFIER = 2500f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getCargoMod().modifyFlat(id, CARGO_MODIFIER);
        stats.getFluxDissipation().modifyMult(id, 1f + - SHIP_STATS_MULTIPLIER);
        stats.getArmorDamageTakenMult().modifyMult(id, 1f + SHIP_STATS_MULTIPLIER);
        stats.getSensorProfile().modifyMult(id, 1f + SHIP_STATS_MULTIPLIER);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        float pad = 3f;
        float opad = 10f;
        Color good = Misc.getPositiveHighlightColor();
        Color bad = Misc.getNegativeHighlightColor();

        tooltip.setBulletedListMode(" - ");
        tooltip.addPara("Increases the cargo capacity by %s", opad, good, Math.round(CARGO_MODIFIER) + "");
        tooltip.addPara("Reduces the ship's flux dissipation by %s", pad, bad, Math.round(SHIP_STATS_MULTIPLIER * 100f) + "%");
        tooltip.addPara("Increases the ship's sensor profile by %s", pad, bad, Math.round(SHIP_STATS_MULTIPLIER * 100f) + "%");
        tooltip.addPara("Reduces the ship's armor strength by %s", pad, bad, Math.round(SHIP_STATS_MULTIPLIER * 100f) + "%");
        tooltip.setBulletedListMode(null);
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        return null;  // No reason to restrict this hullmod
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        return true;  // Always applicable
    }
}