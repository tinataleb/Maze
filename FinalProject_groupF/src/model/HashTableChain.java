/**********************************************************************
* AUTHOR: Curtis Spears
* COURSE: CS 113 Data Structures & Algorithms
* SECTION: Mon/Wed 11 - 12:50 PM 
* LAST MODIFIED: 12/08/2016
***********************************************************************
* TITLE:				HashTableChain
* PROGRAM DESCRIPTION: HashTableChain class - creates HashTable using KWHashMap implementation. Overridden to be chain linked table - for collisions
* 					   Inner Classes: EntrySet, SetIterator, Entry
***********************************************************************/
package model;

//Imports
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

public class HashTableChain<K, V> extends AbstractMap<K, V> implements KWHashMap<K, V> {
	/************************************************************************************************************
	**CONTAINS INNER CLASS ENTRY , ENTRYSET, and SETITERATOR AT VERY END OF THE CLASS ***************************
	*************************************************************************************************************/
	//Constants *************************************************************************************************
	private	static final int START_CAPACITY	= 101; //initial table size: prime, odd number is preferable for collision prevention
	//note: Once numKeys/table.length == LOAD_THRESHOLD, table will be grown by REHASH_TABLE_GROWTH and rehashed
	private	double LOAD_THRESHOLD = 0.75; //percentage filled for which table will be rehashed. 
	//IMPORTANT*ENSURE LENGTH OF TABLE IS AN ODD NUMBER WHEN REHASH_TABLE_GROWTH IS USED FOR COLLISION PREVENTION
	private double REHASH_TABLE_GROWTH = 2; //amount table is grown when table exceeds LOAD_THRESHGOLD, rehash automatically rounds to next odd number
	//NO NEED FOR DELETION CONSTANT AS HASH TABLE IS A CHAIN (COLLISSIONS DON'T AFFECT INDEX)
	
	//Instance Variables ****************************************************************************************	
	//LinkedList currently user is default java LinkedList containing doubly linked list - single can be used as well (currently wasting space)
	private	LinkedList<Entry<K,V>>[] table; //array of inner class entry containing the K key and value
	private	int numKeys;	
	private	int numDeletes; //deletes replaced with DELETED constant in table ???????????????????******************************************************************************
	private int numCollisions;
	private int numOverrides; //times same key is entered and value is overridden 
		
	
	//Constructors ********************************************************************************************** 
	/** HashTableChain: default constructor for HashTableChain class
	 * @param n/a
	 * @return n/a
	 */
	public HashTableChain() {
		this.table = new LinkedList[START_CAPACITY];  
		this.numKeys = 0;
		this.numDeletes = 0;
		this.numCollisions = 0;
		this.numOverrides = 0;
	}
	
	/** HashTableChain: filled startCapacity constructor for HashTableChain class 
	 * @param int size //preferably odd, prime number
	 * @return n/a
	 */
	public HashTableChain(int startCapacity) {
		this.table = new LinkedList[startCapacity]; 
		this.numKeys = 0;
		this.numDeletes = 0;
		this.numCollisions = 0;
		this.numOverrides = 0;
	}
	
	/** HashTableChain: copy constructor for HashTableChain class
	 * @param HashTableChain original //to be copied
	 * @return n/a
	 */
	public HashTableChain(HashTableChain<K,V> original) {
		this.table = original.getTable(); //returns table.clone(): deep copies
		this.numKeys = original.getNumKeys();
		this.numDeletes = original.getNumDeletes();
		this.numCollisions = original.getNumCollisions();
		this.numOverrides = original.getNumOverrides();
	}
	
	//Accessors **************************************************************************************************************
	/** getTable: returns clone of this.table instance
	 * @param n/a
	 * @return LinkedList<Entry<K,V>>[] this.table.clone()
	 */
	public LinkedList<Entry<K,V>>[] getTable() {
		return this.table.clone(); //return deep copy of table
	}
	
	/** getNumKeys: returns numKeys instance variable
	 * @param n/a
	 * @return int numKeys
	 */
	public int getNumKeys() {
		return this.numKeys;
	}
	
	/** getNumDeletes: returns numDeletes instance variable
	 * @param n/a
	 * @return int numDeletes
	 */
	public int getNumDeletes() {
		return this.numDeletes;
	}
	
	/** getNumCollisions: returns numCollisions instance variable
	 * @param n/a
	 * @return int numCollisions
	 */
	public int getNumCollisions() {
		return this.numCollisions;
	}
	
	/** getNumOverrides: returns numOverrides instance variable
	 * @param n/a
	 * @return int numOverrides
	 */
	public int getNumOverrides() {
		return this.numOverrides;
	}
	
	//KWHASHMAP OVERRIDES ***************************************************************************************
	/** get: retrieve item from table using key instance
	 * @param Object key //note: object instead of K due to necessity of hashCode() method
	 * @return V key //null if key not found
	 */
	@Override
	public V get(Object key) {
		int index = key.hashCode() % this.table.length; 
		if(index < 0) {
			index += this.table.length;
		}
		if(this.table[index] == null) {
			return null; //not found
		}
		//else 
		for(Entry<K,V> entry : this.table[index]) {
			if(entry.getKey().equals(key)) {
				return entry.value;
			}
		}
		//else not found
		return null;
	}
	

