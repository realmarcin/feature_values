
package us.kbase.clusterservice;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import us.kbase.kbasefeaturevalues.FloatMatrix2D;


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
    "k",
    "input_data"
})
public class ClusterFloatRowsScikitKmeansParams {

    @JsonProperty("k")
    private Long k;
    /**
     * <p>Original spec-file type: FloatMatrix2D</p>
     * <pre>
     * A simple 2D matrix of floating point numbers with labels/ids for rows and
     * columns.  The matrix is stored as a list of lists, with the outer list
     * containing rows, and the inner lists containing values for each column of
     * that row.  Row/Col ids should be unique.
     * row_ids - unique ids for rows.
     * col_ids - unique ids for columns.
     * values - two dimensional array indexed as: values[row][col]
     * @metadata ws length(row_ids) as n_rows
     * @metadata ws length(col_ids) as n_cols
     * </pre>
     * 
     */
    @JsonProperty("input_data")
    private FloatMatrix2D inputData;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("k")
    public Long getK() {
        return k;
    }

    @JsonProperty("k")
    public void setK(Long k) {
        this.k = k;
    }

    public ClusterFloatRowsScikitKmeansParams withK(Long k) {
        this.k = k;
        return this;
    }

    /**
     * <p>Original spec-file type: FloatMatrix2D</p>
     * <pre>
     * A simple 2D matrix of floating point numbers with labels/ids for rows and
     * columns.  The matrix is stored as a list of lists, with the outer list
     * containing rows, and the inner lists containing values for each column of
     * that row.  Row/Col ids should be unique.
     * row_ids - unique ids for rows.
     * col_ids - unique ids for columns.
     * values - two dimensional array indexed as: values[row][col]
     * @metadata ws length(row_ids) as n_rows
     * @metadata ws length(col_ids) as n_cols
     * </pre>
     * 
     */
    @JsonProperty("input_data")
    public FloatMatrix2D getInputData() {
        return inputData;
    }

    /**
     * <p>Original spec-file type: FloatMatrix2D</p>
     * <pre>
     * A simple 2D matrix of floating point numbers with labels/ids for rows and
     * columns.  The matrix is stored as a list of lists, with the outer list
     * containing rows, and the inner lists containing values for each column of
     * that row.  Row/Col ids should be unique.
     * row_ids - unique ids for rows.
     * col_ids - unique ids for columns.
     * values - two dimensional array indexed as: values[row][col]
     * @metadata ws length(row_ids) as n_rows
     * @metadata ws length(col_ids) as n_cols
     * </pre>
     * 
     */
    @JsonProperty("input_data")
    public void setInputData(FloatMatrix2D inputData) {
        this.inputData = inputData;
    }

    public ClusterFloatRowsScikitKmeansParams withInputData(FloatMatrix2D inputData) {
        this.inputData = inputData;
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
        return ((((((("ClusterFloatRowsScikitKmeansParams"+" [k=")+ k)+", inputData=")+ inputData)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
