package io.fabric8.kubeflix.discovery;

import static org.slf4j.LoggerFactory.getLogger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.netflix.turbine.discovery.Instance;
import com.netflix.turbine.discovery.InstanceDiscovery;

import io.fabric8.kubernetes.api.model.EndpointAddress;
import io.fabric8.kubernetes.api.model.EndpointSubset;
import io.fabric8.kubernetes.api.model.Endpoints;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;

public class OpenShiftDiscovery implements InstanceDiscovery {

    private static final Logger LOGGER = getLogger(OpenShiftDiscovery.class);

    private static final String HYSTRIX_ENABLED = "hystrix.enabled";
    private static final String HYSTRIX_CLUSTER = "hystrix.cluster";

    private static final String DEFAULT = "default";
    private static final Map<String, String> DEFAULT_LABELS = new HashMap<String, String>();

    static {
        DEFAULT_LABELS.put(HYSTRIX_ENABLED, "true");
    }

    private final OpenShiftClient client;
    private final Map<String, String> labels;

    public OpenShiftDiscovery() {
        this(new DefaultOpenShiftClient(), DEFAULT_LABELS);
    }

    public OpenShiftDiscovery(OpenShiftClient client, Map<String, String> labels) {
        this.client = client;
        this.labels = labels;
    }

    public Collection<Instance>  getInstanceList() throws Exception {
        List<Instance> result = new ArrayList<Instance>();
        List<Endpoints> endpoints = client.endpoints().withLabels(labels).list().getItems();
        LOGGER.info(endpoints.size() + " endpoints discovered.");
        
		for (Endpoints endpoint : endpoints) {
            try {
                List<Instance> instances = toInstances(endpoint);
                LOGGER.info("Endpoint instances added: " + instances);
                
				result.addAll(instances);
            } catch (Throwable t) {
                LOGGER.error("Error processing endpoint", t);
            }
        }
        return result;
    }

    private static List<Instance> toInstances(Endpoints e) {
        List<Instance> result = new ArrayList<Instance>();
        for (EndpointSubset subset : e.getSubsets()) {
            String clusterName = e.getMetadata().getLabels().containsKey(HYSTRIX_CLUSTER) ?
                    e.getMetadata().getLabels().get(HYSTRIX_CLUSTER) :
                    DEFAULT;
            for (EndpointAddress address : subset.getAddresses()) {
                result.add(new Instance(address.getIp(), clusterName, true));
            }
        }
        return result;
    }
}
