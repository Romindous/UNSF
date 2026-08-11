package TrueAvarus.UNSF.World.Systems;

import java.awt.*;
import TrueAvarus.UNSF.Constants.Industries;
import TrueAvarus.UNSF.Constants.Items;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.MusicPlayerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.StarSystemType;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.util.Misc;

public class niltrof {

    private static final String ATLANTIS_BOSS = "atlantis_boss";
    private static final String FACTION = "unsf_faction";

    public void generate(SectorAPI sector) {
        // Create the star system
        StarSystemAPI system = sector.createStarSystem("Niltrof");

        // Position the star system
        system.getLocation().set(10000, 15000);
        system.initNonStarCenter();
        system.addTag(Tags.THEME_CORE_POPULATED);
        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

        // Create the stable central location
        final SectorEntityToken center = system.addCustomEntity(
            "niltrof_stable_point",
            "Niltrof Center Point",
            Entities.BASE_INTEL_ICON,
            Factions.NEUTRAL
        );
        center.setLocation(0, 0); // Set it to the center of the star system

        SectorEntityToken star_orbit_1 = system.addCustomEntity(
            "lycaon_stable_point",
            "Niltrof Lycaon Point",
            Entities.BASE_INTEL_ICON,
            Factions.NEUTRAL
        );
        star_orbit_1.setCircularOrbit(center, 40, 12500, 412); // Make it orbit around the central location at 0 distance

        SectorEntityToken star_orbit_2 = system.addCustomEntity(
            "nyxara_stable_point",
            "Niltrof Nyxara Point",
            Entities.BASE_INTEL_ICON,
            Factions.NEUTRAL
        );
        star_orbit_2.setCircularOrbit(center, 200, 16000, 717); // Make it orbit around the central location at 0 distance

        //The orbital periods (in time units) for the given distances are approximately:
        //
        //For 6,000 distance units: 117.84 time units
        //For 2,500 distance units: 31.69 time units
        //For 20,000 distance units: 717.14 time units
        //For 18,000 distance units: 612.30 time units

        system.setLightColor(new Color(200, 229, 255, 255));

        // Create the star
        PlanetAPI niltrof_star = system.initStar("niltrof_star",
            StarTypes.BLUE_SUPERGIANT, 1500f, 500);
        niltrof_star.setCircularOrbit(center, 0, 2500, 32); // Make it orbit around the central location at 0 distance
//        niltrof_star.setName("niltrof");

        PlanetAPI niltrof_gas_giant = system.addPlanet("niltrof_gas_giant",
            center, "Ignara", Planets.GAS_GIANT,
            0, 300f, 6000, 117f);
        PlanetConditionGenerator.generateConditionsForPlanet(niltrof_gas_giant, StarAge.YOUNG);


        niltrof_gas_giant.setCustomDescriptionId("unsf_ignara_planet");
        // Create other star bodies and make them orbit around the center location
        PlanetAPI lycaon_star = system.addPlanet("lycaon_star",
            star_orbit_1, "Lycaon", StarTypes.YELLOW,
            40, 800, 2000, 117);
        system.setSecondary(lycaon_star);
        system.setType(StarSystemType.TRINARY_2FAR);

        PlanetAPI lycaon_water = system.addPlanet("lycaon_water",
            lycaon_star, "Oberon", Planets.PLANET_WATER,
            0, 250f, 2800, 38f);
        PlanetConditionGenerator.generateConditionsForPlanet(lycaon_water, StarAge.YOUNG);
        lycaon_water.setFaction(FACTION);
        lycaon_water.setInteractionImage("illustrations", "oberon");
        lycaon_water.setCustomDescriptionId("unsf_oberon_planet");


        PlanetAPI nyxara_star = system.addPlanet("nyxara_star",
            star_orbit_2, "Nyxara", StarTypes.RED_DWARF,
            200, 400, 2500, 32);
        system.setTertiary(nyxara_star);
        system.setType(StarSystemType.TRINARY_2FAR);

        PlanetAPI nyxara_frozen = system.addPlanet("nyxara_frozen",
            nyxara_star,
            "Argos", Planets.FROZEN2,
            0, 130f, 2000, 23f);
        PlanetConditionGenerator.generateConditionsForPlanet(nyxara_frozen, StarAge.YOUNG);
        nyxara_frozen.setFaction(FACTION);
        nyxara_frozen.setCustomDescriptionId("unsf_argos_planet");

        // CENTRAL ASTEROID BELT
        system.addRingBand(center, "misc", "rings_dust0", 1000f, 1, Color.white, 1000f, 7500, 200f, null, null);
        system.addRingBand(center, "misc", "rings_asteroids0", 500f, 0, Color.white, 500f, 7550, 200f, null, null);
        system.addAsteroidBelt(center, 250, 7525, 850, 200, 200, Terrain.ASTEROID_BELT, "Death belt");

        // PLANET REMAINS
        SectorEntityToken niltrof_field1 = system.addTerrain(Terrain.ASTEROID_FIELD,
            new AsteroidFieldParams(
                800f, // min radius
                1000f, // max radius
                60, // min asteroid count
                80, // max asteroid count
                4f, // min asteroid radius
                35f, // max asteroid radius
                "Planetary Debris")); // null for default name

        niltrof_field1.setCircularOrbit(center, 180, 7525, 200);

        // PLANET REMAINS TAIL
        SectorEntityToken niltrof_field2 = system.addTerrain(Terrain.ASTEROID_FIELD,
            new AsteroidFieldParams(
                600f, // min radius
                300f, // max radius
                30, // min asteroid count
                50, // max asteroid count
                4f, // min asteroid radius
                25f, // max asteroid radius
                "Planetary Debris")); // null for default name

        niltrof_field2.setCircularOrbit(center, 185, 7525, 200);

        // PLANET REMAINS "DUST CLOUD"
        SectorEntityToken niltrof_nebula1 = system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(
            "  xxx " +
                " xxxxx" +
                " xxxxx" +
                " xxxx " +
                "  xx  " +
                "   x  ",
            6, 6, // size of the nebula grid, should match above string
            "terrain", "nebula_grey", 4, 4, null));
        niltrof_nebula1.setId("niltrof_nebula1");
        niltrof_nebula1.setCircularOrbit(center, 180, 7525, 200);

