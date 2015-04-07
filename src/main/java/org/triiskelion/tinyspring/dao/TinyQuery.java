package org.triiskelion.tinyspring.dao;

import com.google.common.base.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.triiskelion.tinyspring.viewmodel.Page;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A tiny flexible JPQL query builder
 * <p/>
 * <ul>
 * <li>select()</li>
 * <li>select().from()</li>
 * <li>select().join(clazz,col,alias)</li>
 * <li>select().from(clazz,alias).join(clazz,col,alias)</li>
 * </ul>
 *
 * @author tian MA
 * @link https://github.com/sebastian1118/tinyspring
 * <p/>
 */
public class TinyQuery<T> {

	protected static Logger log = LoggerFactory.getLogger(TinyQuery.class);

	/**
	 * Table alias for the query
	 */
	public static final String TABLE_ALIAS = "_this";

	protected Class<T> entityClass;

	protected EntityManager entityManager;

	/**
	 * the raw JPQL expression set by <code>query()</code>
	 */
	protected StringBuilder jpqlExp = new StringBuilder();


	// select and join clause
	protected Class selectClass;

	private TinyEntity fromEntity;

	private TinyEntity joinEntity;

	private String joinColumn;

	HashMap<Class, String> aliasMap = new HashMap<>();

	String[] selectedColumns;
	// select and join clause

	/**
	 * The DELETE clause
	 */
	protected StringBuilder deleteClause = new StringBuilder();

	/**
	 * The WHERE clause
	 */
	protected StringBuilder whereClause = new StringBuilder();

	/**
	 * The ORDER BY clause
	 */
	protected StringBuilder orderByClause = new StringBuilder();

	/**
	 * The GROUP BY clause
	 */
	protected StringBuilder groupByClause = new StringBuilder();


	protected boolean distinct = false;

	/**
	 * Map for the query's named parameters.
	 */
	protected HashMap<String, Object> namedParameters = new HashMap<>();

	/**
	 * Map for the query's positional parameters.
	 */
	protected HashMap<Integer, Object> positionalParameters = new HashMap<>();

	protected int index = 0;

	/**
	 * Whether parameter with null value should be ignored.
	 */
	protected boolean ignoreNullParameter = true;

	/**
	 * Start row numbered from 0<br>
	 * It is used for row based pagination
	 */
	protected int startRow = -1;

	/**
	 * Max rows to retrieve<br>
	 * It is used for row based pagination
	 */
	protected int maxRow = -1;

	/**
	 * Start page numbered from 1.<br>
	 * It is used for page based pagination
	 */
	private Integer pageNumber;

	/**
	 * Rows per page to retrieve<br>
	 * It is used for page based pagination
	 */
	private Integer numberPerPage;

	/**
	 * TRUE if page based pagination is applied.
	 */
	private boolean paged = false;

	/**
	 * If TRUE the built jpql will be printed.
	 */
	protected boolean showJpql = true;


	/**
	 * @param entityManager
	 * 		JPA entity manager
	 * @param entityClass
	 * 		entity to query
	 */
	public TinyQuery(EntityManager entityManager, Class<T> entityClass) {

		this(entityManager, entityClass, false);
	}

	/**
	 * @param entityManager
	 * 		JPA entity manager
	 * @param entityClass
	 * 		entity to query
	 * @param showJpql
	 * 		pass TRUE to print final JPQL expression.
	 */
	public TinyQuery(EntityManager entityManager, Class<T> entityClass, boolean showJpql) {

		this.entityManager = entityManager;
		this.entityClass = entityClass;
		this.showJpql = showJpql;
	}

	/**
	 * Use ignoreNull() instead. This method is planned to be removed.
	 *
	 * @param ignored
	 * 		whether null parameters should be ignored.
	 *
	 * @return the same TinyQuery instance
	 *
	 * @see org.triiskelion.tinyspring.dao.TinyQuery#ignoreNull(boolean)
	 */
	@Deprecated
	public TinyQuery<T> ignoreNullParameter(boolean ignored) {

		this.ignoreNullParameter = ignored;
		return this;
	}

