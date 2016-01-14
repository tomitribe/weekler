package com.tomitribe.weekler.service;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class GravatarServiceTest {
    @Test
    public void compute() {
        final GravatarService service = new GravatarService() {{ url = "http://www.gravatar.com/avatar/{hash}?size=200"; }};
        final String url = service.computeIconUrl("GravatarServiceTest@tomitribe.com"); // dont use a real url!
        assertEquals("http://www.gravatar.com/avatar/d555c86d7514666972272c256f9eca79?size=200", url);
    }
}
