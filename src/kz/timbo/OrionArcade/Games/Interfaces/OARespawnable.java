package kz.timbo.OrionArcade.Games.Interfaces;

import kz.timbo.OrionArcade.Objects.OAPlayer;
import org.bukkit.Location;

public interface OARespawnable {

    public Location getSpawnPoint(OAPlayer player);

}
