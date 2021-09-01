package cloud.qasino.quiz.action;

import cloud.qasino.quiz.action.interfaces.Action;
import cloud.qasino.quiz.entity.*;
import cloud.qasino.quiz.event.EventOutput;
import cloud.qasino.quiz.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class FindAllEntitiesForInputAction implements Action<FindAllEntitiesForInputAction.FindAllEntitiesForInputActionDTO, EventOutput.Result> {

    @Resource
    GameRepository gameRepository;
    @Resource
    UserRepository userRepository;
    @Resource
    PlayerRepository playerRepository;
    @Resource
    QuizRepository quizRepository;
    @Resource
    TurnRepository turnRepository;
    @Resource
    LeagueRepository leagueRepository;
    @Resource
    ResultsRepository resultsRepository;

    @Override
    public EventOutput.Result perform(FindAllEntitiesForInputActionDTO actionDto) {

        log.debug("Action: FindAllEntitiesForInputAction");
        int id;
        Pageable pageable = PageRequest.of(actionDto.getSuppliedPages(), actionDto.getSuppliedMaxPerPage());

        id = actionDto.getSuppliedUserId();
        if (!(id == 0)) {
            Optional<User> foundUser = userRepository.findById(Integer.parseInt(String.valueOf(id)));
            if (foundUser.isPresent()) {
                actionDto.setGameUser(foundUser.get());
            } else {
                setErrorMessageNotFound(actionDto, "userId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
            pageable = PageRequest.of(actionDto.getSuppliedPages(), actionDto.getSuppliedMaxPerPage(),
                    Sort.by(
                    Sort.Order.asc("TYPE"),
                    Sort.Order.desc("UPDATED")));
            actionDto.setNewGamesForUser(gameRepository.findAllNewGamesForUserWithPage(id,
                    pageable));
            actionDto.setStartedGamesForUser(gameRepository.findAllStartedGamesForUserWithPage(id,
                    pageable));
            // todo LOW select if one Game is active
            actionDto.setFinishedGamesForUser(gameRepository.findAllFinishedGamesForUserWithPage(id,
                    pageable));
            pageable = PageRequest.of(actionDto.getSuppliedPages(), actionDto.getSuppliedMaxPerPage(),
                    Sort.by(
                            Sort.Order.asc("NAME"),
                            Sort.Order.desc("CREATED")));
            actionDto.setLeaguesForUser(leagueRepository.findAllActiveLeaguesForUserWithPage(id, pageable));
            // todo LOW select if one league is active
        }
        id = actionDto.getSuppliedGameId();
        if (!(id == 0)) {
            Optional<Game> foundGame = gameRepository.findById(Integer.parseInt(String.valueOf(id)));
            if (foundGame.isPresent()) {
                actionDto.setQasinoGame(foundGame.get());
                actionDto.setQasinoGamePlayers(foundGame.get().getPlayers());
                actionDto.setQasinoGameTurn(foundGame.get().getTurn());
                actionDto.setQasinoGameQuizs(foundGame.get().getQuizs());
                actionDto.setQasinoGameQuizMoves(foundGame.get().getTurn().getQuizMoves());
            } else {
                setErrorMessageNotFound(actionDto, "gameId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getAcceptedPlayerId();
        if (!(id == 0)) {
            Optional<Player> foundPlayer = playerRepository.findById(Integer.parseInt(String.valueOf(id)));
            if (foundPlayer.isPresent()) {
                actionDto.setAcceptedPlayer(foundPlayer.get());
            } else {
                setErrorMessageNotFound(actionDto, "acceptedPlayerId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getInvitedPlayerId();
        if (!(id == 0)) {
            Optional<Player> foundPlayer = playerRepository.findById(Integer.parseInt(String.valueOf(id)));
            if (foundPlayer.isPresent()) {
                actionDto.setInvitedPlayer(foundPlayer.get());
            } else {
                setErrorMessageNotFound(actionDto, "invitedPlayerId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getSuppliedTurnPlayerId();
        if (!(id == 0)) {
            Optional<Player> foundPlayer = playerRepository.findById(Integer.parseInt(String.valueOf(id)));
            if (foundPlayer.isPresent()) {
                actionDto.setTurnPlayer(foundPlayer.get());
            } else {
                setErrorMessageNotFound(actionDto, "turnPlayerId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        id = actionDto.getSuppliedLeagueId();
        if (!(id == 0)) {
            Optional<League> foundLeague =
                    leagueRepository.findById(Integer.parseInt(String.valueOf(id)));
            if (foundLeague.isPresent()) {
                actionDto.setQasinoGameLeague(foundLeague.get());
                pageable = PageRequest.of(actionDto.getSuppliedPages(), actionDto.getSuppliedMaxPerPage(),
                        Sort.by(Sort.Order.desc("CREATED")));
                actionDto.setResultsForLeague(resultsRepository.findAllResultsForLeagueWithPage(id,
                        pageable));
            } else {
                setErrorMessageNotFound(actionDto, "leagueId", String.valueOf(id));
                return EventOutput.Result.FAILURE;
            }
        }
        return EventOutput.Result.SUCCESS;
    }

    private void setErrorMessageNotFound(FindAllEntitiesForInputActionDTO actionDto, String id,
                                         String value) {
        actionDto.setHttpStatus(404);
        actionDto.setErrorKey(id);
        actionDto.setErrorValue(value);
        actionDto.setErrorMessage("Entity not found for key" + value);
        actionDto.setUriAndHeaders();
    }

    public interface FindAllEntitiesForInputActionDTO {

        // @formatter:off
        // Getters
        int getSuppliedPages();
        int getSuppliedMaxPerPage();
        int getSuppliedUserId();
        int getSuppliedGameId();
        int getSuppliedLeagueId();
        int getInvitedPlayerId();
        int getAcceptedPlayerId();
        int getSuppliedTurnPlayerId();

        List<League> getLeaguesForUser();

        // Setter
        void setGameUser(User user);
        void setInvitedPlayer(Player player);
        void setAcceptedPlayer(Player player);
        void setTurnPlayer(Player player);

        void setNewGamesForUser(List<Game> games);
        void setStartedGamesForUser(List<Game> games);
        void setFinishedGamesForUser(List<Game> games);

        void setQasinoGame(Game game);
        void setQasinoGamePlayers(List<Player> players);
        void setQasinoGameTurn(Turn turn);
        void setQasinoGameQuizs(List<Quiz> quizzes);
        void setQasinoGameQuizMoves(List<QuizMove> quizMoves);

        void setQasinoGameLeague(League league);
        void setLeaguesForUser(List<League> leagues);
        void setResultsForLeague(List<Result> results);

        void setFriends(List<User> users);

        // error setters
        void setHttpStatus(int status);
        void setErrorKey(String key);
        void setErrorValue(String value);
        void setErrorMessage(String key);
        void setUriAndHeaders();
        // @formatter:on
    }
}
