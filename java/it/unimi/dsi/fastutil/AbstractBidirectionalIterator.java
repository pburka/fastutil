/*		 
 * fastutil: Fast & compact type-specific collections for Java
 *
 * Copyright (C) 2002, 2003 Sebastiano Vigna 
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package it.unimi.dsi.fastutil;

/** An abstract class facilitating the creation of
 * {@linkplain it.unimi.dsi.fastutil.BidirectionalIterator bidirectional iterators}.
 *
 * <P>This class implements a trivial version of {@link #back()}.
 *
 * @see AbstractIterator
 */

public abstract class AbstractBidirectionalIterator extends AbstractIterator implements BidirectionalIterator {

	protected AbstractBidirectionalIterator() {}

	/** This method just iterates {@link #previous()} for at most <code>n</code> times, stopping if {@link
	 * #hasPrevious()} becomes false. */

	public int back( final int n ) { 
		int i = n;
		while( i-- != 0 && hasPrevious() ) previous(); 
		return n - i - 1;
	}

}

// Local Variables:
// mode: jde
// tab-width: 4
// End: