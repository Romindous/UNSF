package TrueAvarus.UNSF.World.Quests;

import java.awt.*;
import java.util.List;
import java.util.Map;
import TrueAvarus.UNSF.Constants.Factions;
import TrueAvarus.UNSF.NPCs.People;
import TrueAvarus.UNSF.Objects.Industries;
import TrueAvarus.UNSF.World.Systems.Niltrof;
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
import exerelin.campaign.intel.missions.remnant.RemnantQuestUtils;

public class Argonauts extends HubMissionWithSearch {

    public static final float CORE_PRICE_MULT = 2.5F;
    public static final String REF_NAME = "$unsf_argonauts";
    protected PersonAPI shady;
    protected MarketAPI target;
    protected MarketAPI sourceMarket;
    protected MarketCMD.RaidDangerLevel danger;

    protected Object readResolve() {
        if (sourceMarket == null && shady != null) {
            sourceMarket = shady.getMarket();
        }

        return this;
    }

    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if (Global.getSector().getMemoryWithoutUpdate().get(REF_NAME)
            instanceof final Argonauts a1) {
            a1.abort(); setGlobalReference(REF_NAME);
        }

        final boolean atPrism = createdAt.getId().equals("nex_prismFreeport");
        if (!createdAt.getId().startsWith(Niltrof.ATLANTIS)) return false;
        if (Global.getSector().getImportantPeople().getData(People.ATLANTIS_SHADY) == null)  {
            People.createAtlantisPersonnel();
        }
        
        shady = getImportantPerson("nex_dissonant");
        if (shady == null) return false;

        personOverride = shady;
        sourceMarket = createdAt;
        setStoryMission();
        requireMarketFaction(new String[]{Factions.TRITACHYON});
        requireMarketNotInHyperspace();
        preferMarketSizeAtLeast(3);
        preferMarketSizeAtMost(5);
        search.marketPrefs.add(m -> m.hasFunctionalIndustry(Industries.PATROLHQ));
        target = pickMarket();
        if (target == null) {
            System.out.println("Failed to find market");
            return false;
        }

