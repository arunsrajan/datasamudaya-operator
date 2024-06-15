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

	 public static final String ZKIMAGE = "zkimage";
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
	 public static final String CONTAINERYAMLPATH = "/dskubernetesdaemonset/dsstandalonestatefulset.yaml";
	 public static final String CONTAINERIMAGE = "arunsrajan/datasamudayacontainer:latest";
	 
	 public static final String ZKMETADATANAME = "datasamudayazk";
	 public static final String ZKSERVICEMETADATANAME = "zoo";
	 public static final String ZOOKEEPERSERVICEYAMLPATH = "/dskubernetesdaemonset/dszookeeperservice.yaml";
	 public static final String STANDALONESERVICEYAMLPATH = "/dskubernetesdaemonset/dsstandaloneservice.yaml";
	
	 public static final String STATEFULSET_MAP_EVENT_SOURCE = "StatefulSetMapEventSource";
	 public static final String SERVICE_MAP_EVENT_SOURCE = "ServiceMapEventSource";
	 
}
