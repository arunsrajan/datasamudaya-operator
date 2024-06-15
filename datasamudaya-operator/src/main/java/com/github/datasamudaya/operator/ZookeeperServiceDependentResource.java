package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPERSERVICEYAMLPATH;

import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.core.util.IOUtils;

import io.fabric8.kubernetes.api.model.Service;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = ZookeeperServiceDiscriminator.class)
public class ZookeeperServiceDependentResource
        extends CRUDKubernetesDependentResource<Service, DatasamudayaOperatorCustomResource> {   

    public ZookeeperServiceDependentResource() {
        super(Service.class);

    }
    @Override
    protected Service desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        try {
			Service standaloneServiceYaml = context.getClient().services().load(IOUtils.toString(new InputStreamReader(
					getClass().
					getResourceAsStream(ZOOKEEPERSERVICEYAMLPATH)))).item();
			return standaloneServiceYaml;
			
		} catch (IOException e) {
			
		}
		return null;        
    }
}