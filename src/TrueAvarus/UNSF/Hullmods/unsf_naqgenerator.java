package TrueAvarus.UNSF.Hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class unsf_naqgenerator extends BaseHullMod {

	private static final float PEAK_PERF_TIME = 100f;
    private static final float PPT_CR_LOSS = 25f;

    private static final float EXPLOSION_POWER = 1000f;

    private static final float SMOD_SYSTEM_COOLDOWN = 15f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getPeakCRDuration().modifyFlat(id, PEAK_PERF_TIME);
		stats.getCRLossPerSecondPercent().modifyPercent(id, -PPT_CR_LOSS);

        final float power = EXPLOSION_POWER / impactOf(hullSize);
        stats.getDynamic().getStat(Stats.EXPLOSION_RADIUS_MULT).modifyPercent(id, power);
        stats.getDynamic().getStat(Stats.EXPLOSION_DAMAGE_MULT).modifyPercent(id, power);

        if (isSMod(stats)) {
            stats.getSystemCooldownBonus().modifyPercent(id, SMOD_SYSTEM_COOLDOWN);
            stats.getSystemRegenBonus().modifyPercent(id, SMOD_SYSTEM_COOLDOWN);
        }
	}

/*Increases peak operating time of this ship by %s seconds.
Also reduces the rate at which combat readiness degrades by %s.

Upon destruction of the ship, generators on board will go critical, increasing ship explosion power by %s.*/
	public String getDescriptionParam(int index, HullSize hullSize) {
        return switch (index) {
            case 0 -> (int) PEAK_PERF_TIME + "";
            case 1 -> (int) PPT_CR_LOSS + "%";
            case 2 -> ((int) EXPLOSION_POWER / impactOf(hullSize)) + "%";
            default -> null;
        };
    }

/*Integrated generators improve ship system reload times by %s.*/
    public String getSModDescriptionParam(int index, HullSize hullSize) {
        return index == 0 ? (int) SMOD_SYSTEM_COOLDOWN + "%" : null;
    }

    private int impactOf(final HullSize size) {
        return switch (size) {
            case CAPITAL_SHIP -> 4;
            case CRUISER -> 3;
            case DESTROYER -> 2;
            default -> 1;
        };
    }

    public boolean isApplicableToShip(ShipAPI ship) {
		return ship != null && (ship.getHullSpec().getNoCRLossTime() < 10000 || ship.getHullSpec().getCRLossPerSecond(ship.getMutableStats()) > 0); 
	}
	
	
	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		/*if (ship == null || ship.getMutableStats() == null) return;

		final MutableShipStatsAPI stats = ship.getMutableStats();
		final float decay = ship.getHullSpec().getCRLossPerSecond(stats);
		if (decay <= 0) return;

		float crPerDep = stats.getCRPerDeploymentPercent().computeEffective(ship.getHullSpec().getCRToDeploy()) / 100f;
        final float minCRPerDep = Global.getSettings().getFloat("crDecayMinDeploymentCostForCalc");
        final float secPerDeplCR = Global.getSettings().getFloat("crDecaySecondsPerDeploymentCostPercent");

		if (crPerDep < minCRPerDep) crPerDep = minCRPerDep;
		if (crPerDep <= 0) return;

		tooltip.addSectionHeading("Combat readiness decay", Alignment.MID, Format.OPAD);

		tooltip.addPara("Without this hullmor or any other modifiers, it would take %s seconds for "
				+ "this ship to lose %s combat readiness, after its peak performance time has run out.", Format.OPAD,
				Format.HIGH, "" + Math.round(secPerDeplCR), Math.round(crPerDep * 100f) + "%");

		float crLossPerSecond = stats.getCRLossPerSecondPercent().computeEffective(decay);
		float seconds = (crPerDep * 100f) / crLossPerSecond;

		tooltip.addPara("With all the modifications currently installed on the ship, it will take %s seconds.",
            Format.OPAD, Format.HIGH, "" + Math.round(seconds));*/
	}

	public String getUnapplicableReason(ShipAPI ship) {
		return "Ship does not suffer from CR degradation";
	}
}


