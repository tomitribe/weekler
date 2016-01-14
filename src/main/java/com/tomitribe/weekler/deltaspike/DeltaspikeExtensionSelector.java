package com.tomitribe.weekler.deltaspike;

import org.apache.deltaspike.core.impl.config.ConfigurationExtension;
import org.apache.deltaspike.core.spi.activation.ClassDeactivator;
import org.apache.deltaspike.core.spi.activation.Deactivatable;

public class DeltaspikeExtensionSelector implements ClassDeactivator {
    @Override
    public Boolean isActivated(final Class<? extends Deactivatable> targetClass) {
        return ConfigurationExtension.class == targetClass;
    }
}
