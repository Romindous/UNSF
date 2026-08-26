package TrueAvarus.UNSF.World.Quests;

import java.awt.*;
import java.util.List;
import java.util.Map;
import TrueAvarus.UNSF.Constants.Factions;
import TrueAvarus.UNSF.NPCs.People;
import TrueAvarus.UNSF.Objects.Industries;
import TrueAvarus.UNSF.World.Systems.Niltrof;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import com.fs.starfarer.api.impl.campaign.missions.hub.ReqMode;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.intel.missions.remnant.RemnantFragments;
import exerelin.campaign.intel.missions.remnant.RemnantM1;
import exerelin.campaign.intel.missions.remnant.RemnantQuestUtils;

import static TrueAvarus.UNSF.NPCs.People.SHADY_ID;

public class Argonauts extends HubMissionWithSearch {

    public static final String REF_NAME = "$unsf_argonauts";
    public static final String SCIENTIST_ID = "unsf_scientist";

    private PersonAPI shady;
    private MarketAPI startMkt;

    private MarketAPI baseMkt;
    private PersonAPI scientist;

    private PlanetAPI star;
    private OrbitalStationAPI station;

    protected Object readResolve() {
        if (startMkt == null && shady != null) {
            startMkt = shady.getMarket();
        }

        return this;
    }

    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        if (Global.getSector().getMemoryWithoutUpdate().get(REF_NAME)
            instanceof final Argonauts a1) {
            a1.abort();
        }
        setGlobalReference(REF_NAME);

        startMkt = createdAt;
        if (!startMkt.getId().startsWith(Niltrof.ATLANTIS)) return false;
        if (Global.getSector().getImportantPeople().getData(SHADY_ID) == null)  {
            People.createAtlantisPersonnel();
        }
        
        shady = getImportantPerson(SHADY_ID);
        if (shady == null) return false;

        personOverride = shady;

        setStoryMission();
        setMissionId("unsf_argonauts");
        requireMarketFaction(new String[]{Factions.TRITACHYON});
        requireMarketNotInHyperspace();
        preferMarketSizeAtLeast(3);
        preferMarketSizeAtMost(5);
        preferMarketNotMilitary();
        search.marketPrefs.add(m -> m.hasFunctionalIndustry(Industries.PATROLHQ));
        baseMkt = pickMarket(true);
        if (baseMkt == null) {
            System.out.println("Failed to find market");
            return false;
        }

