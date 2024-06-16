# Generated Project Skeleton

A simple operator that deploys datasamudaya standalone operator. 

To get custom resource definitions.

To get jar with dependencies.
------------------------------
mvn clean package


To build datasamudaya operator.
--------------------------------
mvn -f pomjar.xml compile jib:dockerBuild@builddatasamudayaoperator

To create hadoop daemonset.
--------------------------
kubectl label nodes datasamudayadaemons-m02 namenode=true
kubectl label nodes datasamudayadaemons datasamudayadaemons-m02 datasamudayadaemons-m03 datanode=true