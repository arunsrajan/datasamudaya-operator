package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DAEMONSET_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.DATANODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.HYPHEN;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.NAMENODE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLEBINDINGNAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLEBINDINGS_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLENAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ROLE_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNTNAME;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICEACCOUNT_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.SERVICE_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STANDALONE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.STATEFULSET_MAP_EVENT_SOURCE;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.WORKER;
import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.ZOOKEEPER;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.rbac.Role;
import io.fabric8.kubernetes.api.model.rbac.RoleBinding;
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
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

@ControllerConfiguration
public class DatasamudayaOperatorReconciler implements Reconciler<DatasamudayaOperatorCustomResource>
														,EventSourceInitializer<DatasamudayaOperatorCustomResource>
														, Cleaner<DatasamudayaOperatorCustomResource>{

	private static final Logger log = LoggerFactory.getLogger(DatasamudayaOperatorReconciler.class);
	ExecutorService threadPool = Executors.newFixedThreadPool(10);
    public UpdateControl<DatasamudayaOperatorCustomResource> reconcile(DatasamudayaOperatorCustomResource primary,
                                                     Context<DatasamudayaOperatorCustomResource> context) {
    	log.info("The Resource To be Reconciled: {}", primary);
    	
    	Optional<Set<ServiceAccount>> previousServiceAccount = Optional.ofNullable(context.getSecondaryResources(ServiceAccount.class));
    	
    	if(previousServiceAccount.isEmpty() || previousServiceAccount.get().isEmpty()) {
    		DatasamudayaServiceAccountDependentResource dssadr = new DatasamudayaServiceAccountDependentResource();
			context.getClient().serviceAccounts().resource(dssadr.desired(primary, context))
			.createOr(Replaceable::update);
    	}
    	
    	Optional<Set<Role>> previousRole = Optional.ofNullable(context.getSecondaryResources(Role.class));
    	
    	if(previousRole.isEmpty() || previousRole.get().isEmpty()) {
    		DatasamudayaRoleDependentResource dsrdr = new DatasamudayaRoleDependentResource();
			context.getClient().rbac().roles().inNamespace(primary.getMetadata().getNamespace()).resource(dsrdr.desired(primary, context))
			.createOr(Replaceable::update);
    	}
    	
    	Optional<Set<RoleBinding>> previousRoleBinding = Optional.ofNullable(context.getSecondaryResources(RoleBinding.class));
    	
    	if(previousRoleBinding.isEmpty() || previousRoleBinding.get().isEmpty()) {
    		DatasamudayaRoleBindingsDependentResource dsrbdr = new DatasamudayaRoleBindingsDependentResource();
			context.getClient().rbac().roleBindings().inNamespace(primary.getMetadata().getNamespace()).resource(dsrbdr.desired(primary, context))
			.createOr(Replaceable::update);
    	}
    	
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
    		Set<Service> services =  previousService.get();
    		String servicePrefix = primary.getMetadata().getName() + HYPHEN;
    		List<String> servicesToReconcile = new ArrayList<>(Arrays.asList(servicePrefix + ZOOKEEPER, servicePrefix+STANDALONE, servicePrefix+NAMENODE));
			List<String> servicesAvailable = services.stream().filter(serviceToFilter->primary.getMetadata().getNamespace()
					.equals(serviceToFilter.getMetadata().getNamespace()))
					.map(service->service.getMetadata().getName()).collect(Collectors.toList());
			servicesToReconcile.removeAll(servicesAvailable);
			for(String serviceToReconcile : servicesToReconcile) {
				if(serviceToReconcile.endsWith(ZOOKEEPER)) {
					isServiceUpdated = true;
					ZookeeperServiceDependentResource zksvc = new ZookeeperServiceDependentResource();
		        	context.getClient().services().inNamespace(primary.getMetadata().getNamespace()).resource(zksvc.desired(primary, context))
		        	.createOr(Replaceable::update);
				} else if(serviceToReconcile.endsWith(STANDALONE)) {
					isServiceUpdated = true;
					StandaloneServiceDependentResource sasvc = new StandaloneServiceDependentResource();
		        	context.getClient().services().inNamespace(primary.getMetadata().getNamespace()).resource(sasvc.desired(primary, context))
		        	.createOr(Replaceable::update);
				} else if(serviceToReconcile.endsWith(NAMENODE)) {
					isServiceUpdated = true;
					NameNodeServiceDependentResource nnsvc = new NameNodeServiceDependentResource();
		        	context.getClient().services().inNamespace(primary.getMetadata().getNamespace()).resource(nnsvc.desired(primary, context))
		        	.createOr(Replaceable::update);
				}
			}    		
    		log.info("Service Updated: {}", primary);
    	}
    	
    	boolean isDaemonSetCreated = false;
    	boolean isDaemonSetUpdated = false;
    	Optional<Set<DaemonSet>> previousDaemonSets = Optional.ofNullable(context.getSecondaryResources(DaemonSet.class));
    	
    	if(previousDaemonSets.isEmpty() || previousDaemonSets.get().isEmpty()) {
    		threadPool.execute(() -> {
	    		NameNodeDaemonSetDependentResource nndsdr = new NameNodeDaemonSetDependentResource();
	        	context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).resource(nndsdr.desired(primary, context))
	        	.createOr(Replaceable::update);
	        	log.info("NameNode Created With Resource: {}", primary);
    		});    		
    		threadPool.execute(() -> {    			
	        	DataNodeDaemonSetDependentResource dndsdr = new DataNodeDaemonSetDependentResource();
	        	context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).resource(dndsdr.desired(primary, context))
	        	.createOr(Replaceable::update);
    		});
        	isDaemonSetCreated = true;
        	log.info("DaemonSet Created: {}", primary);
    	} else {
			Set<DaemonSet> daemonSets =  previousDaemonSets.get();
			String daemonSetPrefix = primary.getMetadata().getName() + HYPHEN;
			List<String> daemonSetsToReconcile = new ArrayList<>(Arrays.asList(daemonSetPrefix + NAMENODE, daemonSetPrefix+DATANODE));
			List<String> daemonSetsAvailable = daemonSets.stream().filter(daemonSetToFilter->primary.getMetadata().getNamespace()
					.equals(daemonSetToFilter.getMetadata().getNamespace()) &&
					daemonSetNotToBeReconciled(primary, nonNull(primary.getStatus())?primary.getStatus().getSpec():null, daemonSetToFilter.getMetadata().getName()))
					.map(daemonSet->daemonSet.getMetadata().getName()).collect(Collectors.toList());
			daemonSetsToReconcile.removeAll(daemonSetsAvailable);
			for(String daemonSetToReconcile : daemonSetsToReconcile) {
				if(daemonSetToReconcile.endsWith(NAMENODE)) {
					isDaemonSetUpdated = true;
					threadPool.execute(() -> {
						NameNodeDaemonSetDependentResource nndsdr = new NameNodeDaemonSetDependentResource();
			        	context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).resource(nndsdr.desired(primary, context))
			        	.createOr(Replaceable::update);
					});
				} else if(daemonSetToReconcile.endsWith(DATANODE)) {
					isDaemonSetUpdated = true;
					threadPool.execute(() -> {
						DataNodeDaemonSetDependentResource dndsdr = new DataNodeDaemonSetDependentResource();
			        	context.getClient().apps().daemonSets().inNamespace(primary.getMetadata().getNamespace()).resource(dndsdr.desired(primary, context))
			        	.createOr(Replaceable::update);
					});
				}
			}    		
    		log.info("DaemonSet Updated: {}", primary);
    	}
    	
    	Optional<Set<StatefulSet>> previousStatefulSet = Optional.ofNullable(context.getSecondaryResources(StatefulSet.class));
    	if(previousStatefulSet.isEmpty() || previousStatefulSet.get().isEmpty()) {
    		threadPool.execute(() -> {
	    		ZookeeperStatefulSetDependentResource zksdr = new ZookeeperStatefulSetDependentResource();
	        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(zksdr.desired(primary, context))
	        	.createOr(Replaceable::update);
    		});
    		threadPool.execute(() -> {    			
	        	StandaloneStatefulSetDependentResource sasdr = new StandaloneStatefulSetDependentResource();
	        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(sasdr.desired(primary, context))
	        	.createOr(Replaceable::update);
    		});
    		threadPool.execute(() -> {    			
	        	ContainerStatefulSetDependentResource csdr = new ContainerStatefulSetDependentResource();
	        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(csdr.desired(primary, context))
	        	.createOr(Replaceable::update);
    		});
        	isStatefulSetCreated = true;
        	log.info("StatefulSet Created: {}", primary);
    	} else {
			Set<StatefulSet> statefulSets =  previousStatefulSet.get();
			String statefulSetPrefix = primary.getMetadata().getName() + HYPHEN;
			List<String> statefulSetsToReconcile = new ArrayList<>(Arrays.asList(statefulSetPrefix + ZOOKEEPER, statefulSetPrefix+STANDALONE, statefulSetPrefix+WORKER));
			List<String> statefulSetsAvailable = statefulSets.stream().filter(statefulSetToFilter->
			primary.getMetadata().getNamespace().equals(statefulSetToFilter.getMetadata().getNamespace()) &&
					statefulSetNotToBeReconciled(primary, nonNull(primary.getStatus())?primary.getStatus().getSpec():null, statefulSetToFilter.getMetadata().getName()))
					.map(statefulSet->statefulSet.getMetadata().getName()).collect(Collectors.toList());
			statefulSetsToReconcile.removeAll(statefulSetsAvailable);
			for(String statefulSetToReconcile : statefulSetsToReconcile) {
				if(statefulSetToReconcile.endsWith(ZOOKEEPER)) {
					isStatefulSetUpdated = true;
					threadPool.execute(() -> {
						ZookeeperStatefulSetDependentResource zksdr = new ZookeeperStatefulSetDependentResource();
			        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(zksdr.desired(primary, context))
			        	.createOr(Replaceable::update);
					});
				} else if(statefulSetToReconcile.endsWith(STANDALONE)) {
					isStatefulSetUpdated = true;
					threadPool.execute(() -> {
						StandaloneStatefulSetDependentResource sasdr = new StandaloneStatefulSetDependentResource();
			        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(sasdr.desired(primary, context))
			        	.createOr(Replaceable::update);
					});
				} else if(statefulSetToReconcile.endsWith(WORKER)) {
					isStatefulSetUpdated = true;
					threadPool.execute(() -> {
						ContainerStatefulSetDependentResource csdr = new ContainerStatefulSetDependentResource();
			        	context.getClient().apps().statefulSets().inNamespace(primary.getMetadata().getNamespace()).resource(csdr.desired(primary, context))
			        	.createOr(Replaceable::update);
					});
				}
			}    		
    		log.info("StatefulSet Updated: {}", primary);
    	}
    	
    	if(isServiceCreated || isStatefulSetCreated || isDaemonSetCreated || isServiceUpdated || isStatefulSetUpdated || isDaemonSetUpdated) {
    		if(isNull(primary.getStatus())) {
    			primary.setStatus(new DatasamudayaOperatorStatus());
    		}
    		primary.getStatus().setNumberofmodifications(primary.getStatus().getNumberofmodifications()+1);
    		primary.getStatus().setSpec(primary.getSpec());
    		return UpdateControl.patchStatus(primary);
    	}
    	
        return UpdateControl.noUpdate();
    }
    
    protected boolean daemonSetNotToBeReconciled(DatasamudayaOperatorCustomResource primary, DatasamudayaOperatorSpec fromStorage, String daemonSetName) {
    	if(isNull(fromStorage)) {
    		return true;
    	}
    	else if(daemonSetName.equals(primary.getMetadata().getName() + HYPHEN + NAMENODE)) {
    		if(nonNull(primary.getSpec().getNamenodeimage()) 
	    			&& !primary.getSpec().getNamenodeimage().equals(fromStorage.getNamenodeimage())
	    			|| isNull(primary.getSpec().getNamenodeimage()) && nonNull(fromStorage.getNamenodeimage())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getNamenoderequestcpu()) 
	    			&& !primary.getSpec().getNamenoderequestcpu().equals(fromStorage.getNamenoderequestcpu())
	    			|| isNull(primary.getSpec().getNamenoderequestcpu()) && nonNull(fromStorage.getNamenoderequestcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getNamenodelimitcpu()) 
	    			&& !primary.getSpec().getNamenodelimitcpu().equals(fromStorage.getNamenodelimitcpu())
	    			|| isNull(primary.getSpec().getNamenodelimitcpu()) && nonNull(fromStorage.getNamenodelimitcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getNamenoderequestmemory()) 
	    			&& !primary.getSpec().getNamenoderequestmemory().equals(fromStorage.getNamenoderequestmemory())
	    			|| isNull(primary.getSpec().getNamenoderequestmemory()) && nonNull(fromStorage.getNamenoderequestmemory())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getNamenodelimitmemory()) 
	    			&& !primary.getSpec().getNamenodelimitmemory().equals(fromStorage.getNamenodelimitmemory())
	    			|| isNull(primary.getSpec().getNamenodelimitmemory()) && nonNull(fromStorage.getNamenodelimitmemory())) {
	    		return false;
	    	}
    	} else if(daemonSetName.equals(primary.getMetadata().getName() + HYPHEN + DATANODE)) {
    		if(nonNull(primary.getSpec().getDatanodeimage()) 
	    			&& !primary.getSpec().getDatanodeimage().equals(fromStorage.getDatanodeimage())
	    			|| isNull(primary.getSpec().getDatanodeimage()) && nonNull(fromStorage.getDatanodeimage())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getDatanoderequestcpu()) 
	    			&& !primary.getSpec().getDatanoderequestcpu().equals(fromStorage.getDatanoderequestcpu())
	    			|| isNull(primary.getSpec().getDatanoderequestcpu()) && nonNull(fromStorage.getDatanoderequestcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getDatanodelimitcpu()) 
	    			&& !primary.getSpec().getDatanodelimitcpu().equals(fromStorage.getDatanodelimitcpu())
	    			|| isNull(primary.getSpec().getDatanodelimitcpu()) && nonNull(fromStorage.getDatanodelimitcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getDatanoderequestmemory()) 
	    			&& !primary.getSpec().getDatanoderequestmemory().equals(fromStorage.getDatanoderequestmemory())
	    			|| isNull(primary.getSpec().getDatanoderequestmemory()) && nonNull(fromStorage.getDatanoderequestmemory())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getDatanodelimitmemory()) 
	    			&& !primary.getSpec().getDatanodelimitmemory().equals(fromStorage.getDatanodelimitmemory())
	    			|| isNull(primary.getSpec().getDatanodelimitmemory()) && nonNull(fromStorage.getDatanodelimitmemory())) {
	    		return false;
	    	}
    	}
		return true;
	}
    
    protected boolean statefulSetNotToBeReconciled(DatasamudayaOperatorCustomResource primary, DatasamudayaOperatorSpec fromStorage, String statefulSetName) {
    	if(isNull(fromStorage)) {
    		return true;
    	}
    	else if(statefulSetName.equals(primary.getMetadata().getName() + HYPHEN + ZOOKEEPER)) {
    		if(nonNull(primary.getSpec().getZkimage()) 
	    			&& !primary.getSpec().getZkimage().equals(fromStorage.getZkimage())
	    			|| isNull(primary.getSpec().getZkimage()) && nonNull(fromStorage.getZkimage())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getZkrequestcpu()) 
	    			&& !primary.getSpec().getZkrequestcpu().equals(fromStorage.getZkrequestcpu())
	    			|| isNull(primary.getSpec().getZkrequestcpu()) && nonNull(fromStorage.getZkrequestcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getZklimitcpu()) 
	    			&& !primary.getSpec().getZklimitcpu().equals(fromStorage.getZklimitcpu())
	    			|| isNull(primary.getSpec().getZklimitcpu()) && nonNull(fromStorage.getZklimitcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getZkrequestmemory()) 
	    			&& !primary.getSpec().getZkrequestmemory().equals(fromStorage.getZkrequestmemory())
	    			|| isNull(primary.getSpec().getZkrequestmemory()) && nonNull(fromStorage.getZkrequestmemory())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getZklimitmemory()) 
	    			&& !primary.getSpec().getZklimitmemory().equals(fromStorage.getZklimitmemory())
	    			|| isNull(primary.getSpec().getZklimitmemory()) && nonNull(fromStorage.getZklimitmemory())) {
	    		return false;
	    	}
    	} else if(statefulSetName.equals(primary.getMetadata().getName() + HYPHEN + STANDALONE)) {
    		if(nonNull(primary.getSpec().getSaimage()) 
	    			&& !primary.getSpec().getSaimage().equals(fromStorage.getSaimage())
	    			|| isNull(primary.getSpec().getSaimage()) && nonNull(fromStorage.getSaimage())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getSarequestcpu()) 
	    			&& !primary.getSpec().getSarequestcpu().equals(fromStorage.getSarequestcpu())
	    			|| isNull(primary.getSpec().getSarequestcpu()) && nonNull(fromStorage.getSarequestcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getSalimitcpu()) 
	    			&& !primary.getSpec().getSalimitcpu().equals(fromStorage.getSalimitcpu())
	    			|| isNull(primary.getSpec().getSalimitcpu()) && nonNull(fromStorage.getSalimitcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getSarequestmemory()) 
	    			&& !primary.getSpec().getSarequestmemory().equals(fromStorage.getSarequestmemory())
	    			|| isNull(primary.getSpec().getSarequestmemory()) && nonNull(fromStorage.getSarequestmemory())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getSalimitmemory()) 
	    			&& !primary.getSpec().getSalimitmemory().equals(fromStorage.getSalimitmemory())
	    			|| isNull(primary.getSpec().getSalimitmemory()) && nonNull(fromStorage.getSalimitmemory())) {
	    		return false;
	    	}
    	} else if(statefulSetName.equals(primary.getMetadata().getName() + HYPHEN + WORKER)) {
    		if(primary.getSpec().getNumberofworkers() != fromStorage.getNumberofworkers()) {
	    		return false;
	    	}else if(nonNull(primary.getSpec().getContainerimage()) 
	    			&& !primary.getSpec().getContainerimage().equals(fromStorage.getContainerimage())
	    			|| isNull(primary.getSpec().getContainerimage()) && nonNull(fromStorage.getContainerimage())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getContainerrequestcpu()) 
	    			&& !primary.getSpec().getContainerrequestcpu().equals(fromStorage.getContainerrequestcpu())
	    			|| isNull(primary.getSpec().getContainerrequestcpu()) && nonNull(fromStorage.getContainerrequestcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getContainerlimitcpu()) 
	    			&& !primary.getSpec().getContainerlimitcpu().equals(fromStorage.getContainerlimitcpu())
	    			|| isNull(primary.getSpec().getContainerlimitcpu()) && nonNull(fromStorage.getContainerlimitcpu())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getContainerrequestmemory()) 
	    			&& !primary.getSpec().getContainerrequestmemory().equals(fromStorage.getContainerrequestmemory())
	    			|| isNull(primary.getSpec().getContainerrequestmemory()) && nonNull(fromStorage.getContainerrequestmemory())) {
	    		return false;
	    	} else if(nonNull(primary.getSpec().getContainerlimitmemory()) 
	    			&& !primary.getSpec().getContainerlimitmemory().equals(fromStorage.getContainerlimitmemory())
	    			|| isNull(primary.getSpec().getContainerlimitmemory()) && nonNull(fromStorage.getContainerlimitmemory())) {
	    		return false;
	    	}
    	}
		return true;
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
      
      InformerEventSource<ServiceAccount, DatasamudayaOperatorCustomResource> sas =
              new InformerEventSource<>(InformerConfiguration.from(ServiceAccount.class, context)
                  .build(), context);
      
      InformerEventSource<Role, DatasamudayaOperatorCustomResource> clusterRoles =
              new InformerEventSource<>(InformerConfiguration.from(Role.class, context)
                  .build(), context);
      
      InformerEventSource<RoleBinding, DatasamudayaOperatorCustomResource> clusterRoleBindings =
              new InformerEventSource<>(InformerConfiguration.from(RoleBinding.class, context)
                  .build(), context);

      return Map.of(STATEFULSET_MAP_EVENT_SOURCE, ssies, 
    		  SERVICE_MAP_EVENT_SOURCE, svces, 
    		  DAEMONSET_MAP_EVENT_SOURCE, daemonsets,
    		  SERVICEACCOUNT_MAP_EVENT_SOURCE, sas,
			  ROLE_MAP_EVENT_SOURCE, clusterRoles,
			  ROLEBINDINGS_MAP_EVENT_SOURCE, clusterRoleBindings
    		  );
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
		context.getClient().rbac().roleBindings().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+ROLEBINDINGNAME).delete();
		context.getClient().rbac().roles().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+ROLENAME).delete();
		context.getClient().serviceAccounts().inNamespace(primary.getMetadata().getNamespace())
		.withName(primary.getMetadata().getName()+HYPHEN+SERVICEACCOUNTNAME).delete();
		return DeleteControl.defaultDelete();
	}
}
