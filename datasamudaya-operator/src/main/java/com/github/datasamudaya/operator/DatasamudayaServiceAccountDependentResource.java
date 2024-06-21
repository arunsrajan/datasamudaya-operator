package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTNAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTYAMLPATH;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = DatasamudayaServiceAccountDiscriminator.class)
public class DatasamudayaServiceAccountDependentResource
        extends CRUDKubernetesDependentResource<ServiceAccount, DatasamudayaOperatorCustomResource> 
		implements Creator<ServiceAccount, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String serviceaccountYaml = "";
	private static final Logger log = LoggerFactory.getLogger(DatasamudayaServiceAccountDependentResource.class);
	public DatasamudayaServiceAccountDependentResource() {
		super(ServiceAccount.class);
		this.serviceaccountYaml = Utils.readResource(SERVICEACCOUNTYAMLPATH);
		log.info("ServiceAccount Yaml:\n {}", serviceaccountYaml);
	}
	
    @Override
    protected ServiceAccount desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        ServiceAccount serviceaccountServiceAccount = context.getClient().serviceAccounts().load(new ByteArrayInputStream(serviceaccountYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        serviceaccountServiceAccount.getMetadata().setName(primaryName+SERVICEACCOUNTNAME);
        serviceaccountServiceAccount.getMetadata().setNamespace(primary.getMetadata().getNamespace());       
		return serviceaccountServiceAccount;
    }
    
    @Override
    public ServiceAccount create(ServiceAccount target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {

		context.getClient().serviceAccounts().inNamespace(primary.getMetadata().getNamespace())
				.withName(primary.getMetadata().getName()+HYPHEN+SERVICEACCOUNTNAME).delete();
    }
}