package com.nutch.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

/**
 * <code> PluginClassLoader < /code>只包含运行时库安装在插件清单文件
 * 和插件所需的图书馆pluguin出口类。可以导出或不导出库。图书馆不出口
 * 只用于插件的<code> PluginClassLoader < /code>。
 * 出口可供<code> PluginClassLoader < /code>的插件，这些插件的依赖。
 *
 * @author 95831
 */
public class PluginClassLoader extends URLClassLoader {

    private URL[] urls;

    private ClassLoader parent;

    public PluginClassLoader(URL[] urls, ClassLoader partent) {
        super(urls, partent);
        this.urls = urls;
        this.parent = partent;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        final PluginClassLoader other = (PluginClassLoader) obj;
        if (parent == null) {
            if (other.parent != null) {
                return false;
            }
        } else if (!parent.equals(other.parent)) {
            return false;
        }
        if(!Arrays.equals(urls,other.urls)){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {

        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((parent == null) ? 0 : parent.hashCode());
        result = PRIME * result + Arrays.hashCode(urls);
        return result;
    }
}
