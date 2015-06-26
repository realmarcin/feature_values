
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
 * <p>Original spec-file type: EstimateKParams</p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "input_matrix",
    "out_workspace",
    "out_estimate_result"
})
public class EstimateKParams {

    @JsonProperty("input_matrix")
    private String inputMatrix;
    @JsonProperty("out_workspace")
    private String outWorkspace;
    @JsonProperty("out_estimate_result")
    private String outEstimateResult;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("input_matrix")
    public String getInputMatrix() {
        return inputMatrix;
    }

    @JsonProperty("input_matrix")
    public void setInputMatrix(String inputMatrix) {
        this.inputMatrix = inputMatrix;
    }

    public EstimateKParams withInputMatrix(String inputMatrix) {
        this.inputMatrix = inputMatrix;
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

    public EstimateKParams withOutWorkspace(String outWorkspace) {
        this.outWorkspace = outWorkspace;
        return this;
    }

    @JsonProperty("out_estimate_result")
    public String getOutEstimateResult() {
        return outEstimateResult;
    }

    @JsonProperty("out_estimate_result")
    public void setOutEstimateResult(String outEstimateResult) {
        this.outEstimateResult = outEstimateResult;
    }

    public EstimateKParams withOutEstimateResult(String outEstimateResult) {
        this.outEstimateResult = outEstimateResult;
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
        return ((((((((("EstimateKParams"+" [inputMatrix=")+ inputMatrix)+", outWorkspace=")+ outWorkspace)+", outEstimateResult=")+ outEstimateResult)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
