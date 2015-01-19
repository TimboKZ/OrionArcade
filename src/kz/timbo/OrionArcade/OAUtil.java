package kz.timbo.OrionArcade;

import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.*;

public class OAUtil {

    public static boolean isSign(Block block) {

        return (block.getType() == Material.SIGN || block.getType() == Material.SIGN_POST || block.getType() == Material.WALL_SIGN);

    }

    public static <K,V extends Comparable<? super V>>
    List<Map.Entry<K, V>> sortByValue(Map<K,V> map) {

        List<Map.Entry<K,V>> sortedEntries = new ArrayList<Map.Entry<K,V>>(map.entrySet());

        Collections.sort(sortedEntries,
                new Comparator<Map.Entry<K,V>>() {
                    @Override
                    public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                        return e2.getValue().compareTo(e1.getValue());
                    }
                }
        );

        return sortedEntries;
    }

}
