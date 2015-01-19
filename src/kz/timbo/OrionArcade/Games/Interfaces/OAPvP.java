package kz.timbo.OrionArcade.Games.Interfaces;

import kz.timbo.OrionArcade.Objects.OAPlayer;

public interface OAPvP {

    public boolean isMeleeAllowed();

    public boolean isBowAllowed();

    public void addKills(OAPlayer player);

    public void addDeaths(OAPlayer player);

}