	/**
	 * Set whether null-valued parameters should be ignored.<br>
	 * If set to TRUE, the predicates in WHERE clause with null-valued parameter will be ignored.
	 * otherwise an IllegalArgumentException will be thrown.<br><br>
	 * Ignoring null-valued predicate is convenient for the optional conditions like filters,
	 * but ignoring a obligatory condition may bring a false result. For example you want to
	 * find user with specific username, if the username passed in as parameter is null and
	 * ignored, the query will retrieve all the users. In this case use
	 * <code>ignoreNull(false)</code> to make TinyQuery check null parameters and throw an
	 * IllegalArgumentException if a null value is encountered.
	 *
	 * @param ignored
	 * 		whether null parameters should be ignored.
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> ignoreNull(boolean ignored) {

		this.ignoreNullParameter = ignored;
		return this;
	}

	public void close() {

		try {
			entityManager.close();
		} catch(IllegalStateException e) {
			log.warn("Can not close a container-managed entity manager.");
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	// BEGIN structural JPQL query
	//

	/**
	 * Delete from the entity class managed by the query. Exclusive to select() and update()
	 * Corresponding JPQL is "DELETE FROM entityClass _this"
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> delete() {

		deleteClause.append(
				String.format("DELETE FROM %s %s", entityClass.getCanonicalName(), TABLE_ALIAS));

		return this;
	}

	/**
	 * Select from the entity class managed by the query. Exclusive to delete() and update()
	 * Corresponding JPQL is "SELECT _this FROM entityClass _this"
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> select() {

		selectClass = entityClass;
		return this;
	}

	public <N> TinyQuery<N> select(Class<N> clazz) {

		return new TinyQuery<>(entityManager, clazz, showJpql).select();
	}

	/**
	 * Select specific columns from the entity class managed by the query. Exclusive to delete()
	 * and update().<br>
	 * the result must be retrieved using <code>getUntypedResultList() </code>
	 *
	 * @return the same TinyQuery instance
	 */
	//todo select from different table is broken!
	public TinyQuery<T> select(String... columns) {

		select();
		this.selectedColumns = columns;
		return this;
	}


