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
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.RuleBasedDialog;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.CoreReputationPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.rulecmd.AddRemoveCommodity;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.intel.SpecialContactIntel;
import exerelin.campaign.intel.missions.remnant.RemnantQuestUtils;

public class Argonauts extends HubMissionWithSearch {

    public static final String REF_NAME = "$unsf_argonauts";
    public static final String SCIENTIST_ID = "unsf_scientist";

    protected PersonAPI shady;
    protected MarketAPI startMkt;

    protected MarketAPI baseMkt;
    protected PersonAPI scientist;

    protected Object readResolve() {
        if (startMkt == null && shady != null) {
            startMkt = shady.getMarket();
        }

        return this;
    }

    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if (Global.getSector().getMemoryWithoutUpdate().get(REF_NAME)
            instanceof final Argonauts a1) {
            a1.abort(); setGlobalReference(REF_NAME);
        }

        startMkt = createdAt;
        if (!startMkt.getId().startsWith(Niltrof.ATLANTIS)) return false;
        if (Global.getSector().getImportantPeople().getData(People.SHADY) == null)  {
            People.createAtlantisPersonnel();
        }
        
        shady = getImportantPerson(People.SHADY);
        if (shady == null) return false;

        personOverride = shady;

        setStoryMission();
        requireMarketFaction(new String[]{Factions.TRITACHYON});
        requireMarketNotInHyperspace();
        preferMarketSizeAtLeast(3);
        preferMarketSizeAtMost(5);
        search.marketPrefs.add(m -> m.hasFunctionalIndustry(Industries.PATROLHQ));
        baseMkt = pickMarket();
        if (baseMkt == null) {
            System.out.println("Failed to find market");
            return false;
        }

        makeImportant(baseMkt, "$unsf_argo_base", new Enum[]{Stage.TALK_SCIENTIST1});
//        makeImportant(shady, "$nex_remM1_returnHere", new Enum[]{Stage.RETURN_CORES});
        setStartingStage(Stage.TALK_SHADY);
        addSuccessStages(new Object[]{Stage.COMPLETED});
        addFailureStages(new Object[]{Stage.FAILED});
        connectWithMemoryFlag(Stage.TALK_SHADY, Stage.MEET_SHADY, baseMkt, "$unsf_argo_shady_talk");
        connectWithMemoryFlag(Stage.MEET_SHADY, Stage.TALK_SCIENTIST1, baseMkt, "$unsf_argo_shady_meet");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST1, Stage.EXPLORE_STATION, baseMkt, "$unsf_argo_sci_talk1");
        connectWithMemoryFlag(Stage.EXPLORE_STATION, Stage.TALK_SCIENTIST2, baseMkt, "$unsf_argo_explore");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST2, Stage.TRY_BH_JUMP, baseMkt, "$unsf_argo_sci_talk2");
        connectWithMemoryFlag(Stage.TRY_BH_JUMP, Stage.TALK_SCIENTIST3, baseMkt, "$unsf_argo_jump");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST3, Stage.RESQ_SCIENTIST, baseMkt, "$unsf_argo_sci_talk3");
        setStageOnMemoryFlag(Stage.COMPLETED, shady, "$unsf_argo_completed");
        setStageOnMemoryFlag(Stage.FAILED, shady, "$unsf_argo_failed");
        addNoPenaltyFailureStages(new Object[]{Stage.FAILED_DECIV});
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST1, Stage.FAILED_DECIV, baseMkt);
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST2, Stage.FAILED_DECIV, baseMkt);
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST3, Stage.FAILED_DECIV, baseMkt);
        setStageOnMarketDecivilized(Stage.FAILED_DECIV, startMkt);
        setRepPersonChangesHigh();
        setRepFactionChangesMedium();
        setCreditReward(CreditReward.HIGH);

