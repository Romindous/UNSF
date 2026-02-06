/*

package TrueAvarus.UNSF.World.Systems;



import TrueAvarus.UNSF.dunno.Custom_industries;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.PlanetConditionGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


public class nebelheim {
    public void generate(SectorAPI sector) {

        float planet1Dist = 4000f; //this is the distance the planet is from the star

        StarSystemAPI system = sector.createStarSystem("Nebelheim");

        system.addTag(Tags.THEME_CORE_POPULATED);
        system.getLocation().set(10000, 20000); //position of the system on the map

        system.setBackgroundTextureFilename("graphics/backgrounds/background1.jpg");




        // create the star and generate the hyperspace anchor for this system
        PlanetAPI nebelheim_star = system.initStar("nebelheim_star", // unique id for this star
                "star_white", // id in planets.json
                400f, // radius (in pixels at default zoom)
                700); // corona radius, from star edge
        system.setLightColor(new Color(255, 252, 222)); // light color in entire system, affects all entities

        system.addRingBand(nebelheim_star, "misc", "rings_dust0", 256f, 2, Color.gray, 400f, 11000f, 500f);
        system.addRingBand(nebelheim_star, "misc", "rings_dust0", 256f, 3, Color.gray, 400f, 12000f, 520f);


        PlanetAPI planet1 = system.addPlanet("nebelheim_1",
                nebelheim_star, //what it's orbiting
                "Prima", //name
                "terran", //the planet type (look in planets.json for more)
                360f*(float)Math.random(), //angle
                130f, //radius
                4000, //distance from star
                360f); //how many days to orbit
        planet1.setCustomDescriptionId("Nebelheim"); //for custom descriptions

        PlanetConditionGenerator.generateConditionsForPlanet(planet1, StarAge.YOUNG);


        PlanetAPI planet2 = system.addPlanet("nebelheim_2",
                nebelheim_star,
                "Secunda",
                "desert",
                360f*(float)Math.random(),
                140,
                9000,
                600);

        PlanetConditionGenerator.generateConditionsForPlanet(planet2, StarAge.YOUNG);

        PlanetAPI planet3 = system.addPlanet("nebelheim_3",
                nebelheim_star,
                "Tertia",
                "frozen",
                360f*(float)Math.random(),
                180,
                14000,
                800);

        PlanetConditionGenerator.generateConditionsForPlanet(planet3, StarAge.YOUNG);

        SectorEntityToken stableLoc1 = system.addCustomEntity("nebelheim_star_stableLoc1", "Stable Location", "stable_location", Factions.NEUTRAL);
        stableLoc1.setCircularOrbit(nebelheim_star, 360f*(float)Math.random(),5500, 860);


        system.autogenerateHyperspaceJumpPoints(true, true); //generates jump points

        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin(); //these lines clear the hyperspace clouds around the system
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);




    MarketAPI planet_1_market = addMarketplace("unsf_faction", planet1, null,
            "Prima",
            7,
            new ArrayList<>(
                    Arrays.asList(
                            Conditions.POPULATION_7,
                            Conditions.HABITABLE,
                            Conditions.FARMLAND_BOUNTIFUL,
                            Conditions.ORGANICS_PLENTIFUL,
                            Conditions.ORE_SPARSE,
                            Conditions.REGIONAL_CAPITAL
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            Submarkets.SUBMARKET_OPEN,
                            Submarkets.GENERIC_MILITARY,
                            "unsf_submarket",
                            Submarkets.SUBMARKET_BLACK,
                            Submarkets.SUBMARKET_STORAGE
                    )
            ),
            new ArrayList<>(
                    Arrays.asList(
                            Industries.POPULATION,
                            Industries.MEGAPORT,
                            Industries.MINING,
                            Industries.FARMING,
                            Industries.STARFORTRESS_HIGH,
                            Industries.HEAVYBATTERIES,
                            Custom_industries.STARGATE_COMPLEX,
                            Industries.HIGHCOMMAND
                    )
            ),
            0.3f,
            true,
            true);

        planet_1_market.addIndustry(Industries.ORBITALWORKS,new ArrayList<String>(Arrays.asList(Items.CORRUPTED_NANOFORGE)));
        planet_1_market.getIndustry(Industries.HIGHCOMMAND).setAICoreId(Commodities.ALPHA_CORE);
        planet_1_market.getIndustry(Industries.STARFORTRESS_HIGH).setAICoreId(Commodities.ALPHA_CORE);






        MarketAPI planet_2_market = addMarketplace("unsf_faction", planet2, null,
                "Secunda",
                7,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7,
                                Conditions.HABITABLE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.GENERIC_MILITARY,
                                "unsf_submarket",
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.HEAVYBATTERIES,
                                Industries.HIGHCOMMAND,
                                Industries.HEAVYINDUSTRY,
                                Custom_industries.STARGATE_COMPLEX,
                                Industries.LIGHTINDUSTRY

                        )
                ),
                0.3f,
                true,
                true);



        MarketAPI planet_3_market = addMarketplace("unsf_faction", planet3, null,
                "Tertia",
                7,
                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_7,
                                Conditions.HABITABLE,
                                Conditions.FARMLAND_POOR,
                                Conditions.ORGANICS_TRACE,
                                Conditions.ORE_ULTRARICH,
                                Conditions.RARE_ORE_ULTRARICH,
                                Conditions.VOLATILES_PLENTIFUL
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.GENERIC_MILITARY,
                                "unsf_submarket",
                                Submarkets.SUBMARKET_BLACK,
                                Submarkets.SUBMARKET_STORAGE
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.MINING,
                                Industries.FARMING,
                                Industries.HEAVYBATTERIES,
                                Industries.FUELPROD,
                                Custom_industries.STARGATE_COMPLEX,
                                Industries.HIGHCOMMAND
                        )
                ),
                0.3f,
                true,
                true);






    }






























        //Shorthand function for adding a market
        public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name,
        int size, ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries, float tarrif,
        boolean freePort, boolean withJunkAndChatter) {
            EconomyAPI globalEconomy = Global.getSector().getEconomy();
            String planetID = primaryEntity.getId();
            String marketID = planetID + "_market";

            MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
            newMarket.setFactionId(factionID);
            newMarket.setPrimaryEntity(primaryEntity);
            newMarket.getTariff().modifyFlat("generator", tarrif);

            //Adds submarkets
            if (null != submarkets) {
                for (String market : submarkets) {
                    newMarket.addSubmarket(market);
                }
            }

            //Adds market conditions
            for (String condition : marketConditions) {
                newMarket.addCondition(condition);
            }

            //Add market industries
            for (String industry : industries) {
                newMarket.addIndustry(industry);
            }

            //Sets us to a free port, if we should
            newMarket.setFreePort(freePort);

            //Adds our connected entities, if any
            if (null != connectedEntities) {
                for (SectorEntityToken entity : connectedEntities) {
                    newMarket.getConnectedEntities().add(entity);
                }
            }

            globalEconomy.addMarket(newMarket, withJunkAndChatter);
            primaryEntity.setMarket(newMarket);
            primaryEntity.setFaction(factionID);

            if (null != connectedEntities) {
                for (SectorEntityToken entity : connectedEntities) {
                    entity.setMarket(newMarket);
                    entity.setFaction(factionID);
                }
            }

            //Finally, return the newly-generated market
            return newMarket;
        }
}

*/