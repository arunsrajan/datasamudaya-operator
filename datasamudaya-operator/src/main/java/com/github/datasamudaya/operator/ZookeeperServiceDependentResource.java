package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPER;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPERSERVICEYAMLPATH;
import static java.util.Objects.isNull;

import java.io.ByteArrayInputStream;
import java.util.Map;

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
		log.info("ZookeeperService Yaml:\n {}", zookeeperServiceYaml);
	}
	
    @Override
    protected Service desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        Service zookeeperService = context.getClient().services().load(new ByteArrayInputStream(zookeeperServiceYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName() + HYPHEN;
        zookeeperService.getMetadata().setName(primaryName+ZOOKEEPER);
        zookeeperService.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String, String> labels = zookeeperService.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+ZOOKEEPER);
        labels = zookeeperService.getSpec().getSelector();
        labels.put(APPLICATION, primaryName+ZOOKEEPER);
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
				.withName(primary.getMetadata().getName() + HYPHEN + ZOOKEEPER).delete();
    }
}