# Generated Project Skeleton

A simple operator that deploys datasamudaya standalone operator. 

To get custom resource definitions.

To get jar with dependencies.
------------------------------
mvn clean package -Dmaven.test.skip=true


To build datasamudaya operator.
--------------------------------
mvn -f pomjar.xml compile jib:dockerBuild@builddatasamudayaoperator

docker push arunsrajan/datasamudaya-operator

To create Kubernetes Cluster with 4 cpus and 3GB RAM of 3 Nodes
---------------------------------------------------------------
minikube start -p datasamudayadaemons --cpus 4 --memory 3g --nodes=3

To create hadoop daemonset.
--------------------------
kubectl label nodes datasamudayadaemons-m02 namenode=true
kubectl label nodes datasamudayadaemons datasamudayadaemons-m02 datasamudayadaemons-m03 datanode=true

To create Services for application
----------------------------------
minikube service datasamudaya-single-ports-standalone -p datasamudayadaemons
minikube service datasamudaya-single-standalone -p datasamudayadaemons