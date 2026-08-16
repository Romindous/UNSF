package TrueAvarus.UNSF.Hullmods;

import java.util.*;
import TrueAvarus.UNSF.Utils.Format;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.HullModFleetEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.hullmods.BaseLogisticsHullMod;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.Objects.HullMods.UNSF_ASGARD_SENSORS;

public class unsf_asgardsensors extends BaseLogisticsHullMod  implements HullModFleetEffect {

    public static final float MIN_CR = 0.1f;
    public static final String HIGH_RES_SENSORS = "core_HighResSensors";

    private static final Map<HullSize, Float> sensors = new HashMap<>();
    private static final Map<HullSize, Float> combat = new HashMap<>();

    public static final Set<String> BLOCKED = new HashSet<>();
    /*	static {
		mag.put(HullSize.FRIGATE, Global.getSettings().getFloat("baseSensorFrigate"));
		mag.put(HullSize.DESTROYER, Global.getSettings().getFloat("baseSensorDestroyer"));
		mag.put(HullSize.CRUISER, Global.getSettings().getFloat("baseSensorCruiser"));
		mag.put(HullSize.CAPITAL_SHIP, Global.getSettings().getFloat("baseSensorCapital"));
	}*/
    static {
        sensors.put(HullSize.FRIGATE, 120f);
        sensors.put(HullSize.DESTROYER, 170f);
        sensors.put(HullSize.CRUISER, 250f);
        sensors.put(HullSize.CAPITAL_SHIP, 500f);

        combat.put(HullSize.FRIGATE, 1500f);
        combat.put(HullSize.DESTROYER, 2000f);
        combat.put(HullSize.CRUISER, 2500f);
        combat.put(HullSize.CAPITAL_SHIP, 3000f);

        BLOCKED.add(HIGH_RES_SENSORS);
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_ASGARD_SENSORS);
        }
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

        stats.getDynamic().getMod(Stats.HRS_SENSOR_RANGE_MOD).modifyFlat(id, sensors.get(hullSize));

        boolean sMod = isSMod(stats);
        if (sMod) {
            float mag = combat.get(hullSize).intValue();
            stats.getSightRadiusMod().modifyFlat(id, mag);
        }
    }

    public String getSModDescriptionParam(int index, HullSize hullSize) {
        if (index == 0) return "" + combat.get(HullSize.FRIGATE).intValue();
        if (index == 1) return "" + combat.get(HullSize.DESTROYER).intValue();
        if (index == 2) return "" + combat.get(HullSize.CRUISER).intValue();
        if (index == 3) return "" + combat.get(HullSize.CAPITAL_SHIP).intValue();
        return null;
    }
    public String getDescriptionParam(int index, HullSize hullSize) {
        return null;
    }

    public void advanceInCampaign(CampaignFleetAPI fleet) {}
    public boolean withAdvanceInCampaign() {
        return false;
    }
    public boolean withOnFleetSync() {
        return true;
    }

    public void onFleetSync(CampaignFleetAPI fleet) {
        final float modifier = getAdjustedHRSModifier(fleet, null, 0f);
        if (modifier <= 0) fleet.getSensorRangeMod().unmodifyFlat(HIGH_RES_SENSORS);
        else fleet.getSensorRangeMod().modifyFlat(HIGH_RES_SENSORS, modifier, "Ships with high resolution sensors");
    }

    @Override
    public boolean shouldAddDescriptionToTooltip(HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
        return false; // no description from the csv
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

        tooltip.addPara("A ship with asgard sensors increases the fleet's sensor range by %s/%s/%s/%s," +
                " depending on hull size. " +
                "Each additional ship with high resolution sensors provides diminishing returns. " +
                "The higher the highest sensor range increase from a single ship in the fleet, the later diminishing returns kick in.",
            Format.OPAD, Format.HIGH,
            "" + sensors.get(HullSize.FRIGATE).intValue(),
            "" + sensors.get(HullSize.DESTROYER).intValue(),
            "" + sensors.get(HullSize.CRUISER).intValue(),
            "" + sensors.get(HullSize.CAPITAL_SHIP).intValue()
        );

        if (isForModSpec || ship == null) return;
        if (Global.getSettings().getCurrentState() == GameState.TITLE) return;

        CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
        float fleetMod = getAdjustedHRSModifier(fleet, null, 0f);
        float currShipMod = sensors.get(hullSize);

        float fleetModWithOneMore = getAdjustedHRSModifier(fleet, null, currShipMod);
        float fleetModWithoutThisShip = getAdjustedHRSModifier(fleet, ship.getFleetMemberId(), 0f);

        tooltip.addPara("The total sensor strength increase for your fleet is %s.", Format.OPAD, Format.HIGH,
            "" + Math.round(fleetMod));

        float cr = ship.getCurrentCR();
        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            if (member.getId().equals(ship.getFleetMemberId())) {
                cr = member.getRepairTracker().getCR();
            }
        }

        if (cr < MIN_CR) {
            LabelAPI label = tooltip.addPara("This ship's combat readiness is below %s " +
                    "and its high resolution sensors can not be utilized. Bringing this ship into readiness " +
                    "would increase the fleetwide bonus to %s.",
                Format.OPAD, Format.HIGH,
                Math.round(MIN_CR * 100f) + "%",
                "" + Math.round(fleetModWithOneMore));
            label.setHighlightColors(Format.BAD, Format.HIGH);
            label.setHighlight(Math.round(MIN_CR * 100f) + "%", "" + Math.round(fleetModWithOneMore));
        } else {
            if (fleetMod > currShipMod) {
                tooltip.addPara("Removing this ship would decrease it to %s. Adding another ship of the same type " +
                        "would increase it to %s.", Format.OPAD, Format.HIGH,
                    "" + Math.round(fleetModWithoutThisShip),
                    "" + Math.round(fleetModWithOneMore));
            } else {
                tooltip.addPara("Adding another ship of the same type " +
                        "would increase it to %s.", Format.OPAD, Format.HIGH,
                    "" + Math.round(fleetModWithOneMore));
            }
        }
    }

    public static float getAdjustedHRSModifier(CampaignFleetAPI fleet, String skipId, float add) {
        float max = 0f;
        float total = 0f;
        for (FleetMemberAPI member : fleet.getFleetData().getMembersListCopy()) {
            if (member.isMothballed()) continue;
            if (member.getRepairTracker().getCR() < MIN_CR) continue;

            if (member.getId().equals(skipId)) {
                continue;
            }
            float v = member.getStats().getDynamic().getMod(Stats.HRS_SENSOR_RANGE_MOD).computeEffective(0f);
            if (v <= 0) continue;

            if (v > max) max = v;
            total += v;
        }
        if (add > max) max = add;
        total += add;

        if (max <= 0) return 0f;
        float units = total / max;
        if (units <= 1) return max;
        float mult = Misc.logOfBase(2.5f, units) + 1f;
        float result = total * mult / units;
        if (result <= 0) {
            result = 0;
        } else {
            result = Math.round(result * 100f) / 100f;
            result = Math.max(result, 1f);
        }
        return result;
    }


}
