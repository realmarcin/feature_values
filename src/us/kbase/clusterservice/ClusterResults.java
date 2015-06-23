
package us.kbase.clusterservice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: ClusterResults</p>
 * <pre>
 * clusters - set of lists consisting of positions of rows from original
 *     array.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "messages",
    "warnings",
    "errors",
    "cluster_labels"
})
public class ClusterResults {

    @JsonProperty("messages")
    private List<String> messages;
    @JsonProperty("warnings")
    private List<String> warnings;
    @JsonProperty("errors")
    private List<String> errors;
    @JsonProperty("cluster_labels")
    private List<Long> clusterLabels;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("messages")
    public List<String> getMessages() {
        return messages;
    }

    @JsonProperty("messages")
    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public ClusterResults withMessages(List<String> messages) {
        this.messages = messages;
        return this;
    }

    @JsonProperty("warnings")
    public List<String> getWarnings() {
        return warnings;
    }

    @JsonProperty("warnings")
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    public ClusterResults withWarnings(List<String> warnings) {
        this.warnings = warnings;
        return this;
    }

    @JsonProperty("errors")
    public List<String> getErrors() {
        return errors;
    }

    @JsonProperty("errors")
    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public ClusterResults withErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    @JsonProperty("cluster_labels")
    public List<Long> getClusterLabels() {
        return clusterLabels;
    }

    @JsonProperty("cluster_labels")
    public void setClusterLabels(List<Long> clusterLabels) {
        this.clusterLabels = clusterLabels;
    }

    public ClusterResults withClusterLabels(List<Long> clusterLabels) {
        this.clusterLabels = clusterLabels;
        return this;
    }

    @JsonAnyGetter
    public Map<java.lang.String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(java.lang.String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public java.lang.String toString() {
        return ((((((((((("ClusterResults"+" [messages=")+ messages)+", warnings=")+ warnings)+", errors=")+ errors)+", clusterLabels=")+ clusterLabels)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
