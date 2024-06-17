package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONESERVICEYAMLPATH;
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

@KubernetesDependent(resourceDiscriminator = StandaloneServiceDiscriminator.class)
public class StandaloneServiceDependentResource
        extends CRUDKubernetesDependentResource<Service, DatasamudayaOperatorCustomResource> 
		implements Creator<Service, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String standaloneServiceYaml = "";
	private static final Logger log = LoggerFactory.getLogger(StandaloneServiceDependentResource.class);
	public StandaloneServiceDependentResource() {
		super(Service.class);
		standaloneServiceYaml = Utils.readResource(STANDALONESERVICEYAMLPATH);
		log.error("Standalone Service Yaml:\n {}", standaloneServiceYaml);
	}
    @Override
    protected Service desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
    	String primaryName = primary.getMetadata().getName()+HYPHEN;
        Service standaloneService = context.getClient().services().load(new ByteArrayInputStream(standaloneServiceYaml.getBytes())).item();
        standaloneService.getMetadata().setName(primaryName+STANDALONE);
        standaloneService.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String, String> labels = standaloneService.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+STANDALONE);
        labels = standaloneService.getSpec().getSelector();
        labels.put(APPLICATION, primaryName+STANDALONE);
		return standaloneService;
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
				.withName(DataSamudayaOperatorConstants.SASERVICEMETADATANAME).delete();
    }
}