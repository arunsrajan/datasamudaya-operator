package com.github.datasamudaya.operator;

import static com.github.datasamudaya.operator.DataSamudayaOperatorConstants.*;

import static java.util.Objects.nonNull;

import java.util.Map;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.ObjectMetaBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;

@KubernetesDependent
public class ConfigMapDependentResource
		extends CRUDKubernetesDependentResource<ConfigMap, DatasamudayaOperatorCustomResource> {

	public ConfigMapDependentResource() {
		super(ConfigMap.class);
	}

	@Override
	protected ConfigMap desired(DatasamudayaOperatorCustomResource primary,
			Context<DatasamudayaOperatorCustomResource> context) {
		return new ConfigMapBuilder()
				.withMetadata(new ObjectMetaBuilder().withName(primary.getMetadata().getName())
						.withNamespace(primary.getMetadata().getNamespace()).build())
				.withData(Map.of(NAMESPACE,
						nonNull(primary.getSpec().getNamespace()) ? primary.getSpec().getNamespace()
								: NAMESPACE_DEFAULT,
						ZKLIMITCPU,
						nonNull(primary.getSpec().getZklimitcpu()) ? primary.getSpec().getZklimitcpu()
								: ZKLIMITCPU_DEFAULT,
						ZKLIMITMEMORY,
						nonNull(primary.getSpec().getZklimitmemory()) ? primary.getSpec().getZklimitmemory()
								: ZKLIMITMEMORY_DEFAULT,
						ZKREQUESTCPU,
						nonNull(primary.getSpec().getZkrequestcpu()) ? primary.getSpec().getZkrequestcpu()
								: ZKREQUESTCPU_DEFAULT,
						ZKREQUESTMEMORY,
						nonNull(primary.getSpec().getZkrequestmemory()) ? primary.getSpec().getZkrequestmemory()
								: ZKREQUESTMEMORY_DEFAULT,
						PODCIDRNODEMAPPINGENABLED,
						nonNull(primary.getSpec().getPodcidrnodemappingenabled()) ? primary.getSpec().getPodcidrnodemappingenabled()
								: PODCIDRNODEMAPPINGENABLED_DEFAULT))
				.build();
	}
}