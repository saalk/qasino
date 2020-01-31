package cloud.qasino.card.game;

public interface QasinoFrontendRequest<T> {
	void inject(final T dto);
}
