package jdbm.helper;

import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import jdbm.InverseHashView;
import jdbm.RecordListener;
import jdbm.SecondaryHashMap;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;
import jdbm.btree.BTree;
import jdbm.btree.BTreeSecondarySortedMap;
import jdbm.htree.HTree;
import jdbm.htree.HTreeSecondaryMap;


/**
 * Utilities related to Secondary Maps
 * 
 * @author Jan Kotek
 *
 */
final public class SecondaryKeyHelper {

	   
    static public <A,K,V> BTree<A,Iterable<K>>  secondaryBTree(String objectName, 
    		final SecondaryKeyExtractor<A,K,V> keyExtractor, Comparator<A> comparator, JdbmBase<K,V> b) 
    		throws IOException{
    	BTree<A,Iterable<K>> secIndex = null;
        long recid = b.getRecordManager().getNamedObject( objectName );
        if ( recid != 0 ) {
            secIndex = BTree.load(b.getRecordManager(), recid );
        } else {
            secIndex = BTree.createInstance( b.getRecordManager(), comparator );
            b.getRecordManager().setNamedObject( objectName, secIndex.getRecid() );
        }
        
        //second final variable so it can be accesed from listener
        final BTree<A,Iterable<K>> secIndex2 = secIndex;
        
        b.addRecordListener(new RecordListener<K, V>() {

			public void recordInserted(K key, V value) throws IOException {
				A secKey = keyExtractor.extractSecondaryKey(key,value);
				if(secKey == null)
					return;
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) kk = new ArrayList<K>();
				kk.add(key);
				secIndex2.insert(secKey, kk, true);
			}

			public void recordRemoved(K key, V value) throws IOException {
				A secKey = keyExtractor.extractSecondaryKey(key,value);
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) return;
				kk.remove(key);
				if(kk.isEmpty())
					secIndex2.remove(secKey);
				else
					secIndex2.insert(secKey, kk, true);				
			}

			public void recordUpdated(K key, V oldValue, V newValue)
					throws IOException {
				A oldSecKey = keyExtractor.extractSecondaryKey(key, oldValue);
				A newSecKey = keyExtractor.extractSecondaryKey(key, newValue);
				if(oldSecKey==null && newSecKey == null)
					return;
				
				if(oldSecKey==null && newSecKey!=null){
					//insert new record
					recordInserted(key, newValue);
					return;
				}
				if(oldSecKey!=null && newSecKey==null){
					//delete old record
					recordRemoved(key, oldValue);
					return;
				}
				
				if(oldSecKey.equals(newSecKey))
					//both keys are equal, nothing
					return;
				
				//remove old key
				recordRemoved(key, oldValue);
				//insert new key
				recordInserted(key,newValue);				
			}
		});

        return secIndex;
    }

    static public <A,K,V> HTree<A,Iterable<K>>  secondaryHTree(String objectName, 
    		final SecondaryKeyExtractor<A,K,V> keyExtractor, JdbmBase<K,V> b) 
    		throws IOException{
    	HTree<A,Iterable<K>> secIndex = null;
        long recid = b.getRecordManager().getNamedObject( objectName );
        if ( recid != 0 ) {
            secIndex = HTree.load(b.getRecordManager(), recid );
        } else {
            secIndex = HTree.createInstance( b.getRecordManager());
            b.getRecordManager().setNamedObject( objectName, secIndex.getRecid() );
        }
        
        //second final variable so it can be accesed from listener
        final HTree<A,Iterable<K>> secIndex2 = secIndex;
        
        b.addRecordListener(new RecordListener<K, V>() {

			public void recordInserted(K key, V value) throws IOException {
				A secKey = keyExtractor.extractSecondaryKey(key,value);
				if(secKey == null)
					return;
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) kk = new ArrayList<K>();
				kk.add(key);
				secIndex2.put(secKey, kk);
			}

			public void recordRemoved(K key, V value) throws IOException {
				A secKey = keyExtractor.extractSecondaryKey(key,value);
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) return;
				kk.remove(key);
				if(kk.isEmpty())
					secIndex2.remove(secKey);
				else
					secIndex2.put(secKey, kk);				
			}

			public void recordUpdated(K key, V oldValue, V newValue)
					throws IOException {
				A oldSecKey = keyExtractor.extractSecondaryKey(key,oldValue);
				A newSecKey = keyExtractor.extractSecondaryKey(key,newValue);
				if(oldSecKey==null && newSecKey == null)
					return;
				
				if(oldSecKey==null && newSecKey!=null){
					//insert new record
					recordInserted(key, newValue);
					return;
				}
				if(oldSecKey!=null && newSecKey==null){
					//delete old record
					recordRemoved(key, oldValue);
					return;
				}
				
				if(oldSecKey.equals(newSecKey))
					//both keys are equal, nothing
					return;
				
				//remove old key
				recordRemoved(key, oldValue);
				//insert new key
				recordInserted(key,newValue);				
			}
		});

        return secIndex;
    }
    
    
    static public <A,K,V> BTree<A,Iterable<K>>  secondaryBTreeManyToOne(String objectName, 
    		final SecondaryKeyExtractor<Iterable<A>,K,V> keyExtractor, Comparator<A> comparator, JdbmBase<K,V> b) 
    		throws IOException{
    	BTree<A,Iterable<K>> secIndex = null;
        long recid = b.getRecordManager().getNamedObject( objectName );
        if ( recid != 0 ) {
            secIndex = BTree.load(b.getRecordManager(), recid );
        } else {
            secIndex = BTree.createInstance( b.getRecordManager(), comparator );
            b.getRecordManager().setNamedObject( objectName, secIndex.getRecid() );
        }
        
        //second final variable so it can be accesed from listener
        final BTree<A,Iterable<K>> secIndex2 = secIndex;
        
        b.addRecordListener(new RecordListener<K, V>() {

			public void recordInserted(K key, V value) throws IOException {
				for(A secKey : keyExtractor.extractSecondaryKey(key,value)){
				if(secKey == null)
					return;
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) kk = new ArrayList<K>();
				kk.add(key);
				secIndex2.insert(secKey, kk, true);
				}
			}

			public void recordRemoved(K key, V value) throws IOException {
				for(A secKey : keyExtractor.extractSecondaryKey(key,value)){
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) return;
				kk.remove(key);
				if(kk.isEmpty())
					secIndex2.remove(secKey);
				else
					secIndex2.insert(secKey, kk, true);
				}
			}

			public void recordUpdated(K key, V oldValue, V newValue)
					throws IOException {
				Iterable<A> oldSecKey = keyExtractor.extractSecondaryKey(key, oldValue);
				Iterable<A> newSecKey = keyExtractor.extractSecondaryKey(key, newValue);
				if(oldSecKey==null && newSecKey == null)
					return;
				
				if(oldSecKey==null && newSecKey!=null){
					//insert new record
					recordInserted(key, newValue);
					return;
				}
				if(oldSecKey!=null && newSecKey==null){
					//delete old record
					recordRemoved(key, oldValue);
					return;
				}
				
				if(oldSecKey.equals(newSecKey))
					//both keys are equal, nothing
					return;
				
				//remove old key
				recordRemoved(key, oldValue);
				//insert new key
				recordInserted(key,newValue);				
			}
		});

        return secIndex;
    }

    static public <A,K,V> HTree<A,Iterable<K>>  secondaryHTreeManyToOne(String objectName, 
    		final SecondaryKeyExtractor<Iterable<A>,K,V> keyExtractor, JdbmBase<K,V> b) 
    		throws IOException{
    	HTree<A,Iterable<K>> secIndex = null;
        long recid = b.getRecordManager().getNamedObject( objectName );
        if ( recid != 0 ) {
            secIndex = HTree.load(b.getRecordManager(), recid );
        } else {
            secIndex = HTree.createInstance( b.getRecordManager());
            b.getRecordManager().setNamedObject( objectName, secIndex.getRecid() );
        }
        
        //second final variable so it can be accesed from listener
        final HTree<A,Iterable<K>> secIndex2 = secIndex;
        
        b.addRecordListener(new RecordListener<K, V>() {

			public void recordInserted(K key, V value) throws IOException {
				for(A secKey : keyExtractor.extractSecondaryKey(key,value)){
				if(secKey == null)
					return;
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) kk = new ArrayList<K>();
				kk.add(key);
				secIndex2.put(secKey, kk);
				}
			}

			public void recordRemoved(K key, V value) throws IOException {
				for(A secKey : keyExtractor.extractSecondaryKey(key,value)){
				List<K> kk = (List<K>) secIndex2.find(secKey);
				if(kk == null) return;
				kk.remove(key);
				if(kk.isEmpty())
					secIndex2.remove(secKey);
				else
					secIndex2.put(secKey, kk);
				}
			}

			public void recordUpdated(K key, V oldValue, V newValue)
					throws IOException {
				Iterable<A> oldSecKey = keyExtractor.extractSecondaryKey(key,oldValue);
				Iterable<A> newSecKey = keyExtractor.extractSecondaryKey(key,newValue);
				if(oldSecKey==null && newSecKey == null)
					return;
				
				if(oldSecKey==null && newSecKey!=null){
					//insert new record
					recordInserted(key, newValue);
					return;
				}
				if(oldSecKey!=null && newSecKey==null){
					//delete old record
					recordRemoved(key, oldValue);
					return;
				}
				
				if(oldSecKey.equals(newSecKey))
					//both keys are equal, nothing
					return;
				
				//remove old key
				recordRemoved(key, oldValue);
				//insert new key
				recordInserted(key,newValue);				
			}
		});

        return secIndex;
    }


    public static <A,K,V> SecondaryHashMap<A,K,V> secondaryHashMap( 
    		String objectName, SecondaryKeyExtractor<A, K, V> secKeyExtractor, JdbmBase<K,V> b){
    	try{
    		HTree<A,Iterable<K>> secTree = secondaryHTree(objectName, secKeyExtractor, b);
    		HTreeSecondaryMap<A, K, V> ret = new HTreeSecondaryMap<A, K, V>(secTree, b);
    		return ret;
    	}catch (IOException e){
    		throw new IOError(e);
    	}
    }

    public static <A,K,V> SecondaryTreeMap<A,K,V> secondaryTreeMap( 
    		String objectName, SecondaryKeyExtractor<A, K, V> secKeyExtractor, 
    		Comparator<A> comparator, JdbmBase<K,V> b){
    	try{
    		BTree<A,Iterable<K>> secTree = secondaryBTree(objectName, secKeyExtractor, comparator,b);
    		BTreeSecondarySortedMap<A, K, V> ret = new BTreeSecondarySortedMap<A, K, V>(secTree, b);
    		return ret;
    	}catch (IOException e){
    		throw new IOError(e);
    	}
    }
    

    public static <A,K,V> SecondaryHashMap<A,K,V> secondaryHashMapManyToOne( 
    		String objectName, SecondaryKeyExtractor<Iterable<A>, K, V> secKeyExtractor, JdbmBase<K,V> b){
    	try{
    		HTree<A,Iterable<K>> secTree = secondaryHTreeManyToOne(objectName, secKeyExtractor, b);
    		HTreeSecondaryMap<A, K, V> ret = new HTreeSecondaryMap<A, K, V>(secTree, b);
    		return ret;
    	}catch (IOException e){
    		throw new IOError(e);
    	}
    }

    public static <A,K,V> SecondaryTreeMap<A,K,V> secondarySortedMapManyToOne( 
    		String objectName, SecondaryKeyExtractor<Iterable<A>, K, V> secKeyExtractor, 
    		Comparator<A> comparator, JdbmBase<K,V> b){
    	try{
    		BTree<A,Iterable<K>> secTree = secondaryBTreeManyToOne(objectName, secKeyExtractor, comparator,b);
    		BTreeSecondarySortedMap<A, K, V> ret = new BTreeSecondarySortedMap<A, K, V>(secTree, b);
    		return ret;
    	}catch (IOException e){
    		throw new IOError(e);
    	}
    }

    
    @SuppressWarnings("unchecked")
	public static <K,V> InverseHashView<K, V> inverseHashView(JdbmBase<K,V> base, String recordName){
    	SecondaryKeyExtractor<Integer,K,V> hashExtractor = new SecondaryKeyExtractor<Integer, K, V>() {
			long hashEqualsIdentityCounter=0;
			public Integer extractSecondaryKey(K key, V value) {
				int hashCode = value.hashCode();
				//little check to protect from hashCode not being overriden
				int identityHashCode = System.identityHashCode(value);
				if(hashCode == identityHashCode) 
					hashEqualsIdentityCounter++;
				else if(hashEqualsIdentityCounter>0) 
					hashEqualsIdentityCounter--;
				//fail if too many objects had hash equal to identityHashCode
				if(hashEqualsIdentityCounter>50) 
					throw new IllegalArgumentException("Object does not implement hashCode() correctly: "+value.getClass());
				return new Integer(hashCode);
			}
		};
		final SecondaryTreeMap<Integer, K, V> inverseMap = secondaryTreeMap(recordName,hashExtractor,ComparableComparator.INSTANCE,base); 
		
		return new InverseHashView<K, V>() {
			public K findKeyForValue(V val) {
				Iterable<K> keys = inverseMap.get(new Integer(val.hashCode()));
				if(keys == null) return null;
				for(K k:keys){
					if(val.equals(inverseMap.getPrimaryValue(k)))
						return k;
				}
				return null;
			}

			public Iterable<K> findKeysForValue(V val) {
				Iterable<K> keys = inverseMap.get(new Integer(val.hashCode()));
				List<K> ret = new ArrayList<K>();
				if(keys == null) return null;
				for(K k:keys){
					if(val.equals(inverseMap.getPrimaryValue(k)))
						ret.add(k);
				}
				return ret;
			}
		};
    }
}
