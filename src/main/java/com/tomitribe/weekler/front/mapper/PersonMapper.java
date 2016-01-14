package com.tomitribe.weekler.front.mapper;

import com.tomitribe.weekler.front.domain.PersonModel;
import com.tomitribe.weekler.service.jpa.Person;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class PersonMapper {
    public PersonModel toModel(final Person p) {
        return new PersonModel(p.getName(), p.getDisplayName(), p.getPhone(), p.getMail(), p.getIcon());
    }
}
