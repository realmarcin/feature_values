package us.kbase.kbasefeaturevalues;

import java.util.ArrayList;
import java.util.List;

public class FloatMatrix2DUtil {
	
	public static List<ItemStat> getRowsStat(FloatMatrix2D matrix, List<Long> indecesFor, List<Long> indecesOn, boolean populateIndecesOn ){
		List<ItemStat> stats = new ArrayList<ItemStat>();
		List<List<Double>> values = matrix.getValues();
		
		// If indecesFor is provided, then we will use indeces from this list, otherwise we will use all rows in the matrix 
		int rowCount = indecesFor != null ? indecesFor.size() : matrix.getRowIds().size();
		
		// The same for columns
		int colCount = indecesOn != null ? indecesOn.size() : matrix.getColIds().size(); 
		
		
		for(int ri = 0; ri < rowCount; ri++){
			int rIndex = indecesFor != null ? indecesFor.get(ri).intValue() : ri;
			
			double avg = 0;
			double min = Double.NaN;
			double max = Double.NaN;
			double std = 0;
			int goodCount = 0;
			int badCount = 0;
			
			for(int ci = 0; ci < colCount; ci++){
				int cIndex = indecesOn != null ? indecesOn.get(ci).intValue() : ci;
				
				Double value = values.get(rIndex).get(cIndex);
				if( value != null ){
					double dval = value.doubleValue();
					goodCount++;
					avg += dval;
					min = Double.isNaN(min) || dval < min ? dval : min; 
					min = Double.isNaN(min) || dval > max ? dval : max;
				} else{
					badCount++;
				}
			}

			
			// Normalize avg and calculate std
			if(goodCount > 1){
				avg /= goodCount;
			
				for(int ci = 0; ci < colCount; ci++){
					int cIndex = indecesOn != null ? indecesOn.get(ci).intValue() : ci;
					
					Double value = values.get(rIndex).get(cIndex);
					if( value != null ){
						std += (avg - value)*(avg - value);
					}
				}
				std = Math.sqrt(std/(goodCount - 1));
			}
			
			ItemStat stat = new ItemStat()
				.withIndexFor((long)rIndex)
				.withAvg(goodCount > 0 ? avg : null)
				.withMin(goodCount > 0 ? min : null)
				.withMax(goodCount > 0 ? max : null)
				.withStd(goodCount > 1 ? std : null)
				.withSize( (long) (goodCount + badCount))
				.withMissingValues((long) badCount)
				.withIndecesOn( populateIndecesOn ? indecesOn : null )
				;
			stats.add(stat);			
		}
		return stats;		
	} 
	
	public static List<ItemStat> getColumnsStat(FloatMatrix2D matrix,List<Long> indecesFor, List<Long> indecesOn, boolean populateIndecesOn ){
		List<ItemStat> stats = new ArrayList<ItemStat>();
		List<List<Double>> values = matrix.getValues();

		// If indecesFor is provided, then we will use indeces from this list, otherwise we will use all columns in the matrix 
		int colCount = indecesFor != null ? indecesFor.size() : matrix.getColIds().size(); 

		// The same for rows
		int rowCount = indecesOn != null ? indecesOn.size() : matrix.getRowIds().size();
		
		for(int ci = 0; ci < colCount; ci++){
			int cIndex = indecesFor != null ? indecesFor.get(ci).intValue() : ci;
						
			double avg = 0;
			double min = Double.NaN;
			double max = Double.NaN;
			double std = 0;
			int goodCount = 0;
			int badCount = 0;
			
			for(int ri = 0; ri < rowCount; ri++){
				int rIndex = indecesOn != null ? indecesOn.get(ri).intValue() : ri;
				
				Double value = values.get(rIndex).get(cIndex);
				if( value != null ){
					double dval = value.doubleValue();
					goodCount++;
					avg += dval;
					min = Double.isNaN(min) || dval < min ? dval : min; 
					min = Double.isNaN(min) || dval > max ? dval : max;
				} else{
					badCount++;
				}
			}

			
			// Normalize avg and calculate std
			if(goodCount > 1){
				avg /= goodCount;
			
				for(int ri = 0; ri < rowCount; ri++){
					int rIndex = indecesOn != null ? indecesOn.get(ri).intValue() : ri;
					
					Double value = values.get(rIndex).get(cIndex);
					if( value != null ){
						std += (avg - value)*(avg - value);
					}
				}
				std = Math.sqrt(std/(goodCount - 1));
			}
			
			ItemStat stat = new ItemStat()
				.withIndexFor((long) cIndex)
				.withAvg(goodCount > 0 ? avg : null)
				.withMin(goodCount > 0 ? min : null)
				.withMax(goodCount > 0 ? max : null)
				.withStd(goodCount > 1 ? std : null)
				.withSize( (long) (goodCount + badCount))
				.withMissingValues((long) badCount)
				.withIndecesOn( populateIndecesOn ? indecesOn : null )
				;
			stats.add(stat);			
		}
		return stats;		
	} 

	
	
}
