
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
 * <p>Original spec-file type: MatrixFeatureSetUI</p>
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
    "feature_set_descriptors",
    "condition_descriptors",
    "feature_set_stat",
    "mtx_set_stat",
    "feature_set_correlation"
})
public class MatrixFeatureSetUI {

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
    @JsonProperty("feature_set_descriptors")
    private List<us.kbase.kbasefeaturevalues.ItemDescriptor> featureSetDescriptors;
    @JsonProperty("condition_descriptors")
    private List<us.kbase.kbasefeaturevalues.ItemDescriptor> conditionDescriptors;
    /**
     * <p>Original spec-file type: SetStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
     *             In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
     *             In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.
     *             
     *             indeces_for - index of the item in a collection for which all statitics is collected
     *             indeces_on - indeces of items in the associated vector on which the statistics is calculated
     *             size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
     *             avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
     *             mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
     *             maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
     *             stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
     *             missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
     *             
     * $$ DTO $$
     * </pre>
     * 
     */
    @JsonProperty("feature_set_stat")
    private SetStat featureSetStat;
    /**
     * <p>Original spec-file type: SetStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
     *             In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
     *             In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.
     *             
     *             indeces_for - index of the item in a collection for which all statitics is collected
     *             indeces_on - indeces of items in the associated vector on which the statistics is calculated
     *             size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
     *             avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
     *             mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
     *             maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
     *             stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
     *             missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
     *             
     * $$ DTO $$
     * </pre>
     * 
     */
    @JsonProperty("mtx_set_stat")
    private SetStat mtxSetStat;
    /**
     * <p>Original spec-file type: PairwiseMatrixStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * To represent a pairwise matrix with sprecalculated statistics. 
     * It can be used to represent pairwise correlation for a set of genes.
     * </pre>
     * 
     */
    @JsonProperty("feature_set_correlation")
    private PairwiseMatrixStat featureSetCorrelation;
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

    public MatrixFeatureSetUI withMtxDescriptor(MatrixDescriptor mtxDescriptor) {
        this.mtxDescriptor = mtxDescriptor;
        return this;
    }

    @JsonProperty("feature_set_descriptors")
    public List<us.kbase.kbasefeaturevalues.ItemDescriptor> getFeatureSetDescriptors() {
        return featureSetDescriptors;
    }

