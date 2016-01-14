package com.tomitribe.weekler.service;

import com.tomitribe.weekler.service.jpa.Person;
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
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@Jars("deltaspike-")
@PersistenceUnitDefinition
@Classes(cdi = true, value = {PersonService.class, TransactionService.class, GravatarService.class, Person.class})
@RunWith(ApplicationComposer.class)
public class PersonServiceTest {
    @Inject
    private PersonService service;

    @Resource
    private UserTransaction ut;

    @PersistenceContext
    private EntityManager em;

    @Before
    @After
    public void reset() throws Exception {
        ut.begin();
        em.createQuery("delete from Person").executeUpdate();
        ut.commit();
    }

    @Test
    public void create() {
        final Person person = service.create("Foo Bar", "foo@dummy.com", "1234567890");
        assertNotNull(person);

        final Person found = service.findByName("foo");
        assertNotNull(found);
        assertEquals("foo@dummy.com", found.getMail());
        assertEquals("1234567890", found.getPhone());
        assertEquals("Foo Bar", found.getDisplayName());
        assertEquals(1, found.getOrdinal());

        // ensure ordinal increments
        assertEquals(2, service.create("Foo Bar", "foo2@dummy.com", "1234567890").getOrdinal());
        assertEquals(3, service.create("Foo Bar", "foo3@dummy.com", "1234567890").getOrdinal());
    }

    @Test
    public void delete() {
        service.create("Foo Bar", "foo@dummy.com", "1234567890");
        service.delete("foo");
        assertNull(service.findByName("foo"));
    }

    @Test
    public void findByOrdinal() {
        service.create("Foo Bar", "foo@dummy.com", "1234567890");
        service.create("Foo2 Bar", "foo2@dummy.com", "1234567890");
        assertEquals("foo", service.findByOrdinal(1).getName());
        assertEquals("foo2", service.findByOrdinal(2).getName());
    }

    @Test
    public void findByMinOrdinal() {
        service.create("Foo Bar", "foo@dummy.com", "1234567890");
        service.create("Foo2 Bar", "foo2@dummy.com", "1234567890");
        assertEquals("foo", service.findByFirstOrdinal().getName());
    }

    @Test
    public void count() {
        service.create("Foo Bar", "foo@dummy.com", "1234567890");
        assertEquals(1, service.countAll());
        service.create("Foo Bar", "foo2@dummy.com", "1234567890");
        assertEquals(2, service.countAll());
    }

    @Test
    public void update() {
        service.create("Foo Bar", "foo@dummy.com", "1234567890");
        assertEquals("foo@dummy.com", service.findByName("foo").getMail());

        final String defaultIcon = service.findByName("foo").getIcon();

        service.update("foo", "Foo Bar", "another@mail.com", "123", "new");

        final Person updated = service.findByName("foo");
        assertNotEquals(defaultIcon, updated.getIcon());
        assertEquals("new", updated.getIcon());
        assertEquals("123", updated.getPhone());
    }

    @Test
    public void findAllWithPagination() {
        service.create("Foo Bar", "foo@dummy.com", "1234567890");
        assertEquals(1, service.findAll(0, 10).size());
        assertEquals(0, service.findAll(1, 10).size());
        assertEquals(0, service.findAll(2, 10).size());

        IntStream.range(0, 101).forEach(i -> service.create("Foo Bar " + i, "foo" + i + "@mail.com", "1234" + i));
        IntStream.range(0, 10).forEach(i -> assertEquals(10, service.findAll(i, 10).size()));
        assertEquals(2 /* foo + foo~100 */, service.findAll(10, 10).size());
        assertEquals(0, service.findAll(11, 10).size());
    }
}
