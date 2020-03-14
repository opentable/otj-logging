package com.opentable.logging;

import java.util.Optional;

import com.opentable.service.K8sInfo;

public class KubernetesLogHolder {
    public static final KubernetesLogHolder EMPTY = new KubernetesLogHolder(Optional.empty());

    private final K8sInfo k8sInfo;
    public KubernetesLogHolder(Optional<K8sInfo> k8sInfo) {
        this.k8sInfo = k8sInfo.orElse(new K8sInfo());
    }

    public String getClusterName() {
        return k8sInfo.getClusterName().orElse(null);
    }

    public String getNameSpace() {
        return k8sInfo.getNamespace().orElse(null);
    }

    public String getNodeHost() {
        return k8sInfo.getNodeHost().orElse(null);
    }

    public String getPodName() {
        return k8sInfo.getPodName().orElse(null);
    }

    public String getServiceName() {
        return k8sInfo.getServiceName().orElse(null);
    }
}
