package si.fri.rso.smartarticle.accounts.api.v1.resources;

import com.kumuluz.ee.common.runtime.EeRuntime;
import javax.enterprise.context.ApplicationScoped;
import javax.json.Json;
import javax.json.JsonObject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;
@Path("demo")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class DemoResource {
    private Logger log = Logger.getLogger(DemoResource.class.getName());
    @GET
    @Path("instanceid")
    public Response getInstanceId() {
        String instanceId =
                "{\"instanceId\" : \"" + EeRuntime.getInstance().getInstanceId() + "\"}";
        return Response.ok(instanceId).build();
    }
    @GET
    @Path("info")
    public Response info() {
        JsonObject json = Json.createObjectBuilder()
                .add("clani", Json.createArrayBuilder().add("aj4880"))
                .add("opis_projekta", "Nas projekt implementira aplikacijo za upravljanje in deljenje clankov.")
                .add("mikrostoritve", Json.createArrayBuilder().add("http://http://159.122.187.161:30473/v1/accounts"))
                .add("github", Json.createArrayBuilder().add("https://github.com/smartarticle/smartarticle-users"))
                .add("travis", Json.createArrayBuilder().add("https://travis-ci.org/smartarticle/smartarticle-users"))
                .add("dockerhub", Json.createArrayBuilder().add("https://hub.docker.com/r/ajugo/smartarticle-users"))
                .build();
        return Response.ok(json.toString()).build();
    }
}