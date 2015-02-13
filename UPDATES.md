## 0.9.0
* rewrite select() from() join() to make more flexibility
* select by column names works for multiple tables
* getResultList() and getPagedResult() can receive a parameter to cast result to another type.
* query() can only define the select clause and be used with other clause methods(where(),
orderBy(), etc.). like:

		query.query("SELECT ... FROM ... JOIN ...")
         .where(...)

## 0.8.0
* getPagedResult() works with query().

## 0.7.0
* security module: add mechanism to authenticate a request statelessly.
