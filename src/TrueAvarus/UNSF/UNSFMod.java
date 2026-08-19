package TrueAvarus.UNSF;


import java.security.SecureRandom;
import java.util.Random;
import TrueAvarus.UNSF.Objects.Items;
import TrueAvarus.UNSF.ItemEffects.ZPM_POWER;
import TrueAvarus.UNSF.NPCs.People;
import TrueAvarus.UNSF.WeaponAI.PDMissileAI;
import TrueAvarus.UNSF.WeaponAI.HorizonMissleAI;
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


    public static final Random srnd = new SecureRandom();

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
        People.createAtlantisPersonnel();
    }

    public static final String HORIZON_NUKE = "unsf_horizon_missile_subprojectile";
    public static final String VLS_0_MISSILE = "unsf_vls_0_projectile";
    @Override
    public PluginPick<MissileAIPlugin> pickMissileAI(MissileAPI missile, ShipAPI launchingShip) {
        final MissileAIPlugin map;
        return switch (missile.getProjectileSpecId()) {
            case VLS_0_MISSILE -> {
                map = new PDMissileAI(missile);
                yield new PluginPick<>(map, CampaignPlugin.PickPriority.MOD_SET);
            }
            case HORIZON_NUKE -> {
                map = new HorizonMissleAI(missile);
                yield new PluginPick<>(map, CampaignPlugin.PickPriority.MOD_SET);
            }
            default -> null;
        };
    }

    // You can add more methods from ModPlugin here. Press Control-O in IntelliJ to see options.

    // Inside your class extending BaseModPlugin
    @Override
    public void onApplicationLoad() {
        // Register the custom item effect
        ItemEffectsRepo.ITEM_EFFECTS.put(Items.UNSF_ZPM, new ZPM_POWER());

    }


}


