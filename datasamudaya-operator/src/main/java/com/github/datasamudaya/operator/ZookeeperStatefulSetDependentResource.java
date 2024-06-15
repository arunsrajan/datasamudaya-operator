package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.CPU;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.MEMORY;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKIMAGE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKLIMITCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKLIMITMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKREQUESTCPU_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZKREQUESTMEMORY_DEFAULT;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPERYAMLPATH;
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

@KubernetesDependent(resourceDiscriminator = ZookeeperStatefulSetDiscriminator.class)
public class ZookeeperStatefulSetDependentResource
        extends CRUDKubernetesDependentResource<StatefulSet, DatasamudayaOperatorCustomResource> {   

    public ZookeeperStatefulSetDependentResource() {
        super(StatefulSet.class);

    }
    @Override
    protected StatefulSet desired(DatasamudayaOperatorCustomResource primary,
                                Context<DatasamudayaOperatorCustomResource> context) {
        try {
			StatefulSet zookeeperStatefulSetYaml = context.getClient().apps().statefulSets().load(IOUtils.toString(new InputStreamReader(
					getClass().
					getResourceAsStream(ZOOKEEPERYAMLPATH)))).item();
			Container zookeeper = zookeeperStatefulSetYaml.getSpec().getTemplate().getSpec().getContainers().get(0);
			zookeeper.setImage(nonNull(primary.getSpec().getZkimage())?primary.getSpec().getZkimage():ZKIMAGE);
			Map<String, Quantity> limits = new HashMap<>();
			limits.put(CPU, nonNull(primary.getSpec().getZklimitcpu())?Quantity.parse(primary.getSpec().getZklimitcpu()):Quantity.parse(ZKLIMITCPU_DEFAULT));
			limits.put(MEMORY, nonNull(primary.getSpec().getZklimitmemory())?Quantity.parse(primary.getSpec().getZklimitmemory()):Quantity.parse(ZKLIMITMEMORY_DEFAULT));
			Map<String, Quantity> requests = new HashMap<>();
			requests.put(CPU, nonNull(primary.getSpec().getZkrequestcpu())?Quantity.parse(primary.getSpec().getZkrequestcpu()):Quantity.parse(ZKREQUESTCPU_DEFAULT));
			requests.put(MEMORY, nonNull(primary.getSpec().getZkrequestmemory())?Quantity.parse(primary.getSpec().getZkrequestmemory()):Quantity.parse(ZKREQUESTMEMORY_DEFAULT));
			zookeeper.getResources().setRequests(requests);
			return zookeeperStatefulSetYaml;
			
		} catch (IOException e) {
			
		}
		return null;        
    }
}