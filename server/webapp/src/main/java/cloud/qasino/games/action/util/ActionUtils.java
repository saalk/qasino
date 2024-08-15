package cloud.qasino.games.action.util;

import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.dto.PlayerDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionUtils  {

    // function to sort hashmap by values
    public static HashMap<Long, Integer> sortByValue(HashMap<Long, Integer> hm) {
        HashMap<Long, Integer> temp
                = hm.entrySet()
                .stream()
                .sorted((i1, i2)
                        -> i1.getValue().compareTo(
                        i2.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        return temp;
    }

    public static List<String> printHashMap(HashMap<Long, Integer> hm) {
        List<String> lines = new ArrayList<>();
        for (Map.Entry<Long, Integer> en : hm.entrySet()) {
            lines.add("[" + en.getKey()
                    + "/"
                    + en.getValue()
                    + "]");
        }
        return lines;
    }
    public static PlayerDto findPlayerByPlayerId(Collection<PlayerDto> listPlayers, Long playerId) {
        return listPlayers.stream()
                .filter(p -> playerId.equals(p.getPlayerId()))
                .findFirst()
                .orElse(null);
    }

}

