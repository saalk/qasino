package cloud.qasino.games.game;

public interface QasinoFrontendRequest<T> {
	void inject(final T dto);
}
