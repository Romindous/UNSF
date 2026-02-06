package TrueAvarus.UNSF.World.Systems;

import TrueAvarus.UNSF.UNSFModPlugin;
import TrueAvarus.UNSF.dunno.Custom_industries;
import TrueAvarus.UNSF.dunno.Items;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.EngagementResultAPI;
import com.fs.starfarer.api.impl.MusicPlayerPluginImpl;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.StarSystemType;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin.AsteroidFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import static TrueAvarus.UNSF.dunno.Items.UNSF_ZPM;

public class niltrof {
    public static String ATLANTIS_BOSS = "atlantis_boss";
    public void generate(SectorAPI sector) {
        // Create the star system
        StarSystemAPI system = sector.createStarSystem("Niltrof");

        // Position the star system
        system.getLocation().set(10000, 15000);
        system.addTag(Tags.THEME_CORE_POPULATED);
        system.setBackgroundTextureFilename("graphics/backgrounds/background5.jpg");

        // Create the stable central location
        SectorEntityToken systemstablepoint = system.addCustomEntity(
                "niltrof_stable_point",
                "Niltrof Lagrange point zero",
                "stable_location",
                Factions.NEUTRAL
        );
        systemstablepoint.setLocation(0, 0); // Set it to the center of the star system

        SectorEntityToken star_orbit_1 = system.addCustomEntity(
                "lycaon_stable_point",
                "Niltrof Lagrange point one",
                "stable_location",
                Factions.NEUTRAL
        );
        star_orbit_1.setCircularOrbit(systemstablepoint, 40, 18000, 612); // Make it orbit around the central location at 0 distance

        SectorEntityToken star_orbit_2 = system.addCustomEntity(
                "nyxara_stable_point",
                "Niltrof Lagrange point two",
                "stable_location",
                Factions.NEUTRAL
        );
        star_orbit_2.setCircularOrbit(systemstablepoint, 200, 20000, 717); // Make it orbit around the central location at 0 distance

        //The orbital periods (in time units) for the given distances are approximately:
        //
        //For 6,000 distance units: 117.84 time units
        //For 2,500 distance units: 31.69 time units
        //For 20,000 distance units: 717.14 time units
        //For 18,000 distance units: 612.30 time units


        // Create the star
        PlanetAPI solgar_star = system.initStar("solgar_star",
                StarTypes.BLUE_SUPERGIANT,
                1500f,
                500);
        solgar_star.setCircularOrbit(systemstablepoint, 0, 2500, 32); // Make it orbit around the central location at 0 distance

        system.setLightColor(new Color(200, 229, 255, 255));

        // Create other celestial bodies and make them orbit around the stable location
        PlanetAPI lycaon_star = system.addPlanet("lycaon_star",
                star_orbit_1,
                "Lycaon",
                StarTypes.YELLOW,
                40, 800, 6000, 117);
        system.setSecondary(lycaon_star);
        system.setType(StarSystemType.TRINARY_2FAR);

        PlanetAPI nyxara_star = system.addPlanet("nyxara_star",
                star_orbit_2,
                "Nyxara",
                StarTypes.RED_DWARF,
                200, 400, 2500, 32);
        system.setTertiary(nyxara_star);
        system.setType(StarSystemType.TRINARY_2FAR);







        PlanetAPI solgar_1 = system.addPlanet("solgar_1",
                systemstablepoint,
                "Ignara",
                "gas_giant",
                0,
                300f,
                6000,
                117f);
        PlanetConditionGenerator.generateConditionsForPlanet(solgar_1, StarAge.YOUNG);
        solgar_1.setCustomDescriptionId("unsf_ignara_planet");




        PlanetAPI lycaon_1 = system.addPlanet("lycaon_1",
                lycaon_star,
                "Oberon",
                "water",
                0,
                250f,
                2800,
                38f);
        PlanetConditionGenerator.generateConditionsForPlanet(lycaon_1, StarAge.YOUNG);
        lycaon_1.setFaction("unsf_faction");
        lycaon_1.setInteractionImage("illustrations", "oberon");
        lycaon_1.setCustomDescriptionId("unsf_oberon_planet");




        PlanetAPI nyxara_1 = system.addPlanet("nyxara_1",
                nyxara_star,
                "Argos",
                "frozen",
                0,
                130f,
                2000,
                23f);
        PlanetConditionGenerator.generateConditionsForPlanet(nyxara_1, StarAge.YOUNG);
        nyxara_1.setFaction("unsf_faction");
        nyxara_1.setCustomDescriptionId("unsf_argos_planet");






        // CENTRAL ASTEROID BELT
        system.addRingBand(systemstablepoint, "misc", "rings_dust0", 1000f, 1, Color.white, 1000f, 7500, 200f, null, null);
        system.addRingBand(systemstablepoint, "misc", "rings_asteroids0", 500f, 0, Color.white, 500f, 7550, 200f, null, null);
        system.addAsteroidBelt(systemstablepoint, 250, 7525, 850, 200, 200, Terrain.ASTEROID_BELT, "Death belt");

        // PLANET REMAINS
        SectorEntityToken niltrof_field1 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldParams(
                        800f, // min radius
                        1000f, // max radius
                        60, // min asteroid count
                        80, // max asteroid count
                        4f, // min asteroid radius
                        35f, // max asteroid radius
                        "Unknown planet remains")); // null for default name

