package TrueAvarus.UNSF.gaylammas;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class unsf_primer extends BaseHullMod {

    private static final String PRIMARY_HULLMOD_ID = "unsf_zpm_hm2"; // Replace with the actual hullmod ID to check for
    private static final String HULLMOD_1 = "unsf_zpm_shieldbooster"; // Replace with the actual hullmod ID to remove
    private static final String HULLMOD_2 = "unsf_zpm_drivebooster"; // Replace with the actual hullmod ID to remove
    private static final String HULLMOD_3 = "unsf_zpm_beambooster"; // Replace with the actual hullmod ID to remove
    private static final String HULLMOD_4 = "unsf_zpm_systembooster"; // Replace with the actual hullmod ID to remove
    private static final String SELF_REMOVING_HULLMOD_ID = "unsf_primer"; // Replace with the actual hullmod ID for itself

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();
        if (ship == null || ship.getVariant() == null) return; // Ensure ship and variant are not null

        boolean hasPrimaryHullmod = ship.getVariant().hasHullMod(PRIMARY_HULLMOD_ID);

        if (!hasPrimaryHullmod) {
            // Remove the three other hullmods if the primary hullmod is not present
            removeHullModIfPresent(ship, HULLMOD_1);
            removeHullModIfPresent(ship, HULLMOD_2);
            removeHullModIfPresent(ship, HULLMOD_3);
            removeHullModIfPresent(ship, HULLMOD_4);
        }
        else{

        }

        // Check if all four hullmods are removed
        boolean allHullmodsRemoved = !ship.getVariant().hasHullMod(PRIMARY_HULLMOD_ID)
                && !ship.getVariant().hasHullMod(HULLMOD_1)
                && !ship.getVariant().hasHullMod(HULLMOD_2)
                && !ship.getVariant().hasHullMod(HULLMOD_3)
                && !ship.getVariant().hasHullMod(HULLMOD_4);

        if (allHullmodsRemoved) {
            // Remove this hullmod if all four hullmods are removed
            removeHullModIfPresent(ship, SELF_REMOVING_HULLMOD_ID);
        }
    }

    private void removeHullModIfPresent(ShipAPI ship, String hullmodId) {
        if (ship != null && ship.getVariant() != null && ship.getVariant().hasHullMod(hullmodId)) {
            ship.getVariant().removeMod(hullmodId);
        }
    }
}
