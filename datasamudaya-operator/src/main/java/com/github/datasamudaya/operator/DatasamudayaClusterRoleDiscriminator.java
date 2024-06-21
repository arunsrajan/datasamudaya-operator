package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLENAME;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.rbac.ClusterRole;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class DatasamudayaClusterRoleDiscriminator implements ResourceDiscriminator<ClusterRole, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<ClusterRole> distinguish(Class<ClusterRole> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<ClusterRole, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<ClusterRole, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(ClusterRole.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+CLUSTERROLENAME,
		    		null));
	}

}
