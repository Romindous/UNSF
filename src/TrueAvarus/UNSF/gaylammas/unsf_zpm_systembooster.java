package TrueAvarus.UNSF.gaylammas;

import TrueAvarus.UNSF.dunno.Format;
import TrueAvarus.UNSF.dunno.HullMods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class unsf_zpm_systembooster extends BaseHullMod {

    public static final float SUPPLY_CONSUM = 15f;
    public static final float FUEL_CONSUM = 40f;
    public static final float FLUX_CAPACITY = 25f;
    public static final float FLUX_DISSIPATION = 15f;
    public static final float COMBAT_READINESS = 0.1f;
    public static final float SYSTEM_COOLDOWN = 15f;
    public static final int SYSTEM_USES = 1;


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getSuppliesPerMonth().modifyPercent(id, -SUPPLY_CONSUM);
        stats.getFuelUseMod().modifyPercent(id, -FUEL_CONSUM);
        stats.getFluxCapacity().modifyPercent(id, FLUX_CAPACITY);
        stats.getFluxDissipation().modifyPercent(id, FLUX_DISSIPATION);
        stats.getMaxCombatReadiness().modifyFlat(id, COMBAT_READINESS, "ZPM Boost");
        stats.getSystemCooldownBonus().modifyPercent(id, SYSTEM_COOLDOWN);
        stats.getSystemRegenBonus().modifyPercent(id, SYSTEM_COOLDOWN);
        stats.getSystemUsesBonus().modifyFlat(id, SYSTEM_USES);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.setBulletedListMode("• ");
        tooltip.addPara("Decreses supply consumption by %s and fuel use by %s.", Format.OPAD, Format.GOOD, (int) SUPPLY_CONSUM + "%", (int) FUEL_CONSUM + "%");
        tooltip.addPara("Increases flux capacity by %s and dissipation by %s.", Format.OPAD, Format.GOOD, (int) FLUX_CAPACITY + "%", (int) FLUX_DISSIPATION + "%");
        tooltip.addPara("Increases max combat readiness by %s.", Format.OPAD, Format.GOOD, (int) COMBAT_READINESS + "");
        tooltip.addPara("Increases system uses by %s, lowers system cooldown and regen time by %s.", Format.OPAD, Format.GOOD, SYSTEM_USES + "", (int) SYSTEM_COOLDOWN + "%");
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
