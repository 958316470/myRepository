package com.nutch.plugin;

import org.apache.hadoop.conf.Configuration;

/**
 * 一个Nutch插件是一套自定义的逻辑，对Nutch的核心功能或其他插件，提供了一个API，
 * 提供一个容器的扩展延伸。插件可以提供一个或一组扩展。扩展是可以动态地安装为扩
 * 展点侦听器的组件。扩展点是一种提供API并调用一组或一组已安装扩展的发布者。每
 * 个插件可以扩展基本的<代码> < /插件代码>。<密码> > < /编码插件实例作为插件相
 * 关功能的生命周期管理点。<密码> < /插件代码>将关联到Nutch插件管理系统关机。一
 * 个可能的用例的<代码>插件< /代码>实施创建或关闭数据库连接。
 *
 * @author 95831
 */
public class Plugin {

    private PluginDescriptor fDescriptor;

    protected Configuration conf;

    public void startUp() throws PluginRuntimeException{}

    public void shutDown() throws PluginRuntimeException{}

    public PluginDescriptor getDescriptor(){
        return fDescriptor;
    }

    private void setDescriptor(PluginDescriptor descriptor){
        fDescriptor = descriptor;
    }

    protected void finalize() throws Throwable{
        super.finalize();
        shutDown();
    }
}
