package TrueAvarus.UNSF.Shipsystem;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;

public class Asgard_fighter_transporter extends BaseShipSystemScript {
	public static final Object KEY_JITTER = new Object();
	public static final Color JITTER_COLOR = new Color(200,200,255,155);


	public void apply(MutableShipStatsAPI stats, String id, State state, float jitterLevel) {
        if (!(stats.getEntity() instanceof final ShipAPI ship)) return;

        if (jitterLevel > 0) {
            final String fightersKey = ship.getId() + "_recall_device_target";
			List<ShipAPI> fighters = null;
			if (!Global.getCombatEngine().getCustomData().containsKey(fightersKey)) {
				fighters = getFighters(ship);
				Global.getCombatEngine().getCustomData().put(fightersKey, fighters);
            } else {
				fighters = (List<ShipAPI>) Global.getCombatEngine().getCustomData().get(fightersKey);
			}
			if (fighters == null) { // shouldn't be possible, but still
				fighters = new ArrayList<ShipAPI>();
			}

			for (ShipAPI fighter : fighters) {
				if (fighter.isHulk()) continue;

				float maxRangeBonus = fighter.getCollisionRadius();
				float jitterRangeBonus = 5f + jitterLevel * maxRangeBonus;


				fighter.setJitter(KEY_JITTER, JITTER_COLOR, jitterLevel, 10, 0f, jitterRangeBonus);
				if (fighter.isAlive()) {
					fighter.setPhased(true);
				}

				if (state == State.IN) {
					float alpha = 1f - jitterLevel * 0.5f;
					fighter.setExtraAlphaMult(alpha);
				}

				if (jitterLevel == 1) {
					if (fighter.getWing() != null && fighter.getWing().getSource() != null) {
						fighter.getWing().getSource().makeCurrentIntervalFast();
						fighter.getWing().getSource().land(fighter);
					} else {
						fighter.setExtraAlphaMult(1);
					}
				}
			}
		}
	}

	public static List<ShipAPI> getFighters(ShipAPI carrier) {
		List<ShipAPI> result = new ArrayList<ShipAPI>();

		for (ShipAPI ship : Global.getCombatEngine().getShips()) {
			if (!ship.isFighter()) continue;
			if (ship.getWing() == null) continue;
			if (ship.getWing().getSourceShip() == carrier) {
				result.add(ship);
			}
		}

		return result;
	}


	public void unapply(MutableShipStatsAPI stats, String id) {
        if (!(stats.getEntity() instanceof final ShipAPI ship)) return;

		final String fightersKey = ship.getId() + "_recall_device_target";
		Global.getCombatEngine().getCustomData().remove(fightersKey);

//		for (ShipAPI fighter : getFighters(ship)) {
//			fighter.setPhased(false);
//			fighter.setCopyLocation(null, 1f, fighter.getFacing());
//		}
	}


	public StatusData getStatusData(int index, State state, float effectLevel) {
		return null;
	}


}








