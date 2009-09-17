/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Content Registry 2.0.
 *
 * The Initial Owner of the Original Code is European Environment
 * Agency.  Portions created by Tieto Eesti are Copyright
 * (C) European Environment Agency.  All Rights Reserved.
 *
 * Contributor(s):
 * Aleksandr Ivanov, Tieto Eesti
 */
package eionet.cr.util;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import eionet.cr.web.util.RecentResourcesCache;

/**
 * @author Aleksandr Ivanov
 * <a href="mailto:aleksandr.ivanov@tietoenator.com">contact</a>
 */
public class RecentDiscoveredFilesCacheTest extends TestCase {
	
	@Test
	public void testCache(){
		RecentResourcesCache cache = RecentResourcesCache.getInstance();
		assertEquals(0, cache.getRecentDiscoveredFiles(100).size());
		for(int i = 0; i < 100; i++){
			cache.updateCache(getTestData(11));
			assertEquals(10, cache.getRecentDiscoveredFiles(10).size());
		}
		
		assertEquals(3, cache.getRecentDiscoveredFiles(3).size());
		assertEquals("8", cache.getRecentDiscoveredFiles(3).get(0).getId());
		
	}

	/**
	 * @return
	 */
	private List<Pair<String, String>> getTestData(int size) {
		List<Pair<String,String>> result = new LinkedList<Pair<String,String>>();
		for(int i=0; i< size; i++) {
			result.add(new Pair<String, String>(i + "", "value"));
		}
		return result;
	}

}
