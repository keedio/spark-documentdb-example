# spark-documentdb-example

Example project showing how to use Microsoft DocumentDB Input/Output formats with Apache Spark.

In this example we write to a remote DocumentDB instance a few thousands lines using hadoop output format classes, read it back using input formats and check written data is correct.

Prerequisites:

1. Maven 3.3.x
2. Scala 2.10.4 successfully installed and available in PATH.
3. You have successfully deployed a DocumentBD instance in Microsoft Azure.

Build the project as:
    
    mvn clean package

Run the example project as:

    java -cp target/com.keedio.azure.example-1.0-SNAPSHOT.jar -Dazure.documentdb.host="https://<personal_documentdb_domain>.documents.azure.com:443/" -Dazure.documentdb.key="<documentdb_primary_key>" -Dazure.documentdb.dbname=<example_documentdb_databasename> -Dazure.documentdb.collection.output="<test_output_collection>" com.keedio.azure.documentdb.example.SaveToDocumentDbTest 
    
Property explanation:

1. `azure.documentdb.host`: the DocumentDB instance URI as provided in the azure portal.
2. `azure.documentdb.key`: the DocumentDB instance primary key (as provided in the azure portal).
3. `azure.documentdb.dbname`: the name of the database as created in the DocumentDB instance.
4. `azure.documentdb.collection.output`: the output collection to write example data to.
    
    