package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLEBINDINGNAME;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class DatasamudayaClusterRoleBindingsDiscriminator implements ResourceDiscriminator<ClusterRoleBinding, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<ClusterRoleBinding> distinguish(Class<ClusterRoleBinding> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<ClusterRoleBinding, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<ClusterRoleBinding, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(ClusterRoleBinding.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+CLUSTERROLEBINDINGNAME,
		    		null));
	}

}
