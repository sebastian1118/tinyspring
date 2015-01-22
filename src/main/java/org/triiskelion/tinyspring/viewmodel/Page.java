package org.triiskelion.tinyspring.viewmodel;

import java.util.ArrayList;
import java.util.List;

/**
 * Page is designed to list of result as long as the result's pagination information<br>
 * User: Sebastian MA
 * Date: July 03, 2014
 * Time: 15:52
 */
public class Page<T> {

	private long page;

	private long max;

	private long total;

	private long totalPage;

	private long dataSize;

	private List<T> data;

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

	public long getTotalPage() {

		return totalPage;
	}


	public long getDataSize() {

		return dataSize;
	}


	public long getPage() {

		return page;
	}


	public long getMax() {

		return max;
	}


	public long getTotal() {

		return total;
	}


	public List<T> getData() {

		return data;
	}

	public <A> Page<A> map(Class<A> clazz, Mapper<T, A> mapper) {

		List<A> newData = new ArrayList<>();
		for(T t : getData()) {
			newData.add(mapper.map(t));
		}
		return new Page<A>(newData, getPage(), getMax(), getTotal());
	}

}
