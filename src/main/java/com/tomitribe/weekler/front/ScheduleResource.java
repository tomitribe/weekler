package com.tomitribe.weekler.front;

import com.tomitribe.weekler.front.domain.WeekModel;
import com.tomitribe.weekler.front.mapper.PersonMapper;
import com.tomitribe.weekler.service.ScheduleService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;
import java.time.temporal.IsoFields;

import static java.util.Optional.ofNullable;

@Path("schedule")
@ApplicationScoped
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ScheduleResource {
    @Inject
    private ScheduleService scheduleService;

    @Inject
    private PersonMapper personMapper;

    @GET
    @Path("week")
    public WeekModel findWeek(@QueryParam("week") final int week, @QueryParam("year") final int year) {
        final LocalDate date = LocalDate.now();
        if (week < 0 || year < 0) {
            return findWeek(date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR), date.getYear());
        }

        if (year > 6 + date.getYear()) {
            throw new IllegalArgumentException("Year too far in the future");
        }

        return ofNullable(scheduleService.find(week, year))
            .filter(w -> w != null) // can be null if past
            .map(w -> new WeekModel(w.getId().getWeek(), w.getId().getYear(), personMapper.toModel(w.getPerson())))
            .orElse(null);
    }

    @PUT // TODO: security
    @Path("week")
    public WeekModel update(final WeekModel model) {
        scheduleService.affectWeekTo(model.getWeek(), model.getYear(), model.getPerson().getName());
        return model;
    }

    @HEAD // TODO: security
    @Path("week/reaffect")
    public void reaffectFrom(@QueryParam("week") final int week, @QueryParam("year") final int year) {
        scheduleService.reaffectPeopleFrom(week, year);
    }
}
