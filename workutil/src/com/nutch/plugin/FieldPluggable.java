package com.nutch.plugin;

import com.nutch.storage.WebPage;

import java.util.Collection;

public interface FieldPluggable extends Pluggable {
    public Collection<WebPage.Field> getFields();
}
