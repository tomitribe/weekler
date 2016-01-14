package com.tomitribe.weekler.service;

import com.tomitribe.weekler.service.jpa.Person;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.Singleton;
import javax.ejb.TransactionAttribute;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Locale;

import static javax.ejb.ConcurrencyManagementType.BEAN;
import static javax.ejb.TransactionAttributeType.SUPPORTS;

@Singleton
@ConcurrencyManagement(BEAN)
public class PersonService {
    @PersistenceContext
    private EntityManager entityManager;

    @Inject
    private TransactionService transactionService;

    @Inject
    private GravatarService gravatarService;

    @TransactionAttribute(SUPPORTS)
    public List<Person> findAll(final int page, final int pageSize) {
        return entityManager.createNamedQuery("Person.findAll", Person.class)
            .setFirstResult(page * pageSize)
            .setMaxResults(pageSize)
            .getResultList();
    }

    public int countAll() {
        return entityManager.createNamedQuery("Person.countAll", Number.class).getSingleResult().intValue();
    }

    public Person findByName(final String name) {
        return entityManager.find(Person.class, name);
    }

    public Person findByOrdinal(final int ordinal) {
        try {
            return entityManager.createNamedQuery("Person.findByOrdinal", Person.class)
                .setParameter("ordinal", ordinal)
                .getSingleResult();
        } catch (final NoResultException nre) {
            return null;
        }
    }

    public int findLastOrdinal() {
        try {
            return unsafeLastOrdinal().intValue();
        } catch (final NoResultException nre) {
            throw new IllegalStateException("No person in the database");
        }
    }

    private Number unsafeLastOrdinal() {
        return entityManager.createNamedQuery("Person.findLastOrdinal", Number.class).getSingleResult();
    }

    public Person findByFirstOrdinal() {
        try {
            return entityManager.createNamedQuery("Person.findByMinOrdinal", Person.class).getSingleResult();
        } catch (final NoResultException nre) {
            throw new IllegalStateException("No person in the database", nre);
        }
    }

    public Person create(final String displayName, final String mail, final String phoneNumber) {
        final Person person = new Person();
        setFields(person, displayName, mail, phoneNumber, gravatarService.computeIconUrl(mail));
        person.setName(computeId(displayName, mail));

        final Number result = unsafeLastOrdinal();
        person.setOrdinal((result == null ? 0 : result.intValue()) + 1);

        entityManager.persist(person);
        entityManager.flush();
        return person;
    }

    public Person delete(final String name) {
        final Person person = entityManager.find(Person.class, name);
        entityManager.remove(person);
        return person;
    }

    public Person update(final String name, final String displayName, final String mail, final String phoneNumber, final String icon) {
        final Person person = entityManager.find(Person.class, name);
        setFields(
            person, displayName, mail, phoneNumber,
            // if icon was not updated recomputed it from mail if it changed else keep the old one
            icon == null || icon.isEmpty() || (person.getIcon().equals(icon) && !person.getMail().equals(mail)) ?
                gravatarService.computeIconUrl(mail) : icon);
        entityManager.flush();
        return person;
    }

    // we generate the id to keep the url human friendly and not just get a number
    private String computeId(final String displayName, final String mail) { // should be url friendly
        final int at = mail.indexOf('@');
        final String proposal1 = at > 0 ? mail.substring(0, at) : mail;
        if (findByName(proposal1) == null) {
            return proposal1;
        }

        final String proposal2 = displayName.replace(" ", "").toLowerCase(Locale.ENGLISH);
        if (findByName(proposal2) == null) {
            return proposal2;
        }

        // let's generate one - should be pretty rare normally, if not this algo needs to be enhanced
        int idx = 0;
        String proposal;
        do {
            proposal = proposal1 + idx;
        } while (findByName(proposal) != null);
        return proposal;
    }

    private void setFields(final Person person,
                           final String displayName, final String mail, final String phoneNumber, final String icon) {
        person.setDisplayName(displayName);
        person.setMail(mail);
        person.setPhone(phoneNumber);
        person.setIcon(icon);
    }
}