        //SATELITES

        // Lycaon Relay
        SectorEntityToken comm = system.addCustomEntity("lycaon_relay", // unique id
            "Lycaon Relay", // name - if null, defaultName from custom_entities.json will be used
            Entities.COMM_RELAY, // type of object, defined in custom_entities.json
            FACTION); // faction
        comm.setCircularOrbit(lycaon_star, 200, 4000, 65);
        comm.setFaction(FACTION);

        // Nyxara Sensor Array
        SectorEntityToken sensor = system.addCustomEntity("nyxara_sensor_array", // unique id
            "Nyxara Sensor Array", // name - if null, defaultName from custom_entities.json will be used
            Entities.SENSOR_ARRAY, // type of object, defined in custom_entities.json
            FACTION); // faction
        sensor.setCircularOrbit(nyxara_star, 200, 4000, 65);
        sensor.setFaction(FACTION);

        // Niltrof Nav Buoy
        SectorEntityToken nav = system.addCustomEntity("niltrof_nav_buoy", // unique id
            "Niltrof Nav Buoy", // name - if null, defaultName from custom_entities.json will be used
            Entities.NAV_BUOY, // type of object, defined in custom_entities.json
            FACTION); // faction
        nav.setCircularOrbit(niltrof_star, 200, 4000, 65);
        nav.setFaction(FACTION);

        // JUMP POINTS

        final JumpPointAPI innerJumpPoint = Global.getFactory().createJumpPoint("niltrof_inner_jump", "Inner Jump Point");
        innerJumpPoint.setCircularOrbit(center, 200, 4800, 80);
        innerJumpPoint.setStandardWormholeToHyperspaceVisual();
        innerJumpPoint.setAutoCreateEntranceFromHyperspace(true);
        system.addEntity(innerJumpPoint);

        final JumpPointAPI nyxaraJumpPoint = Global.getFactory().createJumpPoint("niltrof_nyxara_jump", "Nyxara Jump Point");
        nyxaraJumpPoint.setCircularOrbit(nyxara_star, 140, 2800, 40);
        nyxaraJumpPoint.setStandardWormholeToHyperspaceVisual();
        nyxaraJumpPoint.setAutoCreateEntranceFromHyperspace(true);
        system.addEntity(nyxaraJumpPoint);

        // Autogenerate hyperspace jump points - FUCK THAT - FALSES YOU
        system.autogenerateHyperspaceJumpPoints(true, false);

