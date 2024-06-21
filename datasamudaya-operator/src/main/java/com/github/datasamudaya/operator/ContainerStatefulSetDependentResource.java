package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.COLON;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CONTAINERIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CONTAINERLIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CONTAINERLIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CONTAINERREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CONTAINERREQUESTMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CONTAINERYAMLPATH;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEPORT_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEURL;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.PODCIDRNODEMAPPINGENABLED_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTNAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.WORKER;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKPORT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = ContainerStatefulSetDiscriminator.class)
public class ContainerStatefulSetDependentResource
        extends CRUDKubernetesDependentResource<StatefulSet, DatasamudayaOperatorCustomResource> 
		implements Creator<StatefulSet, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String containerYaml = "";
	private static final Logger log = LoggerFactory.getLogger(ContainerStatefulSetDependentResource.class);
	public ContainerStatefulSetDependentResource() {
		super(StatefulSet.class);
		this.containerYaml = Utils.readResource(CONTAINERYAMLPATH);
		log.info("Container StatefulSet Yaml:\n {}", containerYaml);
	}
	
    @Override
    protected StatefulSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        StatefulSet containerStatefulSet = context.getClient().apps().statefulSets().load(new ByteArrayInputStream(containerYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        containerStatefulSet.getMetadata().setName(primaryName+WORKER);
        containerStatefulSet.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String,String> labels = containerStatefulSet.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+WORKER);
        labels = containerStatefulSet.getSpec().getSelector().getMatchLabels();
        labels.put(APPLICATION, primaryName+WORKER);
        labels = containerStatefulSet.getSpec().getTemplate().getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+WORKER);
		Container container = containerStatefulSet.getSpec().getTemplate().getSpec().getContainers().get(0);
		container.setImage(nonNull(primary.getSpec().getContainerimage())?primary.getSpec().getContainerimage():CONTAINERIMAGE);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getContainerlimitcpu())?Quantity.parse(primary.getSpec().getContainerlimitcpu()):Quantity.parse(CONTAINERLIMITCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getContainerlimitmemory())?Quantity.parse(primary.getSpec().getContainerlimitmemory()):Quantity.parse(CONTAINERLIMITMEMORY_DEFAULT));
		Map<String, Quantity> requests = new HashMap<>();
		requests.put(CPU, nonNull(primary.getSpec().getContainerrequestcpu())?Quantity.parse(primary.getSpec().getContainerrequestcpu()):Quantity.parse(CONTAINERREQUESTCPU_DEFAULT));
		requests.put(MEMORY, nonNull(primary.getSpec().getContainerrequestmemory())?Quantity.parse(primary.getSpec().getContainerrequestmemory()):Quantity.parse(CONTAINERREQUESTMEMORY_DEFAULT));
		container.getResources().setLimits(limits);
		container.getResources().setRequests(requests);
		containerStatefulSet.getSpec().setReplicas(primary.getSpec().getNumberofworkers()>1?primary.getSpec().getNumberofworkers():1);
		container.getEnv().get(0).setValue(nonNull(primary.getSpec().getPodcidrnodemappingenabled())?primary.getSpec().getPodcidrnodemappingenabled():PODCIDRNODEMAPPINGENABLED_DEFAULT);
		container.getEnv().get(4).setValue(primaryName+ZOOKEEPER+COLON+ZKPORT);
		Map<String, String> nameNodeLabel = new HashMap<>();
		nameNodeLabel.put(APPLICATION, primaryName+NAMENODE);
		Pod pod = Utils.waitUntilPodIsUp(context, primary, nameNodeLabel);
		container.getEnv().get(container.getEnv().size()-1).setValue(String.format(NAMENODEURL, pod.getStatus().getPodIP(),
				nonNull(primary.getSpec().getNamenodeport())?primary.getSpec().getNamenodeport():NAMENODEPORT_DEFAULT));
		containerStatefulSet.getSpec().getTemplate().getSpec().setServiceAccountName(primaryName+SERVICEACCOUNTNAME);
		return containerStatefulSet;
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
				.withName(primary.getMetadata().getName()+HYPHEN+WORKER).delete();
    }
}