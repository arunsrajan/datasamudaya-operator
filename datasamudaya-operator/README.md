# Generated Project Skeleton

A simple operator that deploys datasamudaya standalone operator. 

To get custom resource definitions.

mvn clean compile

To get jar with dependencies.
mvn clean package


To build datasamudaya operator.
mvn -f pomjar.xml compile jib:dockerBuild@builddatasamudayaoperator