	/** put: add item to table given a key to be hashed and value to be stored
	 * @param K key
	 * @return V key //null if key not found; if key already in table - replaces and returns previous value
	 */
	@Override
	public V put(K key, V value) {
		//find index based on key hashCode
		int index = key.hashCode() % this.table.length;
		if(index < 0) {
			index += this.table.length;
		}
		//no collision
		if(this.table[index] == null) {
			this.table[index] = new LinkedList();
			this.table[index].addFirst(new Entry<K,V>(key, value)); //add key/value to list
			this.numKeys++;
			if(numKeys > (LOAD_THRESHOLD * table.length)) { //rehashes when table is too full for efficiency
				rehash();
			}
			return null;
		}
		//else
		//collision
		this.numCollisions++; //unique key entered or key already in table being overridden, - both count as collision
		for(Entry<K,V> entry: this.table[index]) {
			//key already in table, replaces value, returns old value
			if(entry.getKey().equals(key)) {
				V oldValue = entry.getValue();
				entry.value = value;
				this.numOverrides++;
				//no need to increment num of keys, none were added just replaced - avoids excessive growth issues
				if(numKeys > (LOAD_THRESHOLD * table.length)) { //rehashes when table is too full for efficiency
					rehash();
				}
				return oldValue;
			}
		}
		//else key is unique to linked list, add to beginning of list at table[index]
		this.table[index].addFirst(new Entry<K,V>(key, value));
		this.numKeys++;
		if(numKeys > (LOAD_THRESHOLD * table.length)) { //rehashes when table is too full for efficiency
			rehash();
		}
		return null;
	}

	/** remove: remove item from table using key instance
	 * @param Object key //note: object instead of K due to necessity of hashCode() method
	 * @return V key //null if key not found
	 */
	@Override
	public V remove(Object key) {
		//find index using key
		int index = key.hashCode() % table.length;
		if(index < 0) {
			index += table.length;
		}
		//item not found, index is null
		if(table[index] == null) {
			return null;
		}
		//search index list for key
		else {
			for(Entry<K,V> entry : table[index]) {
				if(key.equals(entry.getKey())) { //found item
					V value = entry.getValue();
					table[index].remove(entry); //no need to set to DELETED like open source
					this.numKeys--;
					this.numDeletes++;
					//if empty list in table, initialize list null
					if(table[index].size() == 0) { 
						table[index] = null;
					}
					return value;
				}
			}
		}
		return null; //item not found in list
	}
	
	/** isEmpty: return boolean value based on table being null or empty
	 * @param n/a
	 * @return boolean isEmpty //table is empty
	 */
	@Override
	public boolean isEmpty() {
		return this.table == null || this.table.length == 0 || this.numKeys == 0; 
	}
	
	/** size: return size of table
	 * @param n/a
	 * @return int numKeys
	 */
	@Override
	public int size() {
		return HashTableChain.this.numKeys;
	}
	
	
	
	//HASHMAP HELPERS *****************************************************************************************
	/** rehash: expands table size when loadFactor exceeds LOAD_THRESHOLD and rehashed entries 
	 * 			into new array
	 * @post the size of table is doubled and is an odd integer. Each entry from the original
	 * 		 table is reinserted into the expanded table.
	 * @param n/a
	 * @return void
	 */
	 // If you use the rehash of the HashtableOpen the difference is in the re-insertion logic
	 public void rehash() {
		 //clone current table to avoid data loss 
		 LinkedList<Entry<K,V>>[] originalTable = (LinkedList<Entry<K,V>>[])this.table.clone();
		 //create the length (ensuring odd for collision prevention) for new table
		 int newLength = (int) (table.length * REHASH_TABLE_GROWTH);
		 if(newLength % 2 == 0) { //ensure newLength is odd
			 newLength++;
		 }
		 //reinitialize all instance variables and set new size (as everything must be re-added (old data is now irrelevant))
		 this.numKeys = 0;
		 this.numDeletes = 0;
		 this.numCollisions = 0;
		 this.numOverrides = 0;
		 this.table = new LinkedList[newLength]; //reset table
		 
		 //Copy elements from original table over to new table, rehashing keys
		 int index;
		 for(int i = 0; i < originalTable.length; i++) {
			 if(originalTable[i] == null) { //skip null values in old table
				 continue;
			 }
			 for(Entry<K,V> entry : originalTable[i]) {
				 this.put(entry.getKey(), entry.getValue());
			 }
		 } 
	 }
		
