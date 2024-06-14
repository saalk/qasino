# Issues

## 1 - no nice error handling with toast
error no message eg Action [VALIDATE] invalid - game has incorrect ante of 0

## 2 stats overall and for selected player
count all/you/active

## handy sql
```sql

SELECT 
g."type", g."updated", g."state", g."ante", g."initiator",
t."turn_id", t."created",
c."sequence",c."cardmove_id",c."created",c."card_move_details",c."card_id",c."bet",c."start_fiches",c."end_fiches",
p."player_id",p."role",p."seat",p."fiches",p."ai_level"
FROM 
"game" AS g
left JOIN "turn" AS t ON t."game_id" = g."game_id" 
left JOIN "cardmove" AS c ON t."turn_id" = c."turn_id" 
left JOIN "player" AS p ON c."player_id" = p."player_id"

WHERE g."game_id" = 1
ORDER BY c."sequence";

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

UPDATE "card" 
SET  "player_id" = '1' 
WHERE "card_id" = 2;

UPDATE "game" 
SET  "state" = 'STARTED' 
WHERE "game_id" = 15;

DELETE "visitor" 
WHERE "visitor_id" = 14;
```

