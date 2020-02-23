package applyextra.operations.filter;


import com.ing.api.toolkit.trust.rest.param.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.container.DynamicFeature;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;
import java.lang.reflect.Method;
import java.util.*;

@Provider
@Slf4j
public class AllowedSubChannelsDynamicFeature implements DynamicFeature{

    private static final Class<AllowedSubChannels> ANNOTATION_CLASS = AllowedSubChannels.class;


    @Override
    public void configure(final ResourceInfo resourceInfo, final FeatureContext configuration) {

        final Set<String> allowedSubChannels = new TreeSet<>();

        //base access starts with methods
        allowedSubChannels.addAll(getMethodAnnotationValues(resourceInfo.getResourceMethod()));

        if (allowedSubChannels.isEmpty()) {
            allowedSubChannels.addAll(getClassAnnotationValues(resourceInfo.getResourceClass()));
        }

        if (!allowedSubChannels.isEmpty()){

            log.debug("Resource {}#{} only accessible by subchannels: {}", resourceInfo.getResourceClass().getSimpleName(),
                    resourceInfo.getResourceMethod().getName(), allowedSubChannels);

            configuration.register(new AllowedSubChannelsRequestFilter(allowedSubChannels));
        }
    }

    private static List<String> getValues(final Optional<AllowedSubChannels> annotation) {

        final List<String> values;
        if (annotation.isPresent()) {
            values = Arrays.asList(annotation.get().value());
        } else {
            values = Collections.emptyList();
        }

        return values;
    }

    private static List<String> getMethodAnnotationValues(final Method method) {
        return getValues(ReflectionUtils.findAnnotation(method, ANNOTATION_CLASS));
    }

    private static List<String> getClassAnnotationValues(final Class<?> clzz) {
        return getValues(ReflectionUtils.findAnnotation(clzz, ANNOTATION_CLASS));
    }
}
