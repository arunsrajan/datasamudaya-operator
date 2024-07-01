package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLENAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLEBINDINGNAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLEBINDINGYAMLPATH;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTNAME;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = DatasamudayaRoleBindingsDiscriminator.class)
public class DatasamudayaRoleBindingsDependentResource
        extends CRUDKubernetesDependentResource<RoleBinding, DatasamudayaOperatorCustomResource> 
		implements Creator<RoleBinding, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String rolebindingYaml = "";
	private static final Logger log = LoggerFactory.getLogger(DatasamudayaRoleBindingsDependentResource.class);
	public DatasamudayaRoleBindingsDependentResource() {
		super(RoleBinding.class);
		this.rolebindingYaml = Utils.readResource(ROLEBINDINGYAMLPATH);
		log.info("RoleBinding Yaml:\n {}", rolebindingYaml);
	}
	
    @Override
    protected RoleBinding desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        RoleBinding roleBinding = context.getClient().rbac().roleBindings().load(new ByteArrayInputStream(rolebindingYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        roleBinding.getMetadata().setName(primaryName+ROLEBINDINGNAME);
        roleBinding.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        roleBinding.getSubjects().get(0).setName(primaryName+SERVICEACCOUNTNAME);
        roleBinding.getSubjects().get(0).setNamespace(primary.getMetadata().getNamespace());
        roleBinding.getRoleRef().setName(primaryName+ROLENAME);
		return roleBinding;
    }
    
    @Override
    public RoleBinding create(RoleBinding target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
		context.getClient().rbac().roleBindings().inNamespace(primary.getMetadata().getNamespace()).withName(primary.getMetadata().getName()+HYPHEN+ROLEBINDINGNAME).delete();
    }
}