package eu.thedarken.rootvalidator.tools;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public class StringUtils {
    public static String join(@Nullable Object[] objects) {
        if (objects == null) return null;
        return join(Arrays.asList(objects));
    }

    public static String join(@Nullable Collection<?> collection) {
        return join(collection, ", ");
    }

    public static String join(@Nullable Collection<?> collection, @NonNull String separator) {
        if (collection == null) return null;
        if (collection.isEmpty()) return "{}";
        int total = collection.size() * separator.length();
        for (Object s : collection) {
            if (s != null) total += s.toString().length();
        }

        StringBuilder sb = new StringBuilder(total + 2);
        sb.append("{");
        Iterator<?> it = collection.iterator();
        while (it.hasNext()) {
            Object item = it.next();
            if (item != null) sb.append(item.toString());
            if (it.hasNext()) sb.append(separator);
        }
        sb.append("}");
        return sb.toString();
    }

    public static boolean isEmpty(String string) {
        return string == null || string.length() == 0;
    }
}
