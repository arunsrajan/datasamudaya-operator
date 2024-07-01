package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLENAME;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.rbac.Role;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class DatasamudayaRoleDiscriminator implements ResourceDiscriminator<Role, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<Role> distinguish(Class<Role> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<Role, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<Role, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(Role.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+ROLENAME,
		    		null));
	}

}