	//ABSTRACT MAP OVERRIDE *************************************************************************************
	/** entrySet: return set type version of table
	 * @param n/a
	 * @return Set<Map.Entry<K, V>> set
	 */
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		 return new EntrySet();
	}
	
	
	/** containsKey: return boolean value representing whether key was found in table
	 * @param Object target
	 * @return boolean found
	 */
	@Override
	public boolean containsKey(Object target) {
		for(LinkedList<Entry<K, V>> list : table) { //traverse table of linked lists
			if(list != null) {
				for(Entry<K,V> entry : list) { //traverse linked list
					if(entry.getKey().equals((K)target)) { 
						return true; //target found 
					}
				}	
			}
		}
		return false; //target not found 
	}
	
	//Other Required Methods ************************************************************************************
	/** toString: return String representation of table and lists within
	 * @param n/a
	 * @return String table
	 */
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(LinkedList list : table) {
			sb.append(list + "\n");
		}
		return sb.toString();
	} 
	
	
	/************************************************************************************************************
	 **INNER CLASS ENTRYSET (allows set view) *******************************************************************
	 ************************************************************************************************************/
	/** Inner class to implement the set view. */
	private class EntrySet extends java.util.AbstractSet<Map.Entry<K, V>> {
		
		/** size: return size of table
		 * @param n/a
		 * @return int numKeys
		 */
		@Override
		public int size() {
			return HashTableChain.this.numKeys;
		}

		/** iterator: return iterator for the set
		 * @param n/a
		 * @return Iterator<Map.Entry<K, V>> iterator
		 */
		@Override
		public Iterator<Map.Entry<K, V>> iterator() {
			return new SetIterator();
		}
	}
	/************************************************************************************************************
	 **END INNER CLASS ENTRYSET **********************************************************************************
	 ************************************************************************************************************/
	
	
	/************************************************************************************************************
	 **INNER CLASS SETITERATOR **********************************************************************************
	 ************************************************************************************************************/
	/** Inner class to implement the set iterator. */
	private class SetIterator implements Iterator<Map.Entry<K, V>> {
		//Instance Variables *********************************************************************************
		int index = 0;
		Iterator<Entry<K, V>> localIterator = null;
		
		//Iterator Method Overrides ***************************************************************************
		/** hasNext: return boolean on whether iterator has a next element
		 * @param n/a
		 * @return boolean hasNext
		 */
		@Override
		public boolean hasNext() {
			if (localIterator != null) {
				if (localIterator.hasNext()) {
					return true;
				} 
				else {
					localIterator = null;
					index++;
				}
			}
			while (index < table.length && table[index] == null) {
				index++;
			}
			if (index == table.length) {
				return false;
			}
			localIterator = table[index].iterator();
			return localIterator.hasNext();
		}
		
		/** next: return next Entry for set
		 * @param n/a
		 * @return Map.Entry<K, V> next
		 */
		@Override
		public Map.Entry<K, V> next() {
			if (localIterator != null) {
				if (localIterator.hasNext()) {
					index++;
					//localIterator.next();
					return (Map.Entry<K, V>)localIterator.next();
				} 
				//next is null
				else {
					localIterator = null;
					index++;
				}
			}
			//loop through to find next value that's not null until table end
			while (index < table.length && table[index] == null) { 
				index++;
			}
			//end of table
			if (index == table.length) { 
				return null;
			}
			//found index that is not null
			localIterator = table[index].iterator();
			return (Map.Entry<K, V>) localIterator.next();
		}
		
		/** remove: removes Entry at current position
		 * @param n/a
		 * @return n/a
		 */
		@Override
		public void remove() {
			if(localIterator != null) {
				index--;
			}
			localIterator.remove();
			
		}
	}
	/************************************************************************************************************
	 **END INNER CLASS SETITERATOR ******************************************************************************
	 ************************************************************************************************************/
	
	
	
	/************************************************************************************************************
	 **INNER CLASS ENTRY ****************************************************************************************
	 ************************************************************************************************************/
	private static class Entry<K,V> implements Map.Entry<K, V>{
		
		//Instance Variables *************************************************************************
		private K key;
		private V value;
		
		
		//Constructors *******************************************************************************
		/** Entry: filled Entry constructor
		 * @param n/a
		 * @return n/a
		 */
		public Entry(K key, V value) {
			this.key = key;
			this.setValue(value);
		}
		
		//Mutators & Accessors ***********************************************************************
		/** setValue: mutator for V value instance variable
		 * @param V value
		 * @return n/a
		 */
		public V setValue(V value) {
			V last = this.value;
			this.value = value;
			return last;
		}
		
		/** getValue: accessor for K key instance variable
		 * @param n/a
		 * @return K key
		 */
		public K getKey() {
			return this.key;
		}
		
		/** getValue: accessor for V value instance variable
		 * @param n/a
		 * @return V value
		 */
		public V getValue() {
			return this.value;
		}
		
		//Other Methods ******************************************************************************
		/** toString: returns instance variables in readable String format
		 * @param n/a
		 * @return String toString
		 */
		public String toString() {
			return this.key + ": " + this.value;
		}
			
	}
	
	/************************************************************************************************************
	 **END INNER CLASS ENTRY ************************************************************************************
	 ************************************************************************************************************/
	
}