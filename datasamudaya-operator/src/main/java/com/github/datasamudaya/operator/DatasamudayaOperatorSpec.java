package com.github.datasamudaya.operator;

public class DatasamudayaOperatorSpec {

    private String namespace;
    
    private int numberofworkers;
    
    //Zookeeper properties
    private String zkimage;
    private String zkrequestcpu;
    private String zkrequestmemory;
    private String zklimitcpu;
    private String zklimitmemory;
    
    //common properties
    private String podcidrnodemappingenabled;
    private String zkhostport;

    //Standalone properties    
    private String saimage;
    private String sarequestcpu;
    private String sarequestmemory;
    private String salimitcpu;
    private String salimitmemory;
    
    
	//Container properties
	private String containerimage;
	private String containerrequestcpu;
    private String containerrequestmemory;
    private String containerlimitcpu;
    private String containerlimitmemory;
    
    //Namenode properties
  	private String namenodeimage;
  	private String namenoderequestcpu;
    private String namenoderequestmemory;
    private String namenodelimitcpu;
    private String namenodelimitmemory;
    private String namenodeport;
    private String namenodeportwebui;
    
    //Datanode properties
  	private String datanodeimage;
  	private String datanoderequestcpu;
    private String datanoderequestmemory;
    private String datanodelimitcpu;
    private String datanodelimitmemory;
    private String datanodeport;
    private String datanodeportwebui;
    private String datanodeportipc;
    
	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public int getNumberofworkers() {
		return numberofworkers;
	}

	public void setNumberofworkers(int numberofworkers) {
		this.numberofworkers = numberofworkers;
	}

	public String getZkimage() {
		return zkimage;
	}

	public void setZkimage(String zkimage) {
		this.zkimage = zkimage;
	}

	public String getZkrequestcpu() {
		return zkrequestcpu;
	}

	public void setZkrequestcpu(String zkrequestcpu) {
		this.zkrequestcpu = zkrequestcpu;
	}

	public String getZkrequestmemory() {
		return zkrequestmemory;
	}

	public void setZkrequestmemory(String zkrequestmemory) {
		this.zkrequestmemory = zkrequestmemory;
	}

	public String getZklimitcpu() {
		return zklimitcpu;
	}

	public void setZklimitcpu(String zklimitcpu) {
		this.zklimitcpu = zklimitcpu;
	}

	public String getZklimitmemory() {
		return zklimitmemory;
	}

	public void setZklimitmemory(String zklimitmemory) {
		this.zklimitmemory = zklimitmemory;
	}

	public String getPodcidrnodemappingenabled() {
		return podcidrnodemappingenabled;
	}

	public void setPodcidrnodemappingenabled(String podcidrnodemappingenabled) {
		this.podcidrnodemappingenabled = podcidrnodemappingenabled;
	}

	public String getZkhostport() {
		return zkhostport;
	}

	public void setZkhostport(String zkhostport) {
		this.zkhostport = zkhostport;
	}

	public String getSaimage() {
		return saimage;
	}

	public void setSaimage(String saimage) {
		this.saimage = saimage;
	}

	public String getSarequestcpu() {
		return sarequestcpu;
	}

	public void setSarequestcpu(String sarequestcpu) {
		this.sarequestcpu = sarequestcpu;
	}

	public String getSarequestmemory() {
		return sarequestmemory;
	}

	public void setSarequestmemory(String sarequestmemory) {
		this.sarequestmemory = sarequestmemory;
	}

	public String getSalimitcpu() {
		return salimitcpu;
	}

	public void setSalimitcpu(String salimitcpu) {
		this.salimitcpu = salimitcpu;
	}

	public String getSalimitmemory() {
		return salimitmemory;
	}

	public void setSalimitmemory(String salimitmemory) {
		this.salimitmemory = salimitmemory;
	}

	public String getContainerimage() {
		return containerimage;
	}

	public void setContainerimage(String containerimage) {
		this.containerimage = containerimage;
	}

	public String getContainerrequestcpu() {
		return containerrequestcpu;
	}

	public void setContainerrequestcpu(String containerrequestcpu) {
		this.containerrequestcpu = containerrequestcpu;
	}

	public String getContainerrequestmemory() {
		return containerrequestmemory;
	}

	public void setContainerrequestmemory(String containerrequestmemory) {
		this.containerrequestmemory = containerrequestmemory;
	}

	public String getContainerlimitcpu() {
		return containerlimitcpu;
	}

	public void setContainerlimitcpu(String containerlimitcpu) {
		this.containerlimitcpu = containerlimitcpu;
	}

	public String getContainerlimitmemory() {
		return containerlimitmemory;
	}

	public void setContainerlimitmemory(String containerlimitmemory) {
		this.containerlimitmemory = containerlimitmemory;
	}
	
	public String getNamenodeimage() {
		return namenodeimage;
	}

