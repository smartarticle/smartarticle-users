package si.fri.rso.smartarticle.accounts.api.v1.health;


import org.eclipse.microprofile.health.Health;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;
import si.fri.rso.smartarticle.accounts.services.configuration.AppProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Health
@ApplicationScoped
public class AccountServiceHealthCheck  implements HealthCheck{
    @Inject
    private AppProperties appProperties;

    public HealthCheckResponse call() {
        if (appProperties.isHealthy()) { 
            return  HealthCheckResponse.named(AccountServiceHealthCheck.class.getSimpleName()).up().build();
        } else {
            return  HealthCheckResponse.named(AccountServiceHealthCheck.class.getSimpleName()).down().build();
        }
    }
}
