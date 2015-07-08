
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
import us.kbase.kbasefeaturevalues.AnalysisReport;


/**
 * <p>Original spec-file type: ClusterResults</p>
 * <pre>
 * clusters - set of lists consisting of positions of rows from original
 *     matrix;
 * dendrogram - tree in Newick format (node names are positions of 
 *     rows from original matrix).
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "report",
    "cluster_labels",
    "dendrogram"
})
public class ClusterResults {

    /**
     * <p>Original spec-file type: AnalysisReport</p>
     * <pre>
     * A basic report object used for a variety of cases to mark informational
     * messages, warnings, and errors related to processing or quality control
     * checks of raw data.
     * </pre>
     * 
     */
    @JsonProperty("report")
    private AnalysisReport report;
    @JsonProperty("cluster_labels")
    private List<Long> clusterLabels;
    @JsonProperty("dendrogram")
    private String dendrogram;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * <p>Original spec-file type: AnalysisReport</p>
     * <pre>
     * A basic report object used for a variety of cases to mark informational
     * messages, warnings, and errors related to processing or quality control
     * checks of raw data.
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
     * A basic report object used for a variety of cases to mark informational
     * messages, warnings, and errors related to processing or quality control
     * checks of raw data.
     * </pre>
     * 
     */
    @JsonProperty("report")
    public void setReport(AnalysisReport report) {
        this.report = report;
    }

    public ClusterResults withReport(AnalysisReport report) {
        this.report = report;
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

    @JsonProperty("dendrogram")
    public String getDendrogram() {
        return dendrogram;
    }

    @JsonProperty("dendrogram")
    public void setDendrogram(String dendrogram) {
        this.dendrogram = dendrogram;
    }

    public ClusterResults withDendrogram(String dendrogram) {
        this.dendrogram = dendrogram;
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
        return ((((((((("ClusterResults"+" [report=")+ report)+", clusterLabels=")+ clusterLabels)+", dendrogram=")+ dendrogram)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
