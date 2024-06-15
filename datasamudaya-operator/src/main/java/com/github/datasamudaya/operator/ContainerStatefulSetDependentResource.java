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

@KubernetesDependent(resourceDiscriminator = ContainerStatefulSetDiscriminator.class)
public class ContainerStatefulSetDependentResource
        extends CRUDKubernetesDependentResource<StatefulSet, DatasamudayaOperatorCustomResource> {   

    public ContainerStatefulSetDependentResource() {
        super(StatefulSet.class);

    }
    @Override
    protected StatefulSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        try {
			StatefulSet containerStatefulSetYaml = context.getClient().apps().statefulSets().load(IOUtils.toString(new InputStreamReader(
					getClass().
					getResourceAsStream(CONTAINERYAMLPATH)))).item();
			Container container = containerStatefulSetYaml.getSpec().getTemplate().getSpec().getContainers().get(0);
			container.setImage(nonNull(primary.getSpec().getContainerimage())?primary.getSpec().getContainerimage():CONTAINERIMAGE);
			Map<String, Quantity> limits = new HashMap<>();
			limits.put(CPU, nonNull(primary.getSpec().getContainerlimitcpu())?Quantity.parse(primary.getSpec().getContainerlimitcpu()):Quantity.parse(CONTAINERLIMITCPU_DEFAULT));
			limits.put(MEMORY, nonNull(primary.getSpec().getContainerlimitmemory())?Quantity.parse(primary.getSpec().getContainerlimitmemory()):Quantity.parse(CONTAINERLIMITMEMORY_DEFAULT));
			Map<String, Quantity> requests = new HashMap<>();
			limits.put(CPU, nonNull(primary.getSpec().getContainerrequestcpu())?Quantity.parse(primary.getSpec().getContainerrequestcpu()):Quantity.parse(CONTAINERREQUESTCPU_DEFAULT));
			limits.put(MEMORY, nonNull(primary.getSpec().getContainerrequestmemory())?Quantity.parse(primary.getSpec().getContainerrequestmemory()):Quantity.parse(CONTAINERREQUESTMEMORY_DEFAULT));
			container.getResources().setLimits(requests);
			container.getEnv().get(0).setValue(nonNull(primary.getSpec().getPodcidrnodemappingenabled())?primary.getSpec().getPodcidrnodemappingenabled():PODCIDRNODEMAPPINGENABLED_DEFAULT);
			container.getEnv().get(1).setValue(nonNull(primary.getSpec().getZkhostport())?primary.getSpec().getZkhostport():ZKHOSTPORT_DEFAULT);
			return containerStatefulSetYaml;
			
		} catch (IOException e) {
			
		}
		return null;        
    }
}