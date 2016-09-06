package ictsimulationpackage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class Main {
	public static void main(String argv[]) {
		//計算時間の算出
		double calcTime ;
		calcTime = System.nanoTime();
		//24時間
		int hour = 5;
		//一時間=60分
		int timelength = hour * 60;
		// 出力ファイル
		int filenum = 0;
		// 出力フォルダ
		String folder = "/Users/jaga/Documents/domain_project/test_output/test";

		//hakaiとは？
		for (int hakai = 0; hakai < 1; hakai++) {
			//baiは破壊するリンクの数？
			for (int bai = 1; bai < 2; bai++) {
				// for(int b = 40; b < 41; b++){
				
				//ビルディングリストオブジェクトの作成
				BuildingList bldgs = new BuildingList(102);
				
				//全てのリンク情報の取得
				ArrayList<Link> allLinks = bldgs.allLinkList;
				
				double m = bai * 5; // 需要が何倍か
				
				// CallExist[t] = 時刻tに存在する呼の数
				int CallExist[] = new int[timelength];
				
				// CallOccur[t] = 時刻tに生起した呼の数
				int CallOccur[] = new int[timelength];
				
				// CallLoss[t] = 時刻tに損失した呼の数
				int CallLoss[] = new int[timelength];
				
				// CallLossRate[t] = 時刻tの呼損率
				double CallLossRate[] = new double[timelength];
				
				//1リンク設計回線数
				int kaisensu = (int) (221486 * 0.5); //区内
				int outKaisensu = 78276; //区外
				
				//区内リンクの回線数設定
				for(Link ln :  bldgs.linkList){
					ln.iniCap(timelength, kaisensu );
				}
				//区内中継リンクの回線数設定
				for(Link ln : bldgs.exLinkList){
					ln.iniCap(timelength, kaisensu );
				}
				//区外中継リンクの回線数設定
				bldgs.outLink.iniCap(timelength, outKaisensu);

				// 破壊リンクの設定
				int broken[] = { 69, 70, 76, 86 };

//				// 破壊リンクのキャパシティ設定 amountー＞破壊率(片道50%?)
				double ammount = 0.5 * hakai; //=0 破壊はループで回るが0しか取らない。なんだこれ？
//				for (int i = 0; i < broken.length; i++) {
//					//壊れたリンクのt=0における回線数
//					capacity[0][broken[i]] = (int) (capacity[0][broken[i]] * ammount);//=0
//					//壊れたリンクの設計回線数in
//					kaisen[broken[i]] = (int) (kaisen[broken[i]] * ammount); //=0
//				}

				// listofcalllist.get(t) = 時刻tにおいて生起した呼のリスト
				ArrayList<ArrayList<Call>> listofcalllist = new ArrayList<ArrayList<Call>>();
				//max.get(t) = 時刻tにおいて発生した呼の中で最も終了時刻の遅いものの値
				ArrayList<Integer> max = new ArrayList<Integer>();

				// callETlist[t] = 時刻tに終了する呼のリスト
				@SuppressWarnings("unchecked")
				ArrayList<Call> callETlist[] = new ArrayList[timelength + 100];//なんで+100?
				for (int i = 0; i < callETlist.length; i++) {
					//初期化
					callETlist[i] = new ArrayList<Call>();
				}

				// 時間ループの開始
				for (int t = 0; t < timelength; t++) {
					System.out.println("時刻 : " + (t + 1));
					// capacityと現存呼は保存される
					if (t > 0) {
						for(Link ln : allLinks){
							ln.capUpdate(t);
						}
						CallExist[t] = CallExist[t - 1];
					}

					// この時間における呼のリスト
					ArrayList<Call> calllist = new ArrayList<Call>();
					// 呼の生成.
					 System.out.println("呼の生成開始");
					int M = t;//此度発生した呼の終了時刻の最遅値
					int num = 0; //新たに発生した呼の数？
					// 全てのビルの組み合わせを通して考えている。ー＞index
					for (int i = 0; i < 104 * 104; i++) {
						Kosu kosu = new Kosu(i, t);
						 System.out.println( (t+1)+ "分：" + i + "セット目の呼数get");
						// 呼数発生数を、呼数発生確率を用いて求める
						int occur = kosu.Occurrence(m);
//						 System.out.println("ポアソン過程完了");
						// 決定した呼数発生数に従って呼を生起する
						for (int j = 0; j < occur; j++) {
							Call call = new Call(i, t, broken, ammount);// 0時台以外から始める場合、+60*(開始時間-1)
							calllist.add(call);
							num++;
							if (call.EndTime > M) {
								M = call.EndTime;
							}
							call = null;
//							 System.out.println(i + "セット目の" + j + "個目の呼生成");
//							 System.out.println(t + "分別合計" + num);
						}
					}
					max.add(M);
					listofcalllist.add(calllist);
					// calllist.clear();
					CallOccur[t] = listofcalllist.get(t).size();
					 System.out.println("呼の生成完了");

					// 時刻tに生起したそれぞれの呼について通信の成功or失敗を評価
					 System.out.println("通信の成否評価開始");
					Collections.shuffle(listofcalllist.get(t));//LISTの中身をシャッフル
					for (int i = 0; i < listofcalllist.get(t).size(); i++) {
						if (listofcalllist.get(t).get(i).LinkList[0] == -1) {
							//使用するリンクが存在しない場合
							CallLoss[t] += 1;
						} else {
							boolean check = true;
							//リンクjに対して回線余裕が存在するかどうか
							for (int j = 0; j < 103; j++) {
								if (listofcalllist.get(t).get(i).LinkList[j] > 0) {
									check = capacity[t][j] >= listofcalllist.get(t).get(i).LinkList[j];
									if (check == false) {
										break;
									}
								}
							}
							if (check) {
								// EndTimeに終わる呼をcallList[EndTime]に加える
								callETlist[listofcalllist.get(t).get(i).EndTime].add(listofcalllist.get(t).get(i));
								// 0時台以外から始める場合、EndTime-60*(開始時間-1)
								// 各リンクの空き容量を更新
								for (int k = 0; k < 103; k++) {
									capacity[t][k] -= listofcalllist.get(t).get(i).LinkList[k];
								}
								CallExist[t] += 1;
							}else{
								// 損失呼
								CallLoss[t] += 1;
							}
						}
					}
					// System.out.println("通信の成否評価完了");
					listofcalllist.get(t).clear();

					System.out.println("時刻：" + (t + 1));// 0時台以外から始める場合、+60*(開始時間-1)
					for (int i = 0; i < 103; i++) {
						System.out.print(capacity[t][i] + ",");
					}
					System.out.println();

					// 時刻tに終了する呼を消去
					 System.out.println("呼の消去開始");
					for (int i = 0; i < callETlist[t].size(); i++) {
						for (int j = 0; j < 103; j++) {
							capacity[t][j] += callETlist[t].get(i).LinkList[j];
						}
						CallExist[t] -= 1;
					}
					callETlist[t].clear();
					 System.out.println("呼の消去完了");
					 
					//有る時間帯iにおける生起呼の最長終了時間が訪れたらそのリストを破壊する
					for (int i = 0; i < max.size(); i++) {
						// System.out.println(max.get(i));
						if (max.get(i) == t) {// 0時台以外から始める場合、+60*(開始時間-1)
							listofcalllist.get(i).clear();
						}
					}

					// 呼損率の算出
					CallLossRate[t] = ((double) CallLoss[t] * 100) / (double) CallOccur[t];

					System.out.println("生起：" + CallOccur[t] + ", 損失：" + CallLoss[t]);
					System.out.println("呼損率：" + CallLossRate[t]);
					System.out.println("存在：" + CallExist[t]);

				}

				// ファイル出力
				Calendar c = Calendar.getInstance();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MMdd_HHmmss");
				// System.out.println(sdf.format(c.getTime()));
				// String format = sdf.format(c.getTime());
				String format = String.valueOf(filenum);
				File datedir = new File(folder + format);
				datedir.mkdir();
				// summary
				try {
					File file = new File(folder + format + "_summary.txt");
					FileWriter filewriter = new FileWriter(file);
					filewriter.write("需要" + m + "倍");
					filewriter.write("  ");
					filewriter.write("破壊リンク：" + broken);
					filewriter.write("  ");
					filewriter.write("容量" + ammount + "倍");
					filewriter.close();
				} catch (IOException e) {
					System.out.println(e);
				}

				// 生起
				File Occurrencedir = new File(folder + format + "_呼の生起");
				Occurrencedir.mkdir();
				try {
					// 出力先を作成する
					FileWriter fw = new FileWriter(folder + format + "_呼の生起_all.csv", true); // ※１
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallOccur.length; i++) {
						// 内容を指定する
						pw.println(CallOccur[i]);
					}
					// ファイルに書き出す
					pw.close();
					System.out.println("呼の生起の出力が完了しました。");
				} catch (IOException ex) {
					// 例外時処理
					ex.printStackTrace();
				}

				// 損失
				File Lossdir = new File(folder + format + "_損失呼");
				Lossdir.mkdir();
				try {
					// 出力先を作成する
					FileWriter fw = new FileWriter(folder + format + "_損失呼_all.csv", true); // ※１
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallLoss.length; i++) {
						// 内容を指定する
						pw.println(CallLoss[i]);
					}
					// ファイルに書き出す
					pw.close();
					System.out.println("損失呼の出力が完了しました。");
				} catch (IOException ex) {
					// 例外時処理
					ex.printStackTrace();
				}

				// 呼損率
				File CallLossRatedir = new File(folder + format + "_呼損率");
				CallLossRatedir.mkdir();
				try {
					// 出力先を作成する
					FileWriter fw = new FileWriter(folder + format + "_呼損率_all.csv", true); // ※１
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallLossRate.length; i++) {
						// 内容を指定する
						pw.println(CallLossRate[i]);
					}
					// ファイルに書き出す
					pw.close();
					System.out.println("呼損率の出力が完了しました。");
				} catch (IOException ex) {
					// 例外時処理
					ex.printStackTrace();
				}

				// 存在
				File Existdir = new File(folder + format + "_存在呼数");
				Existdir.mkdir();
				try {
					// 出力先を作成する
					FileWriter fw = new FileWriter(folder + format + "_存在呼数_all.csv", true); // ※１
					PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
					for (int i = 0; i < CallExist.length; i++) {
						// 内容を指定する
						pw.println(CallExist[i]);
					}
					// ファイルに書き出す
					pw.close();

					System.out.println("存在呼数の出力が完了しました。");
				} catch (IOException ex) {
					// 例外時処理
					ex.printStackTrace();
				}
				filenum += 1;

				// }
			}
		}
		calcTime = System.nanoTime() - calcTime;
		System.out.println("計算時間：" + (calcTime*(Math.pow(10,-9))) + "s");
		System.out.println("計算時間：" + calcTime + "ns");
	}
}
