package com.tomitribe.weekler.front.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PersonModel {
    private String name;
    private String displayName;
    private String phone;
    private String mail;
    private String icon;
}
