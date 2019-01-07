package si.fri.rso.smartarticle.accounts.api.v1.resources;

import si.fri.rso.smartarticle.accounts.models.entities.Account;
import si.fri.rso.smartarticle.accounts.services.beans.AccountsBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.List;


@RequestScoped
@Path("/accounts")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccountsResource {

    @Context
    protected UriInfo uriInfo;

    @Inject
    private AccountsBean accountsBean;

    @GET
    public Response getAccounts() {

        List<Account> accounts = accountsBean.getAccounts(uriInfo);

        return Response.ok(accounts).build();
    }

    @GET
    @Path("/filtered")
    public Response getAccountsFiltered() {

        List<Account> accounts;

        accounts = accountsBean.getAccounts(uriInfo);

        return Response.status(Response.Status.OK).entity(accounts).build();
    }

    @GET
    @Path("/{accountId}")
    public Response getAccount(@PathParam("accountId") Integer accountId) {

        Account account = accountsBean.getAccount(accountId);

        if (account == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        return Response.status(Response.Status.OK).entity(account).build();
    }

    @POST
    public Response createAccount(Account account) {

        if ((account.getFirstName() == null || account.getFirstName().isEmpty()) || (account.getLastName() == null
                || account.getLastName().isEmpty())) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        } else {
            account = accountsBean.createAccount(account);
        }

        if (account.getId() != null) {
            return Response.status(Response.Status.CREATED).entity(account).build();
        } else {
            return Response.status(Response.Status.CONFLICT).entity(account).build();
        }
    }

    @DELETE
    @Path("{accountId}")
    public Response deleteAccount(@PathParam("accountId") String accountId) {

        boolean deleted = accountsBean.deleteAccount(accountId);

        if (deleted) {
            return Response.status(Response.Status.GONE).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }
}
