package TrueAvarus.UNSF.World.Quests;

import java.awt.*;
import java.util.List;
import java.util.Map;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.RuleBasedDialog;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.StatBonus;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.intel.SpecialContactIntel;
import exerelin.campaign.intel.missions.remnant.RemnantM1;
import exerelin.campaign.intel.missions.remnant.RemnantQuestUtils;

public class ArgonautsQuest extends HubMissionWithSearch {

    public static final float CORE_PRICE_MULT = 2.5F;
    public static final String REF_NAME = "$unsf_argonauts";
    protected PersonAPI shady;
    protected MarketAPI market;
    protected MarketAPI sourceMarket;
    protected MarketCMD.RaidDangerLevel danger;

    protected Object readResolve() {
        if (this.sourceMarket == null && this.shady != null) {
            this.sourceMarket = this.shady.getMarket();
        }

        return this;
    }

    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if (!this.setGlobalReference(REF_NAME)) {
            RemnantM1 existing = (RemnantM1) Global.getSector().getMemoryWithoutUpdate().get(REF_NAME);
            existing.abort();
            this.setGlobalReference(REF_NAME);
        }

        boolean atPrism = createdAt.getId().equals("nex_prismFreeport");
        if (!atPrism && Global.getSector().getEconomy().getMarket("nex_prismFreeport") != null) {
            return false;
        } else {
            String mktFactionId = createdAt.getFactionId();
            if (!atPrism && !"independent".equals(mktFactionId)) {
                return false;
            } else {
                if (Global.getSector().getImportantPeople().getData("nex_dissonant") == null) {
                    RemnantQuestUtils.createDissonant(createdAt);
                }

                this.shady = this.getImportantPerson("nex_dissonant");
                if (this.shady == null) {
                    log.info("Person is null");
                    return false;
                } else {
                    this.personOverride = this.shady;
                    this.sourceMarket = createdAt;
                    this.setStoryMission();
                    this.requireMarketFaction(new String[]{"independent"});
                    this.requireMarketIsNot(createdAt);
                    this.requireMarketNotHidden();
                    this.requireMarketNotInHyperspace();
                    this.preferMarketSizeAtLeast(4);
                    this.preferMarketSizeAtMost(6);
                    this.search.marketPrefs.add(new MarketGroundDefReq(150, 350));
                    this.market = this.pickMarket();
                    this.danger = RaidDangerLevel.HIGH;
                    if (this.market == null) {
                        log.info("Failed to find market");
                        return false;
                    } else {
                        int marines = this.getMarinesRequiredForCustomObjective(this.market, this.danger);
                        if (!this.isOkToOfferMissionRequiringMarines(marines)) {
                        }

                        this.makeImportant(this.market, "$nex_remM1_target", new Enum[]{RemnantM1.Stage.RETRIEVE_CORES});
                        this.makeImportant(this.shady, "$nex_remM1_returnHere", new Enum[]{RemnantM1.Stage.RETURN_CORES});
                        this.setStartingStage(RemnantM1.Stage.RETRIEVE_CORES);
                        this.addSuccessStages(new Object[]{RemnantM1.Stage.COMPLETED});
                        this.addFailureStages(new Object[]{RemnantM1.Stage.FAILED});
                        this.connectWithMemoryFlag(RemnantM1.Stage.RETRIEVE_CORES, RemnantM1.Stage.RETURN_CORES, this.market, "$nex_remM1_needToReturn");
                        this.setStageOnMemoryFlag(RemnantM1.Stage.COMPLETED, this.shady, "$nex_remM1_completed");
                        this.setStageOnMemoryFlag(RemnantM1.Stage.FAILED, this.shady, "$nex_remM1_failed");
                        this.addNoPenaltyFailureStages(new Object[]{RemnantM1.Stage.FAILED_DECIV});
                        this.connectWithMarketDecivilized(RemnantM1.Stage.RETRIEVE_CORES, RemnantM1.Stage.FAILED_DECIV, this.market);
                        this.setStageOnMarketDecivilized(RemnantM1.Stage.FAILED_DECIV, createdAt);
                        this.setRepPersonChangesHigh();
                        this.setRepFactionChangesMedium();
                        this.setCreditReward(CreditReward.HIGH);
                        this.triggerCreateMediumPatrolAroundMarket(this.market, RemnantM1.Stage.RETRIEVE_CORES, 0.0F);
                        return true;
                    }
                }
            }
        }
    }

    protected int getCorePrice() {
        float base = Global.getSettings().getCommoditySpec("beta_core").getBasePrice();
        return Math.round(base * 2.0F * 2.5F);
    }

    protected void updateInteractionDataImpl() {
        this.set("$nex_remM1_personName", this.shady.getNameString());
        this.set("$nex_remM1_manOrWoman", this.shady.getManOrWoman());
        this.set("$nex_remM1_reward", Misc.getWithDGS((float)this.getCreditsReward()));
        this.set("$nex_remM1_systemName", this.market.getStarSystem().getNameWithLowercaseTypeShort());
        this.set("$nex_remM1_marketName", this.market.getName());
        this.set("$nex_remM1_marketOnOrAt", this.market.getOnOrAt());
        this.set("$nex_remM1_dist", this.getDistanceLY(this.market));
        int price = this.getCorePrice();
        this.set("$nex_remM1_danger", this.danger);
        this.set("$nex_remM1_corePriceVal", price);
        this.set("$nex_remM1_corePriceStr", Misc.getWithDGS((float)price));
        this.set("$nex_remM1_marines", Misc.getWithDGS((float)this.getMarinesRequiredForCustomObjective(this.market, this.danger)));
        this.set("$nex_remM1_stage", this.getCurrentStage());
    }

    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = ((Misc.Token)params.get(0)).getString(memoryMap);
        if (null != action) {
            switch (action) {
                case "beginIntro":
                    dialog.getInteractionTarget().setActivePerson(this.shady);
                    dialog.getVisualPanel().showPersonInfo(this.shady, true);
                    this.updateInteractionData(dialog, memoryMap);
                    return false;
                case "accept":
                    this.setMarketMissionRef(this.market, REF_NAME);
                    this.accept(dialog, memoryMap);
                    return true;
                case "cancel":
                    MarketAPI market = dialog.getInteractionTarget().getMarket();
                    market.removePerson(this.shady);
                    this.abort();
                    return false;
                case "raidComplete":
                case "boughtCores":
                    this.sourceMarket.getCommDirectory().addPerson(this.shady);
                    return true;
                case "hasCores":
                    return Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity("beta_core") >= 2.0F;
                case "forceShowPerson":
                    dialog.getVisualPanel().showPersonInfo(this.shady);
                    return true;
                case "complete":
                    BaseMissionHub.set(this.shady, new BaseMissionHub(this.shady));
                    this.sourceMarket.addPerson(this.shady);
                    this.shady.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
                    this.shady.getMemoryWithoutUpdate().set("$nex_remM1_completed", true);
                    ((RuleBasedDialog)dialog.getPlugin()).updateMemory();
                    return true;
                case "complete2":
                    this.shady.getName().setFirst(RemnantQuestUtils.getString("dissonantName1"));
                    this.shady.getName().setLast(RemnantQuestUtils.getString("dissonantName2"));
                    SpecialContactIntel intel = new SpecialContactIntel(this.shady, this.sourceMarket);
                    Global.getSector().getIntelManager().addIntel(intel, false, dialog.getTextPanel());
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_missionCompleted", true);
                    return true;
                case "betray":
                    PersonAPI person = dialog.getInteractionTarget().getActivePerson();
                    FactionAPI pFaction = person.getFaction();
                    float repMult = pFaction.getCustomFloat("AICoreRepMult");
                    CoreReputationPlugin.MissionCompletionRep repPerson = new CoreReputationPlugin.MissionCompletionRep(this.getRepRewardSuccessPerson() * repMult, this.getRewardLimitPerson(), -this.getRepPenaltyFailurePerson(), this.getPenaltyLimitPerson());
                    CoreReputationPlugin.MissionCompletionRep repFaction = new CoreReputationPlugin.MissionCompletionRep(this.getRepRewardSuccessFaction() * repMult, this.getRewardLimitFaction(), -this.getRepPenaltyFailureFaction(), this.getPenaltyLimitFaction());
                    Global.getSector().adjustPlayerReputation(new CoreReputationPlugin.RepActionEnvelope(RepActions.MISSION_SUCCESS, repPerson, dialog.getTextPanel(), true), person);
                    Global.getSector().adjustPlayerReputation(new CoreReputationPlugin.RepActionEnvelope(RepActions.MISSION_SUCCESS, repFaction, dialog.getTextPanel(), true), pFaction.getId());
                    float bounty = Global.getSettings().getCommoditySpec("beta_core").getBasePrice();
                    bounty *= 2.0F * pFaction.getCustomFloat("AICoreValueMult");
                    Global.getSector().getPlayerFleet().getCargo().getCredits().add(bounty);
                    AddRemoveCommodity.addCreditsGainText((int)bounty, dialog.getTextPanel());
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_missionCompleted", true);
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_betrayed", true);
                case "refuse":
                    this.sourceMarket.getCommDirectory().removePerson(this.shady);
                    this.sourceMarket.removePerson(this.shady);
                    this.shady.getMemoryWithoutUpdate().set("$nex_remM1_failed", true);
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_missionCompleted", true);
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_betrayed", true);
                    return false;
            }
        }

        return super.callEvent(ruleId, dialog, params, memoryMap);
    }

    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10.0F;
        Color h = Misc.getHighlightColor();
        String pName = this.shady.getNameString();
        FactionAPI heg = Global.getSector().getFaction("hegemony");
        FactionAPI pl = Global.getSector().getFaction("persean");
        if (this.currentStage == RemnantM1.Stage.RETRIEVE_CORES) {
            info.addPara(RemnantQuestUtils.getString("m1_stage1Desc"), opad, h, new String[]{this.market.getName()});
        } else if (this.currentStage == RemnantM1.Stage.RETURN_CORES) {
            LabelAPI label = info.addPara(RemnantQuestUtils.getString("m1_stage2Desc"), opad, h, new String[]{pName, this.sourceMarket.getName(), heg.getDisplayNameWithArticle(), pl.getDisplayNameLongWithArticle()});
            label.setHighlight(new String[]{pName, this.sourceMarket.getName(), heg.getDisplayNameWithArticleWithoutArticle(), pl.getDisplayNameWithArticleWithoutArticle()});
            label.setHighlightColors(new Color[]{h, this.sourceMarket.getFaction().getBaseUIColor(), heg.getBaseUIColor(), pl.getBaseUIColor()});
        }

    }

    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (this.currentStage == RemnantM1.Stage.RETRIEVE_CORES) {
            info.addPara(RemnantQuestUtils.getString("m1_stage1NextStep"), pad, tc, h, new String[]{this.market.getName()});
            return true;
        } else {
            if (this.currentStage == RemnantM1.Stage.RETURN_CORES) {
                info.addPara(RemnantQuestUtils.getString("m1_stage2NextStep"), pad, tc, this.sourceMarket.getTextColorForFactionOrPlanet(), new String[]{this.sourceMarket.getName()});
            }

            return false;
        }
    }

    public String getBaseName() {
        return RemnantQuestUtils.getString("m1_name");
    }

    public String getPostfixForState() {
        return this.startingStage != null ? "" : super.getPostfixForState();
    }

    public static enum Stage {
        RETRIEVE_CORES,
        RETURN_CORES,
        COMPLETED,
        FAILED,
        FAILED_DECIV;
    }

    public static class MarketGroundDefReq implements HubMissionWithSearch.MarketRequirement {
        Integer min = 0;
        Integer max = Integer.MAX_VALUE;

        public MarketGroundDefReq(Integer min, Integer max) {
            if (min != null) {
                this.min = min;
            }

            if (max != null) {
                this.max = max;
            }

        }

        public boolean marketMatchesRequirement(MarketAPI market) {
            StatBonus defender = market.getStats().getDynamic().getMod("ground_defenses_mod");
            int str = Math.round(defender.computeEffective(0.0F));
            return str >= this.min && str <= this.max;
        }
    }
}