    @JsonProperty("feature_set_descriptors")
    public void setFeatureSetDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> featureSetDescriptors) {
        this.featureSetDescriptors = featureSetDescriptors;
    }

    public MatrixFeatureSetUI withFeatureSetDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> featureSetDescriptors) {
        this.featureSetDescriptors = featureSetDescriptors;
        return this;
    }

    @JsonProperty("condition_descriptors")
    public List<us.kbase.kbasefeaturevalues.ItemDescriptor> getConditionDescriptors() {
        return conditionDescriptors;
    }

    @JsonProperty("condition_descriptors")
    public void setConditionDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> conditionDescriptors) {
        this.conditionDescriptors = conditionDescriptors;
    }

    public MatrixFeatureSetUI withConditionDescriptors(List<us.kbase.kbasefeaturevalues.ItemDescriptor> conditionDescriptors) {
        this.conditionDescriptors = conditionDescriptors;
        return this;
    }

    /**
     * <p>Original spec-file type: SetStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
     *             In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
     *             In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.
     *             
     *             indeces_for - index of the item in a collection for which all statitics is collected
     *             indeces_on - indeces of items in the associated vector on which the statistics is calculated
     *             size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
     *             avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
     *             mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
     *             maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
     *             stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
     *             missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
     *             
     * $$ DTO $$
     * </pre>
     * 
     */
    @JsonProperty("feature_set_stat")
    public SetStat getFeatureSetStat() {
        return featureSetStat;
    }

    /**
     * <p>Original spec-file type: SetStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
     *             In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
     *             In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.
     *             
     *             indeces_for - index of the item in a collection for which all statitics is collected
     *             indeces_on - indeces of items in the associated vector on which the statistics is calculated
     *             size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
     *             avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
     *             mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
     *             maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
     *             stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
     *             missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
     *             
     * $$ DTO $$
     * </pre>
     * 
     */
    @JsonProperty("feature_set_stat")
    public void setFeatureSetStat(SetStat featureSetStat) {
        this.featureSetStat = featureSetStat;
    }

    public MatrixFeatureSetUI withFeatureSetStat(SetStat featureSetStat) {
        this.featureSetStat = featureSetStat;
        return this;
    }

    /**
     * <p>Original spec-file type: SetStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
     *             In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
     *             In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.
     *             
     *             indeces_for - index of the item in a collection for which all statitics is collected
     *             indeces_on - indeces of items in the associated vector on which the statistics is calculated
     *             size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
     *             avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
     *             mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
     *             maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
     *             stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
     *             missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
     *             
     * $$ DTO $$
     * </pre>
     * 
     */
    @JsonProperty("mtx_set_stat")
    public SetStat getMtxSetStat() {
        return mtxSetStat;
    }

    /**
     * <p>Original spec-file type: SetStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * Same as ItemStat, but for a set of Items. Actually it can be modeled as a list<ItemStat>, but sometimes we might need set of sets, and it becomes complicated...
     *             In relation to ExpressionMatrix, this type can be used to build a sparklines acorss all conditions for a collection of genes. 
     *             In this case: indeces_for - indeces of columns representing all (or a subset of) conditions,  indeces_on - indeces of rows representing genes.
     *             
     *             indeces_for - index of the item in a collection for which all statitics is collected
     *             indeces_on - indeces of items in the associated vector on which the statistics is calculated
     *             size - number of elements defined by indeces_on (expected to be the same for all items defined by indeces_for)
     *             avgs - mean values for each item defined by indeces_for across all elements defined by indeces_on 
     *             mins - min values for each item defined by indeces_for across all elements defined by indeces_on 
     *             maxs - max values for each item defined by indeces_for across all elements defined by indeces_on 
     *             stds - std values for each item defined by indeces_for across all elements defined by indeces_on 
     *             missing_values - number of missing values for each item defined by indeces_for across all elements defined by indeces_on 
     *             
     * $$ DTO $$
     * </pre>
     * 
     */
    @JsonProperty("mtx_set_stat")
    public void setMtxSetStat(SetStat mtxSetStat) {
        this.mtxSetStat = mtxSetStat;
    }

    public MatrixFeatureSetUI withMtxSetStat(SetStat mtxSetStat) {
        this.mtxSetStat = mtxSetStat;
        return this;
    }

    /**
     * <p>Original spec-file type: PairwiseMatrixStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * To represent a pairwise matrix with sprecalculated statistics. 
     * It can be used to represent pairwise correlation for a set of genes.
     * </pre>
     * 
     */
    @JsonProperty("feature_set_correlation")
    public PairwiseMatrixStat getFeatureSetCorrelation() {
        return featureSetCorrelation;
    }

    /**
     * <p>Original spec-file type: PairwiseMatrixStat</p>
     * <pre>
     * [PSN; Jul 22, 2015]
     * To represent a pairwise matrix with sprecalculated statistics. 
     * It can be used to represent pairwise correlation for a set of genes.
     * </pre>
     * 
     */
    @JsonProperty("feature_set_correlation")
    public void setFeatureSetCorrelation(PairwiseMatrixStat featureSetCorrelation) {
        this.featureSetCorrelation = featureSetCorrelation;
    }

    public MatrixFeatureSetUI withFeatureSetCorrelation(PairwiseMatrixStat featureSetCorrelation) {
        this.featureSetCorrelation = featureSetCorrelation;
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
        return ((((((((((((((("MatrixFeatureSetUI"+" [mtxDescriptor=")+ mtxDescriptor)+", featureSetDescriptors=")+ featureSetDescriptors)+", conditionDescriptors=")+ conditionDescriptors)+", featureSetStat=")+ featureSetStat)+", mtxSetStat=")+ mtxSetStat)+", featureSetCorrelation=")+ featureSetCorrelation)+", additionalProperties=")+ additionalProperties)+"]");
    }

}
