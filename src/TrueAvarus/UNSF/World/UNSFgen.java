package TrueAvarus.UNSF.World;


//import TrueAvarus.UNSF.World.Systems.nebelheim;

import TrueAvarus.UNSF.World.Systems.niltrof;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;

public class UNSFgen {

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
        FactionAPI myfaction= sector.getFaction("unsf_faction");

        myfaction.setRelationship(path.getId(), RepLevel.SUSPICIOUS);
        myfaction.setRelationship(hegemony.getId(), RepLevel.FAVORABLE);
        myfaction.setRelationship(pirates.getId(), RepLevel.HOSTILE);
        myfaction.setRelationship(tritachyon.getId(), RepLevel.FAVORABLE);
        myfaction.setRelationship(church.getId(), RepLevel.INHOSPITABLE);
        myfaction.setRelationship(kol.getId(), RepLevel.FAVORABLE);
        myfaction.setRelationship(league.getId(), RepLevel.SUSPICIOUS);

    }
}