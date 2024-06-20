package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.Service;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class NameNodeServiceDiscriminator implements ResourceDiscriminator<Service, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<Service> distinguish(Class<Service> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<Service, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<Service, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(Service.class);		
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+NAMENODE,
		    		resource.getMetadata().getNamespace()));
	}

}
