
package us.kbase.kbasefeaturevalues;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * <p>Original spec-file type: ClusterFeaturesParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "method",
    "input_data",
    "out_workspace",
    "out_cluster_id"
})
public class ClusterFeaturesParams {

    @JsonProperty("method")
    private String method;
    @JsonProperty("input_data")
    private String inputData;
    @JsonProperty("out_workspace")
    private String outWorkspace;
    @JsonProperty("out_cluster_id")
    private String outClusterId;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("method")
    public String getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(String method) {
        this.method = method;
    }

    public ClusterFeaturesParams withMethod(String method) {
        this.method = method;
        return this;
    }

    @JsonProperty("input_data")
    public String getInputData() {
        return inputData;
    }

    @JsonProperty("input_data")
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public ClusterFeaturesParams withInputData(String inputData) {
        this.inputData = inputData;
        return this;
    }

    @JsonProperty("out_workspace")
    public String getOutWorkspace() {
        return outWorkspace;
    }

    @JsonProperty("out_workspace")
    public void setOutWorkspace(String outWorkspace) {
        this.outWorkspace = outWorkspace;
    }

    public ClusterFeaturesParams withOutWorkspace(String outWorkspace) {
        this.outWorkspace = outWorkspace;
        return this;
    }

    @JsonProperty("out_cluster_id")
    public String getOutClusterId() {
        return outClusterId;
    }

    @JsonProperty("out_cluster_id")
    public void setOutClusterId(String outClusterId) {
        this.outClusterId = outClusterId;
    }

    public ClusterFeaturesParams withOutClusterId(String outClusterId) {
        this.outClusterId = outClusterId;
        return this;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperties(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    @Override
    public String toString() {
        return ((((((((((("ClusterFeaturesParams"+" [method=")+ method)+", inputData=")+ inputData)+", outWorkspace=")+ outWorkspace)+", outClusterId=")+ outClusterId)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
