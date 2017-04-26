import  java.util.HashSet;
import	java.util.Set;
/**
 * Created by nathanielwendt on 4/2/17.
 */
public class Program3 {

    public ActivityResult selectActivity(ActivityProblem activityProblem){
		HashSet<String> actTable = new HashSet<String>();
		if(activityProblem.activities.length == 0 || activityProblem.funLevels.length == 0 || activityProblem.riskLevels.length == 0){
			return new ActivityResult(0 , actTable);
		}
    	int[][] dMatrix = new int[activityProblem.activities.length + 1][activityProblem.riskBudget + 1];
    	for(int j = activityProblem.riskBudget - 1; j >= 0; j--){
    		dMatrix[0][j] = 0;
		}
    	for (int i = 1; i < activityProblem.activities.length + 1; i++){
    		for (int j = 0; j < activityProblem.riskBudget + 1; j++){
    			if (activityProblem.riskLevels[i-1] > j){
    				dMatrix[i][j] = dMatrix[i-1][j];
				}
				else {
    				int leave = dMatrix[i - 1][j];
    				int keep = dMatrix[i-1][j-activityProblem.riskLevels[i-1]] + activityProblem.funLevels[i-1];
    				dMatrix[i][j] = keep > leave ? keep : leave;
				}
			}
		}
		//boolean foundAct = false;
		int i = activityProblem.activities.length;
		int j = activityProblem.riskBudget;
		while(true){
			if(j == 0 || i == 0){
				break;
			}
			else if(dMatrix[i][j] == dMatrix[i][j-1]){
				j--;
			}
			else if(dMatrix[i][j] > dMatrix [i][j-1]){
				if( dMatrix[i][j] > dMatrix[i-1][j]){
					actTable.add(activityProblem.activities[i-1]);
				}
				else {
					while (dMatrix[i][j] == dMatrix [i-1][j]){
						i--;
					}
					actTable.add(activityProblem.activities[i-1]);
				}

				j = j - activityProblem.riskLevels[i-1];
				if(i == 0){break;}
				while(dMatrix[i][j] == dMatrix[i-1][j]){
					i--;
					if(i == 0){ break;}
				}
			}

		}
		return new ActivityResult(dMatrix[activityProblem.activities.length][activityProblem.riskBudget], actTable);
    	/*
				//ActivityResult activities = new ActivityResult();
				int remainingRisk = activityProblem.riskBudget;
				int[] funTable = new int[activityProblem.activities.length];
				HashSet<String> actTable = new HashSet<String>();
				initFunTable(funTable);
				int maxFunLevel = recSelectActivity(activityProblem, activityProblem.activities.length - 1, remainingRisk, funTable, actTable);

				//System.out.println(maxFunLevel);
        return new ActivityResult(maxFunLevel, actTable);
        */
    }
		/*
		 * Recursively find the set of activities with the largest value
		 * while staying within the weight limit boundaries
		 */
		/*
    public int  recSelectActivity(ActivityProblem activityProblem, int index, int riskBudget, int[] funTable, Set<String> actTable){
				/* If current item does not fit in budget,
				 * store previous value (if not bottom)  and return
				 */
				/*
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
				 //This check doesn't do anything useful
				if(funTable[index] != -1){
								return funTable[index];
				}

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
		//Fill table with -1
    public void  initFunTable(int[] table){
				for(int i = table.length - 1; i >= 0; i--){
								table[i] = -1;
				}
		}
		*/