	/**
	 * Add additional entity to FROM clause besides the entity class managed by the query.
	 * It must be invoked after select().
	 *
	 * @param entityClass
	 * 		entity class
	 * @param alias
	 * 		JPQL table alias
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> from(Class entityClass, String alias) {


		fromEntity = new TinyEntity(entityClass, alias);
		aliasMap.put(entityClass, alias);
		return this;
	}


	/**
	 * Add additional entity to FROM clause besides the entity  which the invoking DAO object
	 * represents. must invoke after <code>select()</code>.
	 *
	 * @param column
	 * 		column to join
	 * @param alias
	 * 		JPQL joined table's alias
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> join(Class entityClass, String column, String alias) {

		joinEntity = new TinyEntity(entityClass, alias);
		joinColumn = column;
		aliasMap.put(entityClass, alias);
		return this;
	}

	/**
	 * Distinguish the result. This will add DISTINCT keyword to the query. Can be invoked
	 * anywhere before retrieving the result.
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> distinct() {

		distinct = true;

		return this;
	}

	/**
	 * Add predicates to the restriction(the WHERE clause).
	 * Safe for multiple invocation, in this case the predicates are conjoined with AND.
	 *
	 * @param predicates
	 * 		the restriction conditions
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> where(TinyPredicate... predicates) {

		and(predicates);
		return this;
	}

	/**
	 * Conjoin the predicates with AND then with the previous predicates.
	 *
	 * @param predicates
	 * 		the restriction conditions, multiple predicates will be conjoined with AND.
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> and(TinyPredicate... predicates) {

		if(predicates == null || predicates.length == 0) {
			return this;
		}

		checkPredicates(predicates);

		TinyPredicate merged = TinyPredicate.and(predicates);
		if(!merged.empty) {
			whereClause.append(whereClause.length() == 0 ? " WHERE " : " AND ")
			           .append(formatPredicate(merged));
		}
		return this;
	}


	/**
	 * Conjoin the predicates and the previous predicates with OR.
	 *
	 * @param predicates
	 * 		the restriction conditions, multiple predicates will be conjoined with AND.
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> or(TinyPredicate... predicates) {

		if(predicates == null || predicates.length == 0) {
			return this;
		}

		checkPredicates(predicates);

		TinyPredicate merged = TinyPredicate.and(predicates);
		if(!merged.empty) {
			whereClause.append(whereClause.length() == 0 ? " WHERE " : " OR ")
			           .append(formatPredicate(merged));
		}
		return this;
	}

	/**
	 * Add an ORDER BY clause. For multiple invocation the clauses will be added successively.
	 *
	 * @param column
	 * 		column to apply
	 * @param orderType
	 * 		asc or desc
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> orderBy(String column, OrderType orderType) {

		return orderBy(null, column, orderType);
	}

	/**
	 * Add an ORDER BY clause for the columns from joined tables.
	 * For multiple invocation the clauses will be added successively.
	 *
	 * @param alias
	 * 		alias of the joined table
	 * @param column
	 * 		column to apply
	 * @param orderType
	 * 		asc or desc
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> orderBy(String alias, String column, OrderType orderType) {

		if(orderByClause.length() == 0) {
			orderByClause.append(" ORDER BY ");
		} else {
			orderByClause.append(",");
		}
		if(alias == null) {
			orderByClause.append(TABLE_ALIAS);
		} else {
			orderByClause.append(alias);
		}
		orderByClause.append(".").append(column).append(" ").append(orderType);
		return this;
	}

	/**
	 * Add an GROUP BY clause. Multiple invocation will be added successively.
	 * </code>
	 *
	 * @param column
	 * 		column to apply
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> groupBy(String column) {

		return groupBy(null, column);
	}

	/**
	 * Add GROUP BY clause for the columns from joined tables.
	 * For multiple invocation the clauses will be added successively.
	 *
	 * @param alias
	 * 		alias of the joined table
	 * @param column
	 * 		column to apply
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> groupBy(String alias, String column) {

		if(groupByClause.length() == 0) {
			groupByClause.append(" GROUP BY ");
		} else {
			groupByClause.append(",");
		}
		if(alias == null) {
			groupByClause.append(TABLE_ALIAS);
		} else {
			groupByClause.append(alias);
		}
		groupByClause.append(".").append(column).append(" ");
		return this;
	}
	//
	// END structural JPQL query
	////////////////////////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////////////////////////
	// BEGIN raw JPQL query
	//

	/**
	 * Set the JPQL query. Invoking this method will discard all precedent queries.
	 *
	 * @param jpql
	 * 		the JPQL expression
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> query(String jpql) {

		jpqlExp.append(jpql);
		return this;
	}

	/**
	 * Add a positional parameter
	 *
	 * @param position
	 * 		the position marked in the JPQL expression.
	 * @param value
	 * 		the value of the parameter
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> param(int position, Object value) {

		positionalParameters.put(position, value);
		return this;
	}

	/**
	 * add a named parameter
	 *
	 * @param name
	 * 		the name marked in the JPQL expression.
	 * @param value
	 * 		the value of the parameter
	 *
	 * @return the same TinyQuery instance
	 */
	public TinyQuery<T> param(String name, Object value) {

		namedParameters.put(name, value);
		return this;
	}
	//
	// END raw JPQL query
	////////////////////////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////////////////////////
	// BEGIN Result retrieving
	//

