/*******************************************************************************
 * Copyright 2010 Cees De Groot, Alex Boisvert, Jan Kotek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package jdbm.recman;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *  This class contains all Unit tests for {@link DataPage}.
 */
public class TestDataPage extends TestCase {

    public TestDataPage(String name) {
    	super(name);
    }
    

    /**
     *  Test basics - read and write at an offset
     */
    public void testReadWrite() throws Exception {
  byte[] data = new byte[RecordFile.DEFAULT_BLOCK_SIZE];
  BlockIo test = new BlockIo(0, data);
  test.writeShort(0, (short) (Magic.BLOCK + Magic.USED_PAGE));
  
  DataPage page = new DataPage(test, RecordFile.DEFAULT_BLOCK_SIZE);
  page.setFirst((short) 1000);
  
  assertEquals("first", 1000, page.getFirst());
    }

    /**
     *  Test factory method.
     */
    public void testFactory() throws Exception {
  byte[] data = new byte[RecordFile.DEFAULT_BLOCK_SIZE];
  BlockIo test = new BlockIo(0, data);
  test.writeShort(0, (short) (Magic.BLOCK + Magic.USED_PAGE));

  DataPage page = DataPage.getDataPageView(test,RecordFile.DEFAULT_BLOCK_SIZE);
  page.setFirst((short) 1000);
  
  assertEquals("first", 1000, page.getFirst());
    }
    
    
    /**
     *  Runs all tests in this class
     */
    public static void main(String[] args) {
  junit.textui.TestRunner.run(new TestSuite(TestDataPage.class));
    }
}
