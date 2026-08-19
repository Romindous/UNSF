package TrueAvarus.UNSF.World.Quests;

import java.util.Random;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithSearch;
import exerelin.campaign.intel.missions.remnant.RemnantM1;

public class Coordinator {
    /**
     * The tag that is applied to the planet the player must travel to.
     */
    private static final String TAG_DESTINATION_PLANET = "Demo_destination_planet";

    static SectorEntityToken getDestinationPlanet() {
        RemnantM1
        return Global.getSector().getEntityById(TAG_DESTINATION_PLANET);
    }

    /**
     * Called when player starts the bar event.
     */
    static void init(Quest qs) {
        switch (qs) {
            case ARGONAUTS:

                break;
        }
//        chooseAndTagDestinationPlanet();
    }

    /**
     * Player has accepted quest.
     */
    static void start(Quest qs) {
        Global.getSector().getIntelManager().addIntel(new DemoIntel());
    }

    /**
     * Very dumb method that idempotently tags a random planet as the destination.
     */
    private static void chooseAndTagDestinationPlanet() {
        if (getDestinationPlanet() == null) {
            StarSystemAPI randomSystem = Global.getSector().getStarSystems()
                .get(new Random().nextInt(Global.getSector().getStarSystems().size()));
            PlanetAPI randomPlanet = randomSystem.getPlanets()
                .get(new Random().nextInt(randomSystem.getPlanets().size()));
            randomPlanet.addTag(TAG_DESTINATION_PLANET);
        }
    }
}
