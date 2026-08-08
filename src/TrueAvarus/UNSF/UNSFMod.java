package TrueAvarus.UNSF;


import java.util.Random;
import TrueAvarus.UNSF.Constants.Items;
import TrueAvarus.UNSF.ItemEffects.ZPM_POWER;
import TrueAvarus.UNSF.NPCs.important_people;
import TrueAvarus.UNSF.WeaponAI.MIRV_proj_GPT;
import TrueAvarus.UNSF.World.UNSFGen;
import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.combat.MissileAIPlugin;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo;
import exerelin.campaign.SectorManager;


public class UNSFMod extends BaseModPlugin {


    public static final Random rnd = new Random();

    @Override
    public void onNewGame() {
        super.onNewGame();
        // The code below requires that Nexerelin is added as a library (not a dependency, it's only needed to compile the mod).
        boolean isNexerelinEnabled = Global.getSettings().getModManager().isModEnabled("nexerelin");

        if (!isNexerelinEnabled || SectorManager.getManager().isCorvusMode()) {
            new UNSFGen().generate(Global.getSector());
        }
    }

    @Override
    public void onNewGameAfterEconomyLoad() {
        super.onNewGameAfterEconomyLoad();

        // Call your personnel creation method
        important_people.createAtlantisPersonnel();
    }

    public static final String HORIZON_NUKE = "unsf_horizon_missile_subprojectile";
    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        String specID = missile.getProjectileSpecId();

        if (specID.equals(HORIZON_NUKE)) {
            MissileAIPlugin newAIPlugin = new MIRV_proj_GPT(missile, launchingShip);
            return new PluginPick<>(newAIPlugin, CampaignPlugin.PickPriority.MOD_SET);
        }
        return null;  // Only one return null at the end
    }

    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.

    // Inside your class extending BaseModPlugin
    @Override
    public void onApplicationLoad() {
        // Register the custom item effect
        ItemEffectsRepo.ITEM_EFFECTS.put(Items.UNSF_ZPM, new ZPM_POWER());

    }


}


