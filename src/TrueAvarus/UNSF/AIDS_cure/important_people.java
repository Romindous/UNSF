package TrueAvarus.UNSF.AIDS_cure;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.People;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.missions.hub.BaseMissionHub;

public class important_people {

    // ATLANTIS PERSONEL
    public static String ATLANTIS_BOSS = "atlantis_boss";
    public static String ATLANTIS_SHADY = "atlantis_shady";


    public static void createAtlantisPersonnel() {

        ImportantPeopleAPI ip = Global.getSector().getImportantPeople();
        MarketAPI market;

        market = Global.getSector().getEconomy().getMarket("atlantis_station_market");
        if (market != null) {

        PersonAPI person = Global.getFactory().createPerson();
        person.setId(ATLANTIS_BOSS);
        person.setImportance(PersonImportance.VERY_HIGH);
        person.setFaction("unsf_faction");
        person.setGender(FullName.Gender.FEMALE);
        person.setRankId(Ranks.CITIZEN);
        person.setPostId(Ranks.POST_FACTION_LEADER);
        person.getName().setFirst("FirstName");
        person.getName().setLast("LastName");
        person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "baird"));

        market.setAdmin(person);
        market.getCommDirectory().addPerson(person, 0);
        market.addPerson(person);
        ip.addPerson(person);



        PersonAPI person2 = Global.getFactory().createPerson();
        person2.setId(ATLANTIS_SHADY);
        person2.setImportance(PersonImportance.VERY_LOW);
        person2.setFaction("unsf_faction");
        person2.setGender(FullName.Gender.FEMALE);
        person2.setRankId(Ranks.CITIZEN);
        person2.setPostId(Ranks.UNKNOWN);
        person2.getName().setFirst("Kiera");
        person2.getName().setLast("Sheppard");
        person2.setPortraitSprite(Global.getSettings().getSpriteName("characters", "kiera"));


        market.getCommDirectory().addPerson(person2, 1);
        market.addPerson(person2);
        ip.addPerson(person2);



        }
    }

    }