//        triggerCreateMediumPatrolAroundMarket(target, Stage.RETRIEVE_CORES, 0.0F);
        return true;
    }

    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        final String action = params.get(0).getString(memoryMap);
        if (null == action || baseMkt == null) return super.callEvent(ruleId, dialog, params, memoryMap);

        switch (action) {
            case "accept":
                setMarketMissionRef(baseMkt, REF_NAME);

                scientist = Global.getSector().getImportantPeople().getPerson(SCIENTIST_ID);
                if (scientist != null) {
                    Global.getSector().getImportantPeople().removePerson(scientist);
                    baseMkt.getCommDirectory().removePerson(scientist);
                    baseMkt.removePerson(scientist);
                }
                scientist = Global.getFactory().createPerson();
                shady.setId(SCIENTIST_ID);
                shady.setImportance(PersonImportance.MEDIUM);
                shady.setFaction(Factions.UNSF);
                shady.setGender(FullName.Gender.FEMALE);
                shady.setRankId(Ranks.CITIZEN);
                shady.setPostId(Ranks.POST_SCIENTIST);
                shady.getName().setFirst("Carmen");
                shady.getName().setLast("McKay");
                scientist.setPortraitSprite(Global.getSettings().getSpriteName("characters", "unsf_carmen"));
                baseMkt.getCommDirectory().addPerson(scientist);
                baseMkt.addPerson(scientist);
                Global.getSector().getImportantPeople().addPerson(scientist);
                setPersonMissionRef(scientist, REF_NAME);

                updateInteractionData(dialog, memoryMap);
                accept(dialog, memoryMap);
                return true;
            case "cancel", "refuse":
                if (shady != null) {
                    startMkt.getCommDirectory().removePerson(shady);
                    startMkt.removePerson(shady);
                }
                if (scientist == null) scientist = Global.getSector().getImportantPeople().getPerson(SCIENTIST_ID);
                if (scientist != null) {
                    Global.getSector().getImportantPeople().removePerson(scientist);
                    baseMkt.getCommDirectory().removePerson(scientist);
                    baseMkt.removePerson(scientist);
                }
                abort();
                return false;
            case "raidComplete":
            case "boughtCores":
                startMkt.getCommDirectory().addPerson(shady);
                return true;
            case "hasCores":
                return Global.getSector().getPlayerFleet().getCargo().getCommodityQuantity("beta_core") >= 2.0F;
            case "forceShowPerson":
                dialog.getVisualPanel().showPersonInfo(shady);
                return true;
            case "complete":
                BaseMissionHub.set(shady, new BaseMissionHub(shady));
                startMkt.addPerson(shady);
                shady.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
                shady.getMemoryWithoutUpdate().set("$nex_remM1_completed", true);
                ((RuleBasedDialog)dialog.getPlugin()).updateMemory();
                return true;
            case "complete2":
                shady.getName().setFirst(RemnantQuestUtils.getString("dissonantName1"));
                shady.getName().setLast(RemnantQuestUtils.getString("dissonantName2"));
                SpecialContactIntel intel = new SpecialContactIntel(shady, startMkt);
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
                startMkt.getCommDirectory().removePerson(shady);
                startMkt.removePerson(shady);
                shady.getMemoryWithoutUpdate().set("$nex_remM1_failed", true);
                Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_missionCompleted", true);
                Global.getSector().getMemoryWithoutUpdate().set("$nex_remM1_betrayed", true);
                return false;
        }

        return super.callEvent(ruleId, dialog, params, memoryMap);
    }

    protected void updateInteractionDataImpl() {
        set("$unsf_argo_shadyName", shady.getName().getFirst());
        set("$unsf_argo_shadyFull", scientist.getNameString());
        set("$unsf_argo_shadySex", shady.getManOrWoman());
        set("$unsf_argo_shadyHeOrShe", shady.getHeOrShe());
        set("$unsf_argo_shadyHisOrHer", shady.getHisOrHer());
        if (scientist != null) {
            set("$unsf_argo_sciName", scientist.getName().getFirst());
            set("$unsf_argo_sciFull", scientist.getNameString());
            set("$unsf_argo_sciSex", scientist.getManOrWoman());
            set("$unsf_argo_sciHeOrShe", scientist.getHeOrShe());
        }
        set("$unsf_argo_reward", Misc.getWithDGS(getCreditsReward()));
        set("$unsf_argo_start_star", startMkt.getStarSystem().getNameWithLowercaseTypeShort());
        set("$unsf_argo_start", startMkt.getName());
        set("$unsf_argo_startOnOrAt", startMkt.getOnOrAt());
        set("$unsf_argo_base_star", baseMkt.getStarSystem().getNameWithLowercaseTypeShort());
        set("$unsf_argo_base", baseMkt.getName());
        set("$unsf_argo_baseOnOrAt", baseMkt.getOnOrAt());
        set("$unsf_argo_dist", getDistanceLY(baseMkt));
        set("$unsf_argo_stage", getCurrentStage());

    }

    public String getBaseName() {
        return Argonauts.class.getSimpleName();
    }

    public String getStageDescriptionText() {
        return null;
    }

    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10.0F;
        Color h = Misc.getHighlightColor();
        String pName = shady.getNameString();
        FactionAPI heg = Global.getSector().getFaction("hegemony");
        FactionAPI pl = Global.getSector().getFaction("persean");
        if (currentStage == Stage.RETRIEVE_CORES) {
            info.addPara(RemnantQuestUtils.getString("m1_stage1Desc"), opad, h, new String[]{baseMkt.getName()});
        } else if (currentStage == Stage.RETURN_CORES) {
            LabelAPI label = info.addPara(RemnantQuestUtils.getString("m1_stage2Desc"), opad, h, new String[]{pName, startMkt.getName(), heg.getDisplayNameWithArticle(), pl.getDisplayNameLongWithArticle()});
            label.setHighlight(new String[]{pName, startMkt.getName(), heg.getDisplayNameWithArticleWithoutArticle(), pl.getDisplayNameWithArticleWithoutArticle()});
            label.setHighlightColors(new Color[]{h, startMkt.getFaction().getBaseUIColor(), heg.getBaseUIColor(), pl.getBaseUIColor()});
        }
    }

    public String getNextStepText() {
        return null;
    }

    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        String text = getNextStepText();
        if (text != null) {
            info.addPara(text, tc, pad);
            return true;
        }
        return false;
    }

    public String getPostfixForState() {
        return startingStage != null ? "" : super.getPostfixForState();
    }

    public enum Stage {
        TALK_SHADY,
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
}
