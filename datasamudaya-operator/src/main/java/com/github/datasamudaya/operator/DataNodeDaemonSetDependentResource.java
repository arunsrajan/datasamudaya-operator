package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HADOOPDATANODEDAEMONSETYAMLPATH;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEURL;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODEIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODELIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODELIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODEREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODEREQUESTMEMORY_DEFAULT;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = DataNodeDaemonSetDiscriminator.class)
public class DataNodeDaemonSetDependentResource
        extends CRUDKubernetesDependentResource<DaemonSet, DatasamudayaOperatorCustomResource> 
		implements Creator<DaemonSet, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String datanodeYaml = "";
	private static final Logger log = LoggerFactory.getLogger(DataNodeDaemonSetDependentResource.class);
	public DataNodeDaemonSetDependentResource() {
		super(DaemonSet.class);
		this.datanodeYaml = Utils.readResource(HADOOPDATANODEDAEMONSETYAMLPATH);
		log.error("Datanode StatefulSet Yaml:\n {}", datanodeYaml);
	}
	
    @Override
    protected DaemonSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
    	DaemonSet dataNodeDaemoneSet = context.getClient().apps().daemonSets().load(new ByteArrayInputStream(datanodeYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        dataNodeDaemoneSet.getMetadata().setName(primaryName+DATANODE);
        dataNodeDaemoneSet.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String,String> labels = dataNodeDaemoneSet.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+DATANODE);
        labels = dataNodeDaemoneSet.getSpec().getSelector().getMatchLabels();
        labels.put(APPLICATION, primaryName+DATANODE);
        labels = dataNodeDaemoneSet.getSpec().getTemplate().getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+DATANODE);
		Container container = dataNodeDaemoneSet.getSpec().getTemplate().getSpec().getContainers().get(0);
		container.setImage(nonNull(primary.getSpec().getDatanodeimage())?primary.getSpec().getDatanodeimage():DATANODEIMAGE);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getDatanodelimitcpu())?Quantity.parse(primary.getSpec().getDatanodelimitcpu()):Quantity.parse(DATANODELIMITCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getDatanodelimitmemory())?Quantity.parse(primary.getSpec().getDatanodelimitmemory()):Quantity.parse(DATANODELIMITMEMORY_DEFAULT));
		Map<String, Quantity> requests = new HashMap<>();
		requests.put(CPU, nonNull(primary.getSpec().getDatanoderequestcpu())?Quantity.parse(primary.getSpec().getDatanoderequestcpu()):Quantity.parse(DATANODEREQUESTCPU_DEFAULT));
		requests.put(MEMORY, nonNull(primary.getSpec().getDatanoderequestmemory())?Quantity.parse(primary.getSpec().getDatanoderequestmemory()):Quantity.parse(DATANODEREQUESTMEMORY_DEFAULT));
		container.getResources().setLimits(limits);
		container.getResources().setRequests(requests);
		Map<String, String> nameNodeLabel = new HashMap<>();
		nameNodeLabel.put(APPLICATION, primaryName+NAMENODE);
		Pod pod = Utils.waitUntilPodIsUp(context, primary, nameNodeLabel);
		container.getEnv().get(1).setValue(String.format(NAMENODEURL, pod.getStatus().getPodIP()));
		return dataNodeDaemoneSet;
    }
    
    @Override
    public DaemonSet create(DaemonSet target, DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
    	if(isNull(target)) {
    		return desired(primary, context);
    	}
		return target;    	
    }
    @Override
    public void delete(DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {

		context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace())
				.withName(primary.getMetadata().getName()+HYPHEN+DATANODE).delete();
    }
}