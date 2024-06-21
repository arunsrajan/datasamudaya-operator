package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLENAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CLUSTERROLEYAMLPATH;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.rbac.ClusterRole;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = DatasamudayaClusterRoleDiscriminator.class)
public class DatasamudayaClusterRoleDependentResource
        extends CRUDKubernetesDependentResource<ClusterRole, DatasamudayaOperatorCustomResource> 
		implements Creator<ClusterRole, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String clusterroleYaml = "";
	private static final Logger log = LoggerFactory.getLogger(DatasamudayaClusterRoleDependentResource.class);
	public DatasamudayaClusterRoleDependentResource() {
		super(ClusterRole.class);
		this.clusterroleYaml = Utils.readResource(CLUSTERROLEYAMLPATH);
		log.info("ClusterRole Yaml:\n {}", clusterroleYaml);
	}
	
    @Override
    protected ClusterRole desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        ClusterRole clusterRole = context.getClient().rbac().clusterRoles().load(new ByteArrayInputStream(clusterroleYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        clusterRole.getMetadata().setName(primaryName+CLUSTERROLENAME);
        clusterRole.getMetadata().setNamespace(primary.getMetadata().getNamespace());
		return clusterRole;
    }
    
    @Override
    public ClusterRole create(ClusterRole target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
		context.getClient().rbac().clusterRoles().withName(primary.getMetadata().getName()+HYPHEN+CLUSTERROLENAME).delete();
    }
}