        /*nyxaraJumpPoint.clearDestinations();
        nyxaraJumpPoint.setLocation(11000, 15000);
        nyxaraJumpPoint.setCircularOrbit(system.getCenter(), 0f, 600f, 20f);
        nyxaraJumpPoint.addDestination(new JumpPointAPI.JumpDestination(niltrof_gas_giant, "Ignara Gravity Well"));
        Global.getSector().getHyperspace().addEntity(nyxaraJumpPoint);
        System.out.println("11221133" + system.getCenter());
        System.out.println(system.getLocation());
        System.out.println(system.getHyperspaceAnchor());
        System.out.println(nyxaraJumpPoint.isInHyperspace());*/

        // MARKETS

        //Blown up planet mining station
        SectorEntityToken AegirgastSt = system.addCustomEntity("aegirgast_station", "Aegirgast Station", "industrial_station_1", FACTION);
        AegirgastSt.setCircularOrbitWithSpin(center, 180, 7525f, 200, 3f, 5f);
        AegirgastSt.setInteractionImage("illustrations", "aegirgast_station");
        AegirgastSt.setCustomDescriptionId("unsf_aegirgast_station");

        //Blown up planet mining station
        SectorEntityToken NidavellirSt = system.addCustomEntity("nidavellir_station", "Nidavellir Station", "industrial_station_1", FACTION);
        NidavellirSt.setCircularOrbitPointingDown(nyxara_star, 270, 600f, 15);
        NidavellirSt.setInteractionImage("illustrations", "orbital");
        NidavellirSt.setCustomDescriptionId("unsf_nidavellir_station");

        SectorEntityToken AtlantisSt = system.addCustomEntity("atlantis_station", "Atlantis Station", "atlantis_station", FACTION);
        AtlantisSt.setCircularOrbitPointingDown(lycaon_star, 270, 5000f, 150);
        AtlantisSt.setInteractionImage("illustrations", "atlantis_station");
        AtlantisSt.setCustomDescriptionId("unsf_atlantis_station");

        //ATLANTIS

        MarketAPI atlantis_market = Global.getFactory().createMarket("atlantis_station_market", AtlantisSt.getName(), 0);
        atlantis_market.setSize(5);
        atlantis_market.setFactionId(AtlantisSt.getFaction().getId());

        atlantis_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        atlantis_market.setPrimaryEntity(AtlantisSt);

        atlantis_market.addCondition(Conditions.POPULATION_5);
        atlantis_market.addCondition(Conditions.HABITABLE);
        atlantis_market.addCondition(Conditions.OUTPOST);
        atlantis_market.addCondition(Conditions.FREE_PORT);
        atlantis_market.addCondition(Conditions.RUINS_SCATTERED);

        Misc.setFullySurveyed(atlantis_market, null, true);
        atlantis_market.addIndustry(Industries.POPULATION);
        atlantis_market.addIndustry(Industries.MEGAPORT);
        atlantis_market.addIndustry(Industries.HEAVYBATTERIES);
        atlantis_market.getIndustry(Industries.HEAVYBATTERIES).setSpecialItem(new SpecialItemData(Items.DRONE_REPLICATOR, null));
        atlantis_market.addIndustry(Industries.HIGHCOMMAND);
        atlantis_market.getIndustry(Industries.HIGHCOMMAND).setSpecialItem(new SpecialItemData(Items.UNSF_ZPM, null));
        atlantis_market.addIndustry(Industries.PLANETARYSHIELD);
        atlantis_market.addIndustry(Industries.FUELPROD);
        atlantis_market.getIndustry(Industries.FUELPROD).setSpecialItem(new SpecialItemData(Items.FULLERENE_SPOOL, null));
        atlantis_market.addIndustry(Industries.WAYSTATION);
        atlantis_market.addIndustry(Industries.LIGHTINDUSTRY);
        atlantis_market.addIndustry(Industries.STARFORTRESS_HIGH);
        atlantis_market.getIndustry(Industries.STARFORTRESS_HIGH).setAICoreId(Commodities.ALPHA_CORE);
        atlantis_market.addIndustry(Industries.STARGATE_COMPLEX);

        atlantis_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        atlantis_market.addSubmarket("unsf_submarket");
        atlantis_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        atlantis_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        atlantis_market.getTariff().modifyFlat("default_tariff", atlantis_market.getFaction().getTariffFraction());

        AtlantisSt.setMarket(atlantis_market);

        Global.getSector().getEconomy().addMarket(atlantis_market, true);

        //todo THIS ADDS CUSTOM MUSIC TO MARKET !!!!!!!!!!!!!!!!!!!!!
        atlantis_market.getMemoryWithoutUpdate().set(MusicPlayerPluginImpl.MUSIC_SET_MEM_KEY, "unsf_atlantis_neutral");

        //AEGIRAST

