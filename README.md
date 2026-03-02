# Discrete Events Calculator

Java example for calculating events on a timeline ordered by a value date.

The best way to read the code is by starting at the test in [src/test/java/org/bffs/DocumentStoreTest.java](src/test/java/org/bffs/DocumentStoreTest.java)

The tests show
1. we can create a timeline with an event on it that can be calculated
2. we can adjust event input data with a date in the past and observe a re-calculation 
3. we can optimize fetch speed and ram usage, by archiving historic data to a different document

