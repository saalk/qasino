package cloud.qasino.games.database.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static org.assertj.core.api.Assertions.assertThat;

class LeagueTest extends QasinoSimulator{

    @Test
    public void givenQasinoLeague_whenCreated_thenReturnValidObjectValues() {

        assertThat(league.getName()).isEqualTo("leagueName");
        assertThat(league.getNameSequence()).isEqualTo(1);
        assertThat(league.isActive()).isEqualTo(true);
        // leagues end by default at the end of the month
        LocalDateTime localDateAndTime = LocalDateTime.now().with(lastDayOfMonth());;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        String result = localDateAndTime.format(formatter);
        String ended = result.substring(0, 8); // check date of end of month as time goes by
        assertThat(league.getEnded().substring(0,8)).isEqualTo(ended);

        // close the league
        league.closeLeaguePerYesterday();

        localDateAndTime = LocalDateTime.now().plusDays(-1); // yesterday
        formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HH:mm-ssSSS-nnnnnnnnn");
        result = localDateAndTime.format(formatter);
        ended = result.substring(0, 8); // check date of yesterday as time goes by
        assertThat(league.getEnded().substring(0,8)).isEqualTo(ended);
        assertThat(league.isActive()).isEqualTo(false);

        // cleanup for next tests and reset the defaults
        league.setActive(true);
        league.endLeagueThisMonth();
    }

}