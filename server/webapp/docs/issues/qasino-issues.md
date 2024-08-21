# Tech Issues

- no nice error handling
- better use of patterns - rules 
- dry,kiss,yagni
- use of toolkit


# Business

- League functions
- Multiplayer
- Another cardgame

## handy sql
```sql

-- PLAYERS for game with optional PLAYING and CARDMOVES if any
SELECT 
g."game_id", p."seat", c."sequence",
g."type", g."updated", g."state", g."ante", g."initiator",
p."player_id", p."role", p."fiches",p."ai_level",
t."playing_id", t."created",
c."cardmove_id", c."move",
FROM 
"game" AS g
left JOIN "player" AS p ON g."game_id" = p."game_id"
left JOIN "playing" AS t ON t."game_id" = g."game_id" 
left JOIN "cardmove" AS c ON t."playing_id" = c."playing_id" AND c."player_id" = p."player_id"
WHERE g."game_id" = 12
ORDER BY 
p."seat", c."sequence";

-- GAMES for initiater
SELECT 
g."game_id", c."sequence",
g."type", g."updated", g."state", g."ante", g."initiator",
t."playing_id", t."created",
c."cardmove_id", c."move", c."created",c."card_move_details",c."card_id",c."bet",c."start_fiches",c."end_fiches",
p."player_id",p."role",p."seat",p."fiches",p."ai_level"
FROM 
"game" AS g
left JOIN "playing" AS t ON t."game_id" = g."game_id" 
left JOIN "cardmove" AS c ON t."playing_id" = c."playing_id" 
left JOIN "player" AS p ON c."player_id" = p."player_id"
WHERE g."initiator" = 3
ORDER BY g."game_id" desc, c."sequence";

-- ROLES for visitor
SELECT 
v."visitor_id", v."username",
r."role_id", r."name",
p."privilege_id", p."name"
FROM "visitor" AS v 
left JOIN "users_roles" AS ur ON v."visitor_id" = ur."visitor_id" 
left JOIN "role" AS r ON r."role_id" = ur."role_id"
left JOIN "roles_privileges" AS rp ON rp."role_id" = r."role_id"
left JOIN "privilege" AS p ON p."privilege_id" = rp."privilege_id"
ORDER BY v."visitor_id";

-- PLAYING for visitor
SELECT 
v."visitor_id", v."username",
g."game_id", g."state",
t."playing_id", t."current_round_number",  t."current_seat_number",  t."current_move_number", 
p."player_id", p."ai_level", p."fiches"
FROM "visitor" AS v 
left JOIN "game" AS g ON v."visitor_id" = g."initiator" 
left JOIN "playing" AS t ON t."game_id" = g."game_id"
left JOIN "player" AS p ON p."player_id" = t."player_id"
ORDER BY v."visitor_id";

UPDATE "card" 
SET  "player_id" = '1' 
WHERE "card_id" = 2;

UPDATE "game" 
SET  
    "state" = 'STARTED' ,  
    "initiator" = 3
WHERE "game_id" = 11;

DELETE "visitor" 
WHERE "visitor_id" = 14;

INSERT INTO "league" ("created", "name", "name_seq", "visitor_id", "is_active", "ended")
VALUES ( '20240712-10:00-15114', 'leagueName', '1', '3', 'true', null);

UPDATE "game" 
SET  "league_id" = '1' 
WHERE "game_id" = '6';

```

