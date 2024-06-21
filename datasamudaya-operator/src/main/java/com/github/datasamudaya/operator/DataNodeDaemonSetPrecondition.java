package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static java.util.Objects.nonNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.dependent.DependentResource;
import io.javaoperatorsdk.operator.processing.dependent.workflow.Condition;

public class DataNodeDaemonSetPrecondition implements Condition<DaemonSet, DatasamudayaOperatorCustomResource>{

	private static final Logger log = LoggerFactory.getLogger(DataNodeDaemonSetPrecondition.class);
	
	@Override
	public boolean isMet(DependentResource<DaemonSet, DatasamudayaOperatorCustomResource> dependentResource,
		DatasamudayaOperatorCustomResource primary, Context<DatasamudayaOperatorCustomResource> context) {
		try {
			DaemonSet nameNodeDaemonSet = context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).withName(primary.getMetadata().getName()+HYPHEN+NAMENODE).get();
			boolean isready = nonNull(nameNodeDaemonSet) && nonNull(nameNodeDaemonSet.getStatus().getNumberAvailable()) && nameNodeDaemonSet.getStatus().getNumberAvailable() >= 1?true:false;
			log.info("Namenode DaemonSet is ready: {}", isready);
			return isready;
		} catch (Exception e) {
			log.error("Error while checking Namenode DaemonSet:", e);
		}
		return false;
	}

}
