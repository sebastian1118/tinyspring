package org.triiskelion.tinyspring.dao;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Sebastian MA
 * Date: August 21, 2014
 * Time: 11:17
 */
public class TinyPredicate {

	/**
	 * Operator of a simple predicate. Available only if the predicate is simple
	 */
	private final Operator operator;

	protected PredicateType predicateType = PredicateType.SIMPLE;

	protected List<TinyPredicate> predicateList = new ArrayList<>();

	protected String column;

	/**
	 * the parameter values of the predicate. Available only if the predicate is simple
	 */
	protected List<Object> values = new ArrayList<>();

	/**
	 * Indicates if the predicate is valid. Available only if the predicate is simple
	 */
	protected boolean isValid = true;

	protected boolean empty = false;


	////////////////////////////////////////////////////////////////////////////////////////
	// construction methods
	////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Creates an empty conjunction predicate with specific type
	 *
	 * @param type
	 * 		type of the predicate
	 */
	protected TinyPredicate(PredicateType type) {

		this.predicateType = type;

		operator = null;
	}


	/**
	 * Creates a simple predicate
	 *
	 * @param alias
	 * @param column
	 * @param operator
	 * @param values
	 */
	//	protected TinyPredicate(String alias, String column, Operator operator, Object... values) {
	//
	//		isValid = validateParameters(operator, values);
	//		empty = !isValid;
	//
	//		if(StringUtils.isBlank(alias)) {
	//			alias = TinyQuery.TABLE_ALIAS;
	//		}
	//
	//		this.operator = operator;
	//		this.column = String.format("%s.%s", alias, column);
	//		Lists.addAll(this.values, values);
	//	}

	/**
	 * Creates a simple predicate
	 *
	 * @param alias
	 * @param column
	 * @param operator
	 * @param values
	 */
	protected TinyPredicate(String alias, String column, Operator operator, List values) {

		isValid = validateParameters(operator, values);
		empty = !isValid;

		if(StringUtils.isBlank(alias)) {
			alias = TinyQuery.TABLE_ALIAS;
		}

		this.operator = operator;
		this.column = String.format("%s.%s", alias, column);
		this.values.addAll(values);
	}