	/**
	 * Execute an update or delete statement
	 *
	 * @return the number of entities updated or deleted
	 */
	public int execute() {

		Query query = createQuery();
		return query.executeUpdate();
	}

	/**
	 * Count the query's result.
	 *
	 * @return number of the result
	 */
	public long count() {

		Query query = createQuery(true);
		return (long) query.getSingleResult();
	}

	/**
	 * Check if query has no result
	 *
	 * @return TRUE if query has no result, FALSE otherwise.
	 */
	public boolean hasNoResult() {

		return !hasResult();
	}

	/**
	 * Check if query has result
	 *
	 * @return TRUE if query has result, FALSE otherwise.
	 */
	public boolean hasResult() {

		Query query = createQuery(true);
		return (long) query.getSingleResult() != 0;
	}

	/**
	 * This method executes the JPA query and return the result of <code>getSingleResult()</code>.
	 * The returned value is untyped.
	 *
	 * @return the untyped result
	 *
	 * @see javax.persistence.Query#getSingleResult()
	 */
	public Object getSingleResult() {

		Query query = createQuery();
		return query.getSingleResult();
	}

	/**
	 * Execute a SELECT query and return the query's first result.
	 * The result is wrapped by com.google.common.base.Optional object.
	 *
	 * @return first element of the result list.
	 */
	public Optional<T> getFirstResult() {

		List<T> result = this.limit(0, 1).getResultList();
		return result.size() > 0 ? Optional.of(result.get(0)) : Optional.<T>absent();
	}

	/**
	 * Limit the number of the results based on page number.
	 * Null parameters will be ignored quietly.
	 *
	 * @param page
	 * 		page numbered from 1, nullable
	 * @param numberPerPage
	 * 		maximum number to retrieve, nullable
	 *
	 * @return the same TinyQuery instance
	 *
	 * @see javax.persistence.Query#setFirstResult(int)
	 * @see javax.persistence.Query#setMaxResults(int)
	 */
	public TinyQuery<T> page(Integer page, Integer numberPerPage) {

		if(page != null && numberPerPage != null) {
			this.pageNumber = page;
			this.numberPerPage = numberPerPage;
			this.paged = true;

			this.startRow = (page - 1) * numberPerPage;
			this.maxRow = numberPerPage;
		}
		return this;
	}

	/**
	 * Limit the number of the results based on rows.
	 * Null parameters will be ignored quietly.
	 *
	 * @param startRow
	 * 		position of the first row, numbered from 0, nullable
	 * @param maxRow
	 * 		maximum number to retrieve, nullable
	 *
	 * @return the same TinyQuery instance
	 *
	 * @see javax.persistence.Query#setFirstResult(int)
	 * @see javax.persistence.Query#setMaxResults(int)
	 */
	public TinyQuery<T> limit(Integer startRow, Integer maxRow) {

		if(startRow != null && maxRow != null) {
			this.startRow = startRow;
			this.maxRow = maxRow;
			this.paged = false;
		}
		return this;
	}

	/**
	 * Execute a SELECT query and return the query results as an List.
	 *
	 * @return the typed result list
	 */
	public List<T> getResultList() {

		Query query = createQuery();
		if(startRow >= 0 && maxRow >= 0) {
			query.setFirstResult(startRow).setMaxResults(maxRow);
		}
		return (List<T>) query.getResultList();
	}

	/**
	 * Execute a SELECT query and return the query results as an List.<br>
	 * The result will be cast into the type given by parameter.
	 *
	 * @return the typed result list
	 */
	public <R> List<R> getResultList(Class<R> clazz) {

		Query query = createQuery();
		if(startRow >= 0 && maxRow >= 0) {
			query.setFirstResult(startRow).setMaxResults(maxRow);
		}
		return (List<R>) query.getResultList();
	}

