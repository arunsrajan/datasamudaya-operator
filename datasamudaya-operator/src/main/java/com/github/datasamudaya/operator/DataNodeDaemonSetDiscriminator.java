package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODE;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class DataNodeDaemonSetDiscriminator implements ResourceDiscriminator<DaemonSet, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<DaemonSet> distinguish(Class<DaemonSet> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<DaemonSet, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<DaemonSet, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(DaemonSet.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+DATANODE,
		    		resource.getMetadata().getNamespace()));
	}

}
