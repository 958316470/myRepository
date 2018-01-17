package com.nutch.api.resources;

import com.nutch.api.impl.db.DbReader;
import com.nutch.api.model.request.DbFilter;
import com.nutch.api.model.response.DbQueryResult;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

@Path("/db")
public class DbResource extends AbstractResource{

    private Map<String, DbReader> readers = new WeakHashMap<String, DbReader>();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public DbQueryResult runQuery(DbFilter filter) {
        if (filter == null) {
            throwBadRequestException("Filter cannot be null!");
        }
        DbQueryResult result = new DbQueryResult();
        Iterator<Map<String, Object>> iterator = getReader().runQuery(filter);
        while (iterator.hasNext()) {
            result.addValues(iterator.next());
        }
        return result;
    }

    private DbReader getReader() {
        String confId = ConfigResource.DEFAULT;
        synchronized (readers) {
            if (!readers.containsKey(confId)) {
                readers.put(confId, new DbReader(confManager.get(confId), null));
            }
            return readers.get(confId);
        }
    }
}
