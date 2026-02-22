package TrueAvarus.UNSF.Hullmods;

import java.util.*;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import org.magiclib.util.MagicIncompatibleHullmods;

import static TrueAvarus.UNSF.dunno.HullMods.UNSF_TRINIUM;

public class unsf_triniumalloy extends BaseHullMod {

    public static final float EMP_RESISTANCE = 25f;
    private static final Map<HullSize, Float> mag = new HashMap<>();

	public static final float MANEUVERABILITY = 15f;
	public static final float SMOD_MANEUVERABILITY = 15f;

    public static final Set<String> BLOCKED = new HashSet<>();
    static {
        mag.put(HullSize.FRIGATE, 250f);
        mag.put(HullSize.DESTROYER, 300f);
        mag.put(HullSize.CRUISER, 350f);
        mag.put(HullSize.CAPITAL_SHIP, 400f);

        BLOCKED.add(HullMods.HEAVYARMOR);
        BLOCKED.add("tahlan_daemonplating");
        BLOCKED.add("tahlan_daemonarmor");
        BLOCKED.add("monjeau_armour");
        BLOCKED.add("nskr_criticalArmor");
        BLOCKED.add("apex_armor");
        BLOCKED.add("apex_cryo_armor");
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
		stats.getArmorBonus().modifyFlat(id, mag.get(hullSize));
        stats.getEmpDamageTakenMult().modifyPercent(id, EMP_RESISTANCE);
		
		final float mvMod = isSMod(stats) ? -SMOD_MANEUVERABILITY : MANEUVERABILITY;
        stats.getAcceleration().modifyPercent(id, mvMod);
        stats.getDeceleration().modifyPercent(id, mvMod);
        stats.getTurnAcceleration().modifyPercent(id, mvMod);
        stats.getMaxTurnRate().modifyPercent(id, mvMod);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + mag.get(HullSize.FRIGATE).intValue();
		if (index == 1) return "" + mag.get(HullSize.DESTROYER).intValue();
		if (index == 2) return "" + mag.get(HullSize.CRUISER).intValue();
		if (index == 3) return "" + mag.get(HullSize.CAPITAL_SHIP).intValue();
        if (index == 4) return (int) EMP_RESISTANCE + "%";
		if (index == 5) return (int) MANEUVERABILITY + "%";
		return null;
	}
	
	
	@Override
	public String getSModDescriptionParam(int index, HullSize hullSize, ShipAPI ship) {
        return index == 0 ? (int) SMOD_MANEUVERABILITY + "%" : null;
    }
	
	@Override
	public boolean isSModEffectAPenalty() {
		return true;
	}

}
