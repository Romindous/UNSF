package TrueAvarus.UNSF.NPCs;

import TrueAvarus.UNSF.Constants.Factions;
import TrueAvarus.UNSF.World.Systems.Niltrof;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;

public class People {

    // ATLANTIS PERSONEL
    public static final String BOSS_ID = "unsf_boss";
    public static final String SHADY_ID = "unsf_shady";

    public static void createAtlantisPersonnel() {

        final ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        final MarketAPI market;

        market = Global.getSector().getEconomy().getMarket(Niltrof.ATLANTIS + "_market");
        if (market != null) {

            final PersonAPI boss = Global.getFactory().createPerson();
            boss.setId(BOSS_ID);
            boss.setImportance(PersonImportance.VERY_HIGH);
            boss.setFaction(Factions.UNSF);
            boss.setGender(FullName.Gender.FEMALE);
            boss.setRankId(Ranks.CITIZEN);
            boss.setPostId(Ranks.POST_FACTION_LEADER);
            boss.getName().setFirst("FirstName");
            boss.getName().setLast("LastName");
            boss.setPortraitSprite(Global.getSettings().getSpriteName("characters", "baird"));

            market.setAdmin(boss);
            market.getCommDirectory().addPerson(boss, 0);
            market.addPerson(boss);
            ip.addPerson(boss);



            final PersonAPI shady = Global.getFactory().createPerson();
            shady.setId(SHADY_ID);
            shady.setImportance(PersonImportance.VERY_LOW);
            shady.setFaction(Factions.UNSF);
            shady.setGender(FullName.Gender.FEMALE);
            shady.setRankId(Ranks.CITIZEN);
            shady.setPostId(Ranks.POST_SHADY);
            shady.getName().setFirst("Kiera");
            shady.getName().setLast("Sheppard");
            shady.setPortraitSprite(Global.getSettings().getSpriteName("characters", "unsf_kiera"));


            market.getCommDirectory().addPerson(shady, 1);
            market.addPerson(shady);
            ip.addPerson(shady);



        }
    }

}



