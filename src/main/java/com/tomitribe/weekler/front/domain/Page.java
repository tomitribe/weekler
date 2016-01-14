package com.tomitribe.weekler.front.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {
    private int total;
    private boolean hasNext;
    private Collection<T> items;
}
