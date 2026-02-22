package TrueAvarus.UNSF.gaylammas;

import TrueAvarus.UNSF.dunno.Format;
import TrueAvarus.UNSF.dunno.HullMods;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class unsf_zpm_beambooster extends BaseHullMod {
    public static final float BEAM_BOOSTER = 25f;
    public static final float ENERGY_FLUX = 15f;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBeamWeaponDamageMult().modifyPercent(id, BEAM_BOOSTER);
        stats.getBeamWeaponTurnRateBonus().modifyPercent(id, BEAM_BOOSTER);
        stats.getBeamWeaponRangeBonus().modifyPercent(id, BEAM_BOOSTER);
        stats.getEnergyWeaponFluxCostMod().modifyPercent(id, -ENERGY_FLUX);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.setBulletedListMode("• ");
        tooltip.addPara("Increases most stats of beam weapons by %s.", Format.OPAD, Format.GOOD, (int) BEAM_BOOSTER + "%");
        tooltip.addPara("Decreases energy weapon flux use by %s.", Format.OPAD, Format.GOOD, (int) ENERGY_FLUX + "%");
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

