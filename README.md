# JDBC
So many questions, so many partial or incorrect answers.

<a name="multi"></a>
##  [How to execute multiple statements](https://www.google.com/search?q=jdbc+multiple+statements+site%3Astackoverflow.com)

It depends on what you want to do and on the database you use.

If you are looking for performances, you will probably want to disable the auto-commit on your connection and only commit when you are done.
It is supported by all database vendors.

 
```
connection.setAutoCommit(false);
// thousands of inserts/updates here
connection.commit();
``` 

---
<a name="batch"></a>
### I want to insert many rows into one table.
Use ```addBatch()``` and ```executeBatch()```, it is fast and implemented by every database.
It is not only fast because of the reduced round-trips but also because the JDBC driver and the database know that they have to repeat the same 
work many times which allows optimizations. 
```
PreparedStatement preparedStatement = connection.prepareStatement("insert into multi_test (id, txt) values (?, ?)")
for each row {
    preparedStatement.setXxx(1, ...);
    preparedStatement.setXxx(2, ...);
    ...
    preparedStatement.addBatch();
}
preparedStatement.executeBatch()
```

<a name="multirow"></a>
Another approach that would work if you have a limited number of rows to insert would be to use 
the [SQL92 multi-row insert syntax](https://en.wikipedia.org/wiki/Insert_(SQL)#Multirow_inserts).
This might be your best option if you have a mix of statements, for example if you want to insert an invoice header and the invoice lines.

Please keep in mind:
1. There is no performance advantage over the batch method.
1. The number of parameters may be limited (2100 for SQL Server for example).
1. The syntax is not supported by all databases (Oracle).



```
PreparedStatement preparedStatement = connection.prepareStatement("insert into multi_test (id, txt) values (?, ?),(?, ?),(?, ?)...")
preparedStatement.setInt(1, 1);
preparedStatement.setString(2, 'one');
preparedStatement.setInt(3, 2);
preparedStatement.setString(4, 'two');
preparedStatement.setInt(5, 3);
preparedStatement.setString(6, 'three');
preparedStatement.execute()
```


### I want to execute a couple of inserts/updates together, many times
Network round-trips may be costly so grouping a couple of inserts/updates in one execution is a good idea.
Some will suggest using stored procedures but let's not start that discussion.

* Derby, HSQL or SqlLite

You are out of luck as those databases do not support multi-statements but it probably doesn't matter because,
as you may have noticed, all 3 are typically embedded or usually installed on the same computer as the application which means that you don't have
to worry about network round-trips.

* MySQL, SQL Server, Postgres

You can execute multiple statements separated by a semicolon, use ```getMoreResults()``` to get the results of each statement.
```
PreparedStatement preparedStatement = connection.prepareStatement("insert ...;update ...");
preparedStatement.execute();        // executes all the statements
preparedStatement.getUpdateCount(); // returns the number of rows affected by the first statement
preparedStatement.getMoreResults(); // Moves to the next result
preparedStatement.getUpdateCount(); // returns the number of rows affected by the second statement
...
```

If you use Postgres, do not include a semicolon at the end of the last statement. It would count as an empty, unnecessary statement.

If you use MySql, you must specify ```allowMultiQueries``` in the connection string: ```jdbc:mysql://...?&allowMultiQueries=true```.


* Oracle

Does not support the execution of multiple statements but it supports the execution of an anonymous block (BEGIN/END) which itself can 
contain your statements.You will not get updateCounts since you did not execute an insert or update but you executed a block.

```
connection.prepareStatement("\n" +
    "BEGIN\n" +
    "  insert into multi_test (id, txt) values (1, 'one');\n" +
    "  insert into multi_test (id, txt) values (2, 'two');\n" +
    "  insert into multi_test (id, txt) values (3, 'tree');\n" +
    "  update multi_test set txt = 'three' where id = 3;\n" +
    "END;")
```

### I want to execute a couple of inserts/updates/selects.
* Derby, HSQL or SqlLite: 
Out of luck as for the previous one

* Oracle: As mentioned before, Oracle allows executing one anonymous block but it cannot contain a SELECT statement. *It may be possible using cursors but I haven't found how yet* 

* MySQL, Postgres and SQL Server
 ```
PreparedStatement preparedStatement = connection.prepareStatement("insert ...;update ...;select ...;select");

preparedStatement.execute();        // One round-trip
preparedStatement.getUpdateCount(); // returns the number of rows affected by the first statement

preparedStatement.getMoreResults(); // Moves to the next result
preparedStatement.getUpdateCount(); // returns the number of rows affected by the second statement

preparedStatement.getMoreResults(); // Moves to the next result (this one will return true BTW)
ResultSet resultSet1 = preparedStatement.getResultSet();
... 
preparedStatement.getMoreResults(); // Moves to the next result (returns true too)
ResultSet resultSet2 = preparedStatement.getResultSet();
... 
```