package com.nostra13.universalimageloader.core.assist.deque;

import java.util.NoSuchElementException;

/**
 * {@link LinkedBlockingDeque} using LIFO algorithm
 * 
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 * @since 1.6.3
 */
public class LIFOLinkedBlockingDeque<T> extends LinkedBlockingDeque<T> {

	private static final long serialVersionUID = -4114786347960826192L;

	/**
	 * Inserts the specified element at the front of this deque if it is possible to do so immediately without violating
	 * capacity restrictions, returning <tt>true</tt> upon success and <tt>false</tt> if no space is currently
	 * available. When using a capacity-restricted deque, this method is generally preferable to the {@link #addFirst
	 * addFirst} method, which can fail to insert an element only by throwing an exception.
	 * 
	 * @param e
	 *            the element to add
	 * @throws ClassCastException
	 *             {@inheritDoc}
	 * @throws NullPointerException
	 *             if the specified element is null
	 * @throws IllegalArgumentException
	 *             {@inheritDoc}
	 */
	@Override
	public boolean offer(T e) {
		return super.offerFirst(e);
	}

	/**
	 * Retrieves and removes the first element of this deque. This method differs from {@link #pollFirst pollFirst} only
	 * in that it throws an exception if this deque is empty.
	 * 
	 * @return the head of this deque
	 * @throws NoSuchElementException
	 *             if this deque is empty
	 */
	@Override
	public T remove() {
		return super.removeFirst();
	}
}