create sequence "hibernate_sequence" start with 1 increment by 1;

    create table "card" (
       "card_id" integer not null,
        "card" varchar(3) not null,
        "created" varchar(25),
        "face" varchar(255) not null,
        "location" varchar(255) not null,
        "position" varchar(255) not null,
        "sequence" integer,
        "game_id" integer not null,
        "player_id" integer,
        primary key ("card_id")
    );

    create table "cardmove" (
       "cardmove_id" integer not null,
        "bet" integer,
        "card_id" varchar(255),
        "card_move_details" varchar(255),
        "created" varchar(25),
        "location" varchar(255),
        "move" varchar(255) not null,
        "move_number" integer,
        "player_id" integer,
        "round_number" integer,
        "turn_id" integer,
        primary key ("cardmove_id")
    );

    create table "game" (
       "game_id" integer not null,
        "ante" integer,
        "day" integer,
        "initiator" integer,
        "month" integer,
        "previous_state" varchar(50),
        "state" varchar(50) not null,
        "style" varchar(10),
        "type" varchar(50) not null,
        "updated" varchar(25) not null,
        "week" varchar(3),
        "year" integer,
        "league_id" integer,
        primary key ("game_id")
    );

    create table "league" (
       "league_id" integer not null,
        "is_active" boolean,
        "created" varchar(25),
        "ended" varchar(25),
        "name" varchar(50) not null,
        "name_seq" integer,
        "visitor_id" integer not null,
        primary key ("league_id")
    );

    create table "player" (
       "player_id" integer not null,
        "ai_level" varchar(50),
        "avatar" varchar(50),
        "created" varchar(25),
        "fiches" integer,
        "is_human" boolean,
        "role" varchar(20) not null,
        "seat" integer,
        "is_winner" boolean,
        "game_id" integer,
        "visitor_id" integer,
        primary key ("player_id")
    );

    create table "result" (
       "result_id" integer not null,
        "created" varchar(25),
        "day" integer,
        "fiches_won" integer,
        "month" integer,
        "type" varchar(50) not null,
        "week" varchar(3),
        "year" integer,
        "game_id" integer not null,
        "player_id" integer not null,
        "visitor_id" integer,
        primary key ("result_id")
    );

    create table "turn" (
       "turn_id" integer not null,
        "active_player_id" integer,
        "current_move_number" integer,
        "current_round_number" integer,
        "day" integer,
        "month" integer,
        "created" varchar(25),
        "week" varchar(3),
        "year" integer,
        "game_id" integer not null,
        primary key ("turn_id")
    );

    create table "visitors" (
       "visitor_id" integer not null,
        "balance" integer,
        "created" varchar(25),
        "day" integer,
        "email" varchar(50),
        "month" integer,
        "secured_loan" integer,
        "visitor_name" varchar(50) not null,
        "visitor_name_seq" integer,
        "week" varchar(3),
        "year" integer,
        primary key ("visitor_id")
    );
create index "cardmove_turn_index" on "cardmove" ("turn_id");
create index "turns_game_index" on "turn" ("game_id");
create index "visitorName_index" on "visitors" ("visitor_name");

    alter table "card" 
       add constraint fk_game_id 
       foreign key ("game_id") 
       references "game";

    alter table "card" 
       add constraint fk_player_id 
       foreign key ("player_id") 
       references "player";

    alter table "cardmove" 
       add constraint fk_turn_id 
       foreign key ("turn_id") 
       references "turn";

    alter table "game" 
       add constraint fk_league_id 
       foreign key ("league_id") 
       references "league";

    alter table "league" 
       add constraint fk_visitor_id 
       foreign key ("visitor_id") 
       references "visitors";

    alter table "player" 
       add constraint fk_game_id 
       foreign key ("game_id") 
       references "game";

    alter table "player" 
       add constraint fk_visitor_id 
       foreign key ("visitor_id") 
       references "visitors";

    alter table "result" 
       add constraint fk_game_id 
       foreign key ("game_id") 
       references "game";

    alter table "result" 
       add constraint fk_player_id 
       foreign key ("player_id") 
       references "player";

    alter table "result" 
       add constraint fk_visitor_id 
       foreign key ("visitor_id") 
       references "visitors";

    alter table "turn" 
       add constraint fk_game_id 
       foreign key ("game_id") 
       references "game";
