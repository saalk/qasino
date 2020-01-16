package cloud.qasino.card.domain.qasino.style;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MaxAnteTest {

    @Test
    public void callFromLabelWithString() {

        assertThrows(NullPointerException.class, () ->MaxAnte.fromLabel("").getLabel());
        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabelWithDefault("").getLabel());

        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabel("h").getLabel());
        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabel("H").getLabel());
        assertEquals(MaxAnte.NORMAL.getLabel(), MaxAnte.fromLabel("N").getLabel());

        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabelWithDefault("x").getLabel());

    }

    @Test
    void callFromLabelWithChar() {
        char space = ' ';
        char h = 'h';
        char H = 'H';
        char N = 'N';
        char x = 'x';

        assertThrows(NullPointerException.class, () ->MaxAnte.fromLabel(space).getLabel());
        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabelWithDefault(space).getLabel());

        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabel(h).getLabel());
        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabel(H).getLabel());
        assertEquals(MaxAnte.NORMAL.getLabel(), MaxAnte.fromLabel(N).getLabel());

        assertEquals(MaxAnte.HIGHEST_WINS.getLabel(), MaxAnte.fromLabelWithDefault(x).getLabel());

    }
}
