package ru.ruranobe.mybatis.handlers;

/**
 * Обработчик enum для mybatis. Для сохранения в БД преобразует название enum в нижний регистр,
 * а при извлечении в верхний. Это полезно для таких случаев, когда строки в БД хранятся в нижнем регистре и конфликтуют
 * с ключевыми словами Java, например, int и float.
 */
public class UpperToLowerCaseEnumTypeHandler<E extends Enum<E>> extends CustomEnumTypeHandler<E> {

	public UpperToLowerCaseEnumTypeHandler(Class<E> type) {
		super(type);
	}

	@Override
	public String encode(E e) {
		return e.name().toLowerCase();
	}

	@Override
	public E decode(String s) {
		return s == null ? null : Enum.valueOf(type, s.toUpperCase());
	}
}