    public SchedulingResult selectScheduling(SchedulingProblem schedulingProblem){
				//Initialize arrays to track costs and create schedules based on starting position
				int[] startMCosts = new int[schedulingProblem.mauiCosts.length];
				int[] startOCosts = new int[schedulingProblem.mauiCosts.length];
				boolean[] mSchedule = new boolean[schedulingProblem.mauiCosts.length];
				boolean[] oSchedule = new boolean[schedulingProblem.oahuCosts.length];
				if(schedulingProblem.mauiCosts.length == 0){
					return new SchedulingResult(new boolean[] {});
				}

				//Initialize arrays by adding starting locations and costs
				startMCosts[0] = schedulingProblem.mauiCosts[0];
				startOCosts[0] = schedulingProblem.oahuCosts[0];
				mSchedule[0] = true;
				oSchedule[0] = false;



				//Recursively find cheapest schedule for staying i days across both islands
				//Makes decisions by adding a day to the plan with i-1 days
				for(int i = 1; i < schedulingProblem.mauiCosts.length; i++){
					int stayOnM;
					int moveToO;
					int stayOnO;
					int moveToM;

					if(mSchedule[i-1]){
						stayOnM = startMCosts[i-1] + schedulingProblem.mauiCosts[i];
						moveToO = startMCosts[i-1] + schedulingProblem.transferCost + schedulingProblem.oahuCosts[i];
					}
					else {
						stayOnM = startMCosts[i-1] + schedulingProblem.oahuCosts[i];
						moveToO = startMCosts[i-1] + schedulingProblem.transferCost + schedulingProblem.mauiCosts[i];
					}
					if(!oSchedule[i-1]){
						stayOnO = startOCosts[i-1] + schedulingProblem.oahuCosts[i];
						moveToM = startOCosts[i-1] + schedulingProblem.transferCost + schedulingProblem.mauiCosts[i];
					}
					else{
						stayOnO = startOCosts[i-1] + schedulingProblem.mauiCosts[i];
						moveToM = startOCosts[i-1] + schedulingProblem.transferCost + schedulingProblem.oahuCosts[i];
					}

					//Update costs and schedule by adding a day and considering staying vs switching
					startMCosts[i] = stayOnM < moveToO ? stayOnM : moveToO;
					mSchedule[i] = stayOnM < moveToO ? mSchedule[i-1] : !mSchedule[i-1];
					startOCosts[i] = stayOnO < moveToM ? stayOnO : moveToM;
					oSchedule[i] = stayOnO < moveToM ? oSchedule[i-1] : !oSchedule[i-1];

				}



        return new SchedulingResult(startMCosts[startMCosts.length-1] < startOCosts[startOCosts.length-1] ? mSchedule : oSchedule);
    }
		/*
		public int recSelectSchedule(int[] schedule, int[] cost, int dayIndex, boolean location, SchedulingProblem s){
				//If bottom of table, fill with starting position value
				if(dayIndex == 0){
					cost[0] = location ? s.mauiCosts[0] : s.oahuCosts[0];
					schedule[0] = location ? 1 : 0;
					return cost[0];
				}
				int stayCost = 0;
				int changeCost = 0;
				int lcost = 0;
				if(schedule[dayIndex - 1] == - 1){
					lcost = recSelectSchedule(schedule,cost, dayIndex-1, location, s);
				}
				//Determine if its more expensive to stay or transfer
				if(schedule[dayIndex - 1] == 1){
					//lcost = recSelectSchedule(schedule, cost, dayIndex - 1, location, s);
					stayCost = s.mauiCosts[dayIndex] + lcost;
					changeCost = s.oahuCosts[dayIndex] + s.transferCost + lcost;
				}
				else if (schedule[dayIndex - 1] == 0){
					//lcost = recSelectSchedule(schedule, cost, dayIndex - 1, location, s);
					stayCost = s.oahuCosts[dayIndex] + lcost;
					changeCost = s.mauiCosts[dayIndex] + s.transferCost + lcost;
				}

				//Add schedule and cost to table depending on cheapest option
				//schedule[dayIndex] = stayCost < changeCost ? schedule[dayIndex - 1] : (schedule[dayIndex - 1]+1)%2;
				if(stayCost < changeCost){
					schedule[dayIndex] = schedule[dayIndex - 1];
				}
				else{
					schedule[dayIndex] = (schedule[dayIndex - 1] + 1)%2;
				}
				cost[dayIndex] = stayCost < changeCost ? cost[dayIndex - 1] + stayCost : cost[dayIndex - 1] + changeCost;
				return cost[dayIndex];
		}
		public boolean[] parseSchedule(int[] schedule){
			boolean[] boolS = new boolean[schedule.length];
			for(int i = schedule.length; i > 0; i--){
				switch (schedule[i]){
					case 0: boolS[i] = false;
					case 1: boolS[i] = true;
					default:
				}

			}
			return boolS;
		}
		*/
}
