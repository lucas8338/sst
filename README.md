# sst - Simple Serializable Table

a java table which is simple and implements the serializable java interface.

## Classes

* `Table`: contains the main logic of the table.
  * `'x'Column`: where 'x' can be "String, Integer, Double, Float, Boolean etc." are specific column implementation for a type. they are used during the interation with a table.
* `TableTools`: an abstract class that contains static methods which are utilities to work with a table, for example, "concat, subsets (row or by column), sorting etc.".
* `Importing`: an abstastract class that contains static methods to work with importing data.
* `Visualization`: an abstract class that contains static methods to work with visualization (printing, ploting, rendering etc.).

## Implementation notes

### why do the table dont have a 'dateTime' or 'date' column?

ans: a 'dateTime' column and a 'date' column type were implemented but was there a lot of problemsin using them. one is because LocalDateTime.toString() method is not stable. some times when thelast element of the time string only contains zero it will return empty example: "2022-01-01T12:30:00" willreturn "2022-01-01T12:30" the last '0' characters were omited. to work with 'LocalDateTime' and 'LocalDate' you need to use the '.format' method, and id would break the 'cleaness' (zen aura) of to have one way to get a string format of a data. would be needed workarounds only to make these dataTypes to work properly and this maybe even dont would fix the problem because there a lot of different times, for example, timedZone etc. which would need a manual fix anyway. i thought maybe would be good to have these 'dated' types for performance reason, but in my benchmark test i have found that this when filtering a 2_000_000 x 5 table to retrieve filter by rows which happens after a date:

* LocalDateTime columnType = ~2.5s.
* String columnType (do "LocalDateTime.parse('string')" in the 'predicate' filter class) = ~4.2s.

there a difference of only 60% of speed. and a string is a universal format. so i need to admit that 'LocalDateTime.parse' method is very fast. an this explains why do the support for 'dated' types were dropped.

### why do the table dont have a row selection by index, column selection by index or filtering method these functionalities are located in the 'TableTools' class?

ans: because i want the 'Table' class to be minimum to avoid bugs. there a lot of confusion when asking for someone "what is a table?" some people will say that a table is something which is similar to microsoft excel 😄 . seriously a table is a square or rectangle format of storage. each row have the same number of columns. and each column have the same number of row. and the idea of 'row' or 'column' is a fantasy created someone or youself because in a graphic gui of microsoft excell, libreoffice etc. that thing which we are looking for are similar to a row (line)... . the word 'same' can be seen as 'stable' this dont change. changing things is a problem for computer or compiled softwares. because you need a specific recipe to do something and if the ingredients keep changing every time will be very hard to handle that.

so the final answer for a table is just: "a table is a colection of colections (array, list etc.) which have the same size (length, index etc.) and the date in them correlates". one example is if you have two arrays, one contains the name, the another one contains the address of that person. do you know a person need to have an address (or it can be a homeless 😄) so if you have the name array with size=10 and the address array with size = 9, so this mean the last person is a homeless? and if i add a 11ª entry in the name array what will hapen with the array with the address? now you can understand why do to use a table instead an array.

the 'addRow' is just a way to tell to add an index in all arrays in the main collection. and 'getColumn()' is obviously. but i have thought why do not to implement "getColumns(String ... columnNames)" to get all columns once. because it add more complexity to the 'Table' class and you can do this same thing storing the result of "getColumns()" and filtering.

a table is something which no-one knows what it need. do table needs 'concat', 'join', 'join_in', 'join_out', 'merge', 'join_left', 'join_right' etc. WHAT DO A TABLE NEED TO HAVE? this is the another reason to place functionality which are commonly found together with table functionality in a separated package, one example is the 'filtering' do a filtering of the data is something which is needed for most of the people, but not for all them. so do a table needs a filtering method? for this reason i have added the 'filter' functionality in the 'TableTools' package, which contains things to work with a table which are related with the table itself (not visualization for example).
