package cloud.qasino.quiz.repository;

import cloud.qasino.quiz.entity.Quiz;
import cloud.qasino.quiz.entity.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer> {

    public final static String FIND_QUIZS_BY_GAME_ID =
            "SELECT * FROM QUIZ " +
                    "WHERE GAME_ID = :gameId ";
    public final static String COUNT_QUIZS_BY_GAME_ID =
            "SELECT count(*) FROM QUIZ  " +
                    "WHERE GAME_ID = :gameId ";

    // counters
    Long countByGame(int gameId);

    // lists
    List<Quiz> findByGame(Game game);
    List<Quiz> findByQuiz(String quiz);

    @Query( value = FIND_QUIZS_BY_GAME_ID,
            countQuery = COUNT_QUIZS_BY_GAME_ID,
            nativeQuery = true)
    List<Quiz> findAllQuizsByGameWithPage(@Param("gameId") int gameId, Pageable pageable);

    List<Quiz> findByGameOrderByLocationAscSequenceAsc(Game game);
}
