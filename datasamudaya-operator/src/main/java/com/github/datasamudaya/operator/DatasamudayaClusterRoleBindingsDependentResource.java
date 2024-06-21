package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLENAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLEBINDINGNAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLEBINDINGYAMLPATH;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTNAME;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.rbac.ClusterRoleBinding;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = DatasamudayaClusterRoleBindingsDiscriminator.class)
public class DatasamudayaClusterRoleBindingsDependentResource
        extends CRUDKubernetesDependentResource<ClusterRoleBinding, DatasamudayaOperatorCustomResource> 
		implements Creator<ClusterRoleBinding, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String clusterrolebindingYaml = "";
	private static final Logger log = LoggerFactory.getLogger(DatasamudayaClusterRoleBindingsDependentResource.class);
	public DatasamudayaClusterRoleBindingsDependentResource() {
		super(ClusterRoleBinding.class);
		this.clusterrolebindingYaml = Utils.readResource(CLUSTERROLEBINDINGYAMLPATH);
		log.info("ClusterRoleBinding Yaml:\n {}", clusterrolebindingYaml);
	}
	
    @Override
    protected ClusterRoleBinding desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        ClusterRoleBinding clusterRoleBinding = context.getClient().rbac().clusterRoleBindings().load(new ByteArrayInputStream(clusterrolebindingYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        clusterRoleBinding.getMetadata().setName(primaryName+CLUSTERROLEBINDINGNAME);
        clusterRoleBinding.getSubjects().get(0).setName(primaryName+SERVICEACCOUNTNAME);
        clusterRoleBinding.getSubjects().get(0).setNamespace(primary.getMetadata().getNamespace());
        clusterRoleBinding.getRoleRef().setName(primaryName+CLUSTERROLENAME);
		return clusterRoleBinding;
    }
    
    @Override
    public ClusterRoleBinding create(ClusterRoleBinding target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
		context.getClient().rbac().clusterRoleBindings().withName(primary.getMetadata().getName()+HYPHEN+CLUSTERROLEBINDINGNAME).delete();
    }
}