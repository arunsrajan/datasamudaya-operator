# Generated Project Skeleton

A simple operator that deploys datasamudaya standalone operator. 

To get custom resource definitions.

To get jar with dependencies.
------------------------------
mvn clean package


To build datasamudaya operator.
--------------------------------
mvn -f pomjar.xml compile jib:dockerBuild@builddatasamudayaoperator

docker push arunsrajan/datasamudaya-operator

To create hadoop daemonset.
--------------------------
kubectl label nodes datasamudayadaemons-m02 namenode=true
kubectl label nodes datasamudayadaemons datasamudayadaemons-m02 datasamudayadaemons-m03 datanode=true


To create Services for application
----------------------------------
minikube service datasamudaya-single-ports-standalone -p datasamudayadaemons
minikube service datasamudaya-single-standalone -p datasamudayadaemons