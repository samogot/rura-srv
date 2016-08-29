package ru.ruranobe.mybatis.entities.base;

import java.util.AbstractMap;
import java.util.Map;

/**
 * Пара ключ-значение.
 * Добавлен конструктор без параметров, чтобы можно было использовать этот класс, например, в mybatis.
 */
public class SimpleEntry<K,V> extends AbstractMap.SimpleEntry<K,V>
{
	public SimpleEntry()
	{
		this(null, null);
	}

	public SimpleEntry(K key, V value)
	{
		super(key, value);
	}

	public SimpleEntry(Map.Entry<? extends K, ? extends V> entry)
	{
		super(entry);
	}
}
