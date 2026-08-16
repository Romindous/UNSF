package TrueAvarus.UNSF.Hullmods;

import TrueAvarus.UNSF.Utils.Format;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class TempestFuelHolds extends BaseLogisticsHullMod {

    private static final float SHIP_STATS_MULTIPLIER = 15f;
    private static final float FUEL_MODIFIER = 2500f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getFuelMod().modifyFlat(id, FUEL_MODIFIER);
        stats.getFuelUseMod().modifyPercent(id, SHIP_STATS_MULTIPLIER);
        stats.getFluxCapacity().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
        stats.getFluxDissipation().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
        stats.getArmorDamageTakenMult().modifyPercent(id, SHIP_STATS_MULTIPLIER);
        stats.getShieldDamageTakenMult().modifyPercent(id, SHIP_STATS_MULTIPLIER);
        stats.getMaxSpeed().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
        stats.getTurnAcceleration().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
        stats.getMaxTurnRate().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
        stats.getAcceleration().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
        stats.getDeceleration().modifyPercent(id, -SHIP_STATS_MULTIPLIER);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.setBulletedListMode("• ");
        tooltip.addPara("Increases the fuel capacity by %s.", Format.OPAD, Format.GOOD, (int) FUEL_MODIFIER + "");
        tooltip.addPara("Makes the ship's overall stats worse by %s.", Format.PAD, Format.BAD, (int) SHIP_STATS_MULTIPLIER + "%");
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