
    create table card (
       card_id bigint auto_increment primary key,
        card varchar(3)not null,
        created varchar(25),
        face varchar(255) not null,
        location varchar(255) not null,
        position varchar(255) not null,
        sequence integer,
        game_id bigint not null,
        player_id bigint
    );

    create table cardmove (
       cardmove_id bigint auto_increment primary key,
        bet integer,
        card_id bigint,
        card_move_details varchar(255),
        created varchar(25),
        location varchar(255),
        move varchar(255) not null,
        move_number integer,
        player_id bigint,
        round_number integer,
        turn_id bigint
    );

    create table game (
       game_id bigint auto_increment primary key,
        ante integer,
        day integer,
        initiator bigint,
        month integer,
        previous_state varchar(50),
        state varchar(50) not null,
        style varchar(10),
        type varchar(50) not null,
        updated varchar(25) not null,
        week varchar(3),
        year integer,
        league_id bigint
    );

    create table league (
       league_id bigint auto_increment primary key,
        is_active boolean,
        created varchar(25),
        ended varchar(25),
        name varchar(50) not null,
        name_seq integer,
        visitor_id bigint not null
    );

    create table player (
       player_id bigint auto_increment primary key,
        ai_level varchar(50),
        avatar varchar(50),
        created varchar(25),
        fiches integer,
        is_human boolean,
        role varchar(20) not null,
        seat integer,
        is_winner boolean,
        game_id bigint,
        visitor_id bigint
    );

    create table result (
       result_id bigint auto_increment primary key,
        created varchar(25),
        day integer,
        fiches_won integer,
        month integer,
        type varchar(50) not null,
        week varchar(3),
        year integer,
        game_id bigint not null,
        player_id bigint not null,
        visitor_id bigint
    );

    create table turn (
       turn_id bigint auto_increment primary key,
        active_player_id bigint,
        current_move_number integer,
        current_round_number integer,
        day integer,
        month integer,
        created varchar(25),
        week varchar(3),
        year integer,
        game_id bigint not null
    );

    create table visitor (
       visitor_id bigint auto_increment primary key,
        balance integer,
        created varchar(25),
        day integer,
        email varchar(50),
        month integer,
        secured_loan integer,
        visitor_name varchar(50) not null,
        visitor_name_seq integer,
        week varchar(3),
        year integer
    );
create index cardmove_turn_index on cardmove (turn_id);
create index turns_game_index on turn (game_id);
create index visitorName_index on visitor (visitor_name);

    alter table card
       add constraint fkcard_game_id
       foreign key (game_id)
       references game;

    alter table card
       add constraint fkcard_player_id
       foreign key (player_id)
       references player;

    alter table cardmove
       add constraint fkcardmove_turn_id
       foreign key (turn_id)
       references turn;

    alter table game
       add constraint fkgame_league_id
       foreign key (league_id)
       references league;

    alter table league
       add constraint fkleague_visitor_id
       foreign key (visitor_id)
       references visitor;

    alter table player
       add constraint fkplayer_game_id
       foreign key (game_id)
       references game;

    alter table player
       add constraint fkplayer_visitor_id
       foreign key (visitor_id)
       references visitor;

    alter table result
       add constraint fkresult_game_id
       foreign key (game_id)
       references game;

    alter table result
       add constraint fkresult_player_id
       foreign key (player_id)
       references player;

    alter table result
       add constraint fkresult_visitor_id
       foreign key (visitor_id)
       references visitor;

    alter table turn
       add constraint fkturn_game_id
       foreign key (game_id)
       references game;
