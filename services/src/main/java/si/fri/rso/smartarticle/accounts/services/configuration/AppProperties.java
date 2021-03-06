package si.fri.rso.smartarticle.accounts.services.configuration;

import com.kumuluz.ee.configuration.cdi.ConfigBundle;
import com.kumuluz.ee.configuration.cdi.ConfigValue;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@ConfigBundle("app-properties")
public class AppProperties {

    @ConfigValue(value = "account-services.enabled", watch = true)
    private boolean accountServicesEnabled;

    @ConfigValue(value = "account-institute-services.enabled", watch = true)
    private boolean accountInstituServicesEnabled;

    @ConfigValue(value = "account-article-services.enabled", watch = true)
    private boolean accountArticleServicesEnabled;

    @ConfigValue(value = "account-collection-services.enabled", watch = true)
    private boolean accountCollectionServicesEnabled;

    @ConfigValue(watch = true)
    private boolean healthy;

    public boolean isAccountServicesEnabled() {
        return accountServicesEnabled;
    }

    public void setAccountServicesEnabled(boolean accountServicesEnabled) {
        this.accountServicesEnabled = accountServicesEnabled;
    }

    public boolean isAccountInstituServicesEnabled() {
        return accountInstituServicesEnabled;
    }

    public void setAccountInstituServicesEnabled(boolean accountInstituServicesEnabled) {
        this.accountInstituServicesEnabled = accountInstituServicesEnabled;
    }

    public boolean isAccountArticleServicesEnabled() {
        return accountArticleServicesEnabled;
    }

    public void setAccountArticleServicesEnabled(boolean accountArticleServicesEnabled) {
        this.accountArticleServicesEnabled = accountArticleServicesEnabled;
    }

    public boolean isAccountCollectionServicesEnabled() {
        return accountCollectionServicesEnabled;
    }

    public void setAccountCollectionServicesEnabled(boolean accountCollectionServicesEnabled) {
        this.accountCollectionServicesEnabled = accountCollectionServicesEnabled;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }
}