        //todo Mining station market code - !!! USE AS TEMPLATE FOR CUSTOM MARKETS FOR OTHER PLANETS/STATIONS !!!!
        MarketAPI aegirast_market = Global.getFactory().createMarket("aegirgast_station_market", AegirgastSt.getName(), 0);
        aegirast_market.setSize(4);
        aegirast_market.setFactionId(AegirgastSt.getFaction().getId());

        aegirast_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        aegirast_market.setPrimaryEntity(AegirgastSt);

        aegirast_market.addCondition(Conditions.POPULATION_4);
        aegirast_market.addCondition(Conditions.ORE_RICH);
        aegirast_market.addCondition(Conditions.RARE_ORE_ABUNDANT);
        aegirast_market.addCondition(Conditions.VOLATILES_TRACE);
        aegirast_market.addCondition(Conditions.INDUSTRIAL_POLITY);
        aegirast_market.addCondition(Conditions.RUINS_EXTENSIVE);

        Misc.setFullySurveyed(aegirast_market, null, true);
        aegirast_market.addIndustry(Industries.POPULATION);
        aegirast_market.addIndustry(Industries.SPACEPORT);
        aegirast_market.addIndustry(Industries.MINING);
        aegirast_market.getIndustry(Industries.MINING).setSpecialItem(new SpecialItemData(Items.UNSF_ZPM, null));
        aegirast_market.addIndustry(Industries.PATROLHQ);
        aegirast_market.addIndustry(Industries.GROUNDDEFENSES);
        aegirast_market.addIndustry(Industries.REFINING);
        aegirast_market.getIndustry(Industries.REFINING).setSpecialItem(new SpecialItemData(Items.CATALYTIC_CORE, null));
        aegirast_market.addIndustry(Industries.BATTLESTATION_HIGH);
        aegirast_market.addIndustry(Industries.STARGATE_COMPLEX);

        aegirast_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        aegirast_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        aegirast_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        aegirast_market.getTariff().modifyFlat("default_tariff", aegirast_market.getFaction().getTariffFraction());

        AegirgastSt.setMarket(aegirast_market);

        Global.getSector().getEconomy().addMarket(aegirast_market, true);

        //NIDAVELLIR

        //Mining station market code - !!! USE AS TEMPLATE FOR CUSTOM MARKETS FOR OTHER PLANETS/STATIONS !!!!
        MarketAPI nidavellir_market = Global.getFactory().createMarket("nidavellir_station_market", NidavellirSt.getName(), 0);
        nidavellir_market.setSize(5);
        nidavellir_market.setFactionId(NidavellirSt.getFaction().getId());

        nidavellir_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        nidavellir_market.setPrimaryEntity(NidavellirSt);

        nidavellir_market.addCondition(Conditions.POPULATION_5);
        nidavellir_market.addCondition(Conditions.HOT);
        nidavellir_market.addCondition(Conditions.INDUSTRIAL_POLITY);
        nidavellir_market.addCondition(Conditions.FRONTIER);

        Misc.setFullySurveyed(nidavellir_market, null, true);
        nidavellir_market.addIndustry(Industries.POPULATION);
        nidavellir_market.addIndustry(Industries.SPACEPORT);
        nidavellir_market.addIndustry(Industries.REFINING);
        nidavellir_market.addIndustry(Industries.ORBITALWORKS);
        nidavellir_market.getIndustry(Industries.ORBITALWORKS).setSpecialItem(new SpecialItemData(Items.PRISTINE_NANOFORGE, null));
        nidavellir_market.addIndustry(Industries.MILITARYBASE);
        nidavellir_market.getIndustry(Industries.MILITARYBASE).setSpecialItem(new SpecialItemData(Items.CRYOARITHMETIC_ENGINE, null));
        nidavellir_market.addIndustry(Industries.GROUNDDEFENSES);
        nidavellir_market.addIndustry(Industries.BATTLESTATION_HIGH);
        nidavellir_market.getIndustry(Industries.BATTLESTATION_HIGH).setAICoreId(Commodities.ALPHA_CORE);
        nidavellir_market.addIndustry(Industries.STARGATE_COMPLEX);

        nidavellir_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        nidavellir_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        nidavellir_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        nidavellir_market.getTariff().modifyFlat("default_tariff", nidavellir_market.getFaction().getTariffFraction());

        NidavellirSt.setMarket(nidavellir_market);

        Global.getSector().getEconomy().addMarket(nidavellir_market, true);

        //OBERON