        makeImportant(startMkt, "$unsf_argo_shady", new Enum[]{Stage.MEET_SHADY});
        makeImportant(baseMkt, "$unsf_argo_base", new Enum[]{Stage.TALK_SCIENTIST1});
//        makeImportant(shady, "$nex_remM1_returnHere", new Enum[]{Stage.RETURN_CORES});
        setStartingStage(Stage.MEET_SHADY);
        addSuccessStages(new Object[]{Stage.COMPLETED});
        addFailureStages(new Object[]{Stage.FAILED});
//        connectWithMemoryFlag(Stage.TALK_SHADY, Stage.MEET_SHADY, baseMkt, "$unsf_argo_shady_talk");
        final MemoryAPI plMem = Global.getSector().getPlayerMemoryWithoutUpdate();
        connectWithMemoryFlag(Stage.MEET_SHADY, Stage.TALK_SCIENTIST1, plMem, "$unsf_argo_shady_meet");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST1, Stage.EXPLORE_STATION, plMem, "$unsf_argo_sci_talk1");
        connectWithMemoryFlag(Stage.EXPLORE_STATION, Stage.TALK_SCIENTIST2, plMem, "$unsf_argo_explore");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST2, Stage.TRY_STAR_JUMP, plMem, "$unsf_argo_sci_talk2");
        connectWithMemoryFlag(Stage.TRY_STAR_JUMP, Stage.TALK_SCIENTIST3, plMem, "$unsf_argo_jump");
        connectWithMemoryFlag(Stage.TALK_SCIENTIST3, Stage.RESQ_SCIENTIST, plMem, "$unsf_argo_sci_take");
        setStageOnMemoryFlag(Stage.COMPLETED, plMem, "$unsf_argo_completed");
        setStageOnMemoryFlag(Stage.FAILED, plMem, "$unsf_argo_failed");
        addNoPenaltyFailureStages(new Object[]{Stage.FAILED_DECIV});
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST1, Stage.FAILED_DECIV, baseMkt);
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST2, Stage.FAILED_DECIV, baseMkt);
        connectWithMarketDecivilized(Stage.TALK_SCIENTIST3, Stage.FAILED_DECIV, baseMkt);
        setStageOnMarketDecivilized(Stage.FAILED_DECIV, startMkt);
        setRepPersonChangesHigh();
        setRepFactionChangesMedium();
        setCreditReward(CreditReward.HIGH);
        setPersonIsPotentialContactOnSuccess(shady);
        updateInteractionDataImpl();

        beginStageTrigger(Stage.MEET_SHADY);
        triggerRunScriptAfterDelay(0, () -> {
            setMarketMissionRef(baseMkt, REF_NAME);
            scientist = Global.getSector().getImportantPeople().getPerson(SCIENTIST_ID);
            if (scientist != null) {
                Global.getSector().getImportantPeople().removePerson(scientist);
                baseMkt.getCommDirectory().removePerson(scientist);
                baseMkt.removePerson(scientist);
            }
            scientist = Global.getFactory().createPerson();
            scientist.setId(SCIENTIST_ID);
            scientist.setImportance(PersonImportance.MEDIUM);
            scientist.setFaction(Factions.UNSF);
            scientist.setGender(FullName.Gender.FEMALE);
            scientist.setRankId(Ranks.CITIZEN);
            scientist.setPostId(Ranks.POST_SCIENTIST);
            scientist.getName().setFirst("Carmen");
            scientist.getName().setLast("McKay");
            scientist.setPortraitSprite(Global.getSettings().getSpriteName("characters", "unsf_carmen"));
            makeImportant(scientist, "$unsf_argo_sci", new Enum[]{Stage.TALK_SCIENTIST1, Stage.TALK_SCIENTIST2, Stage.TALK_SCIENTIST3});
            baseMkt.getCommDirectory().addPerson(scientist);
            baseMkt.addPerson(scientist);
            Global.getSector().getImportantPeople().addPerson(scientist);
            setPersonMissionRef(scientist, REF_NAME);
            updateInteractionDataImpl();
        });
        endTrigger();

        beginStageTrigger(Stage.EXPLORE_STATION);
        triggerRunScriptAfterDelay(0, () -> {
            requireSystemBlackHole();
            requireSystemOnFringeOfSector();
            requireSystemHasAtLeastNumJumpPoints(1);
            requireSystemHasNumPlanets(2);
            requireSystemTags(ReqMode.NOT_ALL, Tags.SYSTEM_ABYSSAL);
            requireSystem(ss -> ss.getConstellation() != null);
            star = pickSystem(true).getStar();
            if (star == null) {
                System.out.println("Failed to find system");
                return;
            }
            final PlanetAPI resPlanet = star.getStarSystem().getPlanets().get(0);
            if (resPlanet == null) {
                System.out.println("Failed to find planet");
                return;
            }
            final LocData loc = new LocData(EntityLocationType.ORBITING_PLANET_OR_STAR,
                resPlanet, star.getStarSystem(), false);
            spawnDebrisField(DEBRIS_SMALL, DEBRIS_DENSE, loc);
            final SectorEntityToken set = spawnEntity(Entities.STATION_RESEARCH, loc);
            System.out.println("Station is a " + set.toString());
            if (!(set instanceof final OrbitalStationAPI os)) {
                System.out.println("Failed to create station");
                return;
            }
            station = os;
            station.setId("unsf_argo_station");
            setEntityMissionRef(station, REF_NAME);
            makeImportant(star, "$unsf_argo_star", new Enum[]{Stage.TRY_STAR_JUMP});
            makeImportant(station, "$unsf_argo_station", new Enum[]{Stage.EXPLORE_STATION});
            updateInteractionDataImpl();
        });
        endTrigger();

        beginStageTrigger(Stage.RESQ_SCIENTIST);
        triggerRunScriptAfterDelay(0, () -> {
            final StarSystemAPI ss = startMkt.getStarSystem();

            updateInteractionDataImpl();
        });
        endTrigger();
