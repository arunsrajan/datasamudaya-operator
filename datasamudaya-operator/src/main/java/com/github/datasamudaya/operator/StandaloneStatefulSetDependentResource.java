package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.PODCIDRNODEMAPPINGENABLED_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SALIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SALIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SAREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SAREQUESTMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONEIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONEYAMLPATH;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKHOSTPORT_DEFAULT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = StandaloneStatefulSetDiscriminator.class)
public class StandaloneStatefulSetDependentResource
        extends CRUDKubernetesDependentResource<StatefulSet, DatasamudayaOperatorCustomResource> 
		implements Creator<StatefulSet, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   
	private String standaloneYaml = "";
	private static final Logger log = LoggerFactory.getLogger(StandaloneStatefulSetDependentResource.class);
    public StandaloneStatefulSetDependentResource() {
        super(StatefulSet.class);
        standaloneYaml = Utils.readResource(STANDALONEYAMLPATH);
        log.error("Standalone StatefulSet Yaml:\n {}", standaloneYaml);
    }
    @Override
    protected StatefulSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        StatefulSet standaloneStatefulSet = context.getClient().apps().statefulSets().load(new ByteArrayInputStream(standaloneYaml.getBytes())).item();
		Container container = standaloneStatefulSet.getSpec().getTemplate().getSpec().getContainers().get(0);
		container.setImage(nonNull(primary.getSpec().getSaimage())?primary.getSpec().getSaimage():STANDALONEIMAGE);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getSalimitcpu())?Quantity.parse(primary.getSpec().getSalimitcpu()):Quantity.parse(SALIMITCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getSalimitmemory())?Quantity.parse(primary.getSpec().getSalimitmemory()):Quantity.parse(SALIMITMEMORY_DEFAULT));
		Map<String, Quantity> requests = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getSarequestcpu())?Quantity.parse(primary.getSpec().getSarequestcpu()):Quantity.parse(SAREQUESTCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getSarequestmemory())?Quantity.parse(primary.getSpec().getSarequestmemory()):Quantity.parse(SAREQUESTMEMORY_DEFAULT));
		container.getResources().setLimits(requests);
		container.getEnv().get(0).setValue(nonNull(primary.getSpec().getPodcidrnodemappingenabled())?primary.getSpec().getPodcidrnodemappingenabled():PODCIDRNODEMAPPINGENABLED_DEFAULT);
		container.getEnv().get(4).setValue(nonNull(primary.getSpec().getZkhostport())?primary.getSpec().getZkhostport():ZKHOSTPORT_DEFAULT);
		return standaloneStatefulSet;
    }
    @Override
    public StatefulSet create(StatefulSet target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {

		context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace())
				.withName(DataSamudayaOperatorConstants.SAMETADATANAME).delete();
    }
}