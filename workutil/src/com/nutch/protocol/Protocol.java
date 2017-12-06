package com.nutch.protocol;

import com.nutch.plugin.FieldPluggable;
import com.nutch.storage.WebPage;
import crawlercommons.robots.BaseRobotRules;
import org.apache.hadoop.conf.Configurable;

public interface Protocol extends FieldPluggable, Configurable{

    public final static String X_POINT_ID = Protocol.class.getName();
    public final static String CHECK_BLOCKING = "protocol.plugin.check.blocking";
    public final static String CHECK_ROBOTS = "protocol.plugin.check.robots";

    ProtocolOutput getProtocolOutput(String url, WebPage page);

    BaseRobotRules getRobotRules(String url, WebPage page);
}
