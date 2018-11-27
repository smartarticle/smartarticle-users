package si.fri.rso.smartarticle.accounts.services.beans;


import com.kumuluz.ee.discovery.annotations.DiscoverService;
import com.kumuluz.ee.rest.beans.QueryParameters;
import com.kumuluz.ee.rest.utils.JPAUtils;
import si.fri.rso.smartarticle.accounts.models.entities.Account;
import si.fri.rso.smartarticle.accounts.services.configuration.AppProperties;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.UriInfo;
import java.util.List;
import java.util.logging.Logger;
import java.util.Optional;


@RequestScoped
public class AccountsBean {

    private Logger log = Logger.getLogger(AccountsBean.class.getName());

    @Inject
    private EntityManager em;

    @Inject
    private AppProperties appProperties;

    @Inject
    private AccountsBean accountsBean;

    @PostConstruct
    private void init() {}

    @Inject
    @DiscoverService("smartarticle-users")
    private Optional<String> baseUrl;

    public List<Account> getAccounts() {
        TypedQuery<Account> query = em.createNamedQuery("Account.getAll", Account.class);
        return query.getResultList();
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
