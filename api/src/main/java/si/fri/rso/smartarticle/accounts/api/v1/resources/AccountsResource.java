package si.fri.rso.smartarticle.accounts.api.v1.resources;



import com.kumuluz.ee.logs.cdi.Log;
import si.fri.rso.smartarticle.accounts.models.entities.Account;
import si.fri.rso.smartarticle.accounts.services.beans.AccountsBean;
import si.fri.rso.smartarticle.accounts.services.configuration.AppProperties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@Log
@ApplicationScoped
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @Context
    protected UriInfo uriInfo;

    @Inject
    private AccountsBean accountsBean;

    @Inject
    private AppProperties appProperties;

    @GET
    public Response getAccounts() {
        if (appProperties.isAccountServicesEnabled()) {
            List<Account> accounts = accountsBean.getAccounts(uriInfo);
            appProperties.setHealthy(true);
            return Response.ok(accounts).build();
        }
        else{
            appProperties.setHealthy(true);
            return Response.ok().build();
        }
    }

    @GET
    @Path("/filtered")
    public Response getAccountsFiltered() {
        if (appProperties.isAccountServicesEnabled()) {
            List<Account> accounts;
            accounts = accountsBean.getAccounts(uriInfo);
            appProperties.setHealthy(true);
            return Response.status(Response.Status.OK).entity(accounts).build();
        }
        else{
            appProperties.setHealthy(true);
            return Response.ok().build();
        }
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") Integer accountId) {
        if (appProperties.isAccountServicesEnabled()) {
            Account account = accountsBean.getAccount(accountId);

            if (account == null) {
                appProperties.setHealthy(true);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
            appProperties.setHealthy(true);
            return Response.status(Response.Status.OK).entity(account).build();
        }
        else{
            appProperties.setHealthy(true);
            return Response.ok().build();
        }
    }

    @POST
    public Response createAccount(Account account) {
        if (appProperties.isAccountServicesEnabled()) {
            if ((account.getFirstName() == null || account.getFirstName().isEmpty()) || (account.getLastName() == null
                    || account.getLastName().isEmpty())) {
                appProperties.setHealthy(true);
                return Response.status(Response.Status.BAD_REQUEST).build();
            } else {
                account = accountsBean.createAccount(account);
            }

            if (account.getId() != null) {
                appProperties.setHealthy(true);
                return Response.status(Response.Status.CREATED).entity(account).build();
            } else {
                appProperties.setHealthy(true);
                return Response.status(Response.Status.CONFLICT).entity(account).build();
            }
        }
        else{
            appProperties.setHealthy(true);
            return Response.ok().build();
        }
    }

    @DELETE
    @Path("{accountId}")
    public Response deleteAccount(@PathParam("accountId") String accountId) {
        if (appProperties.isAccountServicesEnabled()) {
            boolean deleted = accountsBean.deleteAccount(accountId);

            if (deleted) {
                appProperties.setHealthy(true);
                return Response.status(Response.Status.GONE).build();
            } else {
                appProperties.setHealthy(true);
                return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        else{
            appProperties.setHealthy(true);
            return Response.ok().build();
        }
    }
}
