package TrueAvarus.UNSF.Hullmods.ZPM;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

import static TrueAvarus.UNSF.Objects.HullMods.*;

public class unsf_remover extends BaseHullMod {

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        if (!(stats.getEntity() instanceof final ShipAPI ship) || ship.getVariant() == null) return;

        if (!ship.getVariant().hasHullMod(UNSF_ZPM)) {
            // Remove the three other hullmods if the primary hullmod is not present
            removeHullModIfPresent(ship, UNSF_ZPM_BEAMS);
            removeHullModIfPresent(ship, UNSF_ZPM_DRIVE);
            removeHullModIfPresent(ship, UNSF_ZPM_SHIELDS);
            removeHullModIfPresent(ship, UNSF_ZPM_SYSTEM);
            removeHullModIfPresent(ship, ZPM_PRIMER);
        } else {
            removeHullModIfPresent(ship, UNSF_ZPM);
            removeHullModIfPresent(ship, UNSF_ZPM_BEAMS);
            removeHullModIfPresent(ship, UNSF_ZPM_DRIVE);
            removeHullModIfPresent(ship, UNSF_ZPM_SHIELDS);
            removeHullModIfPresent(ship, UNSF_ZPM_SYSTEM);
            removeHullModIfPresent(ship, ZPM_PRIMER);
        }

        // Check if all four hullmods are removed
        if (!ship.getVariant().hasHullMod(UNSF_ZPM)
            && !ship.getVariant().hasHullMod(UNSF_ZPM_BEAMS)
            && !ship.getVariant().hasHullMod(UNSF_ZPM_DRIVE)
            && !ship.getVariant().hasHullMod(UNSF_ZPM_SHIELDS)
            && !ship.getVariant().hasHullMod(UNSF_ZPM_SYSTEM)
            && !ship.getVariant().hasHullMod(ZPM_PRIMER)) {
            // Remove this hullmod if all four hullmods are removed
            removeHullModIfPresent(ship, ZPM_DELETOR);
        }
    }

    private void removeHullModIfPresent(ShipAPI ship, String hullmodId) {
        if (ship != null && ship.getVariant() != null && ship.getVariant().hasHullMod(hullmodId)) {
            ship.getVariant().removeMod(hullmodId);
        }
    }
}