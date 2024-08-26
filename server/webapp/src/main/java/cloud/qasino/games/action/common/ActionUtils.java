package cloud.qasino.games.action.common;

import cloud.qasino.games.dto.model.PlayerDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ActionUtils  {

    // function to sort hashmap by values
    public static HashMap<Long, Integer> sortByValueDesc(HashMap<Long, Integer> unsortedMap) {
        HashMap<Long, Integer> sortedMap
                = unsortedMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
        return sortedMap;
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