        makeImportant(target, "$unsf_argo_sci_mkt", new Enum[]{Stage.TALK_SCIENTIST1});
//        makeImportant(shady, "$nex_remM1_returnHere", new Enum[]{Stage.RETURN_CORES});
        setStartingStage(Stage.MEET_SHADY);
        addSuccessStages(new Object[]{Stage.COMPLETED});
        addFailureStages(new Object[]{Stage.FAILED});
        connectWithMemoryFlag(Stage.TALK_SCIENTIST1, Stage.EXPLORE_STATION, target, "$unsf_argo_talk1");
        connectWithMemoryFlag(Stage.EXPLORE_STATION, Stage.TALK_SCIENTIST2, target, "$unsf_argo_explore");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST2, Stage.TRY_BH_JUMP, target, "$unsf_argo_talk2");
        connectWithMemoryFlag(Stage.TRY_BH_JUMP, Stage.TALK_SCIENTIST3, target, "$unsf_argo_jump");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST3, Stage.RESQ_SCIENTIST, target, "$unsf_argo_talk3");
        setStageOnMemoryFlag(Stage.COMPLETED, shady, "$unsf_argo_completed");
        setStageOnMemoryFlag(Stage.FAILED, shady, "$unsf_argo_failed");
        addNoPenaltyFailureStages(new Object[]{Stage.FAILED_DECIV});
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST1, Stage.FAILED_DECIV, target);
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST2, Stage.FAILED_DECIV, target);
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST3, Stage.FAILED_DECIV, target);
        setStageOnMarketDecivilized(Stage.FAILED_DECIV, createdAt);
        setRepPersonChangesHigh();
        setRepFactionChangesMedium();
        setCreditReward(CreditReward.HIGH);

        triggerCreateMediumPatrolAroundMarket(target, Stage.RETRIEVE_CORES, 0.0F);
        return true;
        
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

                if (shady == null) {
                    log.info("Person is null");
                    return false;
                } else {
                    personOverride = shady;
                    sourceMarket = createdAt;
                    setStoryMission();
                    requireMarketFaction(new String[]{"independent"});
                    requireMarketIsNot(createdAt);
                    requireMarketNotHidden();
                    requireMarketNotInHyperspace();
                    preferMarketSizeAtLeast(4);
                    preferMarketSizeAtMost(6);
                    search.marketPrefs.add(new MarketGroundDefReq(150, 350));
                    target = pickMarket();
                    danger = RaidDangerLevel.HIGH;
                    if (target == null) {
                        log.info("Failed to find market");
                        return false;
                    } else {
                        int marines = getMarinesRequiredForCustomObjective(target, danger);
                        if (!isOkToOfferMissionRequiringMarines(marines)) {
                        }

                        makeImportant(target, "$nex_remM1_target", new Enum[]{Stage.RETRIEVE_CORES});
                        makeImportant(shady, "$nex_remM1_returnHere", new Enum[]{Stage.RETURN_CORES});
                        setStartingStage(Stage.RETRIEVE_CORES);
                        addSuccessStages(new Object[]{Stage.COMPLETED});
                        addFailureStages(new Object[]{Stage.FAILED});
                        connectWithMemoryFlag(Stage.RETRIEVE_CORES, Stage.RETURN_CORES, target, "$nex_remM1_needToReturn");
                        setStageOnMemoryFlag(Stage.COMPLETED, shady, "$nex_remM1_completed");
                        setStageOnMemoryFlag(Stage.FAILED, shady, "$nex_remM1_failed");
                        addNoPenaltyFailureStages(new Object[]{Stage.FAILED_DECIV});
                        connectWithMarketDecivilized(Stage.RETRIEVE_CORES, Stage.FAILED_DECIV, target);
                        setStageOnMarketDecivilized(Stage.FAILED_DECIV, createdAt);
                        setRepPersonChangesHigh();
                        setRepFactionChangesMedium();
                        setCreditReward(CreditReward.HIGH);
                        triggerCreateMediumPatrolAroundMarket(target, Stage.RETRIEVE_CORES, 0.0F);
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
        set("$nex_remM1_personName", shady.getNameString());
        set("$nex_remM1_manOrWoman", shady.getManOrWoman());
        set("$nex_remM1_reward", Misc.getWithDGS((float)getCreditsReward()));
        set("$nex_remM1_systemName", target.getStarSystem().getNameWithLowercaseTypeShort());
        set("$nex_remM1_marketName", target.getName());
        set("$nex_remM1_marketOnOrAt", target.getOnOrAt());
        set("$nex_remM1_dist", getDistanceLY(target));
        int price = getCorePrice();
        set("$nex_remM1_danger", danger);
        set("$nex_remM1_corePriceVal", price);
        set("$nex_remM1_corePriceStr", Misc.getWithDGS((float)price));
        set("$nex_remM1_marines", Misc.getWithDGS((float)getMarinesRequiredForCustomObjective(target, danger)));
        set("$nex_remM1_stage", getCurrentStage());
    }

    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        String action = ((Misc.Token)params.get(0)).getString(memoryMap);
        if (null != action) {
            switch (action) {
                case "beginIntro":
                    dialog.getInteractionTarget().setActivePerson(shady);
                    dialog.getVisualPanel().showPersonInfo(shady, true);
                    updateInteractionData(dialog, memoryMap);
                    return false;
                case "accept":
                    setMarketMissionRef(target, REF_NAME);
                    accept(dialog, memoryMap);
                    return true;
                case "cancel":
                    MarketAPI market = dialog.getInteractionTarget().getMarket();
                    market.removePerson(shady);
                    abort();
                    return false;
                case "raidComplete":
                case "boughtCores":
                    sourceMarket.getCommDirectory().addPerson(shady);
                    return true;
                case "hasCores":
                    return Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity("beta_core") >= 2.0F;
                case "forceShowPerson":
                    dialog.getVisualPanel().showPersonInfo(shady);
                    return true;
                case "complete":
                    BaseMissionHub.set(shady, new BaseMissionHub(shady));
                    sourceMarket.addPerson(shady);
                    shady.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
                    shady.getMemoryWithoutUpdate().set("$nex_remM1_completed", true);
                    ((RuleBasedDialog)dialog.getPlugin()).updateMemory();
                    return true;
                case "complete2":
                    shady.getName().setFirst(RemnantQuestUtils.getString("dissonantName1"));
                    shady.getName().setLast(RemnantQuestUtils.getString("dissonantName2"));
                    SpecialContactIntel intel = new SpecialContactIntel(shady, sourceMarket);
                    Global.getSector().getIntelManager().addIntel(intel, false, dialog.getTextPanel());
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_missionCompleted", true);
                    return true;
                case "betray":
                    PersonAPI person = dialog.getInteractionTarget().getActivePerson();
                    FactionAPI pFaction = person.getFaction();
                    float repMult = pFaction.getCustomFloat("AICoreRepMult");
                    CoreReputationPlugin.MissionCompletionRep repPerson = new CoreReputationPlugin.MissionCompletionRep(getRepRewardSuccessPerson() * repMult, getRewardLimitPerson(), -getRepPenaltyFailurePerson(), getPenaltyLimitPerson());
                    CoreReputationPlugin.MissionCompletionRep repFaction = new CoreReputationPlugin.MissionCompletionRep(getRepRewardSuccessFaction() * repMult, getRewardLimitFaction(), -getRepPenaltyFailureFaction(), getPenaltyLimitFaction());
                    Global.getSector().adjustPlayerReputation(new CoreReputationPlugin.RepActionEnvelope(RepActions.MISSION_SUCCESS, repPerson, dialog.getTextPanel(), true), person);
                    Global.getSector().adjustPlayerReputation(new CoreReputationPlugin.RepActionEnvelope(RepActions.MISSION_SUCCESS, repFaction, dialog.getTextPanel(), true), pFaction.getId());
                    float bounty = Global.getSettings().getCommoditySpec("beta_core").getBasePrice();
                    bounty *= 2.0F * pFaction.getCustomFloat("AICoreValueMult");
                    Global.getSector().getPlayerFleet().getCargo().getCredits().add(bounty);
                    AddRemoveCommodity.addCreditsGainText((int)bounty, dialog.getTextPanel());
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_missionCompleted", true);
                    Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_betrayed", true);
                case "refuse":
                    sourceMarket.getCommDirectory().removePerson(shady);
                    sourceMarket.removePerson(shady);
                    shady.getMemoryWithoutUpdate().set("$nex_remM1_failed", true);
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
        String pName = shady.getNameString();
        FactionAPI heg = Global.getSector().getFaction("hegemony");
        FactionAPI pl = Global.getSector().getFaction("persean");
        if (currentStage == Stage.RETRIEVE_CORES) {
            info.addPara(RemnantQuestUtils.getString("m1_stage1Desc"), opad, h, new String[]{target.getName()});
        } else if (currentStage == Stage.RETURN_CORES) {
            LabelAPI label = info.addPara(RemnantQuestUtils.getString("m1_stage2Desc"), opad, h, new String[]{pName, sourceMarket.getName(), heg.getDisplayNameWithArticle(), pl.getDisplayNameLongWithArticle()});
            label.setHighlight(new String[]{pName, sourceMarket.getName(), heg.getDisplayNameWithArticleWithoutArticle(), pl.getDisplayNameWithArticleWithoutArticle()});
            label.setHighlightColors(new Color[]{h, sourceMarket.getFaction().getBaseUIColor(), heg.getBaseUIColor(), pl.getBaseUIColor()});
        }

    }

    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.RETRIEVE_CORES) {
            info.addPara(RemnantQuestUtils.getString("m1_stage1NextStep"), pad, tc, h, new String[]{target.getName()});
            return true;
        } else {
            if (currentStage == Stage.RETURN_CORES) {
                info.addPara(RemnantQuestUtils.getString("m1_stage2NextStep"), pad, tc, sourceMarket.getTextColorForFactionOrPlanet(), new String[]{sourceMarket.getName()});
            }

            return false;
        }
    }

    public String getBaseName() {
        return RemnantQuestUtils.getString("m1_name");
    }

    public String getPostfixForState() {
        return startingStage != null ? "" : super.getPostfixForState();
    }

    public enum Stage {
        MEET_SHADY,
        TALK_SCIENTIST1,
        EXPLORE_STATION,
        TALK_SCIENTIST2,
        TRY_BH_JUMP,
        TALK_SCIENTIST3,
        RESQ_SCIENTIST,
        COMPLETED,
        FAILED,
        FAILED_TAKEOVER,
        FAILED_DECIV;
    }

    public static class MarketGroundDefReq implements HubMissionWithSearch.MarketRequirement {
        Integer min = 0;
        Integer max = Integer.MAX_VALUE;

        public MarketGroundDefReq(Integer min, Integer max) {
            if (min != null) {
                min = min;
            }

            if (max != null) {
                max = max;
            }

        }

        public boolean marketMatchesRequirement(MarketAPI market) {
            StatBonus defender = market.getStats().getDynamic().getMod("ground_defenses_mod");
            int str = Math.round(defender.computeEffective(0.0F));
            return str >= min && str <= max;
        }
    }
}