//        triggerCreateMediumPatrolAroundMarket(target, Stage.RETRIEVE_CORES, 0.0F);
        return true;
    }

    public boolean callEvent(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        final String action = params.get(0).getString(memoryMap);
        if (null == action || baseMkt == null) return super.callEvent(ruleId, dialog, params, memoryMap);

        switch (action) {
            case "accept":
                accept(dialog, memoryMap);
                return true;
            case "refuse":
                if (shady != null) {
                    Global.getSector().getImportantPeople().removePerson(SHADY_ID);
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
            case "complete":
                BaseMissionHub.set(shady, new BaseMissionHub(shady));
                shady.getMemoryWithoutUpdate().set(BaseMissionHub.NUM_BONUS_MISSIONS, 1);
                shady.getMemoryWithoutUpdate().set("$unsf_argo_completed", true);
                ((RuleBasedDialog) dialog.getPlugin()).updateMemory();
                Global.getSector().getIntelManager().addIntel(new ContactIntel(shady, startMkt), false, dialog.getTextPanel());
                Global.getSector().getMemoryWithoutUpdate().set("$unsf_argo_completed", true);
                endSuccess(dialog, memoryMap);
                return true;
        }

        return super.callEvent(ruleId, dialog, params, memoryMap);
    }

    protected void updateInteractionDataImpl() {
        set("$unsf_argo_shady", shady.getName().getFirst());
        set("$unsf_argo_shadyFull", scientist.getNameString());
        set("$unsf_argo_shadySex", shady.getManOrWoman());
        set("$unsf_argo_shadyHeOrShe", shady.getHeOrShe());
        set("$unsf_argo_shadyHisOrHer", shady.getHisOrHer());
        if (scientist != null) {
            set("$unsf_argo_sci", scientist.getName().getFirst());
            set("$unsf_argo_sciFull", scientist.getNameString());
            set("$unsf_argo_sciSex", scientist.getManOrWoman());
            set("$unsf_argo_sciHeOrShe", scientist.getHeOrShe());
            set("$unsf_argo_sciHisOrHer", scientist.getHisOrHer());
        }
        if (station != null && star != null) {
            set("$unsf_argo_system", star.getName());
            set("$unsf_argo_const", star.getConstellation().getNameWithType());
            set("$unsf_argo_systemDist", getDistanceLY(star));
            set("$unsf_argo_station", station.getName());
        }
        set("$unsf_argo_reward", Misc.getWithDGS(getCreditsReward()));
        set("$unsf_argo_startStar", startMkt.getStarSystem().getNameWithLowercaseTypeShort());
        set("$unsf_argo_start", startMkt.getName());
        set("$unsf_argo_startOnOrAt", startMkt.getOnOrAt());
        set("$unsf_argo_baseStar", baseMkt.getStarSystem().getNameWithLowercaseTypeShort());
        set("$unsf_argo_base", baseMkt.getName());
        set("$unsf_argo_baseOnOrAt", baseMkt.getOnOrAt());
        set("$unsf_argo_baseDist", getDistanceLY(baseMkt));
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
        if (currentStage == Stage.EXPLORE_STATION) {
            info.addPara(RemnantQuestUtils.getString("m1_stage1Desc"), opad, h, new String[]{baseMkt.getName()});
        } else if (currentStage == Stage.MEET_SHADY) {
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
        MEET_SHADY,
        TALK_SCIENTIST1,
        EXPLORE_STATION,
        TALK_SCIENTIST2,
        TRY_STAR_JUMP,
        TALK_SCIENTIST3,
        RESQ_SCIENTIST,
        COMPLETED,
        FAILED,
        FAILED_TAKEOVER,
        FAILED_DECIV;
    }
}
