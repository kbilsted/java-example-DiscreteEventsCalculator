# javaDiscreteEventsCalculator

Java example for calculating events on a time line ordered by a value date.

* Supports adjusting events and re-calculating.
* Supports archiving historic calculation data to optimize fetch speed

The best way to read the code is by starting at the test in [src/test/java/org/bffs/DocumentStoreTest.java](src/test/java/org/bffs/DocumentStoreTest.java)

The tests show
1. we can create a time line with an event
2. we can adjust event data back in time
3. we can archive historic data to a different document (optimize fetch speed)