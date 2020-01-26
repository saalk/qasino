package applyextra.commons.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * Reflection helper class
 */
public final class ReflectionHelper {

	private ReflectionHelper() {
	}

	public static Object getFirstGenericTypeInstanceForClass(Class clasz) {
		Object object = null;
		final Type superType = clasz.getGenericSuperclass();
		if (superType instanceof ParameterizedType) {
			final ParameterizedType parameterizedType = (ParameterizedType) superType;
			if (parameterizedType.getActualTypeArguments().length >= 1
				&& parameterizedType.getActualTypeArguments()[0] instanceof Class<?>) {
				final Class<?> firstType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
				object = instantiateObject(firstType);
			}
		}
		return object;
	}

	public static <T> T instantiateObject(Class<T> type) {
		T object;
		try {
			object = type.newInstance();
		} catch (final InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("unable to instantiate type " + type.getName(), e);
		}
		return object;
	}
}
