package cloud.qasino.games.pattern.prototype;

import cloud.qasino.games.database.entity.enums.player.AiLevel;
import cloud.qasino.games.database.entity.enums.player.Avatar;
import cloud.qasino.games.database.entity.enums.player.PlayerType;
import cloud.qasino.games.pattern.prototype.creational.AverageBot;
import cloud.qasino.games.pattern.prototype.creational.DumbBot;
import cloud.qasino.games.pattern.prototype.creational.SmartBot;

import java.util.Hashtable;

public class BotRegistration {

    // A HashTable that contains the initial model object from which we clone from
    private static final Hashtable<AiLevel, Bot<?>> botPrototypes = new Hashtable<>();

    // Instantiates the initial objects from which we clone from
    static {
        final DumbBot dumbBot = new DumbBot(
                null,
                null,
                PlayerType.BOT,
                0,
                0,
                Avatar.GOBLIN,
                "goblinName",
                AiLevel.DUMB
        );
        final AverageBot averageBot = new AverageBot(
                null,
                null,
                PlayerType.BOT,
                0,
                0,
                Avatar.GOBLIN,
                "goblinName",
                AiLevel.AVERAGE
        );
        final SmartBot smartBot = new SmartBot(
                null,
                null,
                PlayerType.BOT,
                0,
                0,
                Avatar.GOBLIN,
                "goblinName",
                AiLevel.SMART
        );
//        final AverageBot averageBot = new AverageBot();
        botPrototypes.put(AiLevel.DUMB, dumbBot);
        botPrototypes.put(AiLevel.AVERAGE, averageBot);
        botPrototypes.put(AiLevel.SMART, smartBot);
    }

    // Returns clone of object stored in showMap to client
    public static Bot<?> getBot(AiLevel ai) throws CloneNotSupportedException {
        // Switch statement to find out which clone is needed
        return switch (ai) {
            case AVERAGE -> {
                AverageBot averageBotInCache = (AverageBot) botPrototypes.get(ai);
                yield averageBotInCache.clone();
            }
            case DUMB -> {
                DumbBot dumbBotInCache = (DumbBot) botPrototypes.get(ai);
                yield dumbBotInCache.clone();
            }
            case SMART -> {
                SmartBot smartBotInCache = (SmartBot) botPrototypes.get(ai);
                yield smartBotInCache.clone();
            }
            default ->
//                throw new ShowIdNotRecognisedException("Unable to get show: " + ai);
                    null;
        };
    }
}