	public void setNamenodeimage(String namenodeimage) {
		this.namenodeimage = namenodeimage;
	}

	public String getNamenoderequestcpu() {
		return namenoderequestcpu;
	}

	public void setNamenoderequestcpu(String namenoderequestcpu) {
		this.namenoderequestcpu = namenoderequestcpu;
	}

	public String getNamenoderequestmemory() {
		return namenoderequestmemory;
	}

	public void setNamenoderequestmemory(String namenoderequestmemory) {
		this.namenoderequestmemory = namenoderequestmemory;
	}

	public String getNamenodelimitcpu() {
		return namenodelimitcpu;
	}

	public void setNamenodelimitcpu(String namenodelimitcpu) {
		this.namenodelimitcpu = namenodelimitcpu;
	}

	public String getNamenodelimitmemory() {
		return namenodelimitmemory;
	}

	public void setNamenodelimitmemory(String namenodelimitmemory) {
		this.namenodelimitmemory = namenodelimitmemory;
	}

	public String getDatanodeimage() {
		return datanodeimage;
	}

	public void setDatanodeimage(String datanodeimage) {
		this.datanodeimage = datanodeimage;
	}

	public String getDatanoderequestcpu() {
		return datanoderequestcpu;
	}

	public void setDatanoderequestcpu(String datanoderequestcpu) {
		this.datanoderequestcpu = datanoderequestcpu;
	}

	public String getDatanoderequestmemory() {
		return datanoderequestmemory;
	}

	public void setDatanoderequestmemory(String datanoderequestmemory) {
		this.datanoderequestmemory = datanoderequestmemory;
	}

	public String getDatanodelimitcpu() {
		return datanodelimitcpu;
	}

	public void setDatanodelimitcpu(String datanodelimitcpu) {
		this.datanodelimitcpu = datanodelimitcpu;
	}

	public String getDatanodelimitmemory() {
		return datanodelimitmemory;
	}

	public void setDatanodelimitmemory(String datanodelimitmemory) {
		this.datanodelimitmemory = datanodelimitmemory;
	}

	public String getNamenodeport() {
		return namenodeport;
	}

	public void setNamenodeport(String namenodeport) {
		this.namenodeport = namenodeport;
	}

	public String getNamenodeportwebui() {
		return namenodeportwebui;
	}

	public void setNamenodeportwebui(String namenodeportwebui) {
		this.namenodeportwebui = namenodeportwebui;
	}

	public String getDatanodeport() {
		return datanodeport;
	}

	public void setDatanodeport(String datanodeport) {
		this.datanodeport = datanodeport;
	}

	public String getDatanodeportwebui() {
		return datanodeportwebui;
	}

	public void setDatanodeportwebui(String datanodeportwebui) {
		this.datanodeportwebui = datanodeportwebui;
	}

	public String getDatanodeportipc() {
		return datanodeportipc;
	}

	public void setDatanodeportipc(String datanodeportipc) {
		this.datanodeportipc = datanodeportipc;
	}

	@Override
	public String toString() {
		return "DatasamudayaOperatorSpec [namespace=" + namespace + ", numberofworkers=" + numberofworkers
				+ ", zkimage=" + zkimage + ", zkrequestcpu=" + zkrequestcpu + ", zkrequestmemory=" + zkrequestmemory
				+ ", zklimitcpu=" + zklimitcpu + ", zklimitmemory=" + zklimitmemory + ", podcidrnodemappingenabled="
				+ podcidrnodemappingenabled + ", zkhostport=" + zkhostport + ", saimage=" + saimage + ", sarequestcpu="
				+ sarequestcpu + ", sarequestmemory=" + sarequestmemory + ", salimitcpu=" + salimitcpu
				+ ", salimitmemory=" + salimitmemory + ", containerimage=" + containerimage + ", containerrequestcpu="
				+ containerrequestcpu + ", containerrequestmemory=" + containerrequestmemory + ", containerlimitcpu="
				+ containerlimitcpu + ", containerlimitmemory=" + containerlimitmemory + ", namenodeimage="
				+ namenodeimage + ", namenoderequestcpu=" + namenoderequestcpu + ", namenoderequestmemory="
				+ namenoderequestmemory + ", namenodelimitcpu=" + namenodelimitcpu + ", namenodelimitmemory="
				+ namenodelimitmemory + ", namenodeport=" + namenodeport + ", namenodeportwebui=" + namenodeportwebui
				+ ", datanodeimage=" + datanodeimage + ", datanoderequestcpu=" + datanoderequestcpu
				+ ", datanoderequestmemory=" + datanoderequestmemory + ", datanodelimitcpu=" + datanodelimitcpu
				+ ", datanodelimitmemory=" + datanodelimitmemory + ", datanodeport=" + datanodeport
				+ ", datanodeportwebui=" + datanodeportwebui + ", datanodeportipc=" + datanodeportipc + "]";
	}
	
}
