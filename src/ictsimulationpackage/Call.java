package ictsimulationpackage;

import java.util.ArrayList;

public class Call {
	// ある１つの呼のデータ
	static ArrayList<Call> limitList[];// 有る時間に終了する呼のリスト
	static int sumHoldTime[];
	static long areaKosu[] = new long[102];
	static long exKosu[] = new long[102];
	static long areaLossKosu[] = new long[102];
	static long exLossKosu[] = new long[102];
	Building start;
	Building dest;
	int EndTime;
	static long timeLength;
	ArrayList<Link> LinkList;
	boolean success = false;

	static void reset(int length) {
		for (int i = 0; i < length; i++) {
			// 初期化
			limitList[i] = new ArrayList<Call>();
			sumHoldTime[i] = 0;
		}
	}

	Call(int tLength) {
		sumHoldTime = new int[tLength];
		limitList = new ArrayList[tLength];
		for (int i = 0; i < tLength; i++) {
			// 初期化
			limitList[i] = new ArrayList<Call>();
		}
		timeLength = tLength;
		System.out.println("Call Class initialized with timelength:" + timeLength);
	}

	Call(Building start, Building dest, int time) {
		// time:生起時刻、broken[]:壊れたリンク
		this.start = start;
		this.dest = dest;
		// 終了時刻
		int holdTime = HoldingTime.OneHoldingTime();
		this.EndTime = time + holdTime;
		sumHoldTime[time] += holdTime;
		// System.out.println("sum hold time" + sumHoldTime[time]);
		if (start == dest) {
			// 出発ビルと到着ビルが同じ場合
			success = true;
		} else {
			// 使用するリンク
			this.LinkList = LargeRing.route(start, dest);
			if (this.LinkList != null) {
				// 接続に成功した場合
				for (Link ln : this.LinkList) {
					ln.addCap();
				}
				success = true;
			}
		}

		if (success && EndTime < timeLength) {
			// System.out.println("EndTime:" + EndTime);
			limitList[EndTime].add(this);
			// 呼の発生種別をリンクに選り分ける
			if (start.areaBldg != null && dest.areaBldg != null && start != dest &&
					time < 14*60 && time > 12*60) {
				if ((start.areaBldg == dest.areaBldg)) {
					for (Link ln : LinkList) {
						if (ln.id < 102) {
							areaKosu[ln.id]++;
						}
					}
				} else {
					for (Link ln : LinkList) {
						if (ln.id < 102) {
							exKosu[ln.id]++;
						}
					}
				}
			}
		} else if (EndTime < timeLength) {
//			// 呼の発生種別をリンクに選り分ける
//			if (start.areaBldg != null && dest.areaBldg != null && start != dest) {
//				if ((start.areaBldg == dest.areaBldg)) {
//					for (Link ln : LinkList) {
//						if (ln.id < 102) {
//							areaLossKosu[ln.id]++;
//						}
//					}
//				} else {
//					for (Link ln : LinkList) {
//						if (ln.id < 102) {
//							exLossKosu[ln.id]++;
//						}
//					}
//				}
//			}
		}
	}

	void delete() {
		if (LinkList == null) {
			return;
		}
		for (Link ln : LinkList) {
			if (!ln.subCap()) {
				System.out.println("capacity goes to 0");
				System.out.println(1 / 0);
			}
		}
	}
}
