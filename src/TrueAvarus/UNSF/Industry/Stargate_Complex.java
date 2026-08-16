package TrueAvarus.UNSF.Industry;

import TrueAvarus.UNSF.Objects.Items;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;


public class Stargate_Complex extends BaseIndustry {

    private static final String COND_DEF = "stargate_cohesion";
    private static final String COND_ZPM = "stargate_zpmcohesion";

    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        if (size < 5) {
            supply(Commodities.CREW, 3);
            demand(Commodities.HEAVY_MACHINERY, 2);
            demand(Commodities.SUPPLIES, 1);
        } else {
            supply(Commodities.CREW, 5);
            demand(Commodities.HEAVY_MACHINERY, 3);
            demand(Commodities.SUPPLIES, 2);
        }

        // Check if the special item "unsf_zpm" is installed
        if (special != null && Items.UNSF_ZPM.equals(special.getId())) {
            // Apply the "stargate_zpmcohesion" market condition
            if (!market.hasCondition(COND_ZPM)) {
                market.addCondition(COND_ZPM);
            }
        } else {
            // Apply the "stargate_cohesion" market condition
            if (!market.hasCondition(COND_DEF)) {
                market.addCondition(COND_DEF);
            }
        }
    }

    @Override
    public void unapply() {
        super.unapply();

        // Remove "stargate_cohesion" if it exists
        if (market.hasCondition(COND_DEF)) {
            market.removeCondition(COND_DEF);
        }

        // Remove "stargate_zpmcohesion" if it exists
        if (market.hasCondition(COND_ZPM)) {
            market.removeCondition(COND_ZPM);
        }
    }

}




