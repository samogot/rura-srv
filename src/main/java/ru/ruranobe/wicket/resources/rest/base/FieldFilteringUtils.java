package ru.ruranobe.wicket.resources.rest.base;

import org.apache.commons.beanutils.PropertyUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class FieldFilteringUtils
{
    public static HashSet<String> parseFieldsList(String columnsString)
    {
        return new HashSet<>(Arrays.asList(columnsString.split("\\|")));
    }

    public static HashSet<String> getImageFields(HashSet<String> fields)
    {
        return filterFieldList(fields, "image");
    }

    public static void filterAllowedFields(Object o, HashSet<String> fields)
    {
        if (o == null)
        {
            return;
        }
        try
        {
            Map<String, Object> describe = PropertyUtils.describe(o);
            for (String prop : describe.keySet())
            {
                if (!fields.contains(prop) && PropertyUtils.isWriteable(o, prop))
                {
                    try
                    {
                        PropertyUtils.setSimpleProperty(o, prop, null);
                    }
                    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static HashSet<String> filterFieldList(HashSet<String> fields, String prefix)
    {

        HashSet<String> filteredFields = new HashSet<>();
        for (String field : fields)
        {
            if (field.startsWith(prefix) && field.length() > prefix.length())
            {
                filteredFields.add(firstToLower(field.substring(prefix.length())));
            }
        }
        return filteredFields;
    }

    private static String firstToLower(String original)
    {
        if (original == null || original.length() == 0)
        {
            return original;
        }
        return original.substring(0, 1).toLowerCase() + original.substring(1);
    }
}
