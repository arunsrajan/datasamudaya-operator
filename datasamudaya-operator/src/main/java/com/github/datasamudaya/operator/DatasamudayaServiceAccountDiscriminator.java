package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTNAME;

import java.util.Optional;

import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ResourceDiscriminator;
import io.javaoperatorsdk.operator.processing.event.ResourceID;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

public class DatasamudayaServiceAccountDiscriminator implements ResourceDiscriminator<ServiceAccount, DatasamudayaOperatorCustomResource>{

	@Override
	public Optional<ServiceAccount> distinguish(Class<ServiceAccount> statefulSetClass, DatasamudayaOperatorCustomResource resource,
			Context<DatasamudayaOperatorCustomResource> context) {
		InformerEventSource<ServiceAccount, DatasamudayaOperatorCustomResource> ies =
		        (InformerEventSource<ServiceAccount, DatasamudayaOperatorCustomResource>) context
		            .eventSourceRetriever().getResourceEventSourceFor(ServiceAccount.class);
		    return ies.get(new ResourceID(resource.getMetadata().getName()+HYPHEN+SERVICEACCOUNTNAME,
		    		resource.getMetadata().getNamespace()));
	}

}
