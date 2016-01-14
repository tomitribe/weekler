package com.tomitribe.weekler.service.jpa;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(of = "name")
@Entity
@Table(name = "weekler_person")
@NamedQueries({
    @NamedQuery(name = "Person.findAll", query = "select p from Person p order by p.name"),
    @NamedQuery(name = "Person.countAll", query = "select count(p) from Person p"),
    @NamedQuery(name = "Person.findLastOrdinal", query = "select max(p.ordinal) from Person p"),
    @NamedQuery(name = "Person.findByMinOrdinal", query = "select p from Person p where (select min(p2.ordinal) from Person p2) = p.ordinal"),
    @NamedQuery(name = "Person.findByOrdinal", query = "select p from Person p where p.ordinal = :ordinal")
})
public class Person {
    @Id
    @Column(length = 128)
    private String name;

    @Column(length = 256)
    private String displayName;

    @Column(length = 1024)
    private String icon;

    /**
     * Used to compute the affect a user to a week.
     */
    @Column(unique = true)
    private int ordinal;

    @NotNull
    @Column(length = 50)
    private String phone;

    @NotNull
    @Column(length = 128)
    private String mail;
}
