
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
 * <p>Original spec-file type: GetMatrixItemDescriptorsParams</p>
 * <pre>
 * ******************************************
 * 	* data API: parameters and functions      *
 * 	*****************************************
 * </pre>
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Generated("com.googlecode.jsonschema2pojo")
@JsonPropertyOrder({
    "input_data",
    "item_type",
    "item_indeces",
    "item_ids",
    "requested_property_types"
})
public class GetMatrixItemDescriptorsParams {

    @JsonProperty("input_data")
    private java.lang.String inputData;
    @JsonProperty("item_type")
    private java.lang.String itemType;
    @JsonProperty("item_indeces")
    private List<Long> itemIndeces;
    @JsonProperty("item_ids")
    private List<String> itemIds;
    @JsonProperty("requested_property_types")
    private List<String> requestedPropertyTypes;
    private Map<java.lang.String, Object> additionalProperties = new HashMap<java.lang.String, Object>();

    @JsonProperty("input_data")
    public java.lang.String getInputData() {
        return inputData;
    }

    @JsonProperty("input_data")
    public void setInputData(java.lang.String inputData) {
        this.inputData = inputData;
    }

    public GetMatrixItemDescriptorsParams withInputData(java.lang.String inputData) {
        this.inputData = inputData;
        return this;
    }

    @JsonProperty("item_type")
    public java.lang.String getItemType() {
        return itemType;
    }

    @JsonProperty("item_type")
    public void setItemType(java.lang.String itemType) {
        this.itemType = itemType;
    }

    public GetMatrixItemDescriptorsParams withItemType(java.lang.String itemType) {
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

    public GetMatrixItemDescriptorsParams withItemIndeces(List<Long> itemIndeces) {
        this.itemIndeces = itemIndeces;
        return this;
    }

    @JsonProperty("item_ids")
    public List<String> getItemIds() {
        return itemIds;
    }

    @JsonProperty("item_ids")
    public void setItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
    }

    public GetMatrixItemDescriptorsParams withItemIds(List<String> itemIds) {
        this.itemIds = itemIds;
        return this;
    }

    @JsonProperty("requested_property_types")
    public List<String> getRequestedPropertyTypes() {
        return requestedPropertyTypes;
    }

    @JsonProperty("requested_property_types")
    public void setRequestedPropertyTypes(List<String> requestedPropertyTypes) {
        this.requestedPropertyTypes = requestedPropertyTypes;
    }

    public GetMatrixItemDescriptorsParams withRequestedPropertyTypes(List<String> requestedPropertyTypes) {
        this.requestedPropertyTypes = requestedPropertyTypes;
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
        return ((((((((((((("GetMatrixItemDescriptorsParams"+" [inputData=")+ inputData)+", itemType=")+ itemType)+", itemIndeces=")+ itemIndeces)+", itemIds=")+ itemIds)+", requestedPropertyTypes=")+ requestedPropertyTypes)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