	/**
	 * Execute the SELECT statement and return the results wrapped in a Page object.
	 * Only available after invoking <code>page()</code>.
	 *
	 * @return results wrapped in a Page object.
	 *
	 * @see org.triiskelion.tinyspring.viewmodel.Page
	 */
	public Page<T> getPagedResult() {

		if(paged) {
			long total = count();
			List<T> result = getResultList();
			return new Page<>(result, pageNumber, numberPerPage, total);
		} else {
			throw new IllegalStateException("Query is not paged. call page() first.");
		}
	}

	/**
	 * Execute the SELECT statement and return the results wrapped in a Page object.
	 * Only available after invoking <code>page()</code>.<br>
	 * The result will be cast into the type given by parameter.
	 *
	 * @param clazz
	 * 		class to cast
	 *
	 * @return results wrapped in a Page object.
	 *
	 * @see org.triiskelion.tinyspring.viewmodel.Page
	 */
	public <R> Page<R> getPagedResult(Class<R> clazz) {

		if(paged) {
			long total = count();
			List<R> result = getResultList(clazz);
			return new Page<>(result, pageNumber, numberPerPage, total);
		} else {
			throw new IllegalStateException("Query is not paged. call page() first.");
		}
	}

	/**
	 * Execute a SELECT query and return the query results as an untyped List.
	 * This method is used to retrieve array result other than entity objects
	 * like aggregated value or specified columns.
	 *
	 * @return a untyped list of the results
	 *
	 * @see Query#getResultList()
	 */
	public List getUntypedResultList() {

		Query query = createQuery();
		if(startRow >= 0 && maxRow >= 0) {
			query.setFirstResult(startRow).setMaxResults(maxRow);
		}
		return query.getResultList();
	}
	//
	// END Result retrieving
	////////////////////////////////////////////////////////////////////////////////////////


	////////////////////////////////////////////////////////////////////////////////////////
	// BEGIN Internal methods
	//

	protected Query createQuery() {

		return createQuery(false);
	}

	/**
	 * Create JPA query
	 *
	 * @return JPA Query object
	 */
	protected Query createQuery(boolean count) {

		StringBuilder queryString = new StringBuilder();

		if(deleteClause.length() > 0) {
			queryString.append(deleteClause);

		} else {

			queryString.append(buildSelectClause(count));
			queryString.append(buildJoinClause());
		}

		queryString.append(whereClause);
		queryString.append(orderByClause);
		queryString.append(groupByClause);

		if(showJpql) {
			log.info("Query built: " + queryString);
		}

		Query query = entityManager.createQuery(queryString.toString());

		// apply parameters
		for(int key : positionalParameters.keySet()) {
			query.setParameter(key, positionalParameters.get(key));
		}

		for(String key : namedParameters.keySet()) {
			query.setParameter(key, namedParameters.get(key));
		}

		return query;
	}

	protected String buildJoinClause() {

		if(jpqlExp.length() <= 0) {
			StringBuilder joinClause = new StringBuilder();
			if(joinEntity != null) {
				joinClause.append(" JOIN ");
				joinClause.append(String.format("%s.%s %s",
						fromEntity == null ? TABLE_ALIAS : fromEntity.alias,
						joinColumn, joinEntity.alias));
			}
			return joinClause.toString();
		} else {
			return "";
		}
	}

