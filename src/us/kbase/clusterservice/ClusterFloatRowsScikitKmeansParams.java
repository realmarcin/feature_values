
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
 * <p>Original spec-file type: ClusterFloatRowsScikitKmeansParams</p>
 * <pre>
 * values - two dimensional array values[row][col], where first index 
 *     (row) goes vertically over outer list and (col) goes horizontally 
 *     along each each internal list.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "values"
})
public class ClusterFloatRowsScikitKmeansParams {

    @JsonProperty("values")
    private List<List<Double>> values;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("values")
    public List<List<Double>> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<List<Double>> values) {
        this.values = values;
    }

    public ClusterFloatRowsScikitKmeansParams withValues(List<List<Double>> values) {
        this.values = values;
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
        return ((((("ClusterFloatRowsScikitKmeansParams"+" [values=")+ values)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
