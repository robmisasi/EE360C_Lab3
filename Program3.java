import  java.util.HashSet;
import	java.util.Set;
/**
 * Created by nathanielwendt on 4/2/17.
 */
public class Program3 {

    public ActivityResult selectActivity(ActivityProblem activityProblem){
				//ActivityResult activities = new ActivityResult();
				int remainingRisk = activityProblem.riskBudget;
				int[] funTable = new int[activityProblem.activities.length];
				HashSet<String> actTable = new HashSet<String>();
				initFunTable(funTable);
				int maxFunLevel = recSelectActivity(activityProblem, activityProblem.activities.length - 1, remainingRisk, funTable, actTable);

				//System.out.println(maxFunLevel);
        return new ActivityResult(maxFunLevel, actTable);
    }
		/** 
	   * Recursively find the set of activities with the largest value
		 * while staying within the weight limit boundaries
		 */
    public int  recSelectActivity(ActivityProblem activityProblem, int index, int riskBudget, int[] funTable, Set<String> actTable){
				/* If current item does not fit in budget,
				 * store previous value (if not bottom)  and return
				 */

				if (riskBudget - activityProblem.riskLevels[index] < 0){
								if(index == 0){
										funTable[index] = 0;
								}
								else {
										funTable[index] = funTable[index - 1];
								}
								return 0;
				}
				//If bottom of table, fill table and return
				else if(index == 0){
								funTable[0] = activityProblem.funLevels[0];
								return funTable[0];
				}
				//If item is in the table, return that item.
				/* //This check doesn't do anything useful
				if(funTable[index] != -1){
								return funTable[index];
				}
				*/
				//If item fits in budget, calculate whether to choose or ignore it
				int chooseIndex = activityProblem.funLevels[index] + recSelectActivity(activityProblem, index - 1, riskBudget - activityProblem.riskLevels[index], funTable, actTable);
				int leaveIndex = recSelectActivity(activityProblem, index - 1, riskBudget, funTable, actTable);
				//If item is added, update the index and add activity to the list
				//If not, update index and do not add it to the list
				if(chooseIndex > leaveIndex && chooseIndex > funTable[index]){
								funTable[index] = chooseIndex;
								actTable.add(activityProblem.activities[index]);
				}
				else if(leaveIndex > funTable[index]){
								funTable[index] = leaveIndex;
								
				}
				//return largest possible fun value
				return funTable[index];

				
		}
		//Fill fun table with -1
    public void  initFunTable(int[] table){
				for(int i = table.length - 1; i >= 0; i--){
								table[i] = -1;
				}
		}

    public SchedulingResult selectScheduling(SchedulingProblem schedulingProblem){
				//SchedulingResult result = new SchedulingResult();
				boolean[] schedule = new boolean[schedulingProblem.mauiCosts.length];
				//initFunTable(schedule);
				int[] cost = new int[schedulingProblem.mauiCosts.length];
				int dayIndex = schedulingProblem.mauiCosts.length;
				int mauiStart = recSelectSchedule(schedule, cost, dayIndex, true, schedulingProblem);
				int oahuStart = recSelectSchedule(schedule, cost, dayIndex, false, schedulingProblem);
				int bestPrice = mauiStart < oahuStart ? mauiStart : oahuStart;

        return new SchedulingResult(schedule);
    }

		public int recSelectSchedule(boolean[] schedule, int[] cost, int dayIndex, boolean location, SchedulingProblem s){
				//If bottom of table, fill with starting position value
				if(dayIndex == 0){
								cost[0] = location ? schedulingProblem.mauiCosts[0] : schedulingProblem.oahuCosts[0];
								schedule[0] = location ? 1 : 0;
								return cost[0];
				}
				int stayCost;
				int changeCost;
				//Determine if its more expensive to stay or transfer
				if(schedule[dayIndex - 1]){
								int lcost = recSelectSchedule(schedule, cost, dayIndex - 1, location, s);
								stayCost = s.mauiCosts[dayIndex] + lcost;
								changeCost = s.oahuCosts[dayIndex] + s.transferCost + lcost;
				}
				else{
								int lcost = recSelectSchedule(schedule, cost, dayIndex - 1, location, s);
								stayCost = s.oahuCosts[dayIndex] + lcost;
								changeCost = s.mauiCosts[dayIndex] + s.tranferCost + lcost;
				}
				//Add schedule and cost to table depending on cheapest option
				schedule[dayIndex] = stayCost < changeCost ? schedule[dayIndex - 1] : !schedule[dayIndex - 1];
				cost[dayIndex] = stayCost < changeCost ? cost[dayIndex - 1] + stayCost : cost[dayIndex - 1] + changeCost;
				return cost[dayIndex];
		}

