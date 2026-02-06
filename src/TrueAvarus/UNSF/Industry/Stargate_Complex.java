package TrueAvarus.UNSF.Industry;

import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;


public class Stargate_Complex extends BaseIndustry {
    @Override
    public void apply() {
        super.apply(true);

        int size = market.getSize();
        if (size < 5) {
            supply(Commodities.CREW, 3);
            supply(Commodities.SUPPLIES, 3);
        } else {
            supply(Commodities.CREW, 5);
            supply(Commodities.SUPPLIES, 5);
        }

        // Check if the special item "unsf_zpm" is installed
        if (special != null && "unsf_zpm".equals(special.getId())) {
            // Apply the "stargate_zpmcohesion" market condition
            if (!market.hasCondition("stargate_zpmcohesion")) {
                market.addCondition("stargate_zpmcohesion");
            }
        } else {
            // Apply the "stargate_cohesion" market condition
            if (!market.hasCondition("stargate_cohesion")) {
                market.addCondition("stargate_cohesion");
            }
        }
    }

    @Override
    public void unapply() {
        super.unapply();

        // Remove "stargate_cohesion" if it exists
        if (market.hasCondition("stargate_cohesion")) {
            market.removeCondition("stargate_cohesion");
        }

        // Remove "stargate_zpmcohesion" if it exists
        if (market.hasCondition("stargate_zpmcohesion")) {
            market.removeCondition("stargate_zpmcohesion");
        }
    }

}




