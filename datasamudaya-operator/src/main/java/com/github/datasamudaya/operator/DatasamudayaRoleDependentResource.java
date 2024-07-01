package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLENAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLEYAMLPATH;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.rbac.Role;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = DatasamudayaRoleDiscriminator.class)
public class DatasamudayaRoleDependentResource
        extends CRUDKubernetesDependentResource<Role, DatasamudayaOperatorCustomResource> 
		implements Creator<Role, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String roleYaml = "";
	private static final Logger log = LoggerFactory.getLogger(DatasamudayaRoleDependentResource.class);
	public DatasamudayaRoleDependentResource() {
		super(Role.class);
		this.roleYaml = Utils.readResource(ROLEYAMLPATH);
		log.info("Role Yaml:\n {}", roleYaml);
	}
	
    @Override
    protected Role desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        Role role = context.getClient().rbac().roles().load(new ByteArrayInputStream(roleYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        role.getMetadata().setName(primaryName+ROLENAME);
        role.getMetadata().setNamespace(primary.getMetadata().getNamespace());
		return role;
    }
    
    @Override
    public Role create(Role target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
		context.getClient().rbac().roles().inNamespace(primary.getMetadata().getNamespace()).withName(primary.getMetadata().getName()+HYPHEN+ROLENAME).delete();
    }
}