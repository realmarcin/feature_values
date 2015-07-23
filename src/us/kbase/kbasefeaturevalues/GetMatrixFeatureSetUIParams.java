
package us.kbase.kbasefeaturevalues;

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
 * <p>Original spec-file type: GetMatrixFeatureSetUIParams</p>
 * <pre>
 * [PSN; Jul 22, 2015]    
 * Parameters to retrieve FeatureSetUI                
 * Either mtx_id and feature_indeces (or feature_ids), or cluster_set_id and cluster_index should be indicated
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "mtx_id",
    "feature_indeces",
    "feature_ids",
    "cluster_set_id",
    "cluster_index"
})
public class GetMatrixFeatureSetUIParams {

    @JsonProperty("mtx_id")
    private java.lang.String mtxId;
    @JsonProperty("feature_indeces")
    private List<Long> featureIndeces;
    @JsonProperty("feature_ids")
    private List<String> featureIds;
    @JsonProperty("cluster_set_id")
    private java.lang.String clusterSetId;
    @JsonProperty("cluster_index")
    private java.lang.Long clusterIndex;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("mtx_id")
    public java.lang.String getMtxId() {
        return mtxId;
    }

    @JsonProperty("mtx_id")
    public void setMtxId(java.lang.String mtxId) {
        this.mtxId = mtxId;
    }

    public GetMatrixFeatureSetUIParams withMtxId(java.lang.String mtxId) {
        this.mtxId = mtxId;
        return this;
    }

    @JsonProperty("feature_indeces")
    public List<Long> getFeatureIndeces() {
        return featureIndeces;
    }

    @JsonProperty("feature_indeces")
    public void setFeatureIndeces(List<Long> featureIndeces) {
        this.featureIndeces = featureIndeces;
    }

    public GetMatrixFeatureSetUIParams withFeatureIndeces(List<Long> featureIndeces) {
        this.featureIndeces = featureIndeces;
        return this;
    }

    @JsonProperty("feature_ids")
    public List<String> getFeatureIds() {
        return featureIds;
    }

    @JsonProperty("feature_ids")
    public void setFeatureIds(List<String> featureIds) {
        this.featureIds = featureIds;
    }

    public GetMatrixFeatureSetUIParams withFeatureIds(List<String> featureIds) {
        this.featureIds = featureIds;
        return this;
    }

    @JsonProperty("cluster_set_id")
    public java.lang.String getClusterSetId() {
        return clusterSetId;
    }

    @JsonProperty("cluster_set_id")
    public void setClusterSetId(java.lang.String clusterSetId) {
        this.clusterSetId = clusterSetId;
    }

    public GetMatrixFeatureSetUIParams withClusterSetId(java.lang.String clusterSetId) {
        this.clusterSetId = clusterSetId;
        return this;
    }

    @JsonProperty("cluster_index")
    public java.lang.Long getClusterIndex() {
        return clusterIndex;
    }

    @JsonProperty("cluster_index")
    public void setClusterIndex(java.lang.Long clusterIndex) {
        this.clusterIndex = clusterIndex;
    }

    public GetMatrixFeatureSetUIParams withClusterIndex(java.lang.Long clusterIndex) {
        this.clusterIndex = clusterIndex;
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
        return ((((((((((((("GetMatrixFeatureSetUIParams"+" [mtxId=")+ mtxId)+", featureIndeces=")+ featureIndeces)+", featureIds=")+ featureIds)+", clusterSetId=")+ clusterSetId)+", clusterIndex=")+ clusterIndex)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
