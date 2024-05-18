package cloud.qasino.games.configuration;

import cloud.qasino.games.database.entity.Game;
import cloud.qasino.games.database.entity.Player;
import cloud.qasino.games.database.security.Visitor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.hibernate.event.spi.*;

import java.time.LocalDateTime;
import java.util.Date;


/**
 * This class replicates the trigger mechanism for the test database. Contains methods that are triggered on insert and
 * update of any table.
 */
public class TestDatabaseTriggerHandler implements PreUpdateEventListener, PreInsertEventListener {

    private static final String CREATION_TIME = "creationTime";
    private static final String UPDATE_TIME = "updateTime";
    private static final String SEQUENCE_NUMBER = "sequenceNumber";

    /**
     * Method that listens on update events and triggered before an entity is updated in the database.
     * Creation time has to be updated if not present to circument issues caused if saveAndFlush inserts
     * during creation.
     * <p>
     * NOTE: only triggers when the stored entity, and the to update entity are not equal.
     *
     * @param event which contains the entity to be altered
     * @return false after the state of the entity is changed
     */
    @SneakyThrows
    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {

        InputData inputData = generateInput(event);

        if (inputData.getDateType() != null) {
            updateGenericFields(event, inputData);
        }
        return false;
    }

    /**
     * Method that listens on update events and triggered before an entity is inserted in the database.
     *
     * @param event which contains the entity to be altered
     * @return false after the state of the entity is changed
     */
    @SneakyThrows
    @Override
    public boolean onPreInsert(PreInsertEvent event) {

        InputData inputData = generateInput(event);

        if (inputData.getDateType() != null) {
            createGenericFields(event, inputData);
        }
        return false;
    }

    private static void createGenericFields(PreInsertEvent event, InputData inputData) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        for (int i = 0; i < propertyNames.length; i++) {
            if (CREATION_TIME.equals(propertyNames[i])) {
                state[i] = generateDate(inputData.getDateType());
            }
        }
    }

    private static void updateGenericFields(PreUpdateEvent event, InputData inputData) {
        Object[] state = event.getState();
        String[] propertyNames = event.getPersister().getEntityMetamodel().getPropertyNames();
        for (int i = 0; i < propertyNames.length; i++) {
            //Handles the case where update overwrites the state inserted by create
            if (CREATION_TIME.equals(propertyNames[i])) {
                if (!inputData.isCreationTimePresent()) {
                    state[i] = generateDate(inputData.getDateType());
                }
            }
            if (UPDATE_TIME.equals(propertyNames[i])) {
                state[i] = generateDate(inputData.getDateType());
            }
            if (SEQUENCE_NUMBER.equals(propertyNames[i])) {
                state[i] = inputData.getSequenceNumber();
            }
        }
    }

    private static InputData generateInput(AbstractPreDatabaseOperationEvent event) throws NoSuchFieldException {
        Object entity = event.getEntity();
        Class<?> type = null;
        boolean isCreationTimePresent = true;
        if (entity instanceof Visitor) {
            if (event instanceof PreUpdateEvent) {
                Visitor requestEntity = (Visitor) entity;
                isCreationTimePresent = requestEntity.getCreated() != null;
            }
            type = Visitor.class.getDeclaredField(CREATION_TIME).getType();
        }

        if (entity instanceof Game) {
            if (event instanceof PreUpdateEvent) {
                Game requestEntity = (Game) entity;
                isCreationTimePresent = requestEntity.getUpdated() != null;
            }
            type = Game.class.getDeclaredField(CREATION_TIME).getType();
        }
        if (entity instanceof Player) {
            if (event instanceof PreUpdateEvent) {
                Player requestEntity = (Player) entity;
                isCreationTimePresent = requestEntity.getCreated() != null;
            }
            type = Player.class.getDeclaredField(CREATION_TIME).getType();
        }

        return InputData.builder()
                .dateType(type)
                .isCreationTimePresent(isCreationTimePresent)
                .build();
    }

    private static Object generateDate(Object date) {
        return isDateClass(date) ? new Date() : LocalDateTime.now();
    }

    private static boolean isDateClass(Object date) {
        return date == Date.class;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    private static class InputData {
        private final Object dateType;
        private final Integer sequenceNumber;
        private final boolean isCreationTimePresent;
    }

}



