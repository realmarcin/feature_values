
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
 * <p>Original spec-file type: PairwiseMatrixStat</p>
 * <pre>
 * To represent a pairwise matrix with sprecalculated statistics. 
 * It can be used to represent pairwise correlation for a set of genes.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "indeces",
    "size",
    "values",
    "avgs",
    "mins",
    "maxs",
    "stds",
    "missing_values"
})
public class PairwiseMatrixStat {

    @JsonProperty("indeces")
    private List<Long> indeces;
    @JsonProperty("size")
    private java.lang.Long size;
    @JsonProperty("values")
    private List<List<Double>> values;
    @JsonProperty("avgs")
    private List<Double> avgs;
    @JsonProperty("mins")
    private List<Double> mins;
    @JsonProperty("maxs")
    private List<Double> maxs;
    @JsonProperty("stds")
    private List<Double> stds;
    @JsonProperty("missing_values")
    private List<Long> missingValues;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("indeces")
    public List<Long> getIndeces() {
        return indeces;
    }

    @JsonProperty("indeces")
    public void setIndeces(List<Long> indeces) {
        this.indeces = indeces;
    }

    public PairwiseMatrixStat withIndeces(List<Long> indeces) {
        this.indeces = indeces;
        return this;
    }

    @JsonProperty("size")
    public java.lang.Long getSize() {
        return size;
    }

    @JsonProperty("size")
    public void setSize(java.lang.Long size) {
        this.size = size;
    }

    public PairwiseMatrixStat withSize(java.lang.Long size) {
        this.size = size;
        return this;
    }

    @JsonProperty("values")
    public List<List<Double>> getValues() {
        return values;
    }

    @JsonProperty("values")
    public void setValues(List<List<Double>> values) {
        this.values = values;
    }

    public PairwiseMatrixStat withValues(List<List<Double>> values) {
        this.values = values;
        return this;
    }

    @JsonProperty("avgs")
    public List<Double> getAvgs() {
        return avgs;
    }

    @JsonProperty("avgs")
    public void setAvgs(List<Double> avgs) {
        this.avgs = avgs;
    }

    public PairwiseMatrixStat withAvgs(List<Double> avgs) {
        this.avgs = avgs;
        return this;
    }

    @JsonProperty("mins")
    public List<Double> getMins() {
        return mins;
    }

    @JsonProperty("mins")
    public void setMins(List<Double> mins) {
        this.mins = mins;
    }

    public PairwiseMatrixStat withMins(List<Double> mins) {
        this.mins = mins;
        return this;
    }

    @JsonProperty("maxs")
    public List<Double> getMaxs() {
        return maxs;
    }

    @JsonProperty("maxs")
    public void setMaxs(List<Double> maxs) {
        this.maxs = maxs;
    }

    public PairwiseMatrixStat withMaxs(List<Double> maxs) {
        this.maxs = maxs;
        return this;
    }

    @JsonProperty("stds")
    public List<Double> getStds() {
        return stds;
    }

    @JsonProperty("stds")
    public void setStds(List<Double> stds) {
        this.stds = stds;
    }

    public PairwiseMatrixStat withStds(List<Double> stds) {
        this.stds = stds;
        return this;
    }

    @JsonProperty("missing_values")
    public List<Long> getMissingValues() {
        return missingValues;
    }

    @JsonProperty("missing_values")
    public void setMissingValues(List<Long> missingValues) {
        this.missingValues = missingValues;
    }

    public PairwiseMatrixStat withMissingValues(List<Long> missingValues) {
        this.missingValues = missingValues;
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
        return ((((((((((((((((((("PairwiseMatrixStat"+" [indeces=")+ indeces)+", size=")+ size)+", values=")+ values)+", avgs=")+ avgs)+", mins=")+ mins)+", maxs=")+ maxs)+", stds=")+ stds)+", missingValues=")+ missingValues)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
