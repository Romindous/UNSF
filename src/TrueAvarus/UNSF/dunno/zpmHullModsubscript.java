package TrueAvarus.UNSF.dunno;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.*;
import com.fs.starfarer.api.campaign.econ.ImmigrationPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.AbilityPlugin;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.plugins.AutofitPlugin;

import java.util.HashSet;
import java.util.Set;

public class zpmHullModsubscript implements CampaignPlugin {

    // A constant key used to store and retrieve data related to the hullmod tracking.
    private static final String PERSISTENT_KEY = "hullmod_zpm_tracking";


    // This method is called every frame to perform updates related to the plugin's functionality.
    public void advance(float amount) {
        // Retrieve the set of ship IDs from persistent data.
        Set<String> data = getPersistentData();

        // Get the player's cargo object, which manages the player's inventory.
        CargoAPI cargo = Global.getSector().getPlayerFleet().getCargo();

        // Iterate through each ship in the player's fleet.
        for (FleetMemberAPI member : Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy()) {
            // Get the unique ID of the current fleet member (ship).
            String memberId = member.getId();

            // Get the variant of the ship, which contains information about installed hullmods.
            ShipVariantAPI variant = member.getVariant();

            // Check if the ship's ID is in the persistent data and if the hullmod "unsf_zpm_hm" is no longer installed.
            if (data.contains(memberId) && !variant.hasHullMod("unsf_zpm_hm")) {
                // Remove the ship's ID from the persistent data set.
                data.remove(memberId);

                // Update the persistent data with the modified set of ship IDs.
                Global.getSector().getPersistentData().put(PERSISTENT_KEY, data);

                // Add the special item "unsf_zpm" back to the player's cargo.
                cargo.addSpecial((SpecialItemData) Global.getSettings().getSpecialItemSpec("unsf_zpm"), 1);
            }
        }
    }

    private Set<String> getPersistentData() {
        Object data = Global.getSector().getPersistentData().get(PERSISTENT_KEY);
        if (data instanceof Set) {
            return (Set<String>) data;
        } else {
            return new HashSet<>();
        }
    }



    @Override
    public String getId() {
        return null;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(SectorEntityToken interactionTarget) {
        return null;
    }

    @Override
    public PluginPick<InteractionDialogPlugin> pickInteractionDialogPlugin(Object param, SectorEntityToken interactionTarget) {
        return null;
    }

    @Override
    public PluginPick<BattleCreationPlugin> pickBattleCreationPlugin(SectorEntityToken opponent) {
        return null;
    }

    @Override
    public PluginPick<BattleAutoresolverPlugin> pickBattleAutoresolverPlugin(BattleAPI battle) {
        return null;
    }

    @Override
    public PluginPick<ReputationActionResponsePlugin> pickReputationActionResponsePlugin(Object action, String factionId) {
        return null;
    }

    @Override
    public PluginPick<ReputationActionResponsePlugin> pickReputationActionResponsePlugin(Object action, PersonAPI person) {
        return null;
    }

    @Override
    public void updateEntityFacts(SectorEntityToken entity, MemoryAPI memory) {

    }

    @Override
    public void updatePersonFacts(PersonAPI person, MemoryAPI memory) {

    }

    @Override
    public void updateFactionFacts(FactionAPI faction, MemoryAPI memory) {

    }

    @Override
    public void updateGlobalFacts(MemoryAPI memory) {

    }

    @Override
    public void updatePlayerFacts(MemoryAPI memory) {

    }

    @Override
    public void updateMarketFacts(MarketAPI market, MemoryAPI memory) {

    }

    @Override
    public PluginPick<AssignmentModulePlugin> pickAssignmentAIModule(CampaignFleetAPI fleet, ModularFleetAIAPI ai) {
        return null;
    }

    @Override
    public PluginPick<StrategicModulePlugin> pickStrategicAIModule(CampaignFleetAPI fleet, ModularFleetAIAPI ai) {
        return null;
    }

    @Override
    public PluginPick<TacticalModulePlugin> pickTacticalAIModule(CampaignFleetAPI fleet, ModularFleetAIAPI ai) {
        return null;
    }

    @Override
    public PluginPick<NavigationModulePlugin> pickNavigationAIModule(CampaignFleetAPI fleet, ModularFleetAIAPI ai) {
        return null;
    }

    @Override
    public PluginPick<AbilityAIPlugin> pickAbilityAI(AbilityPlugin ability, ModularFleetAIAPI ai) {
        return null;
    }

    @Override
    public PluginPick<FleetStubConverterPlugin> pickStubConverter(FleetStubAPI stub) {
        return null;
    }

    @Override
    public PluginPick<FleetStubConverterPlugin> pickStubConverter(CampaignFleetAPI fleet) {
        return null;
    }

    @Override
    public PluginPick<AutofitPlugin> pickAutofitPlugin(FleetMemberAPI member) {
        return null;
    }

    @Override
    public PluginPick<InteractionDialogPlugin> pickRespawnPlugin() {
        return null;
    }

    @Override
    public PluginPick<ImmigrationPlugin> pickImmigrationPlugin(MarketAPI market) {
        return null;
    }

    @Override
    public PluginPick<AICoreAdminPlugin> pickAICoreAdminPlugin(String commodityId) {
        return null;
    }

    @Override
    public PluginPick<AICoreOfficerPlugin> pickAICoreOfficerPlugin(String commodityId) {
        return null;
    }

    @Override
    public PluginPick<FleetInflater> pickFleetInflater(CampaignFleetAPI fleet, Object params) {
        return null;
    }
}