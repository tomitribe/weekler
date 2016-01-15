package com.tomitribe.weekler.service;

import com.tomitribe.weekler.service.jpa.Person;
import com.tomitribe.weekler.service.jpa.Week;
import org.apache.openejb.api.configuration.PersistenceUnitDefinition;
import org.apache.openejb.junit.ApplicationComposer;
import org.apache.openejb.testing.Classes;
import org.apache.openejb.testing.Jars;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@Jars("deltaspike-")
@PersistenceUnitDefinition
@Classes(cdi = true, value = {
    ScheduleService.class, PersonService.class, TransactionService.class, GravatarService.class,
    Person.class, Week.class, Week.ID.class
})
@RunWith(ApplicationComposer.class)
public class ScheduleSeviceTest {
    @Inject
    private ScheduleService service;

    @Inject
    private PersonService personService;

    @PersistenceContext
    private EntityManager entityManager;

    @Resource
    private UserTransaction ut;

    @PersistenceContext
    private EntityManager em;

    @After
    public void reset() throws Exception {
        ut.begin();
        em.createQuery("delete from Week").executeUpdate();
        em.createQuery("delete from Person").executeUpdate();
        ut.commit();
    }

    @Before
    public void init() throws Exception {
        reset();
        personService.create("Ignored here", "foo1@bar.com", "123455");
        personService.create("Ignored here", "foo2@bar.com", "123456");
        personService.create("Ignored here", "foo3@bar.com", "123457");
    }

    @Test
    public void findAutoCreate() throws Exception {
        final LocalDate date = LocalDate.now();
        final int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final int year = date.getYear();

        { // create first week
            final Week week = service.find(weekNumber, year);
            assertNotNull(week);
            assertEquals(weekNumber, week.getId().getWeek());
            assertEquals(year, week.getId().getYear());
            assertEquals("foo1", week.getPerson().getName());
        }
        for (int i = 0; i < 3; i++) { // direct week creation, iteration to ensure we dont recreate it
            service.find(weekNumber, year);
            assertEquals(1, entityManager.createQuery("select count(w) from Week w", Number.class).getSingleResult().intValue());
        }

        // now a future week
        final Week week = service.find(weekNumber, year + 1);
        assertNotNull(week);
        assertEquals(weekNumber, week.getId().getWeek());
        assertEquals(year + 1, week.getId().getYear());
        assertNotNull(week.getPerson());

        final int total = entityManager.createQuery("select count(w) from Week w", Number.class).getSingleResult().intValue();
        assertTrue(total > 100 && total < 105); // we could be precise but that's not that important

        // now check we affected properly persons
        final Person[] people = IntStream.range(1, 4)
            .mapToObj(i -> service.find(i, year + 1).getPerson())
            .toArray(Person[]::new);
        final int p1Index = asList(people).indexOf(Stream.of(people).filter(p -> p.getName().endsWith("1")).findAny().get());
        IntStream.range(0, 3).forEach(person -> assertEquals("foo" + (1 + person), people[(person + p1Index) % 3].getName()));

        // no creation in the past
        assertNull(service.find(weekNumber, year - 1));
        assertEquals(total, entityManager.createQuery("select count(w) from Week w", Number.class).getSingleResult().intValue());
    }

    @Test
    public void affectWeek() {
        final LocalDate date = LocalDate.now();
        final int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final int year = date.getYear();

        final Week week = service.find(weekNumber, year);
        assertEquals("foo1", week.getPerson().getName());

        service.affectWeekTo(week.getId().getWeek(), week.getId().getYear(), "foo3");
        assertEquals("foo3", service.find(weekNumber, year).getPerson().getName());
    }

    @Test
    public void reaffect() {
        final LocalDate date = LocalDate.now();
        final int weekNumber = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        final int year = date.getYear();

        final BiFunction<Integer, Integer, List<String>> people = (week, y) ->
            entityManager.createQuery("select w from Week w order by w.id.year, w.id.week", Week.class).getResultList().stream()
                .filter(w -> w.getId().getYear() == y && w.getId().getWeek() > week)
                .map(w -> w.getPerson().getName())
                .collect(toList());

        service.find(weekNumber, year + 1); // create 1 year of schedule
        assertTrue(assertOrder(people.apply(5, year + 1).iterator(), asList("foo2", "foo3", "foo1")) >= 4);

        personService.create("Ignored", "new@mail.com", "12325");
        service.reaffectPeopleFrom(5, year + 1);
        assertTrue(assertOrder(people.apply(5, year + 1).iterator(), asList("foo2", "foo3", "new", "foo1")) >= 5);
    }

    private int assertOrder(final Iterator<String> reaffectedPeople, final Collection<String> expectedOrder) {
        // skip until we hit "foo1" cause we know the suite from foo1
        while (!reaffectedPeople.next().equals("foo1")) {
            // no-op
        }

        Iterator<String> expected = expectedOrder.iterator();
        int count = 0;
        while (reaffectedPeople.hasNext()) {
            if (!expected.hasNext()) {
                expected = expectedOrder.iterator();
            }
            assertEquals(expected.next(), reaffectedPeople.next());
            count++;
        }
        return count;
    }
}
