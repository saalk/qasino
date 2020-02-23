package applyextra.commons.components.api2api;

import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import lombok.extern.slf4j.Slf4j;
import nl.ing.riaf.core.metrics.graphite.GraphiteSettings;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

@Lazy
@Slf4j
@Component
public class Api2ApiGraphiteFactory {
    private final int GRAPHITE_POLLING_PERIOD = 5;
    @Resource
    private GraphiteSettings graphiteSettings;

    @PostConstruct
    public void linkGraphiteToFinagleStats() {

        final Graphite graphite = new Graphite(new InetSocketAddress(graphiteSettings.getServerHostName(), graphiteSettings.getServerPort()));
        log.info("graphiteSettings: host:port: ");
        log.info(graphiteSettings.getServerHostName());
        log.info(String.valueOf(graphiteSettings.getServerPort()));
//        final GraphiteReporter reporter = GraphiteReporter.forRegistry(MetricsStatsReceiver.metrics())
        final GraphiteReporter reporter = GraphiteReporter.forRegistry(null)
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build(graphite);
        reporter.start(GRAPHITE_POLLING_PERIOD, TimeUnit.SECONDS);

        throw new UnsupportedOperationException("Api2ApiGraphiteFactory, not compatible with latest finagle clients. MetricsStatsReceiver.metrics() throws noClassDefError in scala, and could not debug it. If you want to keep using this, please find a suitable replacement. Also, no tests where written here, so i strongly recommend to add thoses if you want to keep using this.");
    }
}
