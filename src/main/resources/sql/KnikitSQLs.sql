-- Get record counts for all tables in MySQL database
SELECT table_name, table_rows
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'knikit';

-- get the game, casino combinations
select g.GAME_ID, g.GAMETYPE, g.STATE, c.PLAYER_ID, c.PLAYING_ORDER
from knikit.GAME g join knikit.CASINO c
on g.GAME_ID = c.GAME_ID
order by c.PLAYING_ORDER asc;

-- get the game, deck combinations
select g.GAME_ID, g.GAMETYPE, g.STATE, d.DECK_ID, d.CARD_ID, d.CARD_ORDER, d.PLAYER_ID
from knikit.GAME g join knikit.DECK d
on g.GAME_ID = d.GAME_ID
order by d.CARD_ORDER asc;