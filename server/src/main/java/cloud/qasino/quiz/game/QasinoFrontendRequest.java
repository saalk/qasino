package cloud.qasino.quiz.game;

public interface QasinoFrontendRequest<T> {
	void inject(final T dto);
}
