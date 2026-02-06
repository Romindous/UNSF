package TrueAvarus.UNSF.gaylammas;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoAPI;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;

public class unsf_zpm_hm2 extends BaseHullMod {
    private static final String SPECIAL_ITEM_ID = "unsf_zpm"; // Replace with your special item ID
    private static final String HULLMOD_ID = "unsf_zpm_hm2"; // Replace with your ZPM hullmod ID
    private static final String ADDITIONAL_HULLMOD_1 = "unsf_zpm_shieldbooster"; // Replace with the first additional hullmod ID
    private static final String ADDITIONAL_HULLMOD_2 = "unsf_zpm_drivebooster"; // Replace with the second additional hullmod ID
    private static final String ADDITIONAL_HULLMOD_3 = "unsf_zpm_beambooster"; // Replace with the third additional hullmod ID
    private static final String ADDITIONAL_HULLMOD_6 = "unsf_zpm_systembooster"; // Replace with the third additional hullmod ID
    private static final String ADDITIONAL_HULLMOD_4 = "unsf_deletor"; // Replace with the third additional hullmod ID
    private static final String ADDITIONAL_HULLMOD_5 = "unsf_primer"; // Replace with the third additional hullmod ID

    private static final String ASGARD_HULLMOD_1 = "unsf_asgardshield"; // Replace with the third additional hullmod ID
    private static final String ASGARD_HULLMOD_2 = "unsf_asgardhyperdrive"; // Replace with the third additional hullmod ID


    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        ShipAPI ship = (ShipAPI) stats.getEntity();


        if (ship == null) return; // Early return if the ship is null

        boolean hasZpmHullmod = ship.getVariant().hasHullMod(HULLMOD_ID);
        int shipsWithHullMod = countShipsWithHullMod();
        int specialItemCount = getSpecialItemCount();

        if (hasZpmHullmod) {
            if (specialItemCount >= shipsWithHullMod) {
                // Add additional hullmods if ZPM hullmod is present and enough special items are available
                addHullModIfMissing(ship, ADDITIONAL_HULLMOD_1);
                addHullModIfMissing(ship, ADDITIONAL_HULLMOD_2);
                addHullModIfMissing(ship, ADDITIONAL_HULLMOD_3);
                addHullModIfMissing(ship, ADDITIONAL_HULLMOD_5);
                addHullModIfMissing(ship, ADDITIONAL_HULLMOD_6);
                removeHullModIfPresent(ship, ASGARD_HULLMOD_1);
                removeHullModIfPresent(ship, ASGARD_HULLMOD_2);

            }
            else {addHullModIfMissing(ship, ADDITIONAL_HULLMOD_4); }
            // If there are not enough special items, you might want to handle it but do not remove hullmods here
        } else {
            addHullModIfMissing(ship, ADDITIONAL_HULLMOD_4);
            // If ZPM hullmod is not present, no action is taken here
            return;
        }
    }

    private void addHullModIfMissing(ShipAPI ship, String hullmodId) {
        if (ship.getVariant() != null && !ship.getVariant().hasHullMod(hullmodId)) {
            ship.getVariant().addMod(hullmodId);
        }
    }

    private void removeHullModIfPresent(ShipAPI ship, String hullmodId) {
        if (ship.getVariant() != null && ship.getVariant().hasHullMod(hullmodId)) {
            ship.getVariant().removeMod(hullmodId);
        }
    }

    private int getSpecialItemCount() {
        // Ensure that the sector and player fleet are initialized
        if (Global.getSector() == null || Global.getSector().getPlayerFleet() == null) {
            return 0;  // Return 0 if not properly initialized
        }

        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();
        return (int) cargo.getQuantity(CargoAPI.CargoItemType.SPECIAL, new SpecialItemData(SPECIAL_ITEM_ID, null));
    }

    private int countShipsWithHullMod() {
        // Ensure that the sector and player fleet are initialized
        if (Global.getSector() == null || Global.getSector().getPlayerFleet() == null) {
            return 0;  // Return 0 if not properly initialized
        }

        FleetDataAPI fleetData = Global.getSector().getPlayerFleet().getFleetData();
        if (fleetData == null) {
            return 0;  // Return 0 if fleet data is not available
        }

        int count = 0;
        for (FleetMemberAPI member : fleetData.getMembersListCopy()) {
            if (member != null && member.getVariant() != null) {
                if (member.getVariant().hasHullMod(HULLMOD_ID)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        int shipsWithHullMod = countShipsWithHullMod();
        int specialItemCount = getSpecialItemCount();

        // Check if the ship is valid, has shields, and there are enough special items
        return ship != null
                && ship.getShield() != null
                && specialItemCount >= shipsWithHullMod
                && specialItemCount > 0;  // Ensure there is at least 1 special item
    }

    @Override
    public String getUnapplicableReason(ShipAPI ship) {
        int shipsWithHullMod = countShipsWithHullMod();
        int specialItemCount = getSpecialItemCount();

        if (ship == null) {
            return "Ship is null";
        } else if (ship.getShield() == null) {
            return "Ship has no shields to boost";
        } else if (specialItemCount == 0) {
            return "No ZPMs for installation";
        } else if (specialItemCount < shipsWithHullMod) {
            return "Not enough ZPMs for installation";
        }
        return null; // Applicable if no reason is returned
    }

    @Override
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + countShipsWithHullMod(); // Number of ships with this hullmod
        if (index == 1) return "" + getSpecialItemCount(); // Number of special items required
        if (index == 2) return "" + countShipsWithHullMod(); // Number of special items required

        return null;
    }

}
