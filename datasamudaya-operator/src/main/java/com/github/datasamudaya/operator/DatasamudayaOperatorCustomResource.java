package com.github.datasamudaya.operator;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Group("com.github.datasamudaya.operator")
@Version("v1")
public class DatasamudayaOperatorCustomResource extends CustomResource<DatasamudayaOperatorSpec,DatasamudayaOperatorStatus> implements Namespaced {

	private static final long serialVersionUID = -5336254074827204904L;
	
	@Override
	public String toString() {
		return "DatasamudayaOperatorCustomResource [spec=" + spec + ", status=" + status + "]";
	}
	
}
