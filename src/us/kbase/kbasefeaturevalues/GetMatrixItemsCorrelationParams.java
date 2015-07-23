
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
 * <p>Original spec-file type: GetMatrixItemsCorrelationParams</p>
 * <pre>
 * [PSN; Jul 22, 2015]    
 * Another version of parameters to get statistics for a set of items from the Float2D type of matrices. 
 * This version is more flexible and will be later used to retrieve set of sets (we need to think about optimization).
 *   
 *             To work uniformly with rows and columns, the type of item ('row' or 'column') should be provided.  
 *             
 *             input_data - worskapce reference to the ExpressionMatrix object (later we should allow to work with other Float2DMatrix-like matrices, e.g. fitness)
 *             item_type_for - type of the items for wich the statistics will be calculated: can be either 'row' or 'column'
 *             item_indeces_for - indeces of items for whch statistics should be calculated 
 *             item_indeces_on - indeces of items on whch statistics should be calculated
 *             fl_indeces_on - defines whether the indeces_on should be populated in SetStat objects. The default value = 0. 
 *             fl_indeces_for - defines whether the indeces_for should be populated in SetStat objects. The default value = 0.
 *              
 *             fl_avgs - defines whether the avgs should be populated in SetStat objects. The default value = 0. 
 *             fl_mins - defines whether the mins should be populated in SetStat objects. The default value = 0. 
 *             fl_maxs - defines whether the maxs should be populated in SetStat objects. The default value = 0. 
 *             fl_stds - defines whether the stds should be populated in SetStat objects. The default value = 0. 
 *             fl_missing_values - defines whether the missing_values should be populated in SetStat objects. The default value = 0.
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "input_data",
    "item_type",
    "item_indeces",
    "fl_row_ids",
    "fl_col_ids"
})
public class GetMatrixItemsCorrelationParams {

    @JsonProperty("input_data")
    private String inputData;
    @JsonProperty("item_type")
    private String itemType;
    @JsonProperty("item_indeces")
    private List<Long> itemIndeces;
    @JsonProperty("fl_row_ids")
    private java.lang.Long flRowIds;
    @JsonProperty("fl_col_ids")
    private java.lang.Long flColIds;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("input_data")
    public String getInputData() {
        return inputData;
    }

    @JsonProperty("input_data")
    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public GetMatrixItemsCorrelationParams withInputData(String inputData) {
        this.inputData = inputData;
        return this;
    }

    @JsonProperty("item_type")
    public String getItemType() {
        return itemType;
    }

    @JsonProperty("item_type")
    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public GetMatrixItemsCorrelationParams withItemType(String itemType) {
        this.itemType = itemType;
        return this;
    }

    @JsonProperty("item_indeces")
    public List<Long> getItemIndeces() {
        return itemIndeces;
    }

    @JsonProperty("item_indeces")
    public void setItemIndeces(List<Long> itemIndeces) {
        this.itemIndeces = itemIndeces;
    }

    public GetMatrixItemsCorrelationParams withItemIndeces(List<Long> itemIndeces) {
        this.itemIndeces = itemIndeces;
        return this;
    }

    @JsonProperty("fl_row_ids")
    public java.lang.Long getFlRowIds() {
        return flRowIds;
    }

    @JsonProperty("fl_row_ids")
    public void setFlRowIds(java.lang.Long flRowIds) {
        this.flRowIds = flRowIds;
    }

    public GetMatrixItemsCorrelationParams withFlRowIds(java.lang.Long flRowIds) {
        this.flRowIds = flRowIds;
        return this;
    }

    @JsonProperty("fl_col_ids")
    public java.lang.Long getFlColIds() {
        return flColIds;
    }

    @JsonProperty("fl_col_ids")
    public void setFlColIds(java.lang.Long flColIds) {
        this.flColIds = flColIds;
    }

    public GetMatrixItemsCorrelationParams withFlColIds(java.lang.Long flColIds) {
        this.flColIds = flColIds;
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
        return ((((((((((((("GetMatrixItemsCorrelationParams"+" [inputData=")+ inputData)+", itemType=")+ itemType)+", itemIndeces=")+ itemIndeces)+", flRowIds=")+ flRowIds)+", flColIds=")+ flColIds)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
