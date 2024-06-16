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

	@Override
	public String toString() {
		return "DatasamudayaOperatorSpec [namespace=" + namespace + ", numberofworkers=" + numberofworkers
				+ ", zkimage=" + zkimage + ", zkrequestcpu=" + zkrequestcpu + ", zkrequestmemory=" + zkrequestmemory
				+ ", zklimitcpu=" + zklimitcpu + ", zklimitmemory=" + zklimitmemory + ", podcidrnodemappingenabled="
				+ podcidrnodemappingenabled + ", zkhostport=" + zkhostport + ", saimage=" + saimage + ", sarequestcpu="
				+ sarequestcpu + ", sarequestmemory=" + sarequestmemory + ", salimitcpu=" + salimitcpu
				+ ", salimitmemory=" + salimitmemory + ", containerimage=" + containerimage + ", containerrequestcpu="
				+ containerrequestcpu + ", containerrequestmemory=" + containerrequestmemory + ", containerlimitcpu="
				+ containerlimitcpu + ", containerlimitmemory=" + containerlimitmemory + "]";
	}
	
}
