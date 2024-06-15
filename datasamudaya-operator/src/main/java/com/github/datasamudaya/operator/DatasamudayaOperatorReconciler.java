package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.*;

import java.util.Map;

import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.javaoperatorsdk.operator.api.config.informer.InformerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceContext;
import io.javaoperatorsdk.operator.api.reconciler.EventSourceInitializer;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.javaoperatorsdk.operator.processing.event.source.EventSource;
import io.javaoperatorsdk.operator.processing.event.source.informer.InformerEventSource;

@ControllerConfiguration(dependents = {@Dependent(type = ZookeeperStatefulSetDependentResource.class,
										useEventSourceWithName = STATEFULSET_MAP_EVENT_SOURCE),
										@Dependent(type = StandaloneStatefulSetDependentResource.class,
										useEventSourceWithName = STATEFULSET_MAP_EVENT_SOURCE),
										@Dependent(type = ContainerStatefulSetDependentResource.class,
										useEventSourceWithName = STATEFULSET_MAP_EVENT_SOURCE),
										@Dependent(type = StandaloneServiceDependentResource.class,
										useEventSourceWithName = SERVICE_MAP_EVENT_SOURCE),
										@Dependent(type = ZookeeperServiceDependentResource.class,
										useEventSourceWithName = SERVICE_MAP_EVENT_SOURCE)})
public class DatasamudayaOperatorReconciler implements Reconciler<DatasamudayaOperatorCustomResource>
														,EventSourceInitializer<DatasamudayaOperatorCustomResource>{

    public UpdateControl<DatasamudayaOperatorCustomResource> reconcile(DatasamudayaOperatorCustomResource primary,
                                                     Context<DatasamudayaOperatorCustomResource> context) {
        return UpdateControl.updateResource(primary);
    }
    
    @Override
    public Map<String, EventSource> prepareEventSources(
        EventSourceContext<DatasamudayaOperatorCustomResource> context) {
      InformerEventSource<StatefulSet, DatasamudayaOperatorCustomResource> ssies =
          new InformerEventSource<>(InformerConfiguration.from(StatefulSet.class, context)
              .build(), context);
      
      InformerEventSource<Service, DatasamudayaOperatorCustomResource> svcies =
              new InformerEventSource<>(InformerConfiguration.from(Service.class, context)
                  .build(), context);

      return Map.of(STATEFULSET_MAP_EVENT_SOURCE, ssies, SERVICE_MAP_EVENT_SOURCE, svcies);
    }
}
