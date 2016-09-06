package ictsimulationpackage;

public class CandiCall {
	Building start;
	Building dest;
	int time;
	
	CandiCall(Building s, Building d, int t){
		start = s;
		dest = d;
		time = t;
	}
	
	//呼の生成
	Call generateCall(){
		Call call = new Call(start, dest, time);
		return call;
	}
}
