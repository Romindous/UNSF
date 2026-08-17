package TrueAvarus.UNSF.World;


//import TrueAvarus.UNSF.World.Systems.nebelheim;

import TrueAvarus.UNSF.Constants.Factions;
import TrueAvarus.UNSF.World.Systems.niltrof;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;

public class UNSFGen {

    // THIS IS WHERE YOU PUT REGISTER FOR NEW SYSTEMS YOU BROTHER BLOWING SISTER FUCKING NAKED MOTHER WATCHING ALABAMA ENJOYING DUMB FUCK MORON
    public void generate(SectorAPI sector) {
        initFactionRelationships(sector);
        //new nebelheim().generate(sector);
        new niltrof().generate(sector);
    }

    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI hegemony = sector.getFaction(Factions.HEGEMONY);
        FactionAPI tritachyon = sector.getFaction(Factions.TRITACHYON);
        FactionAPI pirates = sector.getFaction(Factions.PIRATES);
        FactionAPI kol = sector.getFaction(Factions.KOL);
        FactionAPI church = sector.getFaction(Factions.LUDDIC_CHURCH);
        FactionAPI path = sector.getFaction(Factions.LUDDIC_PATH);
        FactionAPI league = sector.getFaction(Factions.PERSEAN);
        FactionAPI myfaction= sector.getFaction(Factions.UNSF);

        myfaction.setRelationship(path.getId(), RepLevel.INHOSPITABLE);
        myfaction.setRelationship(hegemony.getId(), RepLevel.FAVORABLE);
        myfaction.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        myfaction.setRelationship(tritachyon.getId(), RepLevel.SUSPICIOUS);
        myfaction.setRelationship(church.getId(), RepLevel.SUSPICIOUS);
        myfaction.setRelationship(kol.getId(), RepLevel.FAVORABLE);
        myfaction.setRelationship(league.getId(), RepLevel.FAVORABLE);

    }
}