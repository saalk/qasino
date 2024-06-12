package cloud.qasino.games.pattern.prototype;

import cloud.qasino.games.database.entity.enums.player.AiLevel;

import java.util.Hashtable;

public class BotRegistration {
    // A HashTable that contains the initial model object from which we clone from
    public static Hashtable<String, Bot<?>> showMap = new Hashtable<>();

    // Instantiates the initial objects from which we clone from
    public static void loadCache() {
//        final DumbBot dumbBot = new DumbBot();
//        final AverageBot averageBot = new AverageBot();
//        showMap.put("DumbBot", dumbBot);
//        showMap.put("AverageBot", averageBot);
    }

    // Returns clone of object stored in showMap to client
    public static Bot<?> getBot(AiLevel ai) throws CloneNotSupportedException {
        // Switch statement to find out which clone is needed
        switch (ai) {
            case AVERAGE:
                AverageBot averageBotInCache = (AverageBot) showMap.get(ai);
                return averageBotInCache.clone();
            case DUMB:
                DumbBot dumbBotInCache = (DumbBot) showMap.get(ai);
                return dumbBotInCache.clone();
            default:
//                throw new ShowIdNotRecognisedException("Unable to get show: " + ai);
        }
        return null;
    }
}
