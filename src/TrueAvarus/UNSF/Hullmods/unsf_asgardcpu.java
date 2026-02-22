package TrueAvarus.UNSF.Hullmods;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import TrueAvarus.UNSF.dunno.Format;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.dunno.HullMods.UNSF_TRINIUM;

public class unsf_asgardcpu extends BaseHullMod {

    public static final float WEAPON_RANGE = 25f;
    public static final float PD_RANGE = 200f;
    public static final float BEAM_RANGE = 150f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        BLOCKED.add(HullMods.INTEGRATED_TARGETING_UNIT);
        BLOCKED.add(HullMods.DISTRIBUTED_FIRE_CONTROL);
        BLOCKED.add(HullMods.ADVANCED_TARGETING_CORE);
        BLOCKED.add(HullMods.DEDICATED_TARGETING_CORE);
        BLOCKED.add("vice_unified_targeting_core");
        BLOCKED.add("DEX_targetcomp");
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        final Collection<String> hullMods = ship.getVariant().getHullMods();
        for (final String tmp : BLOCKED) {
            //if someone tries to install blocked hullmod, remove it
            if (hullMods.contains(tmp))
                MagicIncompatibleHullmods.removeHullmodWithWarning(
                    ship.getVariant(), tmp, UNSF_TRINIUM);
        }
    }

    public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getBallisticWeaponRangeBonus().modifyFlat(id, WEAPON_RANGE);
        stats.getEnergyWeaponRangeBonus().modifyFlat(id, WEAPON_RANGE);
        stats.getBeamWeaponRangeBonus().modifyFlat(id, BEAM_RANGE);
        stats.getNonBeamPDWeaponRangeBonus().modifyFlat(id, PD_RANGE);
    }

    @Override
    public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
        tooltip.addPara("Increases the non-beam PD range by %s, non-beam weapon range by %s, and beam range by %s.",
            Format.OPAD, Format.GOOD, (int) PD_RANGE + "", (int) WEAPON_RANGE + "%", (int) BEAM_RANGE + "");
        tooltip.addPara("Can not work in conjunction with %s, %s, and other range related hullmods.",
            Format.OPAD, Format.BAD, "DTC", "ITU");
    }
}
