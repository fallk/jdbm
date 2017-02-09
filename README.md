*This project is an unofficial GitHub mirror of the [JDBM2](https://code.google.com/archive/p/jdbm2/) repo on Google Code.
Pull requests will probably not be accepted.*

JDBM2 provides [HashMap](http://download.oracle.com/docs/cd/E17409_01/javase/6/docs/api/java/util/HashMap.html) and
[TreeMap](http://download.oracle.com/docs/cd/E17409_01/javase/6/docs/api/java/util/TreeMap.html) which are backed by
disk storage. It is very easy and fast way to persist your data. JDBM2 also have minimal hardware requirements and is
highly embeddable (jar has only 145 KB).

JDBM2 was developed to support astronomical calculation with data which does not fit into memory. It also provides storage for astronomical planetarium Asterope.

JDBM started 10 years ago as a GDBM port to Java. The [original version](http://jdbm.sourceforge.net/) was developed by
Cees De Groot, Alex Boisvert and others. Last version 1.0 was released in 2005 and is used since then with little bugs found.
This project is a fork of 1.0, which adds more features and performance improvements. JDBM 1.0 has since been continued as
[JDBM3](https://github.com/jankotek/JDBM3) and later as [MapDB](https://github.com/jankotek/mapdb).

Pros:
* Fast, very small footprint
* Transactional
* Standalone jar has only 145 KB.
* Simple usage
* Scales well up to 2000000000 (two billion) records
* Uses Java serialization, no ORM mapping

Cons:
* Synchronized on one 'big lock', does not scale well with concurrent access
* Only one single concurrent transaction
* No triggers, foreign keys and other integrity checks... (compared to SQL database)
* No J2EE or Spring-like support

This project can be best compared to Oracle BerkleyDB Java Edition, but is distributed under the more liberal Apache 2 license and is free as in beer.

Usage example:

```java
    /* create (or open existing) database */
    String fileName = "helloWorld";
    RecordManager recMan = RecordManagerFactory.createRecordManager(fileName);

    /* Creates TreeMap which stores data in database.  
       Constructor method takes recordName (something like SQL table name)*/
    String recordName = "firstTreeMap";
    PrimaryTreeMap<Integer,String> treeMap = recMan.treeMap(recordName); 

    /* add some stuff to map */
    treeMap.put(1, "One");
    treeMap.put(2, "Two");
    treeMap.put(3, "Three");

    System.out.println(treeMap.keySet());
    // > [1, 2, 3]

    /* Map changes are not persisted yet, commit them (save to disk) */
    recMan.commit();

    System.out.println(treeMap.keySet());
    // > [1, 2, 3]

    /* Delete one record. Changes are not commited yet, but are visible. */
    treeMap.remove(2);

    System.out.println(treeMap.keySet());
    // > [1, 3]

    /* Did not like change. Rollback to last commit (undo record remove). */
    recMan.rollback();
```
