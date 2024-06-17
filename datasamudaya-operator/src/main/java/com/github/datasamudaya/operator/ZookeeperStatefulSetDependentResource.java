package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.APPLICATION;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKLIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKLIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKREQUESTMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPER;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPERYAMLPATH;
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

@KubernetesDependent(resourceDiscriminator = ZookeeperStatefulSetDiscriminator.class)
public class ZookeeperStatefulSetDependentResource
        extends CRUDKubernetesDependentResource<StatefulSet, DatasamudayaOperatorCustomResource> 
		implements Creator<StatefulSet, DatasamudayaOperatorCustomResource>, Deleter<DatasamudayaOperatorCustomResource>{   

	private String zkstatefulsetYaml = "";
	private static final Logger log = LoggerFactory.getLogger(ZookeeperStatefulSetDependentResource.class);
    public ZookeeperStatefulSetDependentResource() {
        super(StatefulSet.class);
        zkstatefulsetYaml = Utils.readResource(ZOOKEEPERYAMLPATH);
        log.error("ZookeeperStatefulSet Yaml:\n {}", zkstatefulsetYaml);
    }
    @Override
    protected StatefulSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        StatefulSet zookeeperStatefulSet = context.getClient().apps().statefulSets().load(new ByteArrayInputStream(zkstatefulsetYaml.getBytes())).item();
        String primaryName = primary.getMetadata().getName()+HYPHEN;
        zookeeperStatefulSet.getMetadata().setName(primaryName+ZOOKEEPER);
        zookeeperStatefulSet.getMetadata().setNamespace(primary.getMetadata().getNamespace());
        Map<String,String> labels = zookeeperStatefulSet.getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+ZOOKEEPER);
        labels = zookeeperStatefulSet.getSpec().getSelector().getMatchLabels();
        labels.put(APPLICATION, primaryName+ZOOKEEPER);
        labels = zookeeperStatefulSet.getSpec().getTemplate().getMetadata().getLabels();
        labels.put(APPLICATION, primaryName+ZOOKEEPER);
		Container zookeeper = zookeeperStatefulSet.getSpec().getTemplate().getSpec().getContainers().get(0);
		zookeeper.setImage(nonNull(primary.getSpec().getZkimage())?primary.getSpec().getZkimage():ZKIMAGE);
		Map<String, Quantity> limits = new HashMap<>();
		limits.put(CPU, nonNull(primary.getSpec().getZklimitcpu())?Quantity.parse(primary.getSpec().getZklimitcpu()):Quantity.parse(ZKLIMITCPU_DEFAULT));
		limits.put(MEMORY, nonNull(primary.getSpec().getZklimitmemory())?Quantity.parse(primary.getSpec().getZklimitmemory()):Quantity.parse(ZKLIMITMEMORY_DEFAULT));
		Map<String, Quantity> requests = new HashMap<>();
		requests.put(CPU, nonNull(primary.getSpec().getZkrequestcpu())?Quantity.parse(primary.getSpec().getZkrequestcpu()):Quantity.parse(ZKREQUESTCPU_DEFAULT));
		requests.put(MEMORY, nonNull(primary.getSpec().getZkrequestmemory())?Quantity.parse(primary.getSpec().getZkrequestmemory()):Quantity.parse(ZKREQUESTMEMORY_DEFAULT));
		zookeeper.getResources().setRequests(requests);
		return zookeeperStatefulSet;
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
				.withName(primary.getMetadata().getName()+HYPHEN+ZOOKEEPER).delete();
    }
}