package com.github.datasamudaya.operator;

import io.javaoperatorsdk.operator.api.ObservedGenerationAwareStatus;

public class DatasamudayaOperatorStatus extends ObservedGenerationAwareStatus {

	private int numberofmodifications;
	private DatasamudayaOperatorSpec spec;
	
	public int getNumberofmodifications() {
		return numberofmodifications;
	}
	public void setNumberofmodifications(int numberofmodifications) {
		this.numberofmodifications = numberofmodifications;
	}
	public DatasamudayaOperatorSpec getSpec() {
		return spec;
	}
	public void setSpec(DatasamudayaOperatorSpec spec) {
		this.spec = spec;
	}
}