        MarketAPI oberon_market = Global.getFactory().createMarket("oberon_planet_market", lycaon_water.getName(), 0);
        oberon_market.setSize(6);
        oberon_market.setFactionId(FACTION);
        oberon_market.setPrimaryEntity(lycaon_water);
        oberon_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        //oberon_market.setFactionId(lycaon_water.getFaction().getId());
        oberon_market.addCondition(Conditions.POPULATION_6);
        oberon_market.addCondition(Conditions.HABITABLE);
        oberon_market.addCondition(Conditions.MILD_CLIMATE);
        oberon_market.addCondition(Conditions.WATER_SURFACE);
        oberon_market.addCondition(Conditions.ORGANICS_ABUNDANT);
        oberon_market.addCondition(Conditions.VOLTURNIAN_LOBSTER_PENS);
        oberon_market.addCondition(Conditions.RUINS_VAST);
        oberon_market.addCondition(Conditions.REGIONAL_CAPITAL);

        Misc.setFullySurveyed(oberon_market, null, true);
        oberon_market.addIndustry(Industries.POPULATION);
        oberon_market.addIndustry(Industries.MEGAPORT);
        oberon_market.addIndustry(Industries.MINING);
        oberon_market.addIndustry(Industries.AQUACULTURE);
        oberon_market.addIndustry(Industries.LIGHTINDUSTRY);
        oberon_market.getIndustry(Industries.LIGHTINDUSTRY).setSpecialItem(new SpecialItemData(Items.BIOFACTORY_EMBRYO, null));
        oberon_market.addIndustry(Industries.MILITARYBASE);
        oberon_market.getIndustry(Industries.MILITARYBASE).setSpecialItem(new SpecialItemData(Items.UNSF_ZPM, null));
        oberon_market.addIndustry(Industries.STARFORTRESS_HIGH);
        oberon_market.getIndustry(Industries.STARFORTRESS_HIGH).setAICoreId(Commodities.ALPHA_CORE);
        oberon_market.addIndustry(Industries.HEAVYBATTERIES);
        oberon_market.addIndustry(Industries.STARGATE_COMPLEX);

        oberon_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        oberon_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        oberon_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        oberon_market.getTariff().modifyFlat("default_tariff", oberon_market.getFaction().getTariffFraction());

        lycaon_water.setMarket(oberon_market);
        Global.getSector().getEconomy().addMarket(oberon_market, true);

        //ARGOS

        MarketAPI argos_market = Global.getFactory().createMarket("argos_planet_market", nyxara_frozen.getName(), 0);
        argos_market.setSize(4);
        argos_market.setFactionId(FACTION);
        argos_market.setPrimaryEntity(nyxara_frozen);
        argos_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        //argos_market.setFactionId(lycaon_water.getFaction().getId());
        argos_market.addCondition(Conditions.POPULATION_4);
        argos_market.addCondition(Conditions.NO_ATMOSPHERE);
        argos_market.addCondition(Conditions.POOR_LIGHT);
        argos_market.addCondition(Conditions.COLD);
        argos_market.addCondition(Conditions.ORE_MODERATE);
        argos_market.addCondition(Conditions.VOLATILES_ABUNDANT);
        argos_market.addCondition(Conditions.FREE_PORT);

        Misc.setFullySurveyed(argos_market, null, true);
        argos_market.addIndustry(Industries.POPULATION);
        argos_market.addIndustry(Industries.SPACEPORT);
        argos_market.addIndustry(Industries.MINING);
        argos_market.getIndustry(Industries.MINING).setSpecialItem(new SpecialItemData(Items.SYNCHROTRON, null));
        argos_market.addIndustry(Industries.FUELPROD);
        argos_market.getIndustry(Industries.FUELPROD).setSpecialItem(new SpecialItemData(Items.UNSF_ZPM, null));
        argos_market.addIndustry(Industries.PATROLHQ);
        argos_market.addIndustry(Industries.HEAVYBATTERIES);
        argos_market.addIndustry(Industries.ORBITALSTATION_HIGH);
        argos_market.addIndustry(Industries.STARGATE_COMPLEX);

        argos_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        argos_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        argos_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        argos_market.getTariff().modifyFlat("default_tariff", argos_market.getFaction().getTariffFraction());

        nyxara_frozen.setMarket(argos_market);
        Global.getSector().getEconomy().addMarket(argos_market, true);




















        /* ideas for more planets
        Main star -

        Yellow star - habitable, planet, military powerhouse, staging ground, city underground
        

        Industries  - orbital shipyards creates hella hulls

                    

         */
    }
}

