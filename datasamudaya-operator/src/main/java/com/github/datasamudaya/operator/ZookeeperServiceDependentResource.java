package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPERSERVICEYAMLPATH;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Service;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = ZookeeperServiceDiscriminator.class)
public class ZookeeperServiceDependentResource
        extends CRUDKubernetesDependentResource<Service, DatasamudayaOperatorCustomResource> 
implements Creator<Service, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource> {   

	private String zookeeperServiceYaml = "";
	private static final Logger log = LoggerFactory.getLogger(ZookeeperServiceDependentResource.class);
	public ZookeeperServiceDependentResource() {
		super(Service.class);
		zookeeperServiceYaml = Utils.readResource(ZOOKEEPERSERVICEYAMLPATH);
		log.error("ZookeeperService Yaml:\n {}", zookeeperServiceYaml);
	}
	
    @Override
    protected Service desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        Service zookeeperService = context.getClient().services().load(new ByteArrayInputStream(zookeeperServiceYaml.getBytes())).item();
		return zookeeperService;
    }
    
    @Override
    public Service create(Service target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {

		context.getClient().services().inNamespace(primary.getMetadata().getNamespace())
				.withName(DataSamudayaOperatorConstants.ZKSERVICEMETADATANAME).delete();
    }
}