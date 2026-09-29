package TrueAvarus.UNSF.Commands;

import java.util.List;
import java.util.Map;
import TrueAvarus.UNSF.UNSFMod;
import TrueAvarus.UNSF.World.Quests.Argonauts;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

public class UNSFCMD extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (params == null) return false;
        final String arg = params.get(0) != null ? params.get(0).getString(memoryMap) : null;
        System.out.println("UNSFCMD was called with arg " + arg);
        final MarketAPI market = (dialog != null && dialog.getInteractionTarget() != null) ? dialog.getInteractionTarget().getMarket() : null;
        if (market == null) return true;
        if ("startMission".equals(arg)) {
            final Argonauts argo = new Argonauts();
            argo.createAndAbortIfFailed(market, true);
            UNSFMod.activeMission(argo);
        }
        return true;
    }
}
