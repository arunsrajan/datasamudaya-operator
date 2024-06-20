package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICE_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STATEFULSET_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DAEMONSET_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.WORKER;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPER;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODE;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.client.dsl.Replaceable;
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Cleaner;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.DeleteControl;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

@ControllerConfiguration(dependents = {
										@Dependent(type = NameNodeDaemonSetDependentResource.class,
												useEventSourceWithName = DAEMONSET_MAP_EVENT_SOURCE),
										@Dependent(type = DataNodeDaemonSetDependentResource.class,
										useEventSourceWithName = DAEMONSET_MAP_EVENT_SOURCE,
										reconcilePrecondition = DataNodeDaemonSetPrecondition.class),
										@Dependent(type = ZookeeperStatefulSetDependentResource.class,
										useEventSourceWithName = STATEFULSET_MAP_EVENT_SOURCE),
										@Dependent(type = StandaloneStatefulSetDependentResource.class,
										useEventSourceWithName = STATEFULSET_MAP_EVENT_SOURCE,
										reconcilePrecondition = ZookeeperStatefulSetPrecondition.class),
										@Dependent(type = ContainerStatefulSetDependentResource.class,
										useEventSourceWithName = STATEFULSET_MAP_EVENT_SOURCE,
										reconcilePrecondition = ZookeeperStatefulSetPrecondition.class),
										@Dependent(type = StandaloneServiceDependentResource.class,
										useEventSourceWithName = SERVICE_MAP_EVENT_SOURCE),
										@Dependent(type = ZookeeperServiceDependentResource.class,
										useEventSourceWithName = SERVICE_MAP_EVENT_SOURCE),
										@Dependent(type = NameNodeServiceDependentResource.class,
										useEventSourceWithName = SERVICE_MAP_EVENT_SOURCE)})
public class DatasamudayaOperatorReconciler implements Reconciler<DatasamudayaOperatorCustomResource>
														,EventSourceInitializer<DatasamudayaOperatorCustomResource>
														, Cleaner<DatasamudayaOperatorCustomResource>{

	private static final Logger log = LoggerFactory.getLogger(DatasamudayaOperatorReconciler.class);
	
    public UpdateControl<DatasamudayaOperatorCustomResource> reconcile(DatasamudayaOperatorCustomResource primary,
                                                     Context<DatasamudayaOperatorCustomResource> context) {
    	log.info("The Resource To be Reconciled: {}", primary);
    	
    	Optional<Set<Service>> previousService = Optional.ofNullable(context.getSecondaryResources(Service.class));
    	
    	boolean isServiceCreated = false;
		boolean isStatefulSetCreated = false;
    	boolean isServiceUpdated = false;
    	boolean isStatefulSetUpdated = false;
    	
    	if(previousService.isEmpty() || previousService.get().isEmpty()) {
    		ZookeeperServiceDependentResource zksvc = new ZookeeperServiceDependentResource();
        	context.getClient().services().inNamespace(primary.getMetadata().getNamespace()).resource(zksvc.desired(primary, context))
        	.createOr(Replaceable::update);
    		StandaloneServiceDependentResource sasvc = new StandaloneServiceDependentResource();
        	context.getClient().services().inNamespace(primary.getMetadata().getNamespace()).resource(sasvc.desired(primary, context))
        	.createOr(Replaceable::update);
        	NameNodeServiceDependentResource nnsvc = new NameNodeServiceDependentResource();
        	context.getClient().services().inNamespace(primary.getMetadata().getNamespace()).resource(nnsvc.desired(primary, context))
        	.createOr(Replaceable::update);
        	isServiceCreated = true;
        	log.info("Service Created: {}", primary);
    	} else {
    		isServiceUpdated = true;
    		log.info("Service Updated: {}", primary);
    	}
    	
    	boolean isDaemonSetCreated = false;
    	boolean isDaemonSetUpdated = false;
    	Optional<Set<DaemonSet>> previousDaemonSets = Optional.ofNullable(context.getSecondaryResources(DaemonSet.class));
    	
    	if(previousDaemonSets.isEmpty() || previousDaemonSets.get().isEmpty()) {
    		NameNodeDaemonSetDependentResource nndsdr = new NameNodeDaemonSetDependentResource();
        	context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).resource(nndsdr.desired(primary, context))
        	.createOr(Replaceable::update);
        	DataNodeDaemonSetDependentResource dndsdr = new DataNodeDaemonSetDependentResource();
        	context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).resource(dndsdr.desired(primary, context))
        	.createOr(Replaceable::update);
        	isDaemonSetCreated = true;
        	log.info("DaemonSet Created: {}", primary);
    	} else {
    		isDaemonSetUpdated = true;
    		log.info("DaemonSet Updated: {}", primary);
    	}
    	
    	Optional<Set<StatefulSet>> previousStatefulSet = Optional.ofNullable(context.getSecondaryResources(StatefulSet.class));
    	if(previousStatefulSet.isEmpty() || previousStatefulSet.get().isEmpty()) {
    		ZookeeperStatefulSetDependentResource zksdr = new ZookeeperStatefulSetDependentResource();
        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(zksdr.desired(primary, context))
        	.createOr(Replaceable::update);
        	StandaloneStatefulSetDependentResource sasdr = new StandaloneStatefulSetDependentResource();
        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(sasdr.desired(primary, context))
        	.createOr(Replaceable::update);
        	ContainerStatefulSetDependentResource csdr = new ContainerStatefulSetDependentResource();
        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(csdr.desired(primary, context))
        	.createOr(Replaceable::update);   
        	isStatefulSetCreated = true;
        	log.info("StatefulSet Created: {}", primary);
    	} else {
    		isStatefulSetUpdated = true;
    		log.info("StatefulSet Updated: {}", primary);
    	}
    	
    	if(isServiceCreated || isStatefulSetCreated || isDaemonSetCreated) {
    		return UpdateControl.patchStatus(primary);
    	}
    	
        return UpdateControl.noUpdate();
    }
    
    @Override
    public Map<String, EventSource> prepareEventSources(
        EventSourceContext<DatasamudayaOperatorCustomResource> context) {
      InformerEventSource<StatefulSet, DatasamudayaOperatorCustomResource> ssies =
          new InformerEventSource<>(InformerConfiguration.from(StatefulSet.class, context)
              .build(), context);
      
      InformerEventSource<Service, DatasamudayaOperatorCustomResource> svces =
              new InformerEventSource<>(InformerConfiguration.from(Service.class, context)
                  .build(), context);
      
      InformerEventSource<DaemonSet, DatasamudayaOperatorCustomResource> daemonsets =
              new InformerEventSource<>(InformerConfiguration.from(DaemonSet.class, context)
                  .build(), context);

      return Map.of(STATEFULSET_MAP_EVENT_SOURCE, ssies, SERVICE_MAP_EVENT_SOURCE, svces, DAEMONSET_MAP_EVENT_SOURCE, daemonsets);
    }

	@Override
	public DeleteControl cleanup(DatasamudayaOperatorCustomResource primary,
			Context<DatasamudayaOperatorCustomResource> context) {
		context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+WORKER).delete();
		context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+STANDALONE).delete();
		context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+ZOOKEEPER).delete();
		context.getClient().services().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+STANDALONE).delete();
		context.getClient().services().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName() + HYPHEN + ZOOKEEPER).delete();
		context.getClient().services().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName() + HYPHEN + NAMENODE).delete();
		context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+NAMENODE).delete();
		context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+DATANODE).delete();
		return DeleteControl.defaultDelete();
	}
}
