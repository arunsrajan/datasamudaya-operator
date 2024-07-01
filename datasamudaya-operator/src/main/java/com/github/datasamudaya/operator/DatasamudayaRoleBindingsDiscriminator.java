package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLEBINDINGNAME;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class DatasamudayaRoleBindingsDiscriminator implements ResourceDiscriminator<RoleBinding, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<RoleBinding> distinguish(Class<RoleBinding> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<RoleBinding, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<RoleBinding, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(RoleBinding.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+ROLEBINDINGNAME,
		    		null));
	}

}
