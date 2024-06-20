package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HADOOPNAMENODEDAEMONSETYAMLPATH;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODELIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODELIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEREQUESTMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODEURL;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Deleter;
import io.javaoperatorsdk.operator.processing.dependent.Creator;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent(resourceDiscriminator = NameNodeDaemonSetDiscriminator.class)
public class NameNodeDaemonSetDependentResource
        extends CRUDKubernetesDependentResource<DaemonSet, DatasamudayaOperatorCustomResource> 
		implements Creator<DaemonSet, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String namenodeYaml = "";
	private static final Logger log = LoggerFactory.getLogger(NameNodeDaemonSetDependentResource.class);
	public NameNodeDaemonSetDependentResource() {
		super(DaemonSet.class);
		this.namenodeYaml = Utils.readResource(HADOOPNAMENODEDAEMONSETYAMLPATH);
		log.error("Namenode StatefulSet Yaml:\n {}", namenodeYaml);
	}
	
    @Override
    protected DaemonSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
    	DaemonSet nameNodeDaemoneSet = context.getClient().apps().daemonSets().load(new ByteArrayInputStream(namenodeYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        nameNodeDaemoneSet.getMetadata().setName(primaryName+NAMENODE);
        nameNodeDaemoneSet.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String,String> labels = nameNodeDaemoneSet.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+NAMENODE);
        labels = nameNodeDaemoneSet.getSpec().getSelector().getMatchLabels();
        labels.put(APPLICATION, primaryName+NAMENODE);
        labels = nameNodeDaemoneSet.getSpec().getTemplate().getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+NAMENODE);
		Container container = nameNodeDaemoneSet.getSpec().getTemplate().getSpec().getContainers().get(0);
		container.setImage(nonNull(primary.getSpec().getNamenodeimage())?primary.getSpec().getNamenodeimage():NAMENODEIMAGE);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getNamenodelimitcpu())?Quantity.parse(primary.getSpec().getNamenodelimitcpu()):Quantity.parse(NAMENODELIMITCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getNamenodelimitmemory())?Quantity.parse(primary.getSpec().getNamenodelimitmemory()):Quantity.parse(NAMENODELIMITMEMORY_DEFAULT));
		Map<String, Quantity> requests = new HashMap<>();
		requests.put(CPU, nonNull(primary.getSpec().getNamenoderequestcpu())?Quantity.parse(primary.getSpec().getNamenoderequestcpu()):Quantity.parse(NAMENODEREQUESTCPU_DEFAULT));
		requests.put(MEMORY, nonNull(primary.getSpec().getNamenoderequestmemory())?Quantity.parse(primary.getSpec().getNamenoderequestmemory()):Quantity.parse(NAMENODEREQUESTMEMORY_DEFAULT));
		container.getResources().setLimits(limits);
		container.getResources().setRequests(requests);
		Node primaryNode = context.getClient().nodes().list().getItems().get(0);
		container.getEnv().get(1).setValue(String.format(NAMENODEURL, primaryNode.getStatus().getAddresses().get(0).getAddress()));
		return nameNodeDaemoneSet;
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
				.withName(primary.getMetadata().getName()+HYPHEN+NAMENODE).delete();
    }
}