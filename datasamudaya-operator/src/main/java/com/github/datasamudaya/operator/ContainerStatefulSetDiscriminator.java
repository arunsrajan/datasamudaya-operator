package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.INDEX;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.WORKER;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class ContainerStatefulSetDiscriminator implements ResourceDiscriminator<StatefulSet, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<StatefulSet> distinguish(Class<StatefulSet> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<StatefulSet, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<StatefulSet, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(StatefulSet.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+WORKER+HYPHEN+INDEX,
		    		resource.getMetadata().getNamespace()));
	}

}
