package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPER;
import static java.util.Objects.nonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

public class ZookeeperStatefulSetPrecondition implements Condition<StatefulSet, DatasamudayaOperatorCustomResource>{

	private static final Logger log = LoggerFactory.getLogger(ZookeeperStatefulSetPrecondition.class);
	
	@Override
	public boolean isMet(DependentResource<StatefulSet, DatasamudayaOperatorCustomResource> dependentResource,
		DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
		try {
			StatefulSet zookeeperStatefulSet = context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).withName(primary.getMetadata().getName()+HYPHEN+ZOOKEEPER).get();
			boolean isready = nonNull(zookeeperStatefulSet) && zookeeperStatefulSet.getStatus().getReplicas() == 1?true:false;
			log.info("Zookeeper StatefulSet is ready: {}", isready);
			return isready;
		} catch (Exception e) {
			log.error("Error while checking Zookeeper StatefulSet:", e);
		}
		return false;
	}

}
