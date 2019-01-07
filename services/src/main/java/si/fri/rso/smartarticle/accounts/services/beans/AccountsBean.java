package si.fri.rso.smartarticle.accounts.services.beans;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
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

    @PostConstruct
    private void init() {
        httpClient = ClientBuilder.newClient();
    }

    public List<Account> getAccounts(UriInfo uriInfo) {
        if (appProperties.isExternalServicesEnabled()) {
            QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery())
                    .defaultOffset(0)
                    .build();
            List<Account> acc = JPAUtils.queryEntities(em, Account.class, queryParameters);
            for (Account ac: acc) {
                try {
                    Institution inst = accountsBean.getInstitution(Integer.parseInt(ac.getInstituteId()));
                    ac.setInstitution(inst);
                } catch (InternalServerErrorException e){}
            }
            return acc;
        }
        return null;
    }

    public List<Account> getAccountsFilter(UriInfo uriInfo) {

        QueryParameters queryParameters = QueryParameters.query(uriInfo.getRequestUri().getQuery()).defaultOffset(0)
                .build();

        return JPAUtils.queryEntities(em, Account.class, queryParameters);
    }

    public Account getAccount(Integer accountId) {

        Account account = em.find(Account.class, accountId);

        if (account == null) {
            throw new NotFoundException();
        }
        try {
            Institution inst = accountsBean.getInstitution(Integer.parseInt(account.getInstituteId()));
            account.setInstitution(inst);
        } catch (InternalServerErrorException e){}
        return account;
    }

    public Account createAccount(Account account) {

        try {
            beginTx();
            em.persist(account);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
        }

        return account;
    }

    public Account putAccount(String accountId, Account account) {

        Account c = em.find(Account.class, accountId);

        if (c == null) {
            return null;
        }

        try {
            beginTx();
            account.setId(c.getId());
            account = em.merge(account);
            commitTx();
        } catch (Exception e) {
            rollbackTx();
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
            } catch (Exception e) {
                rollbackTx();
            }
        } else
            return false;

        return true;
    }

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
                throw new InternalServerErrorException(e);
            }
        }
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
