package TrueAvarus.UNSF.Industry.Market_Conditions;


import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.impl.campaign.econ.BaseMarketConditionPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;

public class stargate_zpmcohesion extends BaseMarketConditionPlugin {
    private static final float BASE_ACCESSIBILITY = 2.00f; // 50% boost in decimal
    private static final String desc = "Accessibility boost from market condition";
    public static final float DEMAND_BONUS = -3f;
    public static final float STAB_BONUS = 3f;

    public void apply(String id) {

        // Apply demand decrease
        for (Industry industry : market.getIndustries()) {
            for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllDemand()) {
                mutableCommodityQuantity.getQuantity().modifyFlat("Stargate_demand", DEMAND_BONUS, "Stargate supply train bonus");
            }
        }

        // Apply stability bonus
        this.market.getStability().modifyFlat("Stargate_stab", STAB_BONUS, "Stargate cohesion bonus");

        // Apply the accessibility boost if it's greater than 0
        float a = BASE_ACCESSIBILITY;
        if (a > 0) {
            market.getAccessibilityMod().modifyFlat(getModId(0), a, desc);
        }

    }


    @Override
    public void unapply(String id) {

        // Revert demand decrease
        for (Industry industry : market.getIndustries()) {
            for (MutableCommodityQuantity mutableCommodityQuantity : industry.getAllDemand()) {
                mutableCommodityQuantity.getQuantity().unmodifyFlat("Stargate_demand");
            }
            // Revert stability bonus
            this.market.getStability().unmodifyFlat("Stargate_stab");
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
        float pad = 10f;
        Color highlight = Misc.getHighlightColor();

        // Add the main title
        tooltip.addTitle("Stargate Cohesion");

        // Add description text
        tooltip.addPara("The Stargate Complex enhances market cohesion, providing the following bonuses:", pad);

        // Add a bullet point list with details
        tooltip.addPara("• Reduces demand for all commodities by %s.", pad, highlight, String.valueOf(DEMAND_BONUS));
        tooltip.addPara("• Increases stability by %s.", pad, highlight, String.valueOf(STAB_BONUS));
        tooltip.addPara("• Increases accessibility by %s.", pad, highlight,String.valueOf(BASE_ACCESSIBILITY));

        // If there are more details to show when expanded, include them here

        tooltip.addPara("This market condition represents THE ULTIMATE logistical advantages and stability brought by the Stargate Complex, allowing for better resource management and market accessibility.", pad);
    }
}





