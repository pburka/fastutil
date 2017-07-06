/*
 * Copyright (C) 2017 Peter Burka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unimi.dsi.fastutil;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static org.junit.Assert.*;

import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Supplier;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectAVLTreeSet;
import it.unimi.dsi.fastutil.objects.ObjectRBTreeSet;

/**
 * Test various properties of NavigableSets. We include java.util.TreeSet
 * in this suite to demonstrate consistency with the JDK.
 * <p/>
 * TODO: We're currently testing SortedSet, rather than NavigableSet.
 *
 */

@RunWith(Parameterized.class)
public class NavigableSetTest {
	@Parameters
	public static Collection<Supplier<SortedSet<Integer>>> sets() {
		return java.util.Arrays.asList(
			() -> new TreeSet<>(), // validate test correctness against JDK
			() -> new IntAVLTreeSet(),
			() -> new IntRBTreeSet(),
			() -> new ObjectAVLTreeSet<>(),
			() -> new ObjectRBTreeSet<>());
	}

	private final Supplier<SortedSet<Integer>> supplier;
	
	public NavigableSetTest(Supplier<SortedSet<Integer>> supplier) {
		this.supplier = supplier;
	}

	@Test
	public void testSubSet() {
		SortedSet<Integer> all = supplier.get();
		all.add(10);
		all.add(20);
		all.add(30);
		
		SortedSet<Integer> some = all.subSet(10, 30);
		
		// comparator should be the same in both
		assertSame(all.comparator(), some.comparator());
		
		// lower bound is inclusive, upper is exclusive
		assertEquals(2, some.size());
		assertEquals(Integer.valueOf(10), some.first());
		assertEquals(Integer.valueOf(20), some.last());
		assertTrue(some.contains(10));
		assertTrue(some.contains(20));
		assertFalse(some.contains(30));
		
		// test subsets of the subset
		assertEquals(singleton(10), some.subSet(10, 20));
		assertEquals(singleton(10), some.headSet(20));
		assertEquals(singleton(20), some.tailSet(20));
		assertEquals(emptySet(), some.subSet(20, 20));
		assertEquals(emptySet(), some.headSet(10));
		assertEquals(emptySet(), some.tailSet(21));
		
		// adding an element to the subset affects the parent
		some.add(25);
		assertEquals(3, some.size());
		assertEquals(4, all.size());
		assertTrue(some.contains(25));
		assertTrue(all.contains(25));
		
		// so does removing an element
		some.remove(10);
		assertEquals(2, some.size());
		assertEquals(3, all.size());
		assertFalse(some.contains(10));
		assertFalse(all.contains(10));

		// adding an out of range value to the parent doesn't affect the subset
		all.add(-10);
		assertEquals(2, some.size());
		assertEquals(4, all.size());
		assertFalse(some.contains(-10));
		assertTrue(all.contains(-10));
		
		// clearing the subset removes its elements from the parent
		some.clear();
		assertEquals(0, some.size());
		assertEquals(2, all.size());
		assertEquals(all, new HashSet<>(asList(-10, 30)));
	}

	@Test
	public void testSubSetEmptyRange() {
		SortedSet<Integer> all = supplier.get();
		all.add(10);
		SortedSet<Integer> some = all.subSet(10, 10);
		assertTrue(some.isEmpty());
		assertFalse(some.contains(10));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubSetBadRange()
	{
		SortedSet<Integer> set = supplier.get();
		set.subSet(0, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubSetBadRangeExtreme()
	{
		SortedSet<Integer> set = supplier.get();
		set.subSet(Integer.MAX_VALUE, Integer.MIN_VALUE);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSubSetRestrictedRangeLower()
	{
		// calling subset on a subset should throw if either to or from are out of the original subset's range
		SortedSet<Integer> all = supplier.get();
		SortedSet<Integer> some = all.subSet(10, 20);
		some.subSet(9, 15);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSubSetRestrictedRangeUpper()
	{
		// calling subset on a subset should throw if either to or from are out of the original subset's range
		SortedSet<Integer> all = supplier.get();
		SortedSet<Integer> some = all.subSet(10, 20);
		some.subSet(15, 21);
	}

}
