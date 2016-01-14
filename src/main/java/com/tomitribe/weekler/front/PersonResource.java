package com.tomitribe.weekler.front;

import com.tomitribe.weekler.front.domain.Page;
import com.tomitribe.weekler.front.domain.PersonModel;
import com.tomitribe.weekler.front.mapper.PersonMapper;
import com.tomitribe.weekler.service.PersonService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static java.util.stream.Collectors.toList;

@Path("person")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonResource {
    @Inject
    private PersonService service;

    @Inject
    private PersonMapper personMapper;

    @GET
    @Path("{name}")
    public PersonModel findById(@PathParam("name") final String id) {
        return personMapper.toModel(service.findByName(id));
    }

    @GET
    public Page<PersonModel> findALl(@QueryParam("page") @DefaultValue("0") final int page,
                                     @QueryParam("size") @DefaultValue("25") final int size) {
        final int total = service.countAll();
        return new Page<>(
            total,
            (1 + page) * size < total,
            service.findAll(page, size).stream().map(personMapper::toModel).collect(toList()));
    }

    @POST
    public PersonModel create(final PersonModel model) {
        return personMapper.toModel(service.create(model.getDisplayName(), model.getMail(), model.getPhone()));
    }

    @PUT
    public PersonModel update(final PersonModel model) {
        return personMapper.toModel(service.update(model.getName(), model.getDisplayName(), model.getMail(), model.getPhone(), model.getIcon()));
    }

    @DELETE
    @Path("{name}")
    public PersonModel delete(@PathParam("name") final String id) {
        return personMapper.toModel(service.delete(id));
    }
}
