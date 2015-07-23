
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
 * <p>Original spec-file type: MatrixUI</p>
 * <pre>
 * [PSN; Jul 22, 2015]
 * All info required for visualization of Matrix (ExpressionMatrix) object in the Matrix Viewer
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "mtx_descriptor",
    "row_descriptors",
    "column_descriptors",
    "row_stats",
    "column_stats"
})
public class MatrixUI {

    /**
     * <p>Original spec-file type: MatrixDescriptor</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * General info about matrix, including genome name that needs to be extracted from the genome object
     * </pre>
     * 
     */
    @JsonProperty("mtx_descriptor")
    private MatrixDescriptor mtxDescriptor;
    @JsonProperty("row_descriptors")
    private List<us.kbase.kbasefeaturevalues.ItemDescriptor> rowDescriptors;
    @JsonProperty("column_descriptors")
    private List<us.kbase.kbasefeaturevalues.ItemDescriptor> columnDescriptors;
    @JsonProperty("row_stats")
    private List<us.kbase.kbasefeaturevalues.ItemStat> rowStats;
    @JsonProperty("column_stats")
    private List<us.kbase.kbasefeaturevalues.ItemStat> columnStats;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * <p>Original spec-file type: MatrixDescriptor</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * General info about matrix, including genome name that needs to be extracted from the genome object
     * </pre>
     * 
     */
    @JsonProperty("mtx_descriptor")
    public MatrixDescriptor getMtxDescriptor() {
        return mtxDescriptor;
    }

    /**
     * <p>Original spec-file type: MatrixDescriptor</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * General info about matrix, including genome name that needs to be extracted from the genome object
     * </pre>
     * 
     */
    @JsonProperty("mtx_descriptor")
    public void setMtxDescriptor(MatrixDescriptor mtxDescriptor) {
        this.mtxDescriptor = mtxDescriptor;
    }

    public MatrixUI withMtxDescriptor(MatrixDescriptor mtxDescriptor) {
        this.mtxDescriptor = mtxDescriptor;
        return this;
    }

    @JsonProperty("row_descriptors")
    public List<us.kbase.kbasefeaturevalues.ItemDescriptor> getRowDescriptors() {
        return rowDescriptors;
    }

    @JsonProperty("row_descriptors")
    public void setRowDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> rowDescriptors) {
        this.rowDescriptors = rowDescriptors;
    }

    public MatrixUI withRowDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> rowDescriptors) {
        this.rowDescriptors = rowDescriptors;
        return this;
    }

    @JsonProperty("column_descriptors")
    public List<us.kbase.kbasefeaturevalues.ItemDescriptor> getColumnDescriptors() {
        return columnDescriptors;
    }

    @JsonProperty("column_descriptors")
    public void setColumnDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;
    }

    public MatrixUI withColumnDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> columnDescriptors) {
        this.columnDescriptors = columnDescriptors;
        return this;
    }

    @JsonProperty("row_stats")
    public List<us.kbase.kbasefeaturevalues.ItemStat> getRowStats() {
        return rowStats;
    }

    @JsonProperty("row_stats")
    public void setRowStats(List<us.kbase.kbasefeaturevalues.ItemStat> rowStats) {
        this.rowStats = rowStats;
    }

    public MatrixUI withRowStats(List<us.kbase.kbasefeaturevalues.ItemStat> rowStats) {
        this.rowStats = rowStats;
        return this;
    }

    @JsonProperty("column_stats")
    public List<us.kbase.kbasefeaturevalues.ItemStat> getColumnStats() {
        return columnStats;
    }

    @JsonProperty("column_stats")
    public void setColumnStats(List<us.kbase.kbasefeaturevalues.ItemStat> columnStats) {
        this.columnStats = columnStats;
    }

    public MatrixUI withColumnStats(List<us.kbase.kbasefeaturevalues.ItemStat> columnStats) {
        this.columnStats = columnStats;
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
        return ((((((((((((("MatrixUI"+" [mtxDescriptor=")+ mtxDescriptor)+", rowDescriptors=")+ rowDescriptors)+", columnDescriptors=")+ columnDescriptors)+", rowStats=")+ rowStats)+", columnStats=")+ columnStats)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