	protected String buildSelectClause(boolean count) {

		if(jpqlExp.length() <= 0) {

			StringBuilder selectClause = new StringBuilder();
			selectClause.append("SELECT ");
			if(distinct) {
				selectClause.append(" DISTINCT ");
			}

			String selectAlias;
			if(selectedColumns != null) {
				ArrayList<String> list = new ArrayList<>();
				for(String col : selectedColumns) {
					String[] tokens = col.split("\\.");
					if(tokens.length == 1) {
						list.add(TABLE_ALIAS + "." + col);

					} else {// user.id  m.user.id
						if(aliasMap.values().contains(tokens[0])) { //m.user.id
							list.add(col);
						} else {
							list.add(TABLE_ALIAS + "." + col);
						}
					}

				}
				selectAlias = StringUtils.join(list, ",");

			} else {
				selectAlias = aliasMap.get(selectClass);
				if(selectAlias == null) {
					selectAlias = TABLE_ALIAS;
				}
			}

			if(count) {
				selectClause.append(String.format("count(%s)", selectAlias));
			} else {
				selectClause.append(String.format("%s", selectAlias));
			}

			selectClause.append(" FROM ");
			if(fromEntity == null)
				selectClause.append(selectClass.getCanonicalName()).append(" ").append
						(TABLE_ALIAS);
			else {
				selectClause.append(fromEntity.getEntityClass().getCanonicalName())
				            .append(" ").append(fromEntity.getAlias());
			}

			return selectClause.toString();
		} else {
			if(count) {
				//Pattern p = Pattern.compile("select.*from", Pattern.CASE_INSENSITIVE | Pattern
				//						.UNICODE_CASE);
				//				Matcher m = p.matcher(jpqlExp.toString());

				int begin = jpqlExp.toString().toLowerCase().indexOf("select");
				int end = jpqlExp.toString().toLowerCase().indexOf("from");
				if(begin >= 0 && end > 0) {
					String content = jpqlExp.toString().substring(begin + 7, end-1);
					return jpqlExp.toString()
					              .replaceFirst(" " + content + " ", " count(" + content + ") ");
				} else {
					throw new IllegalArgumentException("count() failed. SELECT FROM not matched");
				}

			} else {
				return jpqlExp.toString();
			}
		}
	}

	/**
	 * @return the JPQL expression
	 */
	public String toString() {

		StringBuilder buffer = new StringBuilder(buildSelectClause(false));
		buffer.append(buildJoinClause());
		buffer.append(whereClause);
		buffer.append(orderByClause);
		buffer.append(groupByClause);
		return buffer.toString();
	}

	/**
	 * Format recursively a predicate and all its descendants into JPQL expression and inject
	 * values of the predicate into query
	 *
	 * @param predicate
	 * 		the TinyPredicate object to parse
	 *
	 * @return parsed JPQL expression
	 */
	protected String formatPredicate(TinyPredicate predicate) {

		if(!predicate.isValid) {
			if(ignoreNullParameter) {
				return "";
			} else {
				throw new IllegalArgumentException(predicate.toString() + " is invalid");
			}
		}
		ArrayList<String> list;
		switch(predicate.predicateType) {
			case SIMPLE:
				return predicate.createExpression(this);
			case AND:
				list = new ArrayList<>();
				for(TinyPredicate p : predicate.predicateList) {
					String exp = formatPredicate(p);
					if(StringUtils.isNotBlank(exp)) {
						list.add(exp);
					}
				}
				return "(" + StringUtils.join(list, " AND ") + ")";
			case OR:
				list = new ArrayList<>();
				for(TinyPredicate p : predicate.predicateList) {
					list.add(formatPredicate(p));
				}
				return "(" + StringUtils.join(list, " OR ") + ")";
			case NOT:
				return "(NOT " + formatPredicate(predicate.predicateList.get(0)) + ")";
			default:
				throw new IllegalArgumentException("Unknown predicate type");
		}
	}

	/**
	 * Check if predicates are all valid.
	 * For invalid predicates if ignoreNullParameter is set to TRUE, an IllegalArgumentException
	 * will be thrown otherwise they will be ignored silently.
	 *
	 * @param predicates
	 */
	private void checkPredicates(TinyPredicate[] predicates) {

		if(!ignoreNullParameter) {
			for(TinyPredicate p : predicates) {
				if(!p.isValid) {
					throw new IllegalArgumentException("Predicate for field:" + p.column + " is " +
							"invalid.");
				}
			}
		}
	}

	//
	// END Internal methods
	////////////////////////////////////////////////////////////////////////////////////////

}
