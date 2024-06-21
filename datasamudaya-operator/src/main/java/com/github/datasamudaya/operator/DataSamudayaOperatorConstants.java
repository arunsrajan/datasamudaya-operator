package com.github.datasamudaya.operator;

public class DataSamudayaOperatorConstants {
	 public static final String NAMESPACE = "namespace";
	 public static final String NAMESPACE_DEFAULT = "default";
	 
	 public static final String CPU = "cpu";
	 public static final String MEMORY = "memory";
	 
	 public static final String SAREQUESTMEMORY_DEFAULT = "64Mi";
	 public static final String SAREQUESTCPU_DEFAULT = "1";
	 public static final String SALIMITMEMORY_DEFAULT = "1024Mi";
	 public static final String SALIMITCPU_DEFAULT = "1";
	 public static final String ZKHOSTPORT_DEFAULT = "zoo:2181";
	 
	 
	 public static final String CONTAINERREQUESTMEMORY_DEFAULT = "64Mi";
	 public static final String CONTAINERREQUESTCPU_DEFAULT = "1";
	 public static final String CONTAINERLIMITMEMORY_DEFAULT = "1024Mi";
	 public static final String CONTAINERLIMITCPU_DEFAULT = "1";

	 public static final String ZKIMAGE = "arunsrajan/datasamudayazk:latest";
	 public static final String ZKREQUESTCPU = "zkrequestcpu";
	 public static final String ZKREQUESTCPU_DEFAULT = "1";
	 public static final String ZKREQUESTMEMORY = "zkrequestmemory";
	 public static final String ZKREQUESTMEMORY_DEFAULT = "64Mi";
	 public static final String ZKLIMITCPU = "zklimitcpu";
	 public static final String ZKLIMITCPU_DEFAULT = "1";
	 public static final String ZKLIMITMEMORY = "zklimitmemory";
	 public static final String ZKLIMITMEMORY_DEFAULT = "1024Mi";
	 
	 public static final String PODCIDRNODEMAPPINGENABLED = "podcidrnodemappingenabled";
	 public static final String PODCIDRNODEMAPPINGENABLED_DEFAULT = "false";
	 
	 public static final String SAMETADATANAME = "datasamudayastandalone";
	 public static final String SASERVICEMETADATANAME = "datasamudayastandalone";
	 public static final String STANDALONEYAMLPATH = "/dskubernetesdaemonset/dsstandalonestatefulset.yaml";
	 public static final String STANDALONEIMAGE = "arunsrajan/datasamudayastandalone:latest";
	 public static final String WORKERYAMLPATH = "/dskubernetesdaemonset/dscontainerstatefulset.yaml";
	 public static final String ZOOKEEPERYAMLPATH = "/dskubernetesdaemonset/dszookeeperstatefulset.yaml";
	 
	 
	 public static final String CONTAINERMETADATANAME = "datasamudayacontainer";
	 public static final String CONTAINERYAMLPATH = "/dskubernetesdaemonset/dscontainerstatefulset.yaml";
	 public static final String CONTAINERIMAGE = "arunsrajan/datasamudayacontainer:latest";
	 
	 public static final String ZKMETADATANAME = "datasamudayazk";
	 public static final String ZKSERVICEMETADATANAME = "zoo";
	 public static final String ZKPORT = "2181";
	 public static final String ZOOKEEPERSERVICEYAMLPATH = "/dskubernetesdaemonset/dszookeeperservice.yaml";
	 public static final String STANDALONESERVICEYAMLPATH = "/dskubernetesdaemonset/dsstandaloneservice.yaml";
	
	 public static final String STATEFULSET_MAP_EVENT_SOURCE = "StatefulSetMapEventSource";
	 public static final String SERVICE_MAP_EVENT_SOURCE = "ServiceMapEventSource";
	 public static final String DAEMONSET_MAP_EVENT_SOURCE = "DaemonSetMapEventSource";
	 
	 public static final String EMPTYSTRING = "";
	 public static final String HYPHEN = "-";
	 public static final String COLON = ":";
	 public static final String WORKER = "worker";
	 public static final String STANDALONE = "standalone";
	 public static final String ZOOKEEPER = "zookeeper";
	 public static final String SERVICE = "svc";
	 public static final String APPLICATION = "app";
	 public static final String INDEX = "0";
	 
	 public static final String HADOOPNAMENODEDAEMONSETYAMLPATH = "/dskubernetesdaemonset/hadoop-namenode-daemonset.yaml";
	 public static final String HADOOPNAMENODESERVICEYAMLPATH = "/dskubernetesdaemonset/hadoop-namenode-service.yaml";
	 public static final String HADOOPDATANODEDAEMONSETYAMLPATH = "/dskubernetesdaemonset/hadoop-datanode-daemonset.yaml";
	 public static final String NAMENODE = "hadoop-namenode";
	 public static final String NAMENODEIMAGE = "arunsrajan/hadoop-namenode:2.0.0-hadoop3.1.3-java8";
	 public static final String NAMENODEREQUESTMEMORY_DEFAULT = "64Mi";
	 public static final String NAMENODEURL = "hdfs://%s:%s";
	 public static final String NAMENODEURLWEBUI = "%s:%s";
	 public static final String NAMENODEPORT_DEFAULT = "9000";
	 public static final String NAMENODEPORTWEBUI_DEFAULT = "9870";
	 public static final String NAMENODEREQUESTCPU_DEFAULT = "250m";
	 public static final String NAMENODELIMITMEMORY_DEFAULT = "1024Mi";
	 public static final String NAMENODELIMITCPU_DEFAULT = "500m";
	 public static final String DATANODE = "hadoop-datanode";
	 public static final String DATANODEIMAGE = "arunsrajan/hadoop-datanode:2.0.0-hadoop3.1.3-java8";
	 public static final String DATANODEREQUESTMEMORY_DEFAULT = "64Mi";
	 public static final String DATANODEREQUESTCPU_DEFAULT = "250m";
	 public static final String DATANODELIMITMEMORY_DEFAULT = "1024Mi";
	 public static final String DATANODELIMITCPU_DEFAULT = "500m";
	 public static final String DATANODEURL = "0.0.0.0:%s";
	 public static final String DATANODEPORT_DEFAULT = "9866";
	 public static final String DATANODEPORTWEBUI_DEFAULT = "9864";
	 public static final String DATANODEPORTIPC_DEFAULT = "9867";
}