	private static TinyPredicate createPredicate(PredicateType type) {

		return new TinyPredicate(type);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// public methods
	////////////////////////////////////////////////////////////////////////////////////////
	public TinyPredicate and(TinyPredicate other) {

		if(other == null) {
			return this;
		}

		return and(this, other);
	}

	public TinyPredicate or(TinyPredicate other) {

		if(other == null) {
			return this;
		}

		return or(this, other);
	}

	public TinyPredicate negate() {

		return not(this);
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// static methods
	////////////////////////////////////////////////////////////////////////////////////////

	public static TinyPredicate isNull(String column) {

		return isNull(null, column);
	}

	public static TinyPredicate isNull(String alias, String column) {

		return new TinyPredicate(alias, column, Operator.isNull, listOf());
	}

	public static TinyPredicate isNotNull(String column) {

		return isNotNull(null, column);
	}

	public static TinyPredicate isNotNull(String alias, String column) {

		return new TinyPredicate(alias, column, Operator.isNotNull, listOf());
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate equal(String column, Object value) {

		return equal(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate equal(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.equal, listOf(value));
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate notEqual(String column, Object value) {

		return notEqual(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate notEqual(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.notEqual, listOf(value));
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate lessThan(String column, Object value) {

		return lessThan(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate lessThan(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.lessThan, listOf(value));
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate lessThanOrEqual(String column, Object value) {

		return lessThanOrEqual(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate lessThanOrEqual(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.lessThanOrEqual, listOf(value));
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate greaterThan(String column, Object value) {

		return greaterThan(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate greaterThan(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.greaterThan, listOf(value));
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate greaterThanOrEqual(String column, Object value) {

		return greaterThanOrEqual(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate greaterThanOrEqual(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.greaterThanOrEqual, listOf(value));
	}

	/**
	 * @param column
	 * @param value
	 *
	 * @return
	 */
	public static TinyPredicate like(String column, Object value) {

		return like(null, column, value);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 * @param value
	 * 		value object
	 * 		ignored if value is null.
	 *
	 * @return
	 */
	public static TinyPredicate like(String alias, String column, Object value) {

		return new TinyPredicate(alias, column, Operator.like, listOf(value));
	}

	/**
	 * @param column
	 *
	 * @return
	 */
	public static TinyPredicate between(String column, Object startValue, Object endValue) {

		return between(null, column, startValue, endValue);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 *
	 * @return
	 */
	public static TinyPredicate between(String alias, String column, Object startValue,
	                                    Object endValue) {

		return new TinyPredicate(alias, column, Operator.between, listOf(startValue,
				endValue));
	}

	/**
	 * @param column
	 *
	 * @return
	 */
	//	public static TinyPredicate in(String column, Object... values) {
	//
	//		return in(null, column, values);
	//	}
	public static TinyPredicate in(String column, List values) {

		return in(null, column, values);
	}

	/**
	 * @param alias
	 * 		JPQL table alias
	 * @param column
	 * 		JPQL column name
	 *
	 * @return
	 */
	//	public static TinyPredicate in(String alias, String column, Object... values) {
	//
	//		return new TinyPredicate(alias, column, Operator.in, values);
	//	}
	public static TinyPredicate in(String alias, String column, List values) {

		return new TinyPredicate(alias, column, Operator.in, values);
	}


	/**
	 * Concatenate the predicates with AND
	 *
	 * @param predicates
	 * 		Nullable
	 *
	 * @return
	 */
	public static TinyPredicate and(TinyPredicate... predicates) {

		if(predicates == null || predicates.length == 0) {
			throw new IllegalArgumentException("operation 'AND' requires at least 1 predicate");
		}

		if(predicates.length == 1) {
			return predicates[0];
		}
		TinyPredicate result = TinyPredicate.createPredicate(PredicateType.AND);
		for(TinyPredicate p : predicates) {
			result.empty |= p.empty;
			if(p.predicateType == PredicateType.AND) {
				result.predicateList.addAll(p.predicateList);
			} else {
				result.predicateList.add(p);
			}
		}
		return result;
	}

	/**
	 * conjunct all the predicates with OR
	 *
	 * @param predicates
	 *
	 * @return
	 */
	public static TinyPredicate or(TinyPredicate... predicates) {

		if(predicates == null || predicates.length == 0) {
			throw new IllegalArgumentException("operation 'OR' requires at least 1 predicate");
		}
		if(predicates.length == 1) {
			return predicates[0];
		}
		TinyPredicate result = TinyPredicate.createPredicate(PredicateType.OR);
		for(TinyPredicate p : predicates) {
			result.empty |= p.empty;
			if(p.predicateType == PredicateType.OR) {
				result.predicateList.addAll(p.predicateList);
			} else {
				result.predicateList.add(p);
			}
		}
		return result;
	}

	public static TinyPredicate not(TinyPredicate predicate) {

		if(predicate == null) {
			throw new IllegalArgumentException("operation 'NOT' requires a predicate");
		}

		TinyPredicate result = TinyPredicate.createPredicate(PredicateType.NOT);
		result.empty = predicate.empty;
		result.predicateList.add(predicate);
		return result;
	}


	/**
	 * Validates the values of the expression.
	 *
	 * @param type
	 * 		type of the operation
	 * @param values
	 * 		values for parameters
	 *
	 * @return
	 */
	protected static boolean validateParameters(Operator type, List values) {

		switch(type) {
			case isNull:
			case isNotNull:
				return true;
			case equal:
			case notEqual:
			case lessThan:
			case lessThanOrEqual:
			case greaterThan:
			case greaterThanOrEqual:
			case like:
				return values != null && values.size() >= 1
						&& values.get(0) != null;
			case between:
				return values != null && values.size() >= 2
						&& values.get(0) != null && values.get(1) != null;
			case in:
				boolean result = values != null && values.size() >= 1;
				if(values != null) {
					for(Object obj : values) {
						result &= obj != null;
					}
				}
				return result;
			default:
				throw new IllegalArgumentException("Unknown operator");
		}

	}

	public String createExpression(TinyQuery query) {

		StringBuilder buffer = new StringBuilder(column);
		switch(operator) {
			case isNull:
				buffer.append(" IS NULL ");
				break;
			case isNotNull:
				buffer.append(" IS NOT NULL ");
				break;
			case equal:
				buffer.append("=");
				break;
			case notEqual:
				buffer.append("<>");
				break;
			case lessThan:
				buffer.append("<");
				break;
			case lessThanOrEqual:
				buffer.append("<=");
				break;
			case greaterThan:
				buffer.append(">");
				break;
			case greaterThanOrEqual:
				buffer.append(">=");
				break;
			case like:
				buffer.append(" LIKE ");
				break;
			case between:
				buffer.append(" BETWEEN");
				break;
			case in:
				buffer.append(" IN ");
				break;
			default:
				throw new IllegalArgumentException("Unknown operator");
		}
		switch(operator) {
			// zero parameter
			case isNull:
			case isNotNull:
				break;

			// 1 parameter
			case equal:
			case notEqual:
			case lessThan:
			case lessThanOrEqual:
			case greaterThan:
			case greaterThanOrEqual:
			case like:
				String valueHolder = column.replace(".", "_") + "_" + query.index;
				query.namedParameters.put(valueHolder, values.get(0));
				query.index++;
				buffer.append(":").append(valueHolder);
				break;

			// 2 parameters
			case between:
				String valueHolder0 = column.replace(".", "_") + "_" + query.index + "_0";
				String valueHolder1 = column.replace(".", "_") + "_" + query.index + "_1";
				query.namedParameters.put(valueHolder0, values.get(0));
				query.namedParameters.put(valueHolder1, values.get(1));
				query.index = query.index + 1;
				buffer.append(":").append(valueHolder0).append(" AND :").append(valueHolder1);
				break;

			// infinite parameters
			case in:
				List<String> list = new ArrayList<>();
				for(Object value : values) {
					valueHolder
							= column.replace(".", "_") + "_" + query.index + "_" + values
							.indexOf(value);
					list.add(":" + valueHolder);
					query.namedParameters.put(valueHolder, value);
				}
				query.index++;
				buffer.append("(").append(StringUtils.join(list, ",")).append(")");
				break;

			default:
				throw new IllegalArgumentException("Unknown operator: " + operator);
		}

		return buffer.toString();
	}

	public enum Operator {
		isNull, isNotNull,
		equal, notEqual,
		lessThan, lessThanOrEqual,
		greaterThan, greaterThanOrEqual,
		between, in, like,
	}

	/**
	 * A predicate is considered simple if it does not contain logical conjunction like AND, OR,
	 * NOT.
	 *
	 * @return TRUE if the predicate is simple, otherwise FALSE.
	 */
	public boolean isSimple() {

		return predicateType == PredicateType.SIMPLE;
	}

	public String toString() {

		return String.format("Predicate[%s]", column != null ? column : predicateType);
	}


	/**
	 * @param parameter
	 * 		nullable
	 *
	 * @return
	 */
	public static String likeParameterContains(String parameter) {

		return parameter == null ? null : "%" + parameter + "%";
	}

	public static List listOf(Object... objects) {

		List result = new ArrayList();
		Collections.addAll(result, objects);
		return result;
	}
}
