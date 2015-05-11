package org.triiskelion.tinyspring.viewmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Page is a wrapper used to hold a list of retrieved data with its pagination
 * information. This object is immutable.
 *
 * @author Sebastian MA
 */
public class Page<T> {

	/**
	 * page number numbered from 1.
	 */
	private final long page;

	/**
	 * maximum number of the data for one page.
	 */
	private final long max;

	/**
	 * total number of the data
	 */
	private final long total;

	/**
	 * total page of the data
	 */
	private final long totalPage;

	/**
	 * actual number of the data retrieved
	 */
	private final long dataSize;

	/**
	 * associated data
	 */
	private final List<T> data;

	/**
	 * @param data
	 * 		data of the current page
	 * @param page
	 * 		current page number
	 * @param max
	 * 		max items per page
	 * @param total
	 * 		total items number
	 */
	public Page(List<T> data, long page, long max, long total) {

		this.page = page;
		this.max = max;
		this.total = total;
		this.totalPage = total / max + (total % max > 0 ? 1 : 0);
		this.data = data;
		this.dataSize = data != null ? data.size() : 0;
	}

	/**
	 * Total page numbers.
	 *
	 * @return
	 */
	public long getTotalPage() {

		return totalPage;
	}

	/**
	 * Actual object count in this page.
	 *
	 * @return
	 */
	public long getDataSize() {

		return dataSize;
	}


	/**
	 * Current page number.
	 *
	 * @return
	 */
	public long getPage() {

		return page;
	}


	/**
	 * Maximum objects per page.
	 *
	 * @return
	 */
	public long getMax() {

		return max;
	}


	/**
	 * Total object count in the set.
	 *
	 * @return
	 */
	public long getTotal() {

		return total;
	}


	/**
	 * Retrieve the list of the object current page holds.
	 *
	 * @return
	 */
	public List<T> getData() {

		return new ArrayList<>(data);
	}

	/**
	 * This method is used to map the objects in this page to another model.
	 * It's usually used to transform  database entities to view models.
	 *
	 * @param mapper
	 * @param <A>
	 * 		the model to map to.
	 *
	 * @return the mapped Page instance
	 */
	public <A> Page<A> map(Class<A> clazz, Mapper<T, A> mapper) {

		List<A> newData = new ArrayList<>();
		for(T t : getData()) {
			newData.add(mapper.map(t));
		}
		return new Page<>(newData, getPage(), getMax(), getTotal());
	}

	/**
	 * This method is used to map the objects in this page to another model.
	 * It's usually used to transform  database entities to view models.
	 *
	 * @param mapper
	 * @param <A>
	 * 		the model to map to.
	 *
	 * @return the mapped instance
	 */
	public <A> Page<A> map(Mapper<T, A> mapper) {

		List<A> newData = new ArrayList<>();
		for(T t : getData()) {
			newData.add(mapper.map(t));
		}
		return new Page<>(newData, getPage(), getMax(), getTotal());
	}

}
