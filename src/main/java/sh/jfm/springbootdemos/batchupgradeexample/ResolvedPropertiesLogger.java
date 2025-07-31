package sh.jfm.springbootdemos.batchupgradeexample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ResolvedPropertiesLogger {

    private static final Logger log =
            LoggerFactory.getLogger(ResolvedPropertiesLogger.class);

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        ConfigurableEnvironment env = event.getApplicationContext().getEnvironment();

        log.debug("Active Spring profiles: {}", String.join(", ", env.getActiveProfiles()));

        env.getPropertySources()
                .stream()
                .filter(ps -> ps instanceof MapPropertySource)
                .filter(ps -> ps.getName().contains("class path resource"))
                .map(ps -> ((MapPropertySource) ps).getSource().keySet())
                .flatMap(Collection::stream)
                .distinct()
                .sorted()
                .forEach(key -> log.debug("{}={}", key, env.getProperty(key)));
    }
}
