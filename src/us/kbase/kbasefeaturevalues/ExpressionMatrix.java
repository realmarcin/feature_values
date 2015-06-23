
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
 * <p>Original spec-file type: ExpressionMatrix</p>
 * <pre>
 * type - ? level, ratio, log-ratio
 * scale - ? probably: raw, loge, log2, log10
 * col_normalization - mean_center, median_center, mode_center, zscore
 * row_normalization - mean_center, median_center, mode_center, zscore
 * data - contains values for (feature,condition) pairs, where 
 *     features correspond to columns and conditions are rows.
 * @optional genome_ref feature_mapping conditionset_ref condition_mapping report
 * @meta ws type genome_ref
 * @meta ws length(data.row_ids) as feature_count
 * @meta ws length(data.col_ids) as condition_count
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "type",
    "scale",
    "row_normalization",
    "col_normalization",
    "genome_ref",
    "feature_mapping",
    "conditionset_ref",
    "condition_mapping",
    "data",
    "report"
})
public class ExpressionMatrix {

    @JsonProperty("type")
    private java.lang.String type;
    @JsonProperty("scale")
    private Double scale;
    @JsonProperty("row_normalization")
    private java.lang.String rowNormalization;
    @JsonProperty("col_normalization")
    private java.lang.String colNormalization;
    @JsonProperty("genome_ref")
    private java.lang.String genomeRef;
    @JsonProperty("feature_mapping")
    private Map<String, String> featureMapping;
    @JsonProperty("conditionset_ref")
    private java.lang.String conditionsetRef;
    @JsonProperty("condition_mapping")
    private Map<String, String> conditionMapping;
    /**
     * <p>Original spec-file type: FloatMatrix2D</p>
     * <pre>
     * row_ids - ids for rows.
     * col_ids - ids for columns.
     * values - two dimensional array values[row][col], where first index 
     *     (row) goes vertically over outer list and (col) goes horizontally 
     *     along each each internal list.
     * @meta ws length(row_ids) as n_rows
     * @meta ws length(col_ids) as n_cols
     * </pre>
     * 
     */
    @JsonProperty("data")
    private FloatMatrix2D data;
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

    public ExpressionMatrix withType(java.lang.String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("scale")
    public Double getScale() {
        return scale;
    }

    @JsonProperty("scale")
    public void setScale(Double scale) {
        this.scale = scale;
    }

    public ExpressionMatrix withScale(Double scale) {
        this.scale = scale;
        return this;
    }

    @JsonProperty("row_normalization")
    public java.lang.String getRowNormalization() {
        return rowNormalization;
    }

    @JsonProperty("row_normalization")
    public void setRowNormalization(java.lang.String rowNormalization) {
        this.rowNormalization = rowNormalization;
    }

    public ExpressionMatrix withRowNormalization(java.lang.String rowNormalization) {
        this.rowNormalization = rowNormalization;
        return this;
    }

    @JsonProperty("col_normalization")
    public java.lang.String getColNormalization() {
        return colNormalization;
    }

    @JsonProperty("col_normalization")
    public void setColNormalization(java.lang.String colNormalization) {
        this.colNormalization = colNormalization;
    }

    public ExpressionMatrix withColNormalization(java.lang.String colNormalization) {
        this.colNormalization = colNormalization;
        return this;
    }

    @JsonProperty("genome_ref")
    public java.lang.String getGenomeRef() {
        return genomeRef;
    }

    @JsonProperty("genome_ref")
    public void setGenomeRef(java.lang.String genomeRef) {
        this.genomeRef = genomeRef;
    }

    public ExpressionMatrix withGenomeRef(java.lang.String genomeRef) {
        this.genomeRef = genomeRef;
        return this;
    }

    @JsonProperty("feature_mapping")
    public Map<String, String> getFeatureMapping() {
        return featureMapping;
    }

    @JsonProperty("feature_mapping")
    public void setFeatureMapping(Map<String, String> featureMapping) {
        this.featureMapping = featureMapping;
    }

    public ExpressionMatrix withFeatureMapping(Map<String, String> featureMapping) {
        this.featureMapping = featureMapping;
        return this;
    }

    @JsonProperty("conditionset_ref")
    public java.lang.String getConditionsetRef() {
        return conditionsetRef;
    }

    @JsonProperty("conditionset_ref")
    public void setConditionsetRef(java.lang.String conditionsetRef) {
        this.conditionsetRef = conditionsetRef;
    }

    public ExpressionMatrix withConditionsetRef(java.lang.String conditionsetRef) {
        this.conditionsetRef = conditionsetRef;
        return this;
    }

    @JsonProperty("condition_mapping")
    public Map<String, String> getConditionMapping() {
        return conditionMapping;
    }

    @JsonProperty("condition_mapping")
    public void setConditionMapping(Map<String, String> conditionMapping) {
        this.conditionMapping = conditionMapping;
    }

    public ExpressionMatrix withConditionMapping(Map<String, String> conditionMapping) {
        this.conditionMapping = conditionMapping;
        return this;
    }

    /**
     * <p>Original spec-file type: FloatMatrix2D</p>
     * <pre>
     * row_ids - ids for rows.
     * col_ids - ids for columns.
     * values - two dimensional array values[row][col], where first index 
     *     (row) goes vertically over outer list and (col) goes horizontally 
     *     along each each internal list.
     * @meta ws length(row_ids) as n_rows
     * @meta ws length(col_ids) as n_cols
     * </pre>
     * 
     */
    @JsonProperty("data")
    public FloatMatrix2D getData() {
        return data;
    }

    /**
     * <p>Original spec-file type: FloatMatrix2D</p>
     * <pre>
     * row_ids - ids for rows.
     * col_ids - ids for columns.
     * values - two dimensional array values[row][col], where first index 
     *     (row) goes vertically over outer list and (col) goes horizontally 
     *     along each each internal list.
     * @meta ws length(row_ids) as n_rows
     * @meta ws length(col_ids) as n_cols
     * </pre>
     * 
     */
    @JsonProperty("data")
    public void setData(FloatMatrix2D data) {
        this.data = data;
    }

    public ExpressionMatrix withData(FloatMatrix2D data) {
        this.data = data;
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

    public ExpressionMatrix withReport(AnalysisReport report) {
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
        return ((((((((((((((((((((((("ExpressionMatrix"+" [type=")+ type)+", scale=")+ scale)+", rowNormalization=")+ rowNormalization)+", colNormalization=")+ colNormalization)+", genomeRef=")+ genomeRef)+", featureMapping=")+ featureMapping)+", conditionsetRef=")+ conditionsetRef)+", conditionMapping=")+ conditionMapping)+", data=")+ data)+", report=")+ report)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
