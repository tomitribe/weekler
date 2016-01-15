package com.tomitribe.weekler.service;

import com.tomitribe.weekler.service.jpa.Person;
import com.tomitribe.weekler.service.jpa.Week;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.stream.IntStream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static javax.ejb.ConcurrencyManagementType.BEAN;

@Singleton
@ConcurrencyManagement(BEAN)
public class ScheduleService {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private PersonService personService;

    public Week find(final int weekNumber, final int year) {
        final Week week = entityManager.find(Week.class, new Week.ID(weekNumber, year));
        if (week != null) {
            return week;
        }

        // weeks are created on the fly with affectation
        return createWeeksUntil(weekNumber, year);
    }

    public void reaffectPeopleFrom(final int weekNumber, final int year) {
        final Week week = entityManager.find(Week.class, new Week.ID(weekNumber, year));
        if (week == null) {
            return;
        }
        entityManager.createNamedQuery("Week.findAllFrom", Week.class)
            .setParameter("year", year)
            .setParameter("week", weekNumber)
            .getResultList().forEach(w -> w.setPerson(selectPerson(w.getId().getWeek(), w.getId().getYear())));
    }

    public void affectWeekTo(final int weekNumber, final int year, final String newPerson) {
        ofNullable(find(weekNumber, year))
            .ifPresent(w -> w.setPerson(requireNonNull(personService.findByName(newPerson), "week person shouldn't be null")));
    }

    private Week createWeeksUntil(final int weekNumber, final int year) {
        // don't create week for past time, this is useless
        final LocalDate date = LocalDate.now();
        if (date.getYear() > year
            || (date.getYear() == year && date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR) > weekNumber)) {
            return null;
        }

        try { // schedule all missing weeks
            final Week lastWeekScheduled = entityManager.createNamedQuery("Week.findAllDesc", Week.class).setMaxResults(1).getSingleResult();
            final int lastScheduledYear = lastWeekScheduled.getId().getYear();
            final boolean sameYear = lastScheduledYear == year;
            IntStream.range(
                lastWeekScheduled.getId().getWeek() + 1,
                sameYear ? weekNumber + 1 : lastWeekNumber(lastScheduledYear) + 1)
                .forEach(week -> createSimpleWeek(lastScheduledYear, week, selectPerson(week, lastScheduledYear)));
            if (!sameYear) {
                IntStream.range(lastScheduledYear + 1, year + 1)
                    .forEach(y -> IntStream.range(1, lastWeekNumber(y) + 1)
                        .forEach(w -> createSimpleWeek(y, w, selectPerson(w, y))));
            }
        } catch (final NoResultException nre) {
            // no week at all planned, just persist this week (now) and start again
            createSimpleWeek(date.getYear(), date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR), personService.findByFirstOrdinal());
            return find(weekNumber, year);
        }

        return entityManager.find(Week.class, new Week.ID(weekNumber, year)); // should be in the cache so cheap
    }

    private int lastWeekNumber(final int lastScheduledYear) {
        return LocalDate.of(lastScheduledYear, 12, 31 /* last week = last day */).get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
    }

    private Week createSimpleWeek(final int year, final int week, final Person person) {
        final Week newWeek = new Week();
        newWeek.setId(new Week.ID(week, year));
        newWeek.setPerson(person);
        entityManager.persist(newWeek);
        return newWeek;
    }

    private Person selectPerson(final int weekNumber, final int year) {
        try {
            final int findLastOrdinal = personService.findLastOrdinal();
            final Week lastWeekScheduled = entityManager.find(
                Week.class, new Week.ID(weekNumber == 1 ? lastWeekNumber(year - 1) : weekNumber - 1, weekNumber == 1 ? year - 1 : year));
            int ordinal = lastWeekScheduled.getPerson().getOrdinal();
            final int originalOrdinal = ordinal;

            Person person;
            do {
                ordinal++;
                if (ordinal > findLastOrdinal) {
                    ordinal = 0;
                }
                person = personService.findByOrdinal(ordinal);
            } while (person == null && originalOrdinal != ordinal);

            if (person == null) { // will fail but just to get a consistent failure
                return personService.findByFirstOrdinal();
            }
            return person;
        } catch (final NoResultException nre) {
            return personService.findByFirstOrdinal();
        }
    }
}
