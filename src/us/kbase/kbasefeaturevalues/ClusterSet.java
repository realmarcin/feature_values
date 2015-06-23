
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
 * <p>Original spec-file type: ClusterSet</p>
 * <pre>
 * One-dimensional cluster set.
 * type - which dimension these clusters were constructed for.
 * report - information collected during cluster construction.
 * @optional original_data report
 *   @meta ws type
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "type",
    "clusters",
    "original_data",
    "report"
})
public class ClusterSet {

    @JsonProperty("type")
    private java.lang.String type;
    @JsonProperty("clusters")
    private List<Map<String, Long>> clusters;
    @JsonProperty("original_data")
    private java.lang.String originalData;
    /**
     * <p>Original spec-file type: AnalysisReport</p>
     * <pre>
     * This object is created during upload of matrices.
     * </pre>
     * 
     */
    @JsonProperty("report")
    private AnalysisReport report;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("type")
    public java.lang.String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(java.lang.String type) {
        this.type = type;
    }

    public ClusterSet withType(java.lang.String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("clusters")
    public List<Map<String, Long>> getClusters() {
        return clusters;
    }

    @JsonProperty("clusters")
    public void setClusters(List<Map<String, Long>> clusters) {
        this.clusters = clusters;
    }

    public ClusterSet withClusters(List<Map<String, Long>> clusters) {
        this.clusters = clusters;
        return this;
    }

    @JsonProperty("original_data")
    public java.lang.String getOriginalData() {
        return originalData;
    }

    @JsonProperty("original_data")
    public void setOriginalData(java.lang.String originalData) {
        this.originalData = originalData;
    }

    public ClusterSet withOriginalData(java.lang.String originalData) {
        this.originalData = originalData;
        return this;
    }

    /**
     * <p>Original spec-file type: AnalysisReport</p>
     * <pre>
     * This object is created during upload of matrices.
     * </pre>
     * 
     */
    @JsonProperty("report")
    public AnalysisReport getReport() {
        return report;
    }

    /**
     * <p>Original spec-file type: AnalysisReport</p>
     * <pre>
     * This object is created during upload of matrices.
     * </pre>
     * 
     */
    @JsonProperty("report")
    public void setReport(AnalysisReport report) {
        this.report = report;
    }

    public ClusterSet withReport(AnalysisReport report) {
        this.report = report;
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
        return ((((((((((("ClusterSet"+" [type=")+ type)+", clusters=")+ clusters)+", originalData=")+ originalData)+", report=")+ report)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
