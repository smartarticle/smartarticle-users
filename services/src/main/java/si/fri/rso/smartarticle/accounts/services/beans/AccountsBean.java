package si.fri.rso.smartarticle.accounts.services.beans;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Timeout;
import si.fri.rso.smartarticle.accounts.models.dtos.Article;
import si.fri.rso.smartarticle.accounts.models.dtos.Collection;
import si.fri.rso.smartarticle.accounts.models.dtos.Institution;
import si.fri.rso.smartarticle.accounts.models.entities.Account;
import si.fri.rso.smartarticle.accounts.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.UriInfo;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;


@RequestScoped
public class AccountsBean {

    private Logger log = Logger.getLogger(AccountsBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private AccountsBean accountsBean;

    private Client httpClient;

    @Inject
    @DiscoverService("smartarticle-institutions")
    private Provider<Optional<String>> institutionBaseProvider;

    @Inject
    @DiscoverService("smartarticle-articles")
    private Provider<Optional<String>> articleBaseProvider;

    @Inject
    @DiscoverService("smartarticle-collections")
    private Provider<Optional<String>> collectionBaseProvider;

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<Account> getAccounts(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                .defaultOffset(0)
                .build();
        List<Account> accounts = JPAUtils.queryEntities(em, Account.class, queryParameters);
        for (Account account: accounts) {
            try {
                if (appProperties.isAccountInstituServicesEnabled()) {
                    account.setInstitution(accountsBean.getInstitution(Integer.parseInt(account.getInstituteId())));
                }
            } catch (InternalServerErrorException e){
                log.severe(e.getMessage());
                appProperties.setHealthy(false);
            }
            try {
                if (appProperties.isAccountArticleServicesEnabled()) {
                    account.setArticles(accountsBean.getArticles(account.getId()));
                }
            } catch (InternalServerErrorException e){
                log.severe(e.getMessage());
                appProperties.setHealthy(false);
            }
            try {
                if (appProperties.isAccountCollectionServicesEnabled()) {
                    account.setCollections(accountsBean.getCollections(account.getId()));
                }
            } catch (InternalServerErrorException e){
                log.severe(e.getMessage());
                appProperties.setHealthy(false);
            }
        }
        return accounts;
    }

    public List<Account> getAccountsFilter(UriInfo uriInfo) {
        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();
        appProperties.setHealthy(true);
        return JPAUtils.queryEntities(em, Account.class, queryParameters);
    }

    public Account getAccount(Integer accountId) {
        Account account = em.find(Account.class, accountId);

        if (account == null) {
            throw new NotFoundException();
        }
        try {
            if (appProperties.isAccountInstituServicesEnabled()) {
                account.setInstitution(accountsBean.getInstitution(Integer.parseInt(account.getInstituteId())));
            }
        } catch (InternalServerErrorException e) {
            log.severe(e.getMessage());
            appProperties.setHealthy(false);
        }
        try {
            if (appProperties.isAccountArticleServicesEnabled()) {
                account.setArticles(accountsBean.getArticles(accountId));
            }
        } catch (InternalServerErrorException e) {
            log.severe(e.getMessage());
            appProperties.setHealthy(false);
        }
        try {
            if (appProperties.isAccountCollectionServicesEnabled()) {
                account.setCollections(accountsBean.getCollections(accountId));
            }
        } catch (InternalServerErrorException e) {
            log.severe(e.getMessage());
            appProperties.setHealthy(false);
        }
        return account;
    }


    public List<Article> getArticles(Integer accountId) {
        Optional<String> baseUrl = articleBaseProvider.get();
        if (baseUrl.isPresent()) {
            try {
                String link = baseUrl.get();
                return httpClient
                        .target(link + "/v1/articles?where=accountId:EQ:" + accountId)
                        .request().get(new GenericType<List<Article>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                appProperties.setHealthy(false);
                throw new InternalServerErrorException(e);
            }
        }
        appProperties.setHealthy(false);
        return null;
    }

    public List<Collection> getCollections(Integer accountId) {
        Optional<String> baseUrl = collectionBaseProvider.get();
        if (baseUrl.isPresent()) {
            try {
                String link = baseUrl.get();
                return httpClient
                        .target(link + "/v1/collections?where=accountId:EQ:" + accountId)
                        .request().get(new GenericType<List<Collection>>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                appProperties.setHealthy(false);
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }

    public Account createAccount(Account account) {
        try {
            beginTx();
            em.persist(account);
            commitTx();
            appProperties.setHealthy(true);
        } catch (Exception e) {
            rollbackTx();
            appProperties.setHealthy(false);
        }

        return account;
    }

    public boolean deleteAccount(String accountId) {
        Account account = em.find(Account.class, accountId);

        if (account != null) {
            try {
                beginTx();
                em.remove(account);
                commitTx();
                appProperties.setHealthy(true);
            } catch (Exception e) {
                rollbackTx();
                appProperties.setHealthy(false);
            }
        } else {
            appProperties.setHealthy(false);
            return false;
        }

        return true;
    }

    @CircuitBreaker(requestVolumeThreshold = 3)
    @Timeout(value = 2, unit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getInstitutionsFallback")
    public Institution getInstitution(Integer institutionId) {
        Optional<String> baseUrl = institutionBaseProvider.get();
        if (baseUrl.isPresent()) {
            try {
                String link = baseUrl.get();
                return httpClient
                        .target(link + "/v1/institutions/info/" + institutionId)
                        .request().get(new GenericType<Institution>() {
                        });
            } catch (WebApplicationException | ProcessingException e) {
                log.severe(e.getMessage());
                String link = baseUrl.get();
                log.severe(link + "/v1/institutions/info/" + institutionId);
                appProperties.setHealthy(false);
                throw new InternalServerErrorException(e);
            }
        }
        return null;
    }


    public Institution getInstitutionsFallback(Integer institutionId) {

        return null;

    }

    private void beginTx() {
        if (!em.getTransaction().isActive())
            em.getTransaction().begin();
    }

    private void commitTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().commit();
    }

    private void rollbackTx() {
        if (em.getTransaction().isActive())
            em.getTransaction().rollback();
    }
}
