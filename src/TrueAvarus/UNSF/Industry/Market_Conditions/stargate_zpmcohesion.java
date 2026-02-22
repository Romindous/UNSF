package TrueAvarus.UNSF.Industry.Market_Conditions;


import TrueAvarus.UNSF.dunno.Format;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

public class stargate_zpmcohesion extends BaseMarketConditionPlugin {
    private static final float ACCESSIBILITY = 0.50f; // 25% boost in decimal
    private static final float INDUSTRY_DEMAND = -2f;
    private static final float STABILITY = 2f;
    private static final String DEMAND_MOD = "Stargate_demand";
    private static final String STABILITY_MOD = "Stargate_stab";

    public void apply(String id) {

        //TODO make all this scale with number of "stargated" worlds

        // Apply demand decrease
        for (Industry industry : market.getIndustries()) {
            for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllDemand()) {
                mutableCommodityQuantity.getQuantity().modifyFlat(DEMAND_MOD, INDUSTRY_DEMAND, "Stargate supply chain bonus");
            }
        }

        // Apply stability bonus
        this.market.getStability().modifyFlat(STABILITY_MOD, STABILITY, "Stargate cohesion bonus");

        // Apply the accessibility boost if it's greater than 0
        market.getAccessibilityMod().modifyFlat(getModId(0), ACCESSIBILITY, "Accessibility boost from stargate availability");

    }


    @Override
    public void unapply(String id) {

        // Revert demand decrease
        for (Industry industry : market.getIndustries()) {
            for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllDemand()) {
                mutableCommodityQuantity.getQuantity().unmodifyFlat(DEMAND_MOD);
            }
            // Revert stability bonus
            this.market.getStability().unmodifyFlat(STABILITY_MOD);
        }
        // Remove the accessibility boost modifiers
        market.getAccessibilityMod().unmodifyFlat(getModId(0));
        market.getAccessibilityMod().unmodifyFlat(getModId(1));
        market.getAccessibilityMod().unmodifyFlat(getModId(2));
    }

    private String getModId(int index) {
        return "accessibility_boost_mod_" + index;
    }
    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded) {

        // Add the main title
        tooltip.addTitle("Stargate Cohesion");

        // Add description text
        tooltip.addPara("The Stargate Complex enhances market cohesion, providing the following bonuses:", Format.PAD);

        tooltip.setBulletedListMode("• ");
        tooltip.addPara("Reduces demand for all commodities by %s.", Format.PAD, Format.HIGH, (int) INDUSTRY_DEMAND + "");
        tooltip.addPara("Increases stability by %s.", Format.PAD, Format.HIGH, (int) STABILITY + "");
        tooltip.addPara("Increases accessibility by %s.", Format.PAD, Format.HIGH, (int) (ACCESSIBILITY * 100f) + "%");
        tooltip.setBulletedListMode(null);

        tooltip.addPara("With the power of a %s, this market has even higher logistical advantages, stability, and accessibility.", Format.PAD, Format.GOOD, "ZPM");
    }
}