        niltrof_field1.setCircularOrbit(systemstablepoint, 180, 7525, 200);

        // PLANET REMAINS TAIL
        SectorEntityToken niltrof_field2 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldParams(
                        600f, // min radius
                        300f, // max radius
                        30, // min asteroid count
                        50, // max asteroid count
                        4f, // min asteroid radius
                        25f, // max asteroid radius
                        "Unknown planet remains")); // null for default name

        niltrof_field2.setCircularOrbit(systemstablepoint, 185, 7525, 200);

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
        niltrof_nebula1.setCircularOrbit(systemstablepoint, 180, 7525, 200);



        //SATELITES

        // Lycaon Relay
        SectorEntityToken relay1 = system.addCustomEntity("lycaon_relay", // unique id
                "Lycaon Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay", // type of object, defined in custom_entities.json
                "unsf_faction"); // faction
        relay1.setCircularOrbit(system.getEntityById("lycaon_star"), 200, 4000, 65);

        // Nyxara Sensor Array
        SectorEntityToken relay2 = system.addCustomEntity("nyxara_sensor_array", // unique id
                "Nyxara Sensor Array", // name - if null, defaultName from custom_entities.json will be used
                "sensor_array", // type of object, defined in custom_entities.json
                "unsf_faction"); // faction
        relay2.setCircularOrbit(system.getEntityById("nyxara_star"), 200, 4000, 65);

        // Solgar Nav Buoy
        SectorEntityToken relay3 = system.addCustomEntity("solgar_nav_buoy", // unique id
                "Solgar Nav Buoy", // name - if null, defaultName from custom_entities.json will be used
                "nav_buoy", // type of object, defined in custom_entities.json
                "unsf_faction"); // faction
        relay3.setCircularOrbit(system.getEntityById("solgar_star"), 200, 4000, 65);













        // IGNARA GAS GIANT JUMP POINT
        JumpPointAPI ignaraJumpPoint = Global.getFactory().createJumpPoint("niltrof_jump1", "Niltrof hyperspace jump point");
        ignaraJumpPoint.setCircularOrbit(system.getEntityById("solgar_1"), 200-60, 1000, 120);
        ignaraJumpPoint.setRelatedPlanet(solgar_1);

        ignaraJumpPoint.setStandardWormholeToHyperspaceVisual();
        system.addEntity(ignaraJumpPoint);


        // Autogenerate hyperspace jump points - FUCK THAT - FALSES YOU
        system.autogenerateHyperspaceJumpPoints(false, false);











        // MARKETS

        //Blown up planet mining station
        SectorEntityToken AegirgastStation = system.addCustomEntity("aegirgast_station", "Aegirgast Station", "industrial_station_1","unsf_faction");
        AegirgastStation.setCircularOrbitWithSpin(systemstablepoint, 180, 7525f, 200, 3f, 5f);
        AegirgastStation.setInteractionImage("illustrations", "aegirgast_station");
        AegirgastStation.setCustomDescriptionId("unsf_aegirgast_station");

        //Blown up planet mining station
        SectorEntityToken NidavellirStation = system.addCustomEntity("nidavellir_station", "Nidavellir Station", "industrial_station_1","unsf_faction");
        //NidavellirStation.setCircularOrbitWithSpin(nyxara_star, 270, 600f, 15, 0f, 0f);
        NidavellirStation.setCircularOrbitPointingDown(system.getEntityById("nyxara_star"), 270, 600f, 15);
        NidavellirStation.setInteractionImage("illustrations", "orbital");
        NidavellirStation.setCustomDescriptionId("unsf_nidavellir_station");

        SectorEntityToken AtlantisStation = system.addCustomEntity("atlantis_station", "Atlantis Station", "atlantis_station","unsf_faction");
        //NidavellirStation.setCircularOrbitWithSpin(nyxara_star, 270, 600f, 15, 0f, 0f);
        AtlantisStation.setCircularOrbitPointingDown(system.getEntityById("lycaon_star"), 270, 5000f, 150);
        AtlantisStation.setInteractionImage("illustrations", "atlantis_station");
        AtlantisStation.setCustomDescriptionId("unsf_atlantis_station");


        //ATLANTIS


        MarketAPI atlantis_market = Global.getFactory().createMarket("atlantis_station_market", AtlantisStation.getName(), 0);
        atlantis_market.setSize(6);
        atlantis_market.setFactionId("unsf_faction");


        atlantis_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        atlantis_market.setPrimaryEntity(AtlantisStation);

        atlantis_market.setFactionId(AtlantisStation.getFaction().getId());
        atlantis_market.addCondition(Conditions.POPULATION_6);
        atlantis_market.addCondition(Conditions.HABITABLE);

        Misc.setFullySurveyed(atlantis_market, null, true);
        atlantis_market.addIndustry(Industries.POPULATION);
        atlantis_market.addIndustry(Industries.MEGAPORT);
        atlantis_market.addIndustry(Industries.HEAVYBATTERIES);
        atlantis_market.addIndustry(Industries.HIGHCOMMAND);
        atlantis_market.addIndustry(Industries.PLANETARYSHIELD);
        atlantis_market.addIndustry("stargate_complex");


        atlantis_market.getIndustry("heavybatteries").setSpecialItem(new SpecialItemData("drone_replicator", null));
        atlantis_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);




        atlantis_market.addSubmarket("unsf_submarket");
        atlantis_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        atlantis_market.getTariff().modifyFlat("default_tariff", atlantis_market.getFaction().getTariffFraction());

        AtlantisStation.setMarket(atlantis_market);

        Global.getSector().getEconomy().addMarket(atlantis_market, true);

        //todo THIS ADDS CUSTOM MUSIC TO MARKET !!!!!!!!!!!!!!!!!!!!!
        atlantis_market.getMemoryWithoutUpdate().set(MusicPlayerPluginImpl.MUSIC_SET_MEM_KEY, "AtlantisStation_music");

        //AEGIRAST


        //todo Mining station market code - !!! USE AS TEMPLATE FOR CUSTOM MARKETS FOR OTHER PLANETS/STATIONS !!!!
        MarketAPI aegirast_market = Global.getFactory().createMarket("aegirgast_station_market", AegirgastStation.getName(), 0);
        aegirast_market.setSize(5);
        aegirast_market.setFactionId("unsf_faction");

        aegirast_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        aegirast_market.setPrimaryEntity(AegirgastStation);

        aegirast_market.setFactionId(AegirgastStation.getFaction().getId());
        aegirast_market.addCondition(Conditions.POPULATION_5);
        aegirast_market.addCondition(Conditions.ORE_ULTRARICH);
        aegirast_market.addCondition(Conditions.RARE_ORE_ULTRARICH);
        aegirast_market.addCondition(Conditions.VOLATILES_PLENTIFUL);
        aegirast_market.addCondition(Conditions.ORGANICS_PLENTIFUL);

        Misc.setFullySurveyed(aegirast_market, null, true);
        aegirast_market.addIndustry(Industries.POPULATION);
        aegirast_market.addIndustry(Industries.MEGAPORT);
        aegirast_market.addIndustry(Industries.MINING);
        aegirast_market.addIndustry(Industries.HIGHCOMMAND);
        aegirast_market.addIndustry(Industries.HEAVYBATTERIES);
        aegirast_market.addIndustry("stargate_complex");


        aegirast_market.getIndustry("heavybatteries").setSpecialItem(new SpecialItemData("drone_replicator", null));
        aegirast_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);

        aegirast_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        aegirast_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        aegirast_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        aegirast_market.getTariff().modifyFlat("default_tariff", aegirast_market.getFaction().getTariffFraction());

        AegirgastStation.setMarket(aegirast_market);

        Global.getSector().getEconomy().addMarket(aegirast_market, true);





















        //NIDAVELLIR


        //Mining station market code - !!! USE AS TEMPLATE FOR CUSTOM MARKETS FOR OTHER PLANETS/STATIONS !!!!
        MarketAPI nidavellir_market = Global.getFactory().createMarket("nidavellir_station_market", NidavellirStation.getName(), 0);
        nidavellir_market.setSize(5);
        nidavellir_market.setFactionId("unsf_faction");

        nidavellir_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        nidavellir_market.setPrimaryEntity(NidavellirStation);

        nidavellir_market.setFactionId(NidavellirStation.getFaction().getId());
        nidavellir_market.addCondition(Conditions.POPULATION_5);

        Misc.setFullySurveyed(nidavellir_market, null, true);
        nidavellir_market.addIndustry(Industries.POPULATION);
        nidavellir_market.addIndustry(Industries.MEGAPORT);
        nidavellir_market.addIndustry(Industries.REFINING);
        nidavellir_market.addIndustry(Industries.ORBITALWORKS);
        nidavellir_market.addIndustry(Industries.HIGHCOMMAND);
        nidavellir_market.addIndustry(Industries.HEAVYBATTERIES);
        nidavellir_market.addIndustry("stargate_complex");
        /*  nidavellir_market.getIndustry("stargate_complex").setSpecialItem(new SpecialItemData("unsf_zpm", null)); */ // THIS BITCH IS TO ADD ZPMS TO INDUSTIRES

        nidavellir_market.getIndustry("orbitalworks").setSpecialItem(new SpecialItemData("pristine_nanoforge", null));
        nidavellir_market.getIndustry("refining").setSpecialItem(new SpecialItemData("catalytic_core", null));
        nidavellir_market.getIndustry("heavybatteries").setSpecialItem(new SpecialItemData("drone_replicator", null));
        nidavellir_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);

        nidavellir_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        nidavellir_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        nidavellir_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        nidavellir_market.getTariff().modifyFlat("default_tariff", nidavellir_market.getFaction().getTariffFraction());

        NidavellirStation.setMarket(nidavellir_market);

        Global.getSector().getEconomy().addMarket(nidavellir_market, true);














        //OBERON


        MarketAPI oberon_market = Global.getFactory().createMarket("oberon_planet_market", lycaon_1.getName(), 0);
        oberon_market.setSize(5);
        oberon_market.setFactionId("unsf_faction");
        oberon_market.setPrimaryEntity(lycaon_1);
        oberon_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        //oberon_market.setFactionId(lycaon_1.getFaction().getId());
        oberon_market.addCondition(Conditions.POPULATION_5);
        oberon_market.addCondition(Conditions.HABITABLE);
        oberon_market.addCondition(Conditions.MILD_CLIMATE);
        oberon_market.addCondition(Conditions.WATER_SURFACE);
        oberon_market.addCondition(Conditions.RUINS_VAST);
        oberon_market.addCondition(Conditions.REGIONAL_CAPITAL);

        Misc.setFullySurveyed(oberon_market, null, true);
        oberon_market.addIndustry(Industries.POPULATION);
        oberon_market.addIndustry(Industries.MEGAPORT);
        oberon_market.addIndustry(Industries.AQUACULTURE);
        oberon_market.addIndustry(Industries.LIGHTINDUSTRY);
        oberon_market.addIndustry(Industries.HIGHCOMMAND);
        oberon_market.addIndustry(Industries.STARFORTRESS_HIGH);
        oberon_market.addIndustry(Industries.HEAVYBATTERIES);
        oberon_market.addIndustry("stargate_complex");
        /*  nidavellir_market.getIndustry("stargate_complex").setSpecialItem(new SpecialItemData("unsf_zpm", null)); */ // THIS BITCH IS TO ADD ZPMS TO INDUSTIRES

        oberon_market.getIndustry("lightindustry").setSpecialItem(new SpecialItemData("biofactory_embryo", null));
        oberon_market.getIndustry("heavybatteries").setSpecialItem(new SpecialItemData("drone_replicator", null));


        oberon_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        oberon_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        oberon_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        oberon_market.getTariff().modifyFlat("default_tariff", oberon_market.getFaction().getTariffFraction());

        lycaon_1.setMarket(oberon_market);
        Global.getSector().getEconomy().addMarket(oberon_market, true);
        oberon_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);
        oberon_market.getIndustry(Industries.STARFORTRESS_HIGH).setAICoreId(Commodities.ALPHA_CORE);



        MarketAPI argos_market = Global.getFactory().createMarket("argos_planet_market", nyxara_1.getName(), 0);
        argos_market.setSize(5);
        argos_market.setFactionId("unsf_faction");
        argos_market.setPrimaryEntity(nyxara_1);
        argos_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        //oberon_market.setFactionId(lycaon_1.getFaction().getId());
        argos_market.addCondition(Conditions.POPULATION_4);
        argos_market.addCondition(Conditions.HABITABLE);
        argos_market.addCondition(Conditions.MILD_CLIMATE);
        argos_market.addCondition(Conditions.VERY_COLD);
        argos_market.addCondition(Conditions.FREE_PORT);
        argos_market.addCondition(Conditions.RUINS_EXTENSIVE);

        Misc.setFullySurveyed(argos_market, null, true);
        argos_market.addIndustry(Industries.POPULATION);
        argos_market.addIndustry(Industries.MEGAPORT);
        argos_market.addIndustry(Industries.FUELPROD);
        argos_market.addIndustry(Industries.HIGHCOMMAND);
        argos_market.addIndustry(Industries.HEAVYBATTERIES);
        argos_market.addIndustry("stargate_complex");
        /*  nidavellir_market.getIndustry("stargate_complex").setSpecialItem(new SpecialItemData("unsf_zpm", null)); */ // THIS BITCH IS TO ADD ZPMS TO INDUSTIRES


        argos_market.getIndustry("heavybatteries").setSpecialItem(new SpecialItemData("drone_replicator", null));
        argos_market.getIndustry("fuelprod").setSpecialItem(new SpecialItemData("unsf_zpm", null));

        argos_market.addSubmarket(Submarkets.SUBMARKET_OPEN);
        argos_market.addSubmarket(Submarkets.SUBMARKET_BLACK);
        argos_market.addSubmarket(Submarkets.SUBMARKET_STORAGE);
        argos_market.getTariff().modifyFlat("default_tariff", argos_market.getFaction().getTariffFraction());

        nyxara_1.setMarket(argos_market);
        Global.getSector().getEconomy().addMarket(argos_market, true);
        argos_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);




















        /* ideas for more planets
        Main star -

        Yellow star - habitable, planet, military powerhouse, staging ground, city underground
        

        Industries  - orbital shipyards creates hella hulls

                    

         */
    }
}

