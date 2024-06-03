# Issues

## 1 - no nice error handling with toast
error no message eg Action [VALIDATE] invalid - game has incorrect ante of 0

## 2 stats overall and for selected player
count all/you/active

## handy sql
```sql
SELECT 
c.*,
g."state", 
t.*
FROM "turn" AS t 
JOIN "game" AS g
ON t."game_id" = g."game_id"
JOIN "cardmove" AS c 
ON t."turn_id" = c."turn_id"
WHERE g."game_id" = 10;

UPDATE "game" 
SET  "state" = 'STARTED' 
WHERE "game_id" = 15;

DELETE "visitor" 
WHERE "visitor_id" = 14;
```

