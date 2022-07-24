package us.mytheria.bloblib.jlib.storage.database.wrapped;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/03/2016
 */
public final class WrappedParameters {
    private final Map<Integer, Object> params = new HashMap<>();

    public WrappedParameters addParameter(int position, Object o) {
        this.params.put(position, o);
        return this;
    }

    public void populate(PreparedStatement ps) throws SQLException {
        for(Map.Entry<Integer, Object> entry : this.params.entrySet()) {
            Object o = entry.getValue();
            if (o instanceof String) ps.setString(entry.getKey(), (String) o);
            else if (o instanceof Integer) ps.setInt(entry.getKey(), (Integer) o);
            else if (o instanceof Boolean) ps.setBoolean(entry.getKey(), (Boolean) o);
            else throw new UnsupportedOperationException("unsupported type " + o.getClass().getSimpleName());
        }
    }
}
