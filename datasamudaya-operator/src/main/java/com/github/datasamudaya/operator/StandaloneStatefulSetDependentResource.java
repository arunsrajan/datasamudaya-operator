package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.*;
import static java.util.Objects.nonNull;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.core.util.IOUtils;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = StandaloneStatefulSetDiscriminator.class)
public class StandaloneStatefulSetDependentResource
        extends CRUDKubernetesDependentResource<StatefulSet, DatasamudayaOperatorCustomResource> {   

    public StandaloneStatefulSetDependentResource() {
        super(StatefulSet.class);

    }
    @Override
    protected StatefulSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        try {
			StatefulSet standaloneStatefulSetYaml = context.getClient().apps().statefulSets().load(IOUtils.toString(new InputStreamReader(
					getClass().
					getResourceAsStream(STANDALONEYAMLPATH)))).item();
			Container container = standaloneStatefulSetYaml.getSpec().getTemplate().getSpec().getContainers().get(0);
			container.setImage(nonNull(primary.getSpec().getSaimage())?primary.getSpec().getSaimage():STANDALONEIMAGE);
			Map<String, Quantity> limits = new HashMap<>();
			limits.put(CPU, nonNull(primary.getSpec().getSalimitcpu())?Quantity.parse(primary.getSpec().getSalimitcpu()):Quantity.parse(SALIMITCPU_DEFAULT));
			limits.put(MEMORY, nonNull(primary.getSpec().getSalimitmemory())?Quantity.parse(primary.getSpec().getSalimitmemory()):Quantity.parse(SALIMITMEMORY_DEFAULT));
			Map<String, Quantity> requests = new HashMap<>();
			limits.put(CPU, nonNull(primary.getSpec().getSarequestcpu())?Quantity.parse(primary.getSpec().getSarequestcpu()):Quantity.parse(SAREQUESTCPU_DEFAULT));
			limits.put(MEMORY, nonNull(primary.getSpec().getSarequestmemory())?Quantity.parse(primary.getSpec().getSarequestmemory()):Quantity.parse(SAREQUESTMEMORY_DEFAULT));
			container.getResources().setLimits(requests);
			container.getEnv().get(0).setValue(nonNull(primary.getSpec().getPodcidrnodemappingenabled())?primary.getSpec().getPodcidrnodemappingenabled():PODCIDRNODEMAPPINGENABLED_DEFAULT);
			container.getEnv().get(1).setValue(nonNull(primary.getSpec().getZkhostport())?primary.getSpec().getZkhostport():ZKHOSTPORT_DEFAULT);
			return standaloneStatefulSetYaml;
			
		} catch (IOException e) {
			
		}
		return null;        
    }
}