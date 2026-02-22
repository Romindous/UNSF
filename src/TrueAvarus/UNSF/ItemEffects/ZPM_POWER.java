package TrueAvarus.UNSF.ItemEffects;


import TrueAvarus.UNSF.UNSFModPlugin;
import TrueAvarus.UNSF.dunno.Items;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MutableCommodityQuantity;
import com.fs.starfarer.api.combat.MutableStat;
import com.fs.starfarer.api.impl.campaign.econ.impl.BoostIndustryInstallableItemEffect;

public class ZPM_POWER extends BoostIndustryInstallableItemEffect {

    private static final String MODIFIER_ID = "stability_supply_price_mod";
    // Independent bonus values for each commodity
    private static final float ORE_BONUS = 2f;
    private static final float RARE_ORE_BONUS = 1f;
    private static final float METALS_BONUS = 2f;
    private static final float RARE_METALS_BONUS = 1f;
    private static final float ORGANICS_BONUS = 2f;
    private static final float SUPPLIES_BONUS = 2f;
    private static final float MACHINERY_BONUS = 2f;
    private static final float SHIP_HULLS_BONUS = 2f;
    private static final float ARMS_BONUS = 2f;
    private static final float FUEL_BONUS = 2f;
    private static final float VOLATILES_BONUS = 2f;
    private static final float DRUGS_BONUS = 2f;
    private static final float CREW_BONUS = 1f;
    private static final float ORGANS_BONUS = 1f;
    private static final float DOMESTIC_GOODS_BONUS = 2f;
    private static final float LUXURY_GOODS_BONUS = 1f;
    private static final float FOOD_BONUS = 2f;
    private static final float MARINES_BONUS = 2f;

    public ZPM_POWER() {
        super(Items.UNSF_ZPM, 0, 0);
    }

    @Override
    public void apply(Industry industry) {
        if (industry == null) return;

        // Apply bonuses based on industry type
        switch (industry.getId()) {
            case "millitarybase":
                applyMillitaryBonuses(industry);
                break;
            case "mining":
                applyMiningBonuses(industry);
                break;
            case "heavyindustry":
                applyHeavyIndustryBonuses(industry);
                break;
            case "fuelprod":
                applyFuelProductionBonuses(industry);
                break;
            case "refining":
                applyRefiningBonuses(industry);
                break;
            case "population":
                applyPopulationBonuses(industry);
                break;
            case "lightindustry":
                applyLightIndustryBonuses(industry);
                break;
            case "farming":
                applyAgricultureBonuses(industry);
                break;
            default:
                break;
        }

        for (final MutableCommodityQuantity dm : industry.getAllDemand()) {
            dm.getQuantity().modifyFlat(MODIFIER_ID, 1, "ZPM DEBUFF");
        }
    }


    private void applyMillitaryBonuses(Industry industry) {
        applyBonus(industry, "crew", CREW_BONUS);
        applyBonus(industry, "marines", MARINES_BONUS);
    }

    private void applyMiningBonuses(Industry industry) {

        applyBonus(industry, "ore", ORE_BONUS);
        applyBonus(industry, "rare_ore", RARE_ORE_BONUS);
        applyBonus(industry, "organics", ORGANICS_BONUS);
        applyBonus(industry, "volatiles", VOLATILES_BONUS);

    }


    private void applyHeavyIndustryBonuses(Industry industry) {
        applyBonus(industry, "supplies", SUPPLIES_BONUS);
        applyBonus(industry, "heavy_machinery", MACHINERY_BONUS);
        applyBonus(industry, "ships", SHIP_HULLS_BONUS);
        applyBonus(industry, "hand_weapons", ARMS_BONUS);
    }

    private void applyFuelProductionBonuses(Industry industry) {
        applyBonus(industry, "fuel", FUEL_BONUS);
    }

    private void applyRefiningBonuses(Industry industry) {
        applyBonus(industry, "metals", METALS_BONUS);
        applyBonus(industry, "rare_metals", RARE_METALS_BONUS);
    }

    private void applyPopulationBonuses(Industry industry) {
        applyBonus(industry, "drugs", DRUGS_BONUS);
        applyBonus(industry, "crew", CREW_BONUS);
        applyBonus(industry, "organs", ORGANS_BONUS);
    }

    private void applyLightIndustryBonuses(Industry industry) {
        applyBonus(industry, "domestic_goods", DOMESTIC_GOODS_BONUS);
        applyBonus(industry, "luxury_goods", LUXURY_GOODS_BONUS);
    }

    private void applyAgricultureBonuses(Industry industry) {
        applyBonus(industry, "food", FOOD_BONUS);
    }

    @Override
    public void unapply(Industry industry) {
        if (industry == null) return;

        // Remove bonuses based on industry type
        switch (industry.getId()) {
            case "millitarybase":
                removeMillitaryBonuses(industry);
                break;
            case "mining":
                removeMiningBonuses(industry);
                break;
            case "heavyindustry":
                removeHeavyIndustryBonuses(industry);
                break;
            case "fuelprod":
                removeFuelProductionBonuses(industry);
                break;
            case "refining":
                removeRefiningBonuses(industry);
                break;
            case "population":
                removePopulationInfrastructureBonuses(industry);
                break;
            case "lightindustry":
                removeLightIndustryBonuses(industry);
                break;
            case "farming":
                removeAgricultureBonuses(industry);
                break;
            default:
                break;
        }

        for (final MutableCommodityQuantity dm : industry.getAllDemand()) {
            dm.getQuantity().unmodifyFlat(MODIFIER_ID);
        }
    }

    private void removeMillitaryBonuses(Industry industry) {
        removeBonus(industry, "crew");
        removeBonus(industry, "marines");
    }

    private void removeMiningBonuses(Industry industry) {
        removeBonus(industry, "ore");
        removeBonus(industry, "rare_ore");
        removeBonus(industry, "organics");
        removeBonus(industry, "volatiles");
    }

    private void removeHeavyIndustryBonuses(Industry industry) {
        removeBonus(industry, "supplies");
        removeBonus(industry, "heavy_machinery");
        removeBonus(industry, "ships");
        removeBonus(industry, "hand_weapons");
    }

    private void removeFuelProductionBonuses(Industry industry) {
        removeBonus(industry, "fuel");
    }

    private void removeRefiningBonuses(Industry industry) {
        removeBonus(industry, "metals");
        removeBonus(industry, "rare_metals");
    }

    private void removePopulationInfrastructureBonuses(Industry industry) {
        removeBonus(industry, "drugs");
        removeBonus(industry, "crew");
        removeBonus(industry, "organs");
    }

    private void removeLightIndustryBonuses(Industry industry) {
        removeBonus(industry, "domestic_goods");
        removeBonus(industry, "luxury_goods");
    }

    private void removeAgricultureBonuses(Industry industry) {
        removeBonus(industry, "food");
    }

    private void applyBonus(Industry industry, String commodityId, float bonusAmount) {
        final MutableStat stat = industry.getSupply(commodityId).getQuantity();
        if (stat.getModifiedValue() > 0f) stat.modifyFlat(MODIFIER_ID, bonusAmount, "ZPM BONUS");
    }

    private void removeBonus(Industry industry, String commodityId) {
        industry.getSupply(commodityId).getQuantity().unmodifyFlat(MODIFIER_ID);
    }

    @Override
    public String[] getSimpleReqs(Industry industry) {
        return new String[]{"not a gas giant"};
    }
}
