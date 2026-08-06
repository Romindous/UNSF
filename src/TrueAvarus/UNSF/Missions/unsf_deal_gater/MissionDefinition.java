package TrueAvarus.UNSF.Missions.unsf_deal_gater;


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
    private static final String[] ENEMY_POOL = {
        "lasher_Standard",
        "vigilance_Strike",
        "hammerhead_Balanced",
        "sunder_Assault",
        "enforcer_Elite",
        "mule_Standard",
        "falcon_Attack",
        "eagle_Balanced",
        "dominator_Assault",
        "heron_Attack",
        "onslaught_Standard"
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

        for (int i = 0; i < 6; i++) {
            api.addToFleet(FleetSide.ENEMY,
                ENEMY_POOL[UNSFMod.rnd.nextInt(ENEMY_POOL.length)],
                FleetMemberType.SHIP, i == 0);
        }

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
}