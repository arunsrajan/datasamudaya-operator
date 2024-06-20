package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.COLON;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEURL;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.PODCIDRNODEMAPPINGENABLED_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SALIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SALIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SAREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SAREQUESTMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONEIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONEYAMLPATH;
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
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        standaloneStatefulSet.getMetadata().setName(primaryName+STANDALONE);
        standaloneStatefulSet.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String,String> labels = standaloneStatefulSet.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+STANDALONE);
        labels = standaloneStatefulSet.getSpec().getSelector().getMatchLabels();
        labels.put(APPLICATION, primaryName+STANDALONE);
        labels = standaloneStatefulSet.getSpec().getTemplate().getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+STANDALONE);
		Container container = standaloneStatefulSet.getSpec().getTemplate().getSpec().getContainers().get(0);
		container.setImage(nonNull(primary.getSpec().getSaimage())?primary.getSpec().getSaimage():STANDALONEIMAGE);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getSalimitcpu())?Quantity.parse(primary.getSpec().getSalimitcpu()):Quantity.parse(SALIMITCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getSalimitmemory())?Quantity.parse(primary.getSpec().getSalimitmemory()):Quantity.parse(SALIMITMEMORY_DEFAULT));
		Map<String, Quantity> requests = new HashMap<>();
		requests.put(CPU, nonNull(primary.getSpec().getSarequestcpu())?Quantity.parse(primary.getSpec().getSarequestcpu()):Quantity.parse(SAREQUESTCPU_DEFAULT));
		requests.put(MEMORY, nonNull(primary.getSpec().getSarequestmemory())?Quantity.parse(primary.getSpec().getSarequestmemory()):Quantity.parse(SAREQUESTMEMORY_DEFAULT));
		container.getResources().setLimits(limits);
		container.getResources().setRequests(requests);
		container.getEnv().get(0).setValue(nonNull(primary.getSpec().getPodcidrnodemappingenabled())?primary.getSpec().getPodcidrnodemappingenabled():PODCIDRNODEMAPPINGENABLED_DEFAULT);
		container.getEnv().get(2).setValue(primaryName+ZOOKEEPER+COLON+ZKPORT);
		Map<String, String> nameNodeLabel = new HashMap<>();
		nameNodeLabel.put(APPLICATION, primaryName+NAMENODE);
		Pod primaryPod = context.getClient().pods().inNamespace(primary.getMetadata().getNamespace()).withLabels(nameNodeLabel).list().getItems().get(0);
		container.getEnv().get(container.getEnv().size()-1).setValue(String.format(NAMENODEURL, primaryPod.getStatus().getHostIP()));
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
				.withName(primary.getMetadata().getName()+HYPHEN+STANDALONE).delete();
    }
}