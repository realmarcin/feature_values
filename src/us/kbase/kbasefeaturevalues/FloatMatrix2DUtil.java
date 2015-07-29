package us.kbase.kbasefeaturevalues;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;

public class FloatMatrix2DUtil {
	
	public static List<ItemStat> getRowsStat(FloatMatrix2D matrix, List<Long> indecesFor, List<Long> indecesOn, boolean populateIndecesOn ){
		RowIterator itFor = new RowIterator(matrix, indecesFor); 
		ColumnIterator itOn = new ColumnIterator(matrix, indecesOn); 		
		return getItemsStat(itOn, itFor, populateIndecesOn);
	}

	public static List<ItemStat> getColumnsStat(FloatMatrix2D matrix, List<Long> indecesFor, List<Long> indecesOn, boolean populateIndecesOn ){
		ColumnIterator itFor = new ColumnIterator(matrix, indecesFor); 
		RowIterator itOn = new RowIterator(matrix, indecesOn); 		
		return getItemsStat(itOn, itFor, populateIndecesOn);
	}	
	
	public static ItemSetStat getRowsSetStat(FloatMatrix2D matrix,
			GetMatrixSetStatParams params) {
		RowIterator itFor = new RowIterator(matrix, params.getItemIndecesFor()); 
		ColumnIterator itOn = new ColumnIterator(matrix, params.getItemIndecesOn()); 		
		return getItemsSetStat(itFor, itOn, params);
	}

