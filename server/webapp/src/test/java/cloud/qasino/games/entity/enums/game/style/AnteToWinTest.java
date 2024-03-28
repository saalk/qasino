package cloud.qasino.games.entity.enums.game.style;


import cloud.qasino.games.database.entity.enums.game.style.AnteToWin;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnteToWinTest {

    @Test
    public void callFromLabelWithString() {

        assertThrows(NullPointerException.class, () -> AnteToWin.fromLabel("").getLabel());
        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabelWithDefault("").getLabel());

        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabel("n").getLabel());
        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabel("N").getLabel());
        assertEquals(AnteToWin.FIVE_TIMES_WINS.getLabel(), AnteToWin.fromLabel("5").getLabel());

        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabelWithDefault("x").getLabel());

    }

    @Test
    void callFromLabelWithChar() {
        char space = ' ';
        char F = '5';
        char N = 'N';
        char n = 'n';
        char x = 'x';

        assertThrows(NullPointerException.class, () -> AnteToWin.fromLabel(space).getLabel());
        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabelWithDefault(space).getLabel());

        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabel(N).getLabel());
        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabel(n).getLabel());
        assertEquals(AnteToWin.FIVE_TIMES_WINS.getLabel(), AnteToWin.fromLabel(F).getLabel());

        assertEquals(AnteToWin.NA.getLabel(), AnteToWin.fromLabelWithDefault(x).getLabel());

    }
}
