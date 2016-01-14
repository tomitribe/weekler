package com.tomitribe.weekler.service.jpa;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "weekler_week")
@NamedQueries({
    @NamedQuery(name = "Week.findAllDesc", query = "select w from Week w order by w.id.year desc, w.id.week desc")
})
public class Week {
    @EmbeddedId
    private ID id;

    @ManyToOne
    private Person person;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class ID {
        private int week;
        private int year;
    }
}