	public static ItemSetStat getColumnsSetStat(FloatMatrix2D matrix,
			GetMatrixSetStatParams params) {
		ColumnIterator itFor = new ColumnIterator(matrix, params.getItemIndecesFor()); 
		RowIterator itOn = new RowIterator(matrix, params.getItemIndecesOn()); 		
		return getItemsSetStat(itFor, itOn, params);
	}
	
	
	private static List<ItemStat> getItemsStat(ItemIterator itOn, ItemIterator itFor, boolean populateIndecesOn ){
		List<ItemStat> stats = new ArrayList<ItemStat>();
		
		for(itFor.init(); itFor.next();){
			int indexFor = itFor.index();
			
			double avg = 0;
			double min = Double.NaN;
			double max = Double.NaN;
			double std = 0;
			int goodCount = 0;
			int badCount = 0;
			
			for(itOn.init(); itOn.next();){
				
				double value = itOn.value(indexFor);
				if( !Double.isNaN(value) ){
					goodCount++;
					avg += value;
					min = Double.isNaN(min) || value < min ? value : min; 
					max = Double.isNaN(max) || value > max ? value : max;
				} else{
					badCount++;
				}
			}
			
			// Normalize avg and calculate std
			if(goodCount > 1){
				avg /= goodCount;			
				for(itOn.init(); itOn.next();){					
					double value = itOn.value(indexFor);
					if( !Double.isNaN(value) ){
						std += (avg - value)*(avg - value);
					}
				}
				std = Math.sqrt(std/(goodCount - 1));
			}
			
			ItemStat stat = new ItemStat()
				.withIndexFor((long)indexFor)
				.withAvg(goodCount > 0 ? avg : null)
				.withMin(goodCount > 0 ? min : null)
				.withMax(goodCount > 0 ? max : null)
				.withStd(goodCount > 1 ? std : null)
				.withSize( (long) (goodCount + badCount))
				.withMissingValues((long) badCount)
				.withIndecesOn( populateIndecesOn ? itOn.indeces() : null )
				;
			stats.add(stat);			
		}
		return stats;		
	} 
	
	
	
	
	private static ItemSetStat getItemsSetStat(ItemIterator itOn, ItemIterator itFor,
			GetMatrixSetStatParams params) {
		
		boolean flIndecesFor = toBoolean( params.getFlIndecesFor() );
		boolean flIndecesOn = toBoolean( params.getFlIndecesOn() );
		boolean flAvgs = toBoolean( params.getFlAvgs() ); 
		boolean flMaxs = toBoolean( params.getFlMaxs() );
		boolean flMins = toBoolean( params.getFlMins() );
		boolean flStds = toBoolean( params.getFlStds() ); 
		boolean flMissingValues = toBoolean( params.getFlMissingValues() );
				
		// For simplicity we will calculate all, but later will include in the output only requested stat
		double[] avgs = new double[itOn.size()];
		double[] maxs = new double[itOn.size()];
		double[] mins = new double[itOn.size()];
		double[] stds = new double[itOn.size()];
		long[] goodCounts = new long[itOn.size()];
		long[] badCounts = new long[itOn.size()];
		
		itFor.init();
		for(int i = 0; itFor.next(); i++){
			int indexFor = itFor.index();
			
			goodCounts[i] = 0;
			badCounts[i] = 0;
			avgs[i] = 0;
			maxs[i] = Double.NaN;
			mins[i] = Double.NaN;
			stds[i] = 0;
						
			for(itOn.init(); itOn.next();){
				
				double value = itOn.value(indexFor);
				if( !Double.isNaN(value) ){
					goodCounts[i]++;
					avgs[i] += value;
					mins[i] = Double.isNaN(mins[i]) || value < mins[i] ? value : mins[i]; 
					maxs[i] = Double.isNaN(maxs[i]) || value > maxs[i] ? value : maxs[i];
				} else{
					badCounts[i]++;
				}
			}
			
			// Normalize avg and calculate std
			if(goodCounts[i] > 1){
				avgs[i] /= goodCounts[i];			
				for(itOn.init(); itOn.next();){					
					double value = itOn.value(indexFor);
					if( !Double.isNaN(value) ){
						stds[i] += (avgs[i] - value)*(avgs[i] - value);
					}
				}
				stds[i] = Math.sqrt(stds[i]/(goodCounts[i] - 1));
			}
		}
		
		return new ItemSetStat()
			.withAvgs( flAvgs ? Arrays.asList(ArrayUtils.toObject(avgs)) : null)
			.withIndecesFor(flIndecesFor ? itFor.indeces() : null)
			.withIndecesOn(flIndecesOn ? itOn.indeces() : null)
			.withMaxs(flMaxs ? Arrays.asList(ArrayUtils.toObject(maxs)) : null)
			.withMins(flMins ? Arrays.asList(ArrayUtils.toObject(mins)) : null)
			.withMissingValues(flMissingValues ? Arrays.asList(ArrayUtils.toObject(badCounts)) : null)
			.withSize((long) itOn.size())
			.withStds(flStds ? Arrays.asList(ArrayUtils.toObject(stds)) : null);
	} 
	
	
	static abstract class ItemIterator{
		int i;
		int size;
		List<Long> indeces;
		FloatMatrix2D matrix;
		public ItemIterator(FloatMatrix2D matrix, List<Long> indeces){
			this.matrix = matrix;
			this.indeces = indeces;
			size = size();
			init();
		}
		public List<Long> indeces() {
			return indeces;
		}
		public abstract int size();
		public abstract double value(int indexOn);
		
		public void init(){
			i = -1;
		}
		
		public boolean next(){
			i++;
			return i < size;
		}
		
		public int index(){
			return indeces != null ? indeces.get(i).intValue() : i; 
		}
	}
	
	static class RowIterator extends ItemIterator{

		public RowIterator(FloatMatrix2D matrix, List<Long> indeces) {
			super(matrix, indeces);
		}

		@Override
		public int size() {
			return indeces != null ? indeces.size() : matrix.getRowIds().size();			
		}

		@Override
		public double value(int indexOn) {
			int indexFor = indeces != null ? indeces.get(i).intValue() : i;
			Double value =  matrix.getValues().get(indexFor).get(indexOn);
			return value == null ? Double.NaN : value.doubleValue();
		}		
	}
	
	static class ColumnIterator extends ItemIterator{

		public ColumnIterator(FloatMatrix2D matrix, List<Long> indeces) {
			super(matrix, indeces);
		}

		@Override
		public int size() {
			return indeces != null ? indeces.size() : matrix.getColIds().size();			
		}

		@Override
		public double value(int indexOn) {
			int indexFor = indeces != null ? indeces.get(i).intValue() : i;
			Double value =  matrix.getValues().get(indexOn).get(indexFor);
			return value == null ? Double.NaN : value.doubleValue();
		}		
	}	
	
	private static  boolean toBoolean(Long value){
		return value != null && value == 1;
	}

}
