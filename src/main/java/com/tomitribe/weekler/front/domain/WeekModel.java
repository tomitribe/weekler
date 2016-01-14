package com.tomitribe.weekler.front.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeekModel {
    private int week;
    private int year;
    private PersonModel person;
}
