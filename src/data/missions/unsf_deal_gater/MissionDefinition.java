package data.missions.unsf_deal_gater;


import TrueAvarus.UNSF.UNSFMod;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;

public class MissionDefinition implements MissionDefinitionPlugin {

    /**
     * Variant IDs.
     * Feel free to expand this list with whatever vanilla or modded variants
     * you want to appear on the title screen.
     */
    private static final String[] BIG_ENEMY_POOL = {
        // Capitals
        "onslaught_Standard",
        "onslaught_Outdated",
        "onslaught_Elite",
        "onslaught_xiv_Elite",
        // Capital ships
        "legion_Assault",
        "legion_Escort",
        "legion_Strike",
        "legion_FS",
        "legion_xiv_Elite",
    };
    private static final String[] MED_ENEMY_POOL = {
        // Destroyers / Medium Escorts
        "enforcer_Escort",
        "enforcer_Balanced",
        "enforcer_Outdated",
        "enforcer_CS",
        "hammerhead_Balanced",
        "enforcer_Elite",
        "enforcer_XIV_Elite",
        "hammerhead_Elite",

        // Medium Combat
        "enforcer_Assault",
        "sunder_CS",
        "sunder_Assault",

        // Cruisers
        "falcon_Attack",
        "falcon_CS",
        "falcon_xiv_Elite",
        "falcon_xiv_Escort",

        "eagle_Assault",
        "eagle_Balanced",
        "eagle_xiv_Elite",

        "dominator_Support",
        "dominator_Assault",
        "dominator_AntiCV",
        "dominator_Outdated",
        "dominator_XIV_Elite",

        "gryphon_Standard",
        "gryphon_FS",
    };
    private static final String[] SML_ENEMY_POOL = {
        // Frigates / Fast Attack
        "lasher_Standard",
        "lasher_Strike",
        "hound_hegemony_Standard",
        "wolf_hegemony_Assault",

        // Small Escorts / Combat
        "lasher_CS",
        "lasher_PD",
        "wolf_hegemony_CS",
        "wolf_hegemony_PD",
        "vigilance_FS",
        "vigilance_Standard",
        "vigilance_Strike",
        "brawler_Assault",
        "centurion_Assault",
    };

    @Override
    public void defineMission(MissionDefinitionAPI api) {

        //-----------------------------------
        // Fleets
        //-----------------------------------

        api.initFleet(FleetSide.PLAYER, "UNSF", FleetGoal.ATTACK, false);
        api.initFleet(FleetSide.ENEMY, "HSS", FleetGoal.ATTACK, true);

        api.setFleetTagline(FleetSide.PLAYER, "UNSF Trade Convoy");
        api.setFleetTagline(FleetSide.ENEMY, "Hegemony Armada");

        //-----------------------------------
        // Player Fleet
        //-----------------------------------

        api.addToFleet(FleetSide.PLAYER, "unsf_astraios_base",
            FleetMemberType.SHIP, "UNSF Parallels", true);
        api.addToFleet(FleetSide.PLAYER, "unsf_daedalus_base",
            FleetMemberType.SHIP, "UNSF Tenacity", false);
        api.addToFleet(FleetSide.PLAYER, "unsf_dauntless_base",
            FleetMemberType.SHIP, "UNSF Long Haul", false);
        api.addToFleet(FleetSide.PLAYER, "unsf_fenrir_base",
            FleetMemberType.SHIP, "UNSF Free Birb", false);
        api.addToFleet(FleetSide.PLAYER, "unsf_hyperion_base",
            FleetMemberType.SHIP, "UNSF Work Work", false);

        //-----------------------------------
        // Enemy Fleet
        //-----------------------------------

        addToFleet(api, BIG_ENEMY_POOL, 1);
        addToFleet(api, MED_ENEMY_POOL, 2);
        addToFleet(api, SML_ENEMY_POOL, 3);

        //-----------------------------------
        // Battlefield
        //-----------------------------------

        final float width = 16000f;
        final float height = 14000f;

        final float minX = -width / 2f;
        final float minY = -height / 2f;

        api.initMap(
            minX,
            width / 2f,
            minY,
            height / 2f
        );

        for (int i = 15 + UNSFMod.rnd.nextInt(10); i > 0; i--) {
            float x = minX + UNSFMod.rnd.nextFloat() * width;
            float y = minY + UNSFMod.rnd.nextFloat() * height;
            float radius = 300f + UNSFMod.rnd.nextFloat() * 900f;
            api.addNebula(x, y, radius);
        }

        // Asteroid field
        api.addAsteroidField(0f, 0f, UNSFMod.rnd.nextFloat() * 360f,
            9000f, 20f, 70f, 80);
    }

    private static void addToFleet(MissionDefinitionAPI api, String[] pool, int num) {
        for (int i = 0; i < num; i++) {
            api.addToFleet(FleetSide.ENEMY,
                pool[UNSFMod.rnd.nextInt(pool.length)],
                FleetMemberType.SHIP, i == 0);
        }
    }
}