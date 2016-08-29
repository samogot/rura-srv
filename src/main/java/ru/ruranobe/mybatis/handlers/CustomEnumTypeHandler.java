package ru.ruranobe.mybatis.handlers;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class CustomEnumTypeHandler<E extends Enum<E>> extends BaseTypeHandler<E> {

	protected final Class<E> type;

	public CustomEnumTypeHandler(Class<E> type) {
		this.type = type;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
		if (jdbcType == null) {
			ps.setString(i, encode(parameter));
		} else {
			ps.setObject(i, encode(parameter), jdbcType.TYPE_CODE); // see r3589
		}
	}

	@Override
	public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return decode(rs.getString(columnName));
	}

	@Override
	public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return decode(rs.getString(columnIndex));
	}

	@Override
	public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return decode(cs.getString(columnIndex));
	}

	/**
	 * Преобразовать enum в строку (например, для сохранения в БД)
	 *
	 * @param e enum
	 * @return строка
	 */
	public abstract String encode(E e);

	/**
	 * Преобразовать строку в enum (например, для конвертации в поле объекта)
	 *
	 * @param s строка
	 * @return enum
	 */
	public abstract E decode(String s);
